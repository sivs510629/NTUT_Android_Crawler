package edu.ntut.selab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/***
 * 
 * @author Roger
 * Order a command to a Android device
 */
public class CommandHelper {
	public static void executeCommand(String[] command) throws IOException {
		System.out.println("Command: " + getCommandValue(command));
		System.out.println("Execute Output: " +
				executeAndGetFeedBack(command));
	}
	
	public static String executeAndGetFeedBack(String[] command) throws IOException {
		String feedBack = null;
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();			
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));
		feedBack = bufferedReader.readLine();
		process.destroy();
		return feedBack;
	}
	
	protected static String getCommandValue(String[] command) {
		final String space = " ";
		String commandValue = "";
		for(int i = 0 ; i<command.length ; i++) {
			commandValue += command[i];
			if(i<command.length-1) {
				commandValue += space;
			}
		}
		return commandValue;
	}
	
}
