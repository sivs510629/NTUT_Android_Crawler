package edu.ntut.selab.event;

import java.io.IOException;

import edu.ntut.selab.CommandHelper;
import edu.ntut.selab.XMLReader;
import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.Point;

public class ClickEvent implements AndroidEvent {
	protected String[] command = null;
	protected String label = null, 
			tempLabel = null;
	protected Point point = null;
	
	public ClickEvent(Point point, String tempLabel) {
		this.point = point;
		this.tempLabel = tempLabel;
		command = new String[]{"\"" + 
				XMLReader.getConfigurationValue(ConfigurationType.ADB) + "\"",
				"shell", "input", "tap", Integer.toString(point.x()),
				Integer.toString(point.y())};
		label = "click(\\\"" + tempLabel + "\\\")";
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
		return label;
	}

	@Override
	public AndroidEvent clone() {
		return new ClickEvent(point.clone(), tempLabel);
	}

}
