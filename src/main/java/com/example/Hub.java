package com.example;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Hub{
	public static final String HOST = "127.0.0.1";
	public static final int PORT = 8484;

	public static final String COMMAND_WHOAMI = "Who Am I?";
	public static final String COMMAND_WHOISHERE = "Who is here?";
	public static final String COMMAND_RELAY = "relay";

	private ServerSocket serverSocket;
	private static long allTimeTotalUsers = 0;
	private List<Long> activeUsers = new ArrayList<>();

	private BlockingQueue<Message> hubIncomingMessageQueue = new LinkedBlockingQueue<>(100);;
	private HashMap<Long, Connection> clientConnectionToClientId = new HashMap<>();;
	private HubMessageProcessor hubMessageProcessor;
	private ExecutorService commandProcessorExecutorService = Executors.newFixedThreadPool(10);


	public static long getNewUserID() {
		long currentID;
		synchronized(Hub.class) {
			allTimeTotalUsers += 1;
			currentID = allTimeTotalUsers;
		}
		return currentID;
	}

	public Hub(){
		serverSocket = getServerSocket(Hub.PORT);
		if (serverSocket == null) {
			System.out.println("Error creating server socket. Exiting.");
			System.exit(0);
		}
		System.out.println("Listening on port " + Hub.PORT + ". Waiting for clients ... ");
	}



	private void startup(){
		Socket incomingNewSocket;
		long clientID;

		try {
			while (true) {
				incomingNewSocket = serverSocket.accept();
				clientID = getNewUserID();
				System.out.println("Got a connection! Assigned Client ID: " + clientID);
				Connection conn = registerNewUser(clientID, incomingNewSocket);
				conn.startCommandProcessor(commandProcessorExecutorService);
			}
		} catch(Exception e) {
			System.out.println("Exception : " + e);
		}
	}

	public Connection registerNewUser(long clientID, Socket socket) {
		activeUsers.add(clientID);
		BlockingQueue<String> newMessageQueue = new LinkedBlockingQueue<>(10);
		Connection connInstance = new Connection(clientID, socket, newMessageQueue, this);
		clientConnectionToClientId.put(clientID, connInstance);
		return connInstance;
	}

	public void disconnectUser(long clientID) {
		if (clientConnectionToClientId.containsKey(clientID)) {
			clientConnectionToClientId.get(clientID).disconnectConnection();
			clientConnectionToClientId.remove(clientID);
		}
		activeUsers.remove(clientID);
		return;
	}

	public List<Long> getActiveUsers() {
		return activeUsers;
	}

	public boolean processMessage(Message msgObject) {
		try {
			this.hubIncomingMessageQueue.put(msgObject);
			return true;
		} catch (InterruptedException ie) {
			System.out.println("Caught InterruptedException while placing message pool request");
		}
		return false;
	}

	public ServerSocket getServerSocket(int portNumber) {
		ServerSocket sSocket = null;
		try {
			sSocket = new ServerSocket(portNumber);
		} catch(IOException ioe) {
			System.out.println("Caught IOException while creating ServerSocket");
			System.exit(0);
		}
		return sSocket;
	}

	public void close() throws IOException{
		serverSocket.close();
	}

	private void startMessageProcessor() {
		hubMessageProcessor = new HubMessageProcessor(hubIncomingMessageQueue, clientConnectionToClientId);
		hubMessageProcessor.start();
	}

	public static void main(String args[]) {
		Hub hub = new Hub();
		hub.startMessageProcessor();
		hub.startup();
	}

}