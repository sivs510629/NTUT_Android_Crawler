package edu.ntut.selab.data;

import java.util.ArrayList;

import org.dom4j.Document;

import edu.ntut.selab.event.AndroidEvent;

/***
 * 
 * @author Roger
 * Data Structure of Android State
 * 
 */

public class TempGUIState {
	protected Document content = null;
	protected EventAndStateID eventAndBeforStateID = null;
	protected ArrayList<AndroidEvent> eventList = null;
	protected int anotherEventThreshold = -1;
	
	@SuppressWarnings("unchecked")
	public TempGUIState(Document stateContent, ArrayList<AndroidEvent> eventList) {
		this.content = (Document)stateContent.clone();
		this.eventList = (ArrayList<AndroidEvent>)eventList.clone();
	}
	
	public Document contentClone() {
		return (Document)content.clone();
	}
	
	public void setEventAndBeforeStateID(EventAndStateID eventAndBeforeStateID) {
		this.eventAndBeforStateID = eventAndBeforeStateID;
	}
	
	public EventAndStateID getEventAndBeforeStateID() {
		return eventAndBeforStateID;
	}
	
	public ArrayList<AndroidEvent> getEventList() {
		return eventList;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<AndroidEvent> getEventListClone() {
		return (ArrayList<AndroidEvent>)eventList.clone();
	}
	
	public TempGUIState clone() {
		TempGUIState clone = new TempGUIState(content, eventList);
		clone.setEventAndBeforeStateID(eventAndBeforStateID);
		return null;		
	}
	
}
