package com.example;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

//It ingests messages from hubIncomingMessageQueue and put corresponding string into queues for each client
public class HubMessageProcessor extends Thread {
	private BlockingQueue<Message> hubIncomingMessageQueue;
	private HashMap<Long, Connection> clientConnectionToClientId;

	public HubMessageProcessor(BlockingQueue<Message> hubIncomingMessageQueue,
			HashMap<Long, Connection> clientConnectionToClientId) {
		this.hubIncomingMessageQueue = hubIncomingMessageQueue;
		this.clientConnectionToClientId = clientConnectionToClientId;
	}

	public int getRemainingCapacity() {
		return this.hubIncomingMessageQueue.remainingCapacity();
	}

	@Override
	public void run() {

		Message message;
		try {
			while (!Thread.currentThread().isInterrupted()) {
				message = this.hubIncomingMessageQueue.poll(10000, TimeUnit.MILLISECONDS);
				if (message != null) {
					List<Long> recipients = message.getRecipients();
					StringBuilder sb = new StringBuilder();
					String hubOrClient = message.getSender() == 0 ? "Hub" : "Client " + message.getSender();
					sb.append(hubOrClient + " says: ");
					sb.append(message.getMessageBody());
					String msgBody = sb.toString();
					for (Long client : recipients) {
						Socket connSocket = clientConnectionToClientId.get(client).getConnectionSocket();
						try {
							DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());
							outToClient.writeBytes(msgBody + "\n");
						} catch (Exception e) {
							System.out.println("Caught : " + e);
						}
					}
				}
			}
		} catch (InterruptedException ie) {
			System.out.println("Caught InterruptedException");
		}

	}
}