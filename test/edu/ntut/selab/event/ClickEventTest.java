package edu.ntut.selab.event;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import edu.ntut.selab.XMLReader;
import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.Point;

public class ClickEventTest {

	@Test
	public void testClickEvent() {
		Point p = new Point(2, 5);
		ClickEvent e = new ClickEvent(p, "");
		String[] command = {"\"" + 
				XMLReader.getConfigurationValue(ConfigurationType.ADB) + "\"",
				"shell", "input", "tap", "2", "5"};
		assertTrue(Arrays.equals(command, e.command));
		assertEquals("click 2,5", e.label);
		assertEquals(p.x(), e.point.x());
		assertEquals(p.y(), e.point.y());
	}
	
	@Test
	public void testGetReportLabel() {
		Point p = new Point(2, 5);
		ClickEvent e = new ClickEvent(p, "");
		assertEquals("click 2,5", e.getReportLabel());
	}
}
