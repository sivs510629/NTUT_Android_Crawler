package edu.ntut.selab.event;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import edu.ntut.selab.XMLReader;
import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.Point;

public class ScrollEventTest {

	@Test
	public void testScrollEvent() {
		Point startPoint = new Point(2, 5),
				endPoint = new Point(6, 9);
		SwipeEvent e = new SwipeEvent(startPoint, endPoint);
		String[] command = {"\"" + 
				XMLReader.getConfigurationValue(ConfigurationType.ADB) + "\"",
				"shell", "input", "swipe", "2", "5", "6", "9"};
		assertTrue(Arrays.equals(command, e.command));
		assertEquals("swipe from 2,5 to 6,9", e.label);
		assertEquals(startPoint.x(), e.startPoint.x());
		assertEquals(startPoint.y(), e.startPoint.y());		
		assertEquals(endPoint.x(), e.endPoint.x());
		assertEquals(endPoint.y(), e.endPoint.y());
	}
	
	@Test
	public void testGetReportLabel() {
		assertEquals("swipe from 2,5 to 6,9", new SwipeEvent(new Point(2, 5), new Point(6, 9)).getReportLabel());
	}

}
