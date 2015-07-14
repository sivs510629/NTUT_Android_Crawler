package edu.ntut.selab;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

import edu.ntut.selab.data.EventAndStateID;
import edu.ntut.selab.data.GUIState;
import edu.ntut.selab.data.Point;
import edu.ntut.selab.data.StateResult;
import edu.ntut.selab.event.ClickEvent;

public class ReportGeneratorTest {

	@Test
	public void testGenerateReport() {
		String timestamp = "test";
		File f = new File("gui_pages/" + timestamp);
		f.mkdirs();
		ArrayList<GUIState> guiStateList = new ArrayList<GUIState>();
		GUIState g = new GUIState(0, null, -1);
		g.addEventAndNextState(new EventAndStateID(0, new ClickEvent(new Point(9, 8), "")));
		g.addEventAndNextState(new EventAndStateID(1, new ClickEvent(new Point(2, 5), "")));
		g.addEventAndNextState(new EventAndStateID(2, new ClickEvent(new Point(4, 7), "")));
		guiStateList.add(g);
		guiStateList.add(new GUIState(1, null, 0));
		guiStateList.add(new GUIState(2, null, 1));
		guiStateList.get(0).addEventAndNextState(new EventAndStateID(1, new ClickEvent(new Point(1,3), "")));
		ArrayList<Integer> stateCountList = new ArrayList<Integer>();
		stateCountList.add(2);
		stateCountList.add(1);
		//StateResult result = new StateResult(guiStateList, stateCountList);
		//ReportGenerator rg = new ReportGenerator(timestamp, result);
		fail("not done");
	}
	
	@Test
	public void testGetActivityCount() {
		//TODO delete test result
		String timestamp = "test";
		File f = new File("gui_pages/" + timestamp);
		f.mkdirs();
		ArrayList<GUIState> guiStateList = new ArrayList<GUIState>();
		GUIState g = new GUIState(0, null, -1);
		g.addEventAndNextState(new EventAndStateID(0, new ClickEvent(new Point(9, 8), "")));
		g.addEventAndNextState(new EventAndStateID(1, new ClickEvent(new Point(2, 5), "")));
		g.addEventAndNextState(new EventAndStateID(2, new ClickEvent(new Point(4, 7), "")));
		guiStateList.add(g);
		guiStateList.add(new GUIState(1, null, 0));
		guiStateList.add(new GUIState(2, null, 1));
		guiStateList.get(0).addEventAndNextState(new EventAndStateID(1, new ClickEvent(new Point(1,3), "")));
		ArrayList<Integer> stateCountList = new ArrayList<Integer>();
		stateCountList.add(2);
		stateCountList.add(1);
		/*
		StateResult result = new StateResult(guiStateList, stateCountList);
		ReportGenerator rg = new ReportGenerator(timestamp, result);
		assertEquals(0, rg.getActivityCount(0));
		assertEquals(0, rg.getActivityCount(1));
		assertEquals(1, rg.getActivityCount(2));
		*/
		fail("not done");
	}
	
	@Test
	public void testCreateHierarchyResultSVGFile() {
		String timestamp = "test";
		File f = new File("gui_pages/" + timestamp);
		f.mkdirs();
		ArrayList<GUIState> guiStateList = new ArrayList<GUIState>();
		GUIState g = new GUIState(0, null, -1);
		g.addEventAndNextState(new EventAndStateID(0, new ClickEvent(new Point(9, 8), "")));
		g.addEventAndNextState(new EventAndStateID(1, new ClickEvent(new Point(2, 5), "")));
		g.addEventAndNextState(new EventAndStateID(2, new ClickEvent(new Point(4, 7), "")));
		guiStateList.add(g);
		guiStateList.add(new GUIState(1, null, 0));
		guiStateList.add(new GUIState(2, null, 1));
		guiStateList.get(0).addEventAndNextState(new EventAndStateID(1, new ClickEvent(new Point(1,3), "")));
		ArrayList<Integer> stateCountList = new ArrayList<Integer>();
		stateCountList.add(2);
		stateCountList.add(1);
		/*
		StateResult result = new StateResult(guiStateList, stateCountList);
		ReportGenerator rg = new ReportGenerator(timestamp, result);
		rg.imagePath = "C:\\Users\\Roger\\Desktop\\AndroidCrawler\\AndroidCrawler\\gui_pages\\test";
		File dotFile = new File("C:\\Users\\Roger\\Desktop\\AndroidCrawler\\AndroidCrawler\\gui_pages\\test\\test.dot");
		rg.createSVGFile(dotFile);
		*/
		fail("not doen");
	}

}
