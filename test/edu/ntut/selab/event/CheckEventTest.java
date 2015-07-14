package edu.ntut.selab.event;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import edu.ntut.selab.XMLReader;
import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.Point;

public class CheckEventTest {

	@Test
	public void testCheckEvent() {
		Point p = new Point(2, 5);
		CheckEvent e = new CheckEvent(p, "");
		String[] command = {"\"" + 
				XMLReader.getConfigurationValue(ConfigurationType.ADB) + "\"",
				"shell", "input", "tap", "2", "5"};
		assertTrue(Arrays.equals(command, e.command));
		assertEquals("check 2,5", e.label);
	}

	@Test
	public void testGetReportLabel() {
		Point p = new Point(2, 5);
		String tempLabel = "tempLabel";
		CheckEvent e = new CheckEvent(p, tempLabel);
		assertEquals("check \\\"" + tempLabel + "\\\"", e.getReportLabel());
	}

}
