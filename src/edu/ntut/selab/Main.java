package edu.ntut.selab;

import edu.ntut.selab.data.StateResult;

public class Main {
	public static void main(String[] args) {
		StateResult result = null;
		Controller controller = new Controller();
		result = controller.startToRun();
		new ReportGenerator(controller.xmlReaderTimestamp(), result);
	}	
}
