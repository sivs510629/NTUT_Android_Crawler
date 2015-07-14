package edu.ntut.selab;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.ElementAndSiblings;
import edu.ntut.selab.data.Point;
import edu.ntut.selab.event.EditTextEvent;
import edu.ntut.selab.testHelper.ConfigCopyHelper;
import edu.ntut.selab.testHelper.TextXMLCopyHelper;

public class XMLReaderTest {
	@Test
	public void testGetConfigurationValue() {
		ConfigCopyHelper.backupAndChangeToTestConfig();
		String value = XMLReader.getConfigurationValue(ConfigurationType.PackageName);
		assertEquals("org.korosoft.simplenotepad.android", value);
		ConfigCopyHelper.restoreConfigAndDeleteBackup();
	}
	
	@Test
	public void testGetEventList() {
		EditTextEvent e2 = new EditTextEvent(new Point(0,0), 5, "a b", "");
		try {
			e2.enterValue();
		} 
		catch (NullPointerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		fail("");
		TextXMLCopyHelper.backupAndChangeToTestConfig();
		
		
		
		
		TextXMLCopyHelper.restoreConfigAndDeleteBackup();
		
	}
	
	@Test
	public void testGetTextXMLValue() {
		TextXMLCopyHelper.backupAndChangeToTestConfig();
		Document d = null;
		try {
			d = (new SAXReader()).read("testData/testGetTextXMLValue/0.xml");
		} 
		catch (DocumentException e) {
			e.printStackTrace();
		}
		XMLReader r = new XMLReader();
		ArrayList<String> valueList = r.getTextXMLValue(d, "[16,66][704,1237]");
		assertEquals(2, valueList.size());
		assertEquals("testText1", valueList.get(0));
		assertEquals("testText2", valueList.get(1));
		TextXMLCopyHelper.restoreConfigAndDeleteBackup();
	}

	@Test
	public void testStartParsingXML() {
		XMLReader reader = new XMLReader();
		Path sourcePath = new File("testData/parsingXML.xml").toPath(),
				targetPath = new File(reader.guiPagesPath + "/0.xml").toPath();
		CopyOption option = StandardCopyOption.REPLACE_EXISTING;
		try {
			new File(System.getProperty("user.dir") +
					"/" + reader.guiPagesPath).mkdirs();
			Files.copy(sourcePath, targetPath, option);
		} 
		catch(IOException e) {
			e.printStackTrace();
		}				
		reader.startParsingXML();		
		String label = reader.tempGUIState.getEventList().get(0).getReportLabel();
		assertEquals("swipe from 2,4 to 6,4", label);
		
		label = reader.tempGUIState.getEventList().get(1).getReportLabel();
		assertEquals("swipe from 6,4 to 2,4", label);
		
		label = reader.tempGUIState.getEventList().get(2).getReportLabel();
		assertEquals("swipe from 4,2 to 4,6", label);
		
		label = reader.tempGUIState.getEventList().get(3).getReportLabel();
		assertEquals("swipe from 4,6 to 4,2", label);
		
		label = reader.tempGUIState.getEventList().get(12).getReportLabel();
		assertEquals("check 7,7", label);
		
		label = reader.tempGUIState.getEventList().get(16).getReportLabel();
		assertEquals("swipe from 7,9 to 7,5", label);
		
		label = reader.tempGUIState.getEventList().get(17).getReportLabel();
		assertEquals("click 9,9", label);
		
		label = reader.tempGUIState.getEventList().get(18).getReportLabel();
		assertEquals("click 10,20", label);
		
		label = reader.tempGUIState.getEventList().get(19).getReportLabel();
		assertEquals("click 12,22", label);
		
		label = reader.tempGUIState.getEventList().get(20).getReportLabel();
		assertEquals("click 13,13", label);
		
		label = reader.tempGUIState.getEventList().get(21).getReportLabel();
		assertEquals("click 14,24", label);
		
		label = reader.tempGUIState.getEventList().get(22).getReportLabel();
		assertEquals("Menu", label);
		
		label = reader.tempGUIState.getEventList().get(23).getReportLabel();
		assertEquals("Back", label);
		
		label = reader.tempGUIState.getEventList().get(24).getReportLabel();
		assertEquals("Home", label);
		
		assertEquals(25, reader.tempGUIState.getEventList().size());
		
		File testFile = new File(reader.guiPagesPath + "/0.xml");
		testFile.delete();
		testFile = new File(reader.guiPagesPath);
		testFile.delete();
	}

	@Test
	public void testGetNextElementAndSibling() {
		SAXReader fileReader = new SAXReader();
		Document document = null;
		try {
			document = fileReader.read("testData/parsingXML.xml");
		} 
		catch(DocumentException e) {
			e.printStackTrace();
		}
		Element element = document.getRootElement();
		element = (Element)element.elementIterator().next();
		ArrayList<List<?>> siblingElements = new ArrayList<List<?>>();
		ElementAndSiblings elementAndSiblings = 
				XMLReader.getNextElementAndSiblings(element, siblingElements);
		element = elementAndSiblings.element;
		String index = element.attribute("index").getText();
		assertEquals("1", index);
		siblingElements = elementAndSiblings.siblingElements;
		int siblingSize = siblingElements.size();
		assertEquals(1, siblingSize);
		elementAndSiblings = 
				XMLReader.getNextElementAndSiblings(element, siblingElements);
		element = elementAndSiblings.element;
		index = element.attribute("index").getText();
		assertEquals("2", index);
		siblingElements = elementAndSiblings.siblingElements;
		siblingSize = siblingElements.size();
		assertEquals(0, siblingSize);
		for(int i = 0 ; i<3 ; i++) {
			elementAndSiblings = 
					XMLReader.getNextElementAndSiblings(element, siblingElements);
			element = elementAndSiblings.element;
			siblingElements = elementAndSiblings.siblingElements;
		}
		index = element.attribute("index").getText();
		assertEquals("5", index);
		siblingSize = siblingElements.size();
		assertEquals(2, siblingSize);
	}

	@Test
	public void testGetUpperLeftPoint() {
		XMLReader reader = new XMLReader();
		String bounds = "[1,2][3,4]";
		int x = reader.getUpperLeftPoint(bounds).x(),
				y = reader.getUpperLeftPoint(bounds).y();
		assertEquals(1, x);
		assertEquals(2, y);
		bounds = "[12,34][56,78]";
		x = reader.getUpperLeftPoint(bounds).x();
		y = reader.getUpperLeftPoint(bounds).y();
		assertEquals(12, x);
		assertEquals(34, y);
	}

	@Test
	public void testGetLowerRightPoint() {
		XMLReader reader = new XMLReader();
		String bounds = "[1,2][3,4]";
		int x = reader.getLowerRightPoint(bounds).x(),
				y = reader.getLowerRightPoint(bounds).y();
		assertEquals(3, x);
		assertEquals(4, y);
		bounds = "[12,34][56,78]";
		x = reader.getLowerRightPoint(bounds).x();
		y = reader.getLowerRightPoint(bounds).y();
		assertEquals(56, x);
		assertEquals(78, y);
	}
	
}
