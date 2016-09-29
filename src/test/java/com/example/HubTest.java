package com.example;

import java.net.Socket;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HubTest {

	private Socket socket;
	private Hub hub;

	@Before
	public void setUp() throws Exception {
		hub = new Hub();
	}

	@After
	public void cleanUp() throws Exception {
		hub.close();
	}

	@Test
	public void testRegisterNewUser() {
		List<Long> activeUsers = hub.getActiveUsers();
		Assert.assertEquals(activeUsers.size(),0);

		hub.registerNewUser(Hub.getNewUserID(), socket);
		activeUsers = hub.getActiveUsers();
		Assert.assertEquals(activeUsers.size(),1);

		hub.registerNewUser(Hub.getNewUserID(), socket);
		activeUsers = hub.getActiveUsers();
		Assert.assertEquals(activeUsers.size(),2);

	}

	@Test
	public void testDisconnectUser() {
		List<Long> activeUsers = hub.getActiveUsers();
		Assert.assertEquals(activeUsers.size(),0);

		long user1 = Hub.getNewUserID();
		hub.registerNewUser(user1, socket);
		activeUsers = hub.getActiveUsers();
		Assert.assertEquals(activeUsers.size(),1);

		hub.disconnectUser(user1);
		Assert.assertEquals(activeUsers.size(),0);

		user1 = Hub.getNewUserID();
		hub.registerNewUser(user1, socket);

		user1 = Hub.getNewUserID();
		hub.registerNewUser(user1, socket);

		hub.disconnectUser(user1);
		Assert.assertEquals(activeUsers.size(),1);

	}

}