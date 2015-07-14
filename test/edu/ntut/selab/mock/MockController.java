package edu.ntut.selab.mock;

import java.util.ArrayList;

import edu.ntut.selab.Controller;
import edu.ntut.selab.event.AndroidEvent;

public class MockController extends Controller {
	
	//for unit test: testGoToTargetState(targetStateID)
	public AndroidEvent nextEvent = null;
	public ArrayList<Integer> pathOfTargetState = null;
	private MockEventGenerator eventGenerator = new MockEventGenerator();
	public int i = 1;
	public void goToTargetState(int targetStateID) {
		pathOfTargetState = getPathOfTargetID(targetStateID);
		if(i<pathOfTargetState.size()) {
			try {
				nextEvent = getEventToNextState(
						pathOfTargetState.get(i-1), pathOfTargetState.get(i));
				eventGenerator.run(nextEvent);
			}
			catch(NullPointerException e) {
			}	
		}
	}
	
	protected ArrayList<Integer> getPathOfTargetID(int targetStateID) {
		return pathOfTargetState;
	}
	
}
