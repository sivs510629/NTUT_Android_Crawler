package edu.ntut.selab;

public class TimeHelper {
	public static void sleep(long ms) throws InterruptedException {
		Thread.sleep(ms);
	}
	
	public static long getWaitingTime(String configurationType) throws NumberFormatException {
		String time = 
				XMLReader.getConfigurationValue(configurationType); 
		return Long.parseLong(time)*1000;
	}
}
