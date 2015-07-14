package edu.ntut.selab;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import edu.ntut.selab.data.ElementAndSiblings;
import edu.ntut.selab.data.EventAndStateID;
import edu.ntut.selab.data.GUIState;
import edu.ntut.selab.data.Point;
import edu.ntut.selab.data.TempGUIState;
import edu.ntut.selab.event.AndroidEvent;
import edu.ntut.selab.event.ClickEvent;
import edu.ntut.selab.event.LongClickEvent;
import edu.ntut.selab.mock.MockController;
import edu.ntut.selab.testHelper.ConfigCopyHelper;

public class ControllerTest {
	@Test
	public void testStartToRun() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testTurnOffSoftKeyboard() {
		Controller ctrl = new Controller();
		ctrl.turnOffSoftKeyboard();
		fail("Not yet implemented");
	}
	
	@Test
	public void testIsOtherApp() {
		ConfigCopyHelper.backupAndChangeToTestConfig();
		Controller ctrl = new Controller();
		SAXReader reader = new SAXReader();
		Document d = null, dDifferent = null;
		try {
			d = reader.read("testData/SimpleNote.xml");
			dDifferent = reader.read("testData/parsingXML.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		TempGUIState tempState = new TempGUIState(dDifferent, new ArrayList<AndroidEvent>());
		assertTrue(ctrl.isOtherApp(tempState));
		tempState = new TempGUIState(d, new ArrayList<AndroidEvent>());		
		assertFalse(ctrl.isOtherApp(tempState));
		ConfigCopyHelper.restoreConfigAndDeleteBackup();
	}
	
	@Test
	public void testIsActivityChanged_2() {
		Controller controller = new Controller();
		controller.oldCurrentActivityName = "com.garena.gamecenter.ui.home.HomeActivity";
		controller.isActivityChanged();
		fail("");
	}
	
	@Test
	public void testGetCurrentActivityName() {
		Controller controller = new Controller();
		controller.getCurrentActivityName();
		fail("");
	}

	@Test
	public void testIsActivityChanged() {
		Controller controller = new Controller();
		SAXReader reader = new SAXReader();
		Document doc = null, docEqual = null, docDifferent = null;
		try {
			doc = reader.read(
					"testData/testIsActivityChanged/Activity_1.xml");
			docEqual = reader.read(
					"testData/testIsActivityChanged/Activity_2_equal.xml");
			docDifferent = reader.read(
					"testData/testIsActivityChanged/Activity_3_different.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		ArrayList<AndroidEvent> notUsedEventList = new ArrayList<AndroidEvent>();
		TempGUIState state = new TempGUIState(doc, notUsedEventList),
				stateEqual = new TempGUIState(docEqual, notUsedEventList),
				stateDifferent = new TempGUIState(docDifferent, notUsedEventList);
		assertTrue(controller.isActivityChanged(null, state));
		assertTrue(controller.isActivityChanged(state, null));
		assertFalse(controller.isActivityChanged(state, stateEqual));
		assertTrue(controller.isActivityChanged(state, stateDifferent));
		assertTrue(controller.isActivityChanged(stateDifferent, state));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testHasNextElement() {
		Controller controller = new Controller();
		SAXReader reader = new SAXReader();
		Document doc = null, docEqual = null, docDifferent = null;
		try {
			doc = reader.read(
					"testData/testHasNextElement/Activity_1.xml");
			docEqual = reader.read(
					"testData/testHasNextElement/Activity_2_equal.xml");
			docDifferent = reader.read(
					"testData/testHasNextElement/Activity_3_different.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		//doc, docEqual
		//<hierarchy
		Element oldElement = (Element)doc.getRootElement().clone(),
				newElement = (Element)docEqual.getRootElement().clone();
		ArrayList<List<?>> oldSiblings = new ArrayList<List<?>>(),
				newSiblings = new ArrayList<List<?>>();
		ElementAndSiblings oldES = new ElementAndSiblings(oldElement, oldSiblings),
				newES = new ElementAndSiblings(newElement, newSiblings),
				tempES = null;
		assertEquals("Continue To Run", controller.hasNextElement(oldES, newES));
		//<node index="0"
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)oldElement.clone(), (ArrayList<List<?>>)oldSiblings.clone());
		oldElement = tempES.element;
		oldSiblings = tempES.siblingElements;
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)newElement.clone(), (ArrayList<List<?>>)newSiblings.clone());
		newElement = tempES.element;
		newSiblings = tempES.siblingElements;
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Continue To Run", controller.hasNextElement(oldES, newES));
		//<node index="1"
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)oldElement.clone(), (ArrayList<List<?>>)oldSiblings.clone());
		oldElement = tempES.element;
		oldSiblings = tempES.siblingElements;
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)newElement.clone(), (ArrayList<List<?>>)newSiblings.clone());
		newElement = tempES.element;
		newSiblings = tempES.siblingElements;
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Continue To Run", controller.hasNextElement(oldES, newES));
		//<node index="2"
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)oldElement.clone(), (ArrayList<List<?>>)oldSiblings.clone());
		oldElement = tempES.element;
		oldSiblings = tempES.siblingElements;
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)newElement.clone(), (ArrayList<List<?>>)newSiblings.clone());
		newElement = tempES.element;
		newSiblings = tempES.siblingElements;
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Not Changed", controller.hasNextElement(oldES, newES));

		//doc, docDifferent
		//<hierarchy
		oldElement = (Element)doc.getRootElement().clone();
		newElement = (Element)docDifferent.getRootElement().clone();
		oldSiblings = new ArrayList<List<?>>();
		newSiblings = new ArrayList<List<?>>();	
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Continue To Run", controller.hasNextElement(oldES, newES));
		//<node index="0"
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)oldElement.clone(), (ArrayList<List<?>>)oldSiblings.clone());
		oldElement = tempES.element;
		oldSiblings = tempES.siblingElements;
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)newElement.clone(), (ArrayList<List<?>>)newSiblings.clone());
		newElement = tempES.element;
		newSiblings = tempES.siblingElements;		
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Continue To Run", controller.hasNextElement(oldES, newES));
		//<node index="1"
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)oldElement.clone(), (ArrayList<List<?>>)oldSiblings.clone());
		oldElement = tempES.element;
		oldSiblings = tempES.siblingElements;
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)newElement.clone(), (ArrayList<List<?>>)newSiblings.clone());
		newElement = tempES.element;
		newSiblings = tempES.siblingElements;		
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Continue To Run", controller.hasNextElement(oldES, newES));
		//<node index="2"
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)oldElement.clone(), (ArrayList<List<?>>)oldSiblings.clone());
		oldElement = tempES.element;
		oldSiblings = tempES.siblingElements;
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)newElement.clone(), (ArrayList<List<?>>)newSiblings.clone());
		newElement = tempES.element;
		newSiblings = tempES.siblingElements;		
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Changed", controller.hasNextElement(oldES, newES));
		
		//docDifferent, doc
		//<hierarchy
		oldElement = (Element)docDifferent.getRootElement().clone();
		newElement = (Element)doc.getRootElement().clone();
		oldSiblings = new ArrayList<List<?>>();
		newSiblings = new ArrayList<List<?>>();
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Continue To Run", controller.hasNextElement(oldES, newES));
		//<node index="0"
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)oldElement.clone(), (ArrayList<List<?>>)oldSiblings.clone());
		oldElement = tempES.element;
		oldSiblings = tempES.siblingElements;
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)newElement.clone(), (ArrayList<List<?>>)newSiblings.clone());
		newElement = tempES.element;
		newSiblings = tempES.siblingElements;		
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Continue To Run", controller.hasNextElement(oldES, newES));
		//<node index="1"
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)oldElement.clone(), (ArrayList<List<?>>)oldSiblings.clone());
		oldElement = tempES.element;
		oldSiblings = tempES.siblingElements;
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)newElement.clone(), (ArrayList<List<?>>)newSiblings.clone());
		newElement = tempES.element;
		newSiblings = tempES.siblingElements;		
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Continue To Run", controller.hasNextElement(oldES, newES));
		//<node index="2"
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)oldElement.clone(), (ArrayList<List<?>>)oldSiblings.clone());
		oldElement = tempES.element;
		oldSiblings = tempES.siblingElements;
		tempES = XMLReader.getNextElementAndSiblings(
				(Element)newElement.clone(), (ArrayList<List<?>>)newSiblings.clone());
		newElement = tempES.element;
		newSiblings = tempES.siblingElements;		
		oldES = new ElementAndSiblings(oldElement, oldSiblings);
		newES = new ElementAndSiblings(newElement, newSiblings);
		assertEquals("Changed", controller.hasNextElement(oldES, newES));
	}

	@Test
	public void testIsInGUIStateList() {
		Controller controller = new Controller();
		SAXReader reader = new SAXReader();
		Document doc = null, docEqual = null, docDifferent = null;
		try {
			doc = reader.read(
					"testData/testIsStateChanged/State_1.xml");
			docEqual = reader.read(
					"testData/testIsStateChanged/State_2_equal.xml");
			docDifferent = reader.read(
					"testData/testIsStateChanged/State_3_different.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		GUIState stateEqual = new GUIState(1, docEqual, 0),
				stateDifferent = new GUIState(2, docDifferent, 1);
		ArrayList<AndroidEvent> notUsedEventList = new ArrayList<AndroidEvent>();
		TempGUIState tempState = new TempGUIState(doc, notUsedEventList);
		controller.guiStateList.add(stateDifferent);
		assertFalse(controller.isInGUIStateList(tempState));
		controller.guiStateList.add(stateEqual);
		assertTrue(controller.isInGUIStateList(tempState));
		
		GUIState gState = new GUIState(3, doc, 2);
		Controller otherController = new Controller();
		otherController.guiStateList.add(gState);
		TempGUIState otherTempState = new TempGUIState(docEqual, notUsedEventList),
				equalTempState = new TempGUIState(docDifferent, notUsedEventList);
		assertTrue(otherController.isInGUIStateList(otherTempState));
		assertFalse(otherController.isInGUIStateList(equalTempState));
	}
	
	@Test
	public void testIsInTempGUIStateList() {
		Controller controller = new Controller();
		SAXReader reader = new SAXReader();
		Document doc = null, docEqual = null, docDifferent = null;
		try {
			doc = reader.read(
					"testData/testIsStateChanged/State_1.xml");
			docEqual = reader.read(
					"testData/testIsStateChanged/State_2_equal.xml");
			docDifferent = reader.read(
					"testData/testIsStateChanged/State_3_different.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		ArrayList<AndroidEvent> notUsedEventList = new ArrayList<AndroidEvent>();
		TempGUIState stateEqual = new TempGUIState(docEqual, notUsedEventList),
				stateDifferent = new TempGUIState(docDifferent, notUsedEventList),
				tempState = new TempGUIState(doc, notUsedEventList);
		controller.tempStateList.add(stateDifferent);
		assertFalse(controller.isInTempGUIStateList(tempState));
		controller.tempStateList.add(stateEqual);
		assertTrue(controller.isInTempGUIStateList(tempState));
		
		TempGUIState tState = new TempGUIState(doc, notUsedEventList);
		Controller otherController = new Controller();
		otherController.tempStateList.add(tState);
		TempGUIState otherTempState = new TempGUIState(docEqual, notUsedEventList),
				equalTempState = new TempGUIState(docDifferent, notUsedEventList);
		assertTrue(otherController.isInTempGUIStateList(otherTempState));
		assertFalse(otherController.isInTempGUIStateList(equalTempState));
	}

	@Test
	public void testIsStateChanged() {
		Controller controller = new Controller();
		SAXReader reader = new SAXReader();
		Document doc = null, docEqual = null, docDifferent = null;
		try {
			doc = reader.read(
					"testData/testIsStateChanged/State_1.xml");
			docEqual = reader.read(
					"testData/testIsStateChanged/State_2_equal.xml");
			docDifferent = reader.read(
					"testData/testIsStateChanged/State_3_different.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		assertFalse(controller.isStateChanged(doc, docEqual));
		assertTrue(controller.isStateChanged(doc, docDifferent));
		assertTrue(controller.isStateChanged(docDifferent, doc));
	}

	@Test
	public void testIsAttributeEqual() {
		Controller controller = new Controller();
		SAXReader fileReader = new SAXReader();
		Document document1 = null, documentEqual = null, documentDifferent = null;
		try {
			document1 = fileReader.read(
					"testData/testIsAttributeEqual/testNodeAttribute_1.xml");
			documentEqual = fileReader.read(
					"testData/testIsAttributeEqual/testNodeAttribute_2_equal.xml");
			documentDifferent = fileReader.read(
					"testData/testIsAttributeEqual/testNodeAttribute_3_different.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		Element e1 = document1.getRootElement(),
				e2 = documentEqual.getRootElement(),
				e3 = documentDifferent.getRootElement();
		assertTrue(controller.isAttributeEqual(e1, e2));
		assertTrue(controller.isAttributeEqual(e1, e3));
		e1 = (Element)e1.elementIterator().next();
		e2 = (Element)e2.elementIterator().next();
		e3 = (Element)e3.elementIterator().next();
		assertTrue(controller.isAttributeEqual(e1, e2));
		assertFalse(controller.isAttributeEqual(e1, e3));
		e1 = (Element)e1.elementIterator().next();
		e2 = (Element)e2.elementIterator().next();
		e3 = (Element)e3.elementIterator().next();
		assertTrue(controller.isAttributeEqual(e1, e2));
		assertTrue(controller.isAttributeEqual(e1, e3));
		
	}

	@Test
	public void testRollback() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testRestartApp() {
		Controller ctrl = new Controller();
		ctrl.killADBServer();
		ctrl.startApp();
		ctrl.restartApp();
		ctrl.stopApp();
		ctrl.killADBServer();
		fail("Not yet implemented");
	}
	
	@Test
	public void testStartApp() {
		testStopApp();
		fail("Not yet implemented");
	}
	
	@Test
	public void testStopApp() {
		Controller ctrl = new Controller();
		ctrl.killADBServer();
		ctrl.startApp();
		ctrl.stopApp();
		ctrl.killADBServer();
		fail("Not yet implemented");
	}

	@Test
	public void testGoToTargetState() {		
		MockController mController = new MockController();		
		SAXReader reader = new SAXReader();
		Document tempDoc = null;
		try {
			tempDoc = reader.read("testData/parsingXML.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		GUIState s0 = new GUIState(0, tempDoc, -1),
				s1 = new GUIState(1, tempDoc, 0),
				s2 = new GUIState(2, tempDoc, 1),
				s3 = new GUIState(3, tempDoc, 1);				
		AndroidEvent tempEventA = new ClickEvent(new Point(1,2), ""),
				tempEventB = new LongClickEvent(new Point(3,4), "");
		EventAndStateID eventAndNextState = new EventAndStateID(3, tempEventA);
		s1.addEventAndNextState(eventAndNextState);	
		eventAndNextState = new EventAndStateID(1, tempEventB);
		s0.addEventAndNextState(eventAndNextState);
		mController.guiStateList.add(s0);
		mController.guiStateList.add(s1);
		mController.guiStateList.add(s2);
		mController.guiStateList.add(s3);
		ArrayList<Integer> path = new ArrayList<Integer>();
		path.add(0);
		path.add(1);
		path.add(3);
		mController.pathOfTargetState = path;
		mController.i = 2;
		mController.goToTargetState(3);
		assertEquals(tempEventA.getReportLabel(),
				mController.nextEvent.getReportLabel());
		mController.i = 1;
		mController.goToTargetState(1);
		assertEquals(tempEventB.getReportLabel(),
				mController.nextEvent.getReportLabel());
	}

	@Test
	public void testGetPathOfTargetID() {
		ArrayList<Integer> path = new ArrayList<Integer>();
		Controller controller = new Controller();
		int stateID = 0, parentID = -1, targetStateID = 2;
		SAXReader reader = new SAXReader();
		Document tempDoc = null;
		try {
			tempDoc = reader.read("testData/parsingXML.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		GUIState guiState = new GUIState(stateID, tempDoc, parentID);
		controller.guiStateList.add(guiState);
		stateID = 1;
		parentID = 0;
		guiState = new GUIState(stateID, tempDoc, parentID);
		controller.guiStateList.add(guiState);
		stateID = 2;
		parentID = 1;
		guiState = new GUIState(stateID, tempDoc, parentID);
		controller.guiStateList.add(guiState);
		path = controller.getPathOfTargetID(targetStateID);
		assertEquals(3, path.size());
		assertEquals(0, path.get(0).intValue());
		assertEquals(1, path.get(1).intValue());
		assertEquals(2, path.get(2).intValue());
		targetStateID = 1;
		path = controller.getPathOfTargetID(targetStateID);
		assertEquals(2, path.size());
		assertEquals(0, path.get(0).intValue());
		assertEquals(1, path.get(1).intValue());
		targetStateID = 0;
		path = controller.getPathOfTargetID(targetStateID);
		assertEquals(1, path.size());
		assertEquals(0, path.get(0).intValue());
	}

	@Test
	public void testGetIndexOfGUIStateListByStateID() {
		Controller controller = new Controller();
		int targetStateID = 0;
		SAXReader reader = new SAXReader();
		Document tempDoc = null;
		try {
			tempDoc = reader.read("testData/parsingXML.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		controller.guiStateList.add(new GUIState(-1, tempDoc, -2));
		controller.guiStateList.add(new GUIState(0, tempDoc, -3));
		controller.guiStateList.add(new GUIState(1, tempDoc, -4));
		int index = controller.getIndexOfGUIStateListByStateID(targetStateID);
		assertEquals(1, index);
		targetStateID = 1;
		index = controller.getIndexOfGUIStateListByStateID(targetStateID);
		assertEquals(2, index);
	}

	@Test
	public void testGetEventToNextStateID() {
		SAXReader reader = new SAXReader();
		Document tempDoc = null;
		try {
			tempDoc = reader.read("testData/parsingXML.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		GUIState s0 = new GUIState(0, tempDoc, -1),
				s1 = new GUIState(1, tempDoc, 0),
				s2 = new GUIState(2, tempDoc, 1),
				s3 = new GUIState(3, tempDoc, 1);
		Controller controller = new Controller();
		controller.guiStateList.add(s0);
		controller.guiStateList.add(s1);
		controller.guiStateList.add(s2);
		controller.guiStateList.add(s3);
		AndroidEvent tempEvent = new ClickEvent(new Point(1,2), ""),
				gottenEvent = null;
		EventAndStateID eventAndNextState = new EventAndStateID(3, tempEvent);
		s1.addEventAndNextState(eventAndNextState);
		gottenEvent = controller.getEventToNextState(1, 3);
		assertEquals(tempEvent.getReportLabel(), gottenEvent.getReportLabel());
	}

}
