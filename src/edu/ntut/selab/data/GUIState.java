package edu.ntut.selab.data;

import java.util.ArrayList;
import org.dom4j.Document;

public class GUIState {
	protected int id = -1;
	protected Document content = null;
	protected int parentID = -1;
	protected ArrayList<EventAndStateID> eventAndNextStateIDList = new ArrayList<EventAndStateID>();
	
	public GUIState(int stateID, Document stateContent, int parentID) {
		id = stateID;
		content = stateContent;
		this.parentID = parentID;
	}
	
	public Document contentClone() {
		return (Document)content.clone();
	}
	
	public int getID() {
		return id;
	}

	public void addEventAndNextState(EventAndStateID eventAndNextState) {
		eventAndNextStateIDList.add(eventAndNextState);
	}
	
	public ArrayList<EventAndStateID> getEventAndNextStateList() {
		return eventAndNextStateIDList;
	}
	
	public int getParentID() {
		return parentID;
	}
	
	public GUIState clone() {
		GUIState cloneObject = new GUIState(id, (Document)content.clone(), parentID);
		for(int i = 0 ; i<eventAndNextStateIDList.size() ; i++) {
			cloneObject.addEventAndNextState(eventAndNextStateIDList.get(i).clone());
		}
		return cloneObject;
	}
}
