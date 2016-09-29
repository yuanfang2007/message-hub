package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
//receive commands from socket, parse it and give it to Hub
public class CommandProcessor extends Thread {
	protected Socket connSocket;
	protected Long clientID;
	private Hub hub;

	public CommandProcessor(Socket s, long id, Hub hub) {
		this.connSocket = s;
		this.clientID = id;
		this.hub = hub;
	}

	long getClientID() {
		return this.clientID;
	}

	public String getCSVString(List<Long> nums) {
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		for (ListIterator<Long> iter = nums.listIterator(); iter.hasNext();) {
			Long userId = iter.next();
			if (userId == this.clientID) {
				continue;
			}
			sb.append(prefix);
			prefix = ",";
			sb.append(userId);
		}
		return sb.toString();
	}

	/**
	* relay <list of clients> body: <message body>
	**/
	public Message parseMessage(String incomingMsg) {
		if (incomingMsg.equalsIgnoreCase(Hub.COMMAND_WHOAMI)) {
			List<Long> recipientsList = new ArrayList<>();
			recipientsList.add(this.clientID);
			return new Message(0, recipientsList, Long.toString(this.getClientID()));
		}
		else if (incomingMsg.equalsIgnoreCase(Hub.COMMAND_WHOISHERE)) {
			List<Long> activeUsers = hub.getActiveUsers();
			String usersList = this.getCSVString(activeUsers);
			List<Long> recipientsList = new ArrayList<>();
			recipientsList.add(this.clientID);
			return new Message(0, recipientsList, usersList);
		} else {
			List<Long> recipientsList = new ArrayList<>();
			try {
				String[] words = incomingMsg.split("\\s+");
				if (words.length > 0 && words[0].equalsIgnoreCase(Hub.COMMAND_RELAY)) {
					String[] recipientsStr = words[1].split(",");
					for (int i = 0; i < recipientsStr.length; i++) {
						try {
							Long num = Long.valueOf(recipientsStr[i]).longValue();
							recipientsList.add(num);
						} catch (NumberFormatException nfe) {
							System.out.println("NumberFormatException trying to convert:" + recipientsStr[i]);
							throw new Exception("NumberFormatException");
						}
					}

					String msgBody = "";
					int index = incomingMsg.indexOf(":");
					if (index > 0 && index < incomingMsg.length() - 2) {
						msgBody = incomingMsg.substring(index + 1);
					}

					return new Message(this.clientID, recipientsList, msgBody);
				}
			} catch (Exception e) {
				recipientsList.add(this.clientID);
				return new Message(0, recipientsList, "bad command, caught exception");
			}
			recipientsList.add(this.clientID);
			return new Message(0, recipientsList, "bad command");
		}
	}

	@Override
	public void run() {
		String incomingMsg = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
			while ((incomingMsg = reader.readLine()) != null) {
				Message msgObject = this.parseMessage(incomingMsg);
				if (msgObject != null) {
					hub.processMessage(msgObject);
				}else{
					System.out.println("client quit!");
					break;
				}
			}

			System.out.println("Client:" + this.clientID + " has disconnected.");
			hub.disconnectUser(this.clientID);
			return;

		} catch (Exception e) {
			System.out.println("Caught : " + e);
		}
	}
}
