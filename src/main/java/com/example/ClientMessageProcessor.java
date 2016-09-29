package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;

//pull message from socket and print it out
public class ClientMessageProcessor extends Thread {

	protected Socket connSocket;
	private String incomingMsg = null;
	private	BufferedReader reader = null;

	public ClientMessageProcessor(Socket s) {
		this.connSocket = s;
	}

	@Override
	public void run() {
		try {
			reader = new BufferedReader(new InputStreamReader(this.connSocket.getInputStream()));
			while(!Thread.currentThread().isInterrupted() && (incomingMsg = reader.readLine())!= null) {
				System.out.println(incomingMsg);
				System.out.print("[" + Client.DATE_FORMAT.format(new Date()) + "]:");
				if (Thread.currentThread().isInterrupted()) {
					break;
				}
			}
		} catch (Exception e) {
			System.out.println("Socket closed. Bye!");
			return;
		}
	}

}
