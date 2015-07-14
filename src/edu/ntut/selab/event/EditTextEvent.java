package edu.ntut.selab.event;

import java.io.IOException;

import edu.ntut.selab.CommandHelper;
import edu.ntut.selab.XMLReader;
import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.Point;

public class EditTextEvent implements AndroidEvent {

	protected String[] textCmd = null,
			clickCmd = null,
			enterCmd = null,
			backspaceCmd = null;
	protected String label = null,
			tempLabel = null,
			value = null,
			adb = null;
	protected Point point = null;
	protected int backspaceCount = 0;
	
	public EditTextEvent(Point point, int backspaceCount, String value, String tempLabel) {
		this.point = point;
		this.value = value;
		this.tempLabel = tempLabel;
		this.backspaceCount = backspaceCount;
		adb = "\"" +
				XMLReader.getConfigurationValue(ConfigurationType.ADB) +
				"\"";
		clickCmd = new String[]{adb, "shell", "input", "tap",
				Integer.toString(point.x()), Integer.toString(point.y())};
		enterCmd = new String[]{adb, "shell", "input", "keyevent", "KEYCODE_ENTER"};
		backspaceCmd = new String[]{adb, "shell", "input", "keyevent", "KEYCODE_DEL"};
		label = "\\\"" + tempLabel + "\\\".input(\\\"" + value + "\\\")";
	}
	
	protected void clearText() throws IOException {
		CommandHelper.executeCommand(clickCmd);
		int count = backspaceCount;
		while(count>0) {
			CommandHelper.executeCommand(backspaceCmd);
			count--;
		}
	}
	
	public void enterValue() throws IOException {
		String tempValue = null;
		for(int i = 0 ; i<value.length() ; i++) {
			tempValue = value.substring(i, i+1);
			if(tempValue.compareTo(" ") == 0) {
				tempValue = "%s";
			}
			CommandHelper.executeCommand(
					new String[]{adb, "shell", "input",	"text", "\"" +
							tempValue + "\""});
		}
	}
	
	protected void turnOffSoftKeyboard() {
		String[] command = {adb, "shell", "dumpsys",
				"input_method", "|", "grep", "\"mInputShown=true\""};
		String feedBack = null;
		try {
			feedBack = CommandHelper.executeAndGetFeedBack(command);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		if(feedBack != null) {
			(new BackKeyEvent()).execute();
		}
	}

	@Override
	public void execute() {
		try {
			clearText();
			enterValue();
			CommandHelper.executeCommand(enterCmd);
			CommandHelper.executeCommand(backspaceCmd);
			turnOffSoftKeyboard();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getReportLabel() {
		return label;
	}

	@Override
	public AndroidEvent clone() {
		return new EditTextEvent(point.clone(), backspaceCount, new String(value), tempLabel);
	}

}
