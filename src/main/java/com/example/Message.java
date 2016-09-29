/**
* Message class
**/
package com.example;

import java.util.List;

public class Message {
	private long sender;
	private List<Long> recipients;
	private String messageBody;

	public Message(long sender, List<Long> recipients, String msg) {
		this.sender = sender;
		this.recipients = recipients;
		this.messageBody = msg;
	}

	public long getSender() {
		return this.sender;
	}

	public List<Long> getRecipients() {
		return this.recipients;
	}

	public String getMessageBody() {
		return this.messageBody;
	}

}