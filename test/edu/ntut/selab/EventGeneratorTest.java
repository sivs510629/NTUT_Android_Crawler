package edu.ntut.selab;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.Point;
import edu.ntut.selab.event.AndroidEvent;
import edu.ntut.selab.event.BackKeyEvent;
import edu.ntut.selab.event.LongClickEvent;

public class EventGeneratorTest {

	@Test
	public void testRun() {
		AndroidEvent longClickEvent = new LongClickEvent(new Point(360,360), ""),
				backKeyEvent = new BackKeyEvent();
		EventGenerator eg = new EventGenerator();
		String adb = XMLReader.getConfigurationValue(ConfigurationType.ADB);
		adb = "\"" + adb + "\"";
		try {
			CommandHelper.executeCommand(new String[]{adb, "kill-server"});
			CommandHelper.executeCommand(new String[]{adb, "devices"});
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		eg.run(longClickEvent);		
		eg.run(backKeyEvent);
		fail("Not yet implemented");
	}
}
