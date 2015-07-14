package edu.ntut.selab.event;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import edu.ntut.selab.XMLReader;
import edu.ntut.selab.data.ConfigurationType;

public class BackKeyEventTest {

	@Test
	public void testBackKeyEvent() {
		String[] command = {"\"" + 
				XMLReader.getConfigurationValue(ConfigurationType.ADB) +
				"\"", "shell", "input", "keyevent", "KEYCODE_BACK"};
		BackKeyEvent e = new BackKeyEvent();
		assertTrue(Arrays.equals(command, e.command));
	}

	@Test
	public void testGetReportLabel() {
		assertEquals("Press Back Key", new BackKeyEvent().getReportLabel());
	}

}
