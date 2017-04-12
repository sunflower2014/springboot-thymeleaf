package com.sunflower.mqtt;

import java.io.IOException;
import java.sql.Timestamp;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class SampleAsyncWait implements MqttCallback {
	// Private instance variables
	private MqttAsyncClient client;
	private String brokerUrl;
	private boolean quietMode;
	private MqttConnectOptions conOpt;
	private boolean clean;
	private String password;
	private String userName;
	
	public SampleAsyncWait(String brokerUrl, String clientId, boolean cleanSession, boolean quietMode, String userName, String password) throws MqttException {
		this.brokerUrl = brokerUrl;
		this.quietMode = quietMode;
		this.clean = cleanSession;
		this.userName = userName;
		this.password = password;
		
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
	
	public void publish(String topicName, int qos, byte[] payload) throws MqttException {
		log("Connecting to "+brokerUrl + " with client ID "+client.getClientId());
		IMqttToken conToken = client.connect(conOpt, null, null);
		conToken.waitForCompletion();
		log("Connected");
		
		String time = new Timestamp(System.currentTimeMillis()).toString();
		log("Publishing at: " + time + " to topic \"" + topicName + "\" qos " + qos);
		
		// Construct the message to send
		MqttMessage message = new MqttMessage(payload);
		message.setQos(qos);
		
		IMqttDeliveryToken pubToken = client.publish(topicName, message, null, null);
		pubToken.waitForCompletion();
		log("Published");
		
		log("Disconnecting");
		IMqttToken discToken = client.disconnect(null, null);
		discToken.waitForCompletion();
		log("Disconnected");
	}
	
	public void subscribe(String topicName, int qos) throws MqttException {
		log("Connecting to "+brokerUrl + " with client ID "+client.getClientId());
		IMqttToken conToken = client.connect(conOpt, null, null);
		conToken.waitForCompletion();
		log("Connected");
		
		log("Subscribing to topic \""+topicName+"\" qos "+qos);
		IMqttToken subToken = client.subscribe(topicName, qos, null, null);
		subToken.waitForCompletion();
		log("Subscribed to topic \"" + topicName);
		
		log("Press <Enter> to exit");
		try {
			System.in.read();
		} catch (IOException e) {
			
		}
		
		log("Disconnecting");
		IMqttToken discToken = client.disconnect(null, null);
		discToken.waitForCompletion();
		log("Disconnected");
	}
	

	@Override
	public void connectionLost(Throwable cause) {
		log("Connection to " + brokerUrl + " lost!" + cause);
		System.exit(1);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		try {
			log("Delivery complete callback: Publish Completed " + token.getMessage());
		} catch (Exception ex) {
			log("Exception in delivery complete callback" + ex);
		}
	}
	
    private void log(String message) {
    	if (!quietMode) {
    		System.out.println(message);
    	}
    }

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		String time = new Timestamp(System.currentTimeMillis()).toString();
		System.out.println("Time:\t" + time + 
				"	Topic:\t" + topic +
				"	Message:\t" + new String(message.getPayload()) +
				"	QoS:\t" + message.getQos());	
	}
	
	static void printHelp() {
	      System.out.println(
	          "Syntax:\n\n" +
	              "    SampleAsyncWait [-h] [-a publish|subscribe] [-t <topic>] [-m <message text>]\n" +
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
		String message 		= "Message from async waiter Paho MQTTv3 Java client sample";
		int qos 			= 2;
		String broker 		= "127.0.0.1";
		int port 			= 1883;
		String clientId 	= null;
		String subTopic		= "Sample/#";
		String pubTopic 	= "Sample/Java/v3";
		boolean cleanSession = true;			// Non durable subscriptions
		boolean ssl = false;
		String userName = null;
		String password = null;
		
		// Parse the arguments -
		for (int i=0; i<args.length; i++) {
			// Check this is a valid argument
			if (args[i].length() == 2 && args[i].startsWith("-")) {
				char arg = args[i].charAt(1);
				// Handle arguments that take no-value
				switch(arg) {
					case 'h': case '?':	printHelp(); return;
					case 'q': quietMode = true;	continue;
				}

				// Now handle the arguments that take a value and
				// ensure one is specified
				if (i == args.length -1 || args[i+1].charAt(0) == '-') {
					System.out.println("Missing value for argument: "+args[i]);
					printHelp();
					return;
				}
				switch(arg) {
					case 'a': action = args[++i];                 break;
					case 't': topic = args[++i];                  break;
					case 'm': message = args[++i];                break;
					case 's': qos = Integer.parseInt(args[++i]);  break;
					case 'b': broker = args[++i];                 break;
					case 'p': port = Integer.parseInt(args[++i]); break;
					case 'i': clientId = args[++i];				  break;
					case 'c': cleanSession = Boolean.valueOf(args[++i]).booleanValue();  break;
					case 'k': System.getProperties().put("javax.net.ssl.keyStore", args[++i]); break;
					case 'w': System.getProperties().put("javax.net.ssl.keyStorePassword", args[++i]); break;
					case 'r': System.getProperties().put("javax.net.ssl.trustStore", args[++i]);  break;
					case 'v': ssl = Boolean.valueOf(args[++i]).booleanValue();  break;
					case 'u': userName = args[++i];               break;
					case 'z': password = args[++i];               break;
					default:
						System.out.println("Unrecognised argument: "+args[i]);
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
			SampleAsyncWait sampleClient = new SampleAsyncWait(url, clientId, cleanSession, quietMode, userName, password);
			
			if (action.equals("publish")) {
				sampleClient.publish(topic, qos, message.getBytes());
			} else if (action.equals("subscribe")) {
				sampleClient.subscribe(topic, qos);
			}
		} catch(MqttException me) {
			// Display full details of any exception that occurs
			System.out.println("reason "+me.getReasonCode());
			System.out.println("msg "+me.getMessage());
			System.out.println("loc "+me.getLocalizedMessage());
			System.out.println("cause "+me.getCause());
			System.out.println("excep "+me);
			me.printStackTrace();			
		}
	}
}
