package com.example;

import static org.junit.Assert.assertEquals;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CommandProcessorTest {

	private CommandProcessor commandProcessor;
	private long mockClientID = 25;
	private Hub hub;

	@Before
	public void setUp() throws Exception {
		Socket socket = Mockito.mock(Socket.class);
		hub = Mockito.mock(Hub.class);
		commandProcessor = new CommandProcessor(socket, mockClientID, hub);
	}

	@Test
	public void testWhoAmICommand() {
		long whoAmIResponse = commandProcessor.getClientID();
		assertEquals(whoAmIResponse,mockClientID);

		commandProcessor = new CommandProcessor(Mockito.mock(Socket.class), (22), hub);
		assertEquals(commandProcessor.getClientID(),22);
	}

	@Test
	public void testGetCSVString() {
		List<Long> userIDList = new ArrayList<>();
		userIDList.add(Long.valueOf(22));
		userIDList.add(Long.valueOf(20));
		userIDList.add(Long.valueOf(21));

		String csvString = commandProcessor.getCSVString(userIDList);
		assertEquals(csvString, "22,20,21");
	}

	@Test
	public void testParseMessage() {
		// well-formed relay message of the format:
		// relay <list of clients> body: <message body>
		Message msg = commandProcessor.parseMessage("relay 2,3,4 body: Hi There!");
		List<Long> recipientList = new ArrayList<>();
		recipientList.add(Long.valueOf(2));
		recipientList.add(Long.valueOf(3));
		recipientList.add(Long.valueOf(4));

		assertEquals(msg.getSender(), 25);
		assertEquals(msg.getRecipients(), recipientList);
		assertEquals(msg.getMessageBody(), " Hi There!");


		// Only valid IDs are extracted from the recipients CSV list.
		msg = commandProcessor.parseMessage("relay 2,4 body: Hi There!");
		recipientList = new ArrayList<>();
		recipientList.add(Long.valueOf(2));
		recipientList.add(Long.valueOf(4));
		assertEquals(msg.getSender(), 25);
		assertEquals(msg.getRecipients(), recipientList);
		assertEquals(msg.getMessageBody(), " Hi There!");
	}

	public static void main(String args[]) {
      org.junit.runner.JUnitCore.main("com.example.CommandReceiverTest");
    }

}