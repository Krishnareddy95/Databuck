package com.databuck.bean;

public class AlertEventMaster {
	private int eventId;
	private String eventName;
	private String eventModuleName;
	private String eventCommunicationType;
	private int eventMessageCode;
	private String eventCompletionMessage;
	private String eventCompletionStatus;
	private String eventMessageBody;
	private String eventFocusObject;
	private String eventMessageSubject;

	public String getEventMessageSubject() {
		return eventMessageSubject;
	}

	public void setEventMessageSubject(String eventMessageSubject) {
		this.eventMessageSubject = eventMessageSubject;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventModuleName() {
		return eventModuleName;
	}

	public void setEventModuleName(String eventModuleName) {
		this.eventModuleName = eventModuleName;
	}

	public String getEventCommunicationType() {
		return eventCommunicationType;
	}

	public void setEventCommunicationType(String eventCommunicationType) {
		this.eventCommunicationType = eventCommunicationType;
	}

	public int getEventMessageCode() {
		return eventMessageCode;
	}

	public void setEventMessageCode(int eventMessageCode) {
		this.eventMessageCode = eventMessageCode;
	}

	public String getEventCompletionMessage() {
		return eventCompletionMessage;
	}

	public void setEventCompletionMessage(String eventCompletionMessage) {
		this.eventCompletionMessage = eventCompletionMessage;
	}

	public String getEventCompletionStatus() {
		return eventCompletionStatus;
	}

	public void setEventCompletionStatus(String eventCompletionStatus) {
		this.eventCompletionStatus = eventCompletionStatus;
	}

	public String getEventMessageBody() {
		return eventMessageBody;
	}

	public void setEventMessageBody(String eventMessageBody) {
		this.eventMessageBody = eventMessageBody;
	}

	public String getEventFocusObject() {
		return eventFocusObject;
	}

	public void setEventFocusObject(String eventFocusObject) {
		this.eventFocusObject = eventFocusObject;
	}
}
