package edu.ntut.selab.event;

import java.io.IOException;

import edu.ntut.selab.CommandHelper;
import edu.ntut.selab.XMLReader;
import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.Point;

public class SwipeEvent implements AndroidEvent {
	protected String[] command = null;
	protected String label = null;
	protected Point startPoint = null, endPoint = null;
	
	public SwipeEvent(Point startPoint, Point endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		command = new String[]{"\"" + 
				XMLReader.getConfigurationValue(ConfigurationType.ADB) +
				"\"", "shell", "input", "swipe", Integer.toString(startPoint.x()),
				Integer.toString(startPoint.y()), Integer.toString(endPoint.x()),
				Integer.toString(endPoint.y())};
		createLabel(startPoint, endPoint);
	}
	
	public void createLabel(Point startPoint, Point endPoint) {
		if(startPoint.x() == endPoint.x()) {
			if(endPoint.y() > startPoint.y()) {
				label = "swipe(\\\"Down\\\")"; 
			}
			else if(endPoint.y()<startPoint.y()) {
				label = "swipe(\\\"Up\\\")";
			}
			else {
				label = "swipe(\\\"as click\\\")";
			}
		}
		else if(startPoint.y() == endPoint.y()) {
			if(endPoint.x()>startPoint.x()) {
				label = "swipe(\\\"Right\\\")";
			}
			else if(endPoint.x()<startPoint.x()) {
				label = "swipe(\\\"Left\\\")";
			}
		}
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
		return new SwipeEvent(startPoint.clone(), endPoint.clone());
	}

}
