package com.sunflower.mqtt;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class SampleAsyncCallBack implements MqttCallback {
	int state = BEGIN;
	MqttAsyncClient client;
	String brokerUrl;
	private boolean quietMode;
	private MqttConnectOptions conOpt;
	private boolean clean;
	Throwable ex = null;
	Object waiter = new Object();
	boolean donext = false;
	private String password;
	private String userName;
	
	static final int BEGIN = 0;
	static final int CONNECTED = 1;
	static final int PUBLISHED = 2;
	static final int SUBSCRIBED = 3;
	static final int DISCONNECTED = 4;
	static final int FINISH = 5;
	static final int ERROR = 6;
	static final int DISCONNECT = 7;
	
	public SampleAsyncCallBack(String brokerUrl, String clientId, boolean cleanSession, boolean quietMode, String userName, String password) throws MqttException {
		this.brokerUrl = brokerUrl;
		this.quietMode = quietMode;
		this.clean = cleanSession;
		this.password = password;
		this.userName = userName;
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);
		
		try {
			conOpt = new MqttConnectOptions();
			conOpt.setCleanSession(clean);
			if (password != null) {
				conOpt.setPassword(this.password.toCharArray());
			}
			if (userName != null) {
				conOpt.setUserName(this.userName);
			}
			
			client = new MqttAsyncClient(this.brokerUrl, clientId, dataStore);
			client.setCallback(this);
		} catch (MqttException e) {
			e.printStackTrace();
			log("Unable to set up client: " + e.toString());
			System.exit(1);
		}
	}
	
	public void publish(String topicName, int qos, byte[] payload) throws Throwable {
		while (state != FINISH) {
			switch(state) {
			case BEGIN:
				MqttConnector con = new MqttConnector();
				con.doConnect();
				break;
			case CONNECTED:
				Publisher pub = new Publisher();
				pub.doPublish(topicName, qos, payload);
				break;
			case PUBLISHED:
				state = DISCONNECT;
				donext = true;
				break;
			case DISCONNECT:
				Disconnector disc = new Disconnector();
				disc.doDisconnect();
				break;
			case ERROR:
				throw ex;
			case DISCONNECTED:
				state = FINISH;
				donext = true;
				break;
			}
			
//			if (state != FINISH) {
				waitForStateChange(10000);
//			}
		}
	}
	
	private void waitForStateChange(int maxTTW) throws MqttException {
		synchronized (waiter) {
			if (!donext) {
				try {
					waiter.wait(maxTTW);
				} catch (InterruptedException e) {
					log("timed out");
					e.printStackTrace();
				}
				
				if (ex != null) {
					throw (MqttException)ex;
				}
			}
			donext = false;
		}
	}
	
	public void subscribe(String topicName, int qos) throws Throwable {
		while (state != FINISH) {
			switch(state) {
			case BEGIN:
				MqttConnector con = new MqttConnector();
				con.doConnect();
				break;
			case CONNECTED:
				Subscriber sub = new Subscriber();
				sub.doSubscribe(topicName, qos);
				break;
			case SUBSCRIBED:
				log("Press <Enter> to exit");
				try {
					System.in.read();
				} catch (IOException e) {
					
				}
				state = DISCONNECT;
				donext = true;
				break;
			case DISCONNECT:
				Disconnector dis = new Disconnector();
				dis.doDisconnect();
				break;
			case ERROR:
				throw ex;
			case DISCONNECTED:
				state = FINISH;
				donext = true;
				break;
			}
		}
		
//		if (state != FINISH && state != DISCONNECT) {
			waitForStateChange(10000);
//		}
	}

	@Override
	public void connectionLost(Throwable cause) {
		log("Connection to " + brokerUrl + " lost!" + cause);
		System.exit(1);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		log("Delivery complete callback: Publish Completed "+Arrays.toString(token.getTopics()));
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String time = new Timestamp(System.currentTimeMillis()).toString();
		System.out.println("Time:\t" +time +
                           "  Topic:\t" + topic +
                           "  Message:\t" + new String(message.getPayload()) +
                           "  QoS:\t" + message.getQos());
	}
	
    void log(String message) {
    	if (!quietMode) {
    		System.out.println(message);
    	}
    }
    
    static void printHelp() {
        System.out.println(
            "Syntax:\n\n" +
                "    SampleAsyncCallBack [-h] [-a publish|subscribe] [-t <topic>] [-m <message text>]\n" +
                "            [-s 0|1|2] -b <hostname|IP address>] [-p <brokerport>] [-i <clientID>]\n\n" +
                "    -h  Print this help text and quit\n" +
                "    -q  Quiet mode (default is false)\n" +
                "    -a  Perform the relevant action (default is publish)\n" +
                "    -t  Publish/subscribe to <topic> instead of the default\n" +
                "            (publish: \"Sample/Java/v3\", subscribe: \"Sample/#\")\n" +
                "    -m  Use <message text> instead of the default\n" +
                "            (\"Message from MQTTv3 Java client\")\n" +
                "    -s  Use this QoS instead of the default (2)\n" +
                "    -b  Use this name/IP address instead of the default (m2m.eclipse.org)\n" +
                "    -p  Use this port instead of the default (1883)\n\n" +
                "    -i  Use this client ID instead of SampleJavaV3_<action>\n" +
                "    -c  Connect to the server with a clean session (default is false)\n" +
                "     \n\n Security Options \n" +
                "     -u Username \n" +
                "     -z Password \n" +
                "     \n\n SSL Options \n" +
                "    -v  SSL enabled; true - (default is false) " +
                "    -k  Use this JKS format key store to verify the client\n" +
                "    -w  Passpharse to verify certificates in the keys store\n" +
                "    -r  Use this JKS format keystore to verify the server\n" +
                " If javax.net.ssl properties have been set only the -v flag needs to be set\n" +
                "Delimit strings containing spaces with \"\"\n\n" +
                "Publishers transmit a single message then disconnect from the server.\n" +
                "Subscribers remain connected to the server and receive appropriate\n" +
                "messages until <enter> is pressed.\n\n"
            );
      }
    
    public static void main(String[] args) {
    	// Default settings:
		boolean quietMode 	= false;
		String action 		= "publish";
		String topic 		= "";
		String message 		= "Message from async callback Paho MQTTv3 Java client sample";
		int qos 			= 2;
		String broker 		= "m2m.eclipse.org";
		int port 			= 1883;
		String clientId 	= null;
		String subTopic		= "Sample/#";
		String pubTopic 	= "Sample/Java/v3";
		boolean cleanSession = true;			// Non durable subscriptions
		boolean ssl = false;
		String password = null;
		String userName = null;
		
		// Parse the arguments
		for (int i = 0; i < args.length; i++) {
			// Check this is a valid argument
			if (args[i].length() == 2 && args[i].startsWith("-")) {
				char arg = args[i].charAt(1);
				switch(arg) {
				case 'h':case '?':	printHelp(); return;
				case 'q':quietMode = true; continue;
				}
				
				if (i == args.length -1 || args[i+1].charAt(0) == '-') {
					System.out.println("Missing value for argument: "+args[i]);
					printHelp();
					return;
				}
				
				switch(arg) {
				case 'a': action = args[++i];	break;
				case 't': topic = args[++i];	break;
				case 'm': message = args[++i];	break;
				case 's': qos = Integer.parseInt(args[++i]); break;
				case 'b': broker = args[++i]; break;
				case 'p': port = Integer.parseInt(args[++i]); break;
				case 'i': clientId = args[++i]; break;
				case 'c': cleanSession = Boolean.valueOf(args[++i]).booleanValue(); break;
				case 'k': System.getProperties().put("javax.net.ssl.keyStore", args[++i]); break;
		        case 'w': System.getProperties().put("javax.net.ssl.keyStorePassword", args[++i]); break;
		        case 'r': System.getProperties().put("javax.net.ssl.trustStore", args[++i]); break;
		        case 'v': ssl = Boolean.valueOf(args[++i]).booleanValue();  break;
		        case 'u': userName = args[++i];               break;
		        case 'z': password = args[++i];               break;
		        default:
		        	System.out.println("Unrecognised argument: " + args[i]);
		        	printHelp();
		        	return;
				}
			} else {
				System.out.println("Unrecognised argument: "+args[i]);
				printHelp();
				return;
			}
		}
		
		// Validate the provided arguments
		if (!action.equals("publish") && !action.equals("subscribe")) {
			System.out.println("Invalid action: "+action);
			printHelp();
			return;
		}
		if (qos < 0 || qos > 2) {
			System.out.println("Invalid QoS: "+qos);
			printHelp();
			return;
		}
		if (topic.equals("")) {
			// Set the default topic according to the specified action
			if (action.equals("publish")) {
				topic = pubTopic;
			} else {
				topic = subTopic;
			}
		}

		String protocol = "tcp://";
		if (ssl) {
			protocol = "ssl://";
		}
		String url = protocol + broker + ":" + port;
		
		if (clientId == null||clientId.equals("")) {
			clientId = "SampleJavaV3_" + action;
		}
		
		try {
			SampleAsyncCallBack sampleClient = new SampleAsyncCallBack(url, clientId, cleanSession, quietMode, userName, password);
			
			if (action.equals("publish")) {
				sampleClient.publish(topic,qos,message.getBytes());
			} else if (action.equals("subscribe")){
				sampleClient.subscribe(topic,qos);
			}
		} catch (MqttException me) {
			// Display full details of any exception that occurs
			System.out.println("reason "+me.getReasonCode());
			System.out.println("msg "+me.getMessage());
			System.out.println("loc "+me.getLocalizedMessage());
			System.out.println("cause "+me.getCause());
			System.out.println("excep "+me);
			me.printStackTrace();
		} catch (Throwable th) {
			System.out.println("Throwable caught " + th);
			th.printStackTrace();
		}
    }

    public class MqttConnector {
    	public MqttConnector() {
    	}
    	
    	public void doConnect() {
    		log("Connecting to " + brokerUrl + " with client ID " + client.getClientId());
    		IMqttActionListener conListener = new IMqttActionListener() {
				
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					log("Connected");
					state = CONNECTED;
					carryOn();
				}
				
				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					ex = exception;
					state = ERROR;
					log("connect failed" + exception);
					carryOn();
				}
				
				public void carryOn() {
					synchronized (waiter) {
						donext = true;
						waiter.notifyAll();
					}
				}
			};
			
			try {
				client.connect(conOpt, "Connect sample context", conListener);
			} catch(MqttException e) {
				state = ERROR;
				donext = true;
				ex = e;
			}
    	}
    }
    
    public class Publisher {
    	public void doPublish(String topicName, int qos, byte[] payload) {
    		MqttMessage message = new MqttMessage(payload);
    		message.setQos(qos);
    		
    		String time = new Timestamp(System.currentTimeMillis()).toString();
    		log("Publishing at: " + time + " to topic \"" + topicName + "\" qos " + qos);
    		
    		IMqttActionListener pubListener = new IMqttActionListener() {

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					ex = exception;
					state = ERROR;
					log("Publish failed" + exception);
					carryOn();
				}

				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					log("Publish Completed");
					state = PUBLISHED;
					carryOn();
				}
				
				public void carryOn() {
					synchronized (waiter) {
						donext = true;
						waiter.notifyAll();
					}
				}
    		};
    		
    		try {
    			client.publish(topicName, message, "Pub sample context", pubListener);
    		} catch (MqttException e) {
    			state = ERROR;
    			donext = true;
    			ex = e;
    		}
    	}
    }
    
    public class Subscriber {
    	public void doSubscribe(String topicName, int qos) {
    		log("Subscribing to topic \"" + topicName + "\" qos " + qos);
    		
    		IMqttActionListener subListener = new IMqttActionListener() {
				
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					log("Subscribe Completed");
					state = SUBSCRIBED;
					carryOn();
				}
				
				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					ex = exception;
					state = ERROR;
					log("Subscribe failed" + exception);
					carryOn();
				}
				
				public void carryOn() {
					synchronized (waiter) {
						donext = true;
						waiter.notifyAll();
					}
				}
			};
			
			try {
				client.subscribe(topicName, qos, "Subscribe sample context", subListener);
			} catch (MqttException e) {
				state = ERROR;
				donext = true;
				ex = e;
			}
    	}
    }
    
    public class Disconnector {
    	public void doDisconnect() {
    		log("Disconnecting");
    		
    		IMqttActionListener discListener = new IMqttActionListener() {
				
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					log("Disconnect Completed");
					state = DISCONNECTED;
					carryOn();
				}
				
				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					ex = exception;
					state = ERROR;
					log("Disconnect failed" + exception);
					carryOn();
				}
				
				public void carryOn() {
					synchronized (waiter) {
						donext = true;
						waiter.notifyAll();
					}
				}
			};
			
			try {
				client.disconnect("Disconnect sample context", discListener);
			} catch (MqttException e) {
				state = ERROR;
				donext = true;
				ex = e;
			}
    	}
    }
}
