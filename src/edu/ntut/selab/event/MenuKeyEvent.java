package edu.ntut.selab.event;

import java.io.IOException;

import edu.ntut.selab.CommandHelper;
import edu.ntut.selab.XMLReader;
import edu.ntut.selab.data.ConfigurationType;

public class MenuKeyEvent implements AndroidEvent {
	protected String[] command = null;
	
	public MenuKeyEvent() {
		command = new String[]{"\"" + 
				XMLReader.getConfigurationValue(ConfigurationType.ADB) +
				"\"", "shell", "input", "keyevent", "KEYCODE_MENU"};
	}
	
	@Override
	public void execute() {
		try {
			CommandHelper.executeCommand(command);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getReportLabel() {
		return "press(\\\"MenuKey\\\")";
	}

	@Override
	public AndroidEvent clone() {
		return new MenuKeyEvent();
	}

}