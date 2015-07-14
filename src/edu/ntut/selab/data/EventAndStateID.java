package edu.ntut.selab.data;

import edu.ntut.selab.event.AndroidEvent;

public class EventAndStateID {
	protected int stateID = -1;
	protected AndroidEvent event = null;
	
	public EventAndStateID(int stateID, AndroidEvent event) {
		this.stateID = stateID;
		this.event = event;
	}
	
	public int stateID() {
		return stateID;
	}
	
	public AndroidEvent event() {
		return event;
	}
	
	public EventAndStateID clone() {
		AndroidEvent cloneEvent = null;
		if(event != null) {
			cloneEvent = event.clone();
		}
		EventAndStateID cloneObject = new EventAndStateID(stateID, cloneEvent);
		return cloneObject;
	}
}
