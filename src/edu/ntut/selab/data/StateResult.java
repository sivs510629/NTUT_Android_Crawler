package edu.ntut.selab.data;

import java.util.ArrayList;

/***
 * 
 * Represent the final state result
 */
public class StateResult {
	public ArrayList<GUIState> guiStateList = null;
	//public ArrayList<Integer> stateCountList = null;
	public ArrayList<String> activityNameList = null;
	public StateResult(ArrayList<GUIState> guiStateList, ArrayList<String> activityNameList) {
		this.guiStateList = guiStateList;
		this.activityNameList = activityNameList;
	}
}
