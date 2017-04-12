package com.sunflower.mqtt;

import java.io.IOException;
import java.sql.Timestamp;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class Sample implements MqttCallback {

	@Override
	public void connectionLost(Throwable cause) {
		log("Connection to " + brokerUrl + " lost!" + cause);
		System.exit(1);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws MqttException {
		String time = new Timestamp(System.currentTimeMillis()).toString();
		System.out.println("Time:\t" + time + 
						   " Topic:\t" + topic + 
						   " Message:\t" + new String(message.getPayload()) +
						   " QoS:\t" + message.getQos());
	}
	
	/**
	 * Publish / send a message to an MQTT server
	 * @param topicName the name of the topic to publish to
	 * @param qos the quality of service to delivery the message at (0, 1, 2)
	 * @param payload the set of bytes to send to the MQTT server
	 * @throws MqttException
	 */
	public void publish(String topicName, int qos, byte[] payload) throws MqttException {
		// Connect to the MQTT server
		log("Connecting to " + brokerUrl + " with client ID " + client.getClientId());
		client.connect(conOpt);
		log("Connected");
		
		String time = new Timestamp(System.currentTimeMillis()).toString();
		log("Publishing at: " + time + " to topic \"" + topicName + "\" qos " + qos);
		
		// Create and configure a message
		MqttMessage message = new MqttMessage(payload);
		message.setQos(qos);
		
		client.publish(topicName, message);
		client.disconnect();
		log("Disconnected");
	}
	
	/**
	 * Subscribe to a topic on an MQTT server
	 * Once subscribed this method waits for the messages to arrive from the server
	 * that match the subscription. It continues listening for messages until the enter key is pressed.
	 * @param topicName to subscribe to (can be wild carded)
	 * @param qos 
	 * @throws MqttException
	 */
	public void subscribe(String topicName, int qos) throws MqttException {
		client.connect(conOpt);
		log("Connected to " + brokerUrl + " with client ID " + client.getClientId());
		
		log("Subscribing to topic \"" + topicName + "\" qos " + qos);
		client.subscribe(topicName, qos);
		
		log("Press <Enter> to exit");
		try {
			System.in.read();
		} catch (IOException e) {
			
		}
		client.disconnect();
		log("Disconnected");
	}
	
	public static void main(String[] args) {
		// Default settings
		boolean quietMode = false;
		String action = "subscribe";
		String topic = "";
		String message = "Message from blocking Paho MQTTv3 Java Client sample";
		int qos = 1;
		String broker = "192.168.18.3";
		int port = 1883;
		String clientId = null;
		String subTopic = "MQTT Examples";
		String pubTopic = "Sample/Java/v3";
		boolean cleanSession = true;
		boolean ssl = false;
		String password = "zhidou.link";
		String userName = "admin";
		// Parse the arguments
		for (int i = 0; i < args.length; i++) {
			if (args[i].length() == 2 && args[i].startsWith("-")) {
				char arg = args[i].charAt(1);
				switch(arg) {
				case 'h': case '?': printHelp(); return;
				case 'q': quietMode = true; continue;
				}
				
				if (i == args.length -1||args[i+1].charAt(0) == '-') {
					System.out.println("Missing value for argument: " + args[i]);
					printHelp();
					return;
				}
				switch(arg) {
				case 'a': action = args[++i];	break;
				case 't': topic = args[++i];	break;
				case 'm': message = args[++i];	break;
				case 's': qos = Integer.parseInt(args[++i]);	break;
				case 'b': broker = args[++i];	break;
				case 'p': port = Integer.parseInt(args[++i]);	break;
				case 'i': clientId = args[++i];	break;
				case 'c': cleanSession = Boolean.valueOf(args[++i]).booleanValue();	break;
				case 'k': System.getProperties().put("javax.net.ssl.keyStore", args[++i]);	break;
				case 'w': System.getProperties().put("javax.net.ssl.keyStorePassword", args[++i]);	break;
				case 'r': System.getProperties().put("javax.net.ssl.trustStore", args[++i]);	break;
				case 'v': ssl = Boolean.valueOf(args[++i]).booleanValue();	break;
				case 'u': userName = args[++i];	break;
				case 'z': password = args[++i]; break;
				default:
					System.out.println("Unrecognised argument: " + args[i]);
					printHelp();
					return;
				}
			} else {
				System.out.println("Unrecognised argument: " + args[i]);
				printHelp();
				return;
			}
		}
		
		// Validate the provided arguments
		if (!action.equals("publish") && !action.equals("subscribe")) {
			System.out.println("Invalid action: " + action);
			printHelp();
			return;
		}
		
		if (qos < 0 || qos > 2) {
			System.out.println("Invalid QoS: " + qos);
			printHelp();
			return;
		}
		
		if (topic.equals("")) {
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
			clientId = "SampleJavaV3_" + action + "_" + Math.random() * 10;
		}
		
		try {
			Sample sampleClient = new Sample(url, clientId, cleanSession, quietMode, userName, password);
			
			// Perform the requested action
			if (action.equals("publish")) {
				sampleClient.publish(topic, qos, message.getBytes());
			} else if (action.equals("subscribe")) {
				sampleClient.subscribe(topic, qos);
			}
			
		} catch(MqttException me) {
			System.out.println("reason " + me.getReasonCode());
			System.out.println("msg " + me.getMessage());
			System.out.println("loc " + me.getLocalizedMessage());
			System.out.println("cause " + me.getCause());
			System.out.println("excep " + me);
			me.printStackTrace();
		}
	}
	
	// Private instance variables
	private MqttClient client;
	private String brokerUrl;
	private boolean quietMode;
	private MqttConnectOptions conOpt;
	private boolean clean;
	private String password;
	private String userName;
	
	public Sample(String brokerUrl, String clientId, boolean cleanSession, boolean quietMode, String userName, String password) throws MqttException {
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
			if(password != null) {
				conOpt.setPassword(this.password.toCharArray());
			}
			if(userName != null) {
				conOpt.setUserName(this.userName);
			}
			
			client = new MqttClient(this.brokerUrl, clientId, dataStore);
			client.setCallback(this);
		} catch (MqttException e) {
			e.printStackTrace();
			log("Unable to set up client: " + e.toString());
			System.exit(1);
		}
	}
	
	private void log(String message) {
		if (!quietMode) {
			System.out.println(message);
		}
	}
	
	// End of MqttCallback methods
	static void printHelp() {
		System.out.println(
			"Syntax:\n\n" + 
	              "    Sample [-h] [-a publish|subscribe] [-t <topic>] [-m <message text>]\n" +
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
}
