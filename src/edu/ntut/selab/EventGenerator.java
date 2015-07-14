package edu.ntut.selab;

import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.event.AndroidEvent;

//Not completed
public class EventGenerator {
	protected long waitingTime = 0;
	
	public EventGenerator() {
		try {
			waitingTime = TimeHelper.getWaitingTime(ConfigurationType.EventWaitingTime);
		} 
		catch(NumberFormatException e) {
			e.printStackTrace();
		}
	}
	public long getWaitingTime() {
		return waitingTime;
	}
	
	public void run(AndroidEvent event) {
		try {
			event.execute();
			TimeHelper.sleep(waitingTime);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
