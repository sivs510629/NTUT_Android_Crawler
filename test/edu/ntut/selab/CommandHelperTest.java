package edu.ntut.selab;

import static org.junit.Assert.*;

import org.junit.Test;

public class CommandHelperTest {
	@Test
	public void testGetCommandValue() {
		String[] command = {"a","bb","ccc"};
		String value = CommandHelper.getCommandValue(command);
		assertEquals("a bb ccc", value);
	}
	
}
