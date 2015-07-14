package edu.ntut.selab.event;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import edu.ntut.selab.CommandHelper;
import edu.ntut.selab.XMLReader;
import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.Point;

public class LongClickEvent implements AndroidEvent {
	protected String label = null,
			tempLabel = null;
	protected Point point = null;
	protected String monkeyRunner = null;
	
	public LongClickEvent(Point point, String tempLabel) {
		this.point = point;
		this.tempLabel = tempLabel;
		monkeyRunner = XMLReader.getConfigurationValue(ConfigurationType.MonkeyRunner);
		label = "longClick(\\\"" + tempLabel + "\\\")";
	}
	
	@Override
	public void execute() {
		File script = getLongPressScript();
		String[] command = new String[]{"\"" + monkeyRunner + "\"", "\"" + script.getAbsolutePath() + "\""};
		try {
			CommandHelper.executeCommand(command);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		script.delete();
	}

	@Override
	public String getReportLabel() {
		return label;
	}

	@Override
	public AndroidEvent clone() {
		return new LongClickEvent(point.clone(), tempLabel);
	}
	
	protected File getLongPressScript() {
		File scriptFile = new File("gui_pages/monkeyrunner.script");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(scriptFile, "UTF-8");
		} 
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		writer.println("from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice");
		writer.println("device = MonkeyRunner.waitForConnection()");
		writer.println("device.drag((" + point.x() + "," + point.y()+
				"),(" + point.x() + "," + point.y() + "),3,1)");
		writer.close();
		return scriptFile;
	}

}
