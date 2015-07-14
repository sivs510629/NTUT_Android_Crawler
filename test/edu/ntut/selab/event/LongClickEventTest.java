package edu.ntut.selab.event;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.junit.Test;

import edu.ntut.selab.data.Point;
import edu.ntut.selab.testHelper.ConfigCopyHelper;

public class LongClickEventTest {

	@Test
	public void testLongClickEvent() {
		ConfigCopyHelper.backupAndChangeToTestConfig();
		Point p = new Point(2, 5);
		LongClickEvent e = new LongClickEvent(p, "");
		assertEquals(p.x(), e.point.x());
		assertEquals(p.y(), e.point.y());
		assertEquals("D:\\Tools\\adt-bundle-windows-x86_64-20140702\\sdk\\tools\\monkeyrunner.bat",
				e.monkeyRunner);
		assertEquals("long-click 2,5", e.label);
		ConfigCopyHelper.restoreConfigAndDeleteBackup();
	}

	@Test
	public void testGetReportLabel() {
		assertEquals("long-click 2,5", new LongClickEvent(new Point(2, 5), "").getReportLabel());
	}

	@Test
	public void testGetLongPressScript() {
		LongClickEvent event = new LongClickEvent(new Point(7,11), "");
		File script = event.getLongPressScript();
		assertEquals("gui_pages\\monkeyrunner.script", script.getPath());
		FileReader fr = null;
		BufferedReader br = null;
		String s1 = null,
				s2 = null,
				s3 = null;
		try {
			fr = new FileReader(script);
			br = new BufferedReader(fr);
			s1 = br.readLine();
			s2 = br.readLine();
			s3 = br.readLine();
		} 
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
		assertEquals("from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice", s1);
		assertEquals("device = MonkeyRunner.waitForConnection()", s2);
		assertEquals("device.drag((7,11),(7,11),2,1)", s3);
		try {
			br.close();
			fr.close();
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
		script.delete();
	}

}
