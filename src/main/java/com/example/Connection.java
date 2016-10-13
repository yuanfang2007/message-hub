package com.example;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

//read command string from socket, parse string to Message, save into MessageQueue[CommandProcessor],
//Ingest MessageQueue and put strings into socket[HubMessageProcessor]
public class Connection {
	private long clientId;
	private Socket connectionSocket;
	private BlockingQueue<String> queue;
	private Hub hub;
	private CommandProcessor commandProcessor;

	public Connection(long id, Socket s, BlockingQueue<String> mQ, Hub hub) {
		this.clientId = id;
		this.connectionSocket = s;
		this.queue = mQ;
		this.hub = hub;
	}

	public void startCommandProcessor() {
		commandProcessor = new CommandProcessor(this.connectionSocket, this.clientId, this.hub);
		commandProcessor.start();
	}

	public void disconnectConnection() {
		if (commandProcessor != null) {
			commandProcessor.interrupt();
		}
	}

	public long getClientId() {
		return clientId;
	}

	public void setClientId(long clientId) {
		this.clientId = clientId;
	}

	public Socket getConnectionSocket() {
		return connectionSocket;
	}

	public void setConnectionSocket(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	public BlockingQueue<String> getQueue() {
		return queue;
	}

	public void setQueue(BlockingQueue<String> queue) {
		this.queue = queue;
	}

	public Hub getHub() {
		return hub;
	}

	public void setHub(Hub hub) {
		this.hub = hub;
	}

	public CommandProcessor getCommandProcessor() {
		return commandProcessor;
	}

	public void setCommandProcessor(CommandProcessor commandProcessor) {
		this.commandProcessor = commandProcessor;
	}
}