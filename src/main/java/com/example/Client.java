package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

public class Client implements Callable<Void>{
	public static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Socket connectionSocket;
	private DataOutputStream outstreamToServer;

	public Client() throws UnknownHostException, IOException{
		System.out.println("Connecting to " + Hub.HOST);
		connectionSocket= new Socket(Hub.HOST, Hub.PORT);
		System.out.println("Connected to " + Hub.HOST + " . Now write a msg like below:");
		System.out.println(Hub.COMMAND_WHOAMI);
		System.out.println(Hub.COMMAND_WHOISHERE);
		System.out.println("relay 2,3 body:hello friends");

		outstreamToServer = new DataOutputStream(connectionSocket.getOutputStream());
		System.out.print("[" + DATE_FORMAT.format(new Date()) + "]:");
	}

	@Override
	public Void call() throws Exception {
		String keyInput = null;
		BufferedReader brIn = new BufferedReader(new InputStreamReader(System.in));
		ClientMessageProcessor clientMessageProcessor = new ClientMessageProcessor(connectionSocket);
		clientMessageProcessor.start();
		while ((keyInput = brIn.readLine()) != null ) {
			System.out.print("[" + DATE_FORMAT.format(new Date()) + "]:");
			outstreamToServer.writeBytes(keyInput + '\n');
			outstreamToServer.flush();

			if (keyInput.equalsIgnoreCase("Bye")) {
				clientMessageProcessor.interrupt();
				connectionSocket.close();
				break;
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		Client client = new Client();
		client.call();
	}

}