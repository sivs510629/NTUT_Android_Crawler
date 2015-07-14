package edu.ntut.selab.event;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import edu.ntut.selab.XMLReader;
import edu.ntut.selab.data.ConfigurationType;

public class MenuKeyEventTest {

	@Test
	public void testMenuKeyEvent() {
		String[] command = {"\"" + 
				XMLReader.getConfigurationValue(ConfigurationType.ADB) +
				"\"", "shell", "input", "keyevent", "KEYCODE_MENU"};
		MenuKeyEvent e = new MenuKeyEvent();
		assertTrue(Arrays.equals(command, e.command));
	}

	@Test
	public void testGetReportLabel() {
		assertEquals("Press Menu Key", new MenuKeyEvent().getReportLabel());
	}

}
