package edu.ntut.selab;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import edu.ntut.selab.CommandHelper;
import edu.ntut.selab.data.NodeAttribute;
import edu.ntut.selab.data.ElementAndSiblings;
import edu.ntut.selab.data.Point;
import edu.ntut.selab.data.TempGUIState;
import edu.ntut.selab.data.TextXMLType;
import edu.ntut.selab.event.AndroidEvent;
import edu.ntut.selab.event.BackKeyEvent;
import edu.ntut.selab.event.CheckEvent;
import edu.ntut.selab.event.ClickEvent;
import edu.ntut.selab.event.EditTextEvent;
import edu.ntut.selab.event.LongClickEvent;
import edu.ntut.selab.event.MenuKeyEvent;
import edu.ntut.selab.event.SwipeEvent;
import edu.ntut.selab.event.SwipeType;

public class XMLReader {
	protected int guiIndex = 0;
	protected TempGUIState tempGUIState = null;
	protected Document document = null;
	protected CommandHelper commandHelper = new CommandHelper();
	protected String guiPagesPath = null;
	protected String timeStamp = null;
	final String slash = "/";
	final String adbPath = getConfigurationValue("adb");
	
	public XMLReader() {
		timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").
				format(Calendar.getInstance().getTime());
		guiPagesPath = new String("gui_pages" + slash + timeStamp);
	}
	
	public TempGUIState read() {
		downloadXML();		
		startParsingXML();
		guiIndex++;
		return tempGUIState;
	}
	
	protected void downloadXML() {		
		String[] dumpUIXMLCmd = {"\"" + adbPath + "\"", "shell",
						"uiautomator", "dump",
						"/data/local/tmp/" + guiIndex + ".xml"}, 
				downloadUIXMLCmd = {"\"" + adbPath + "\"", "pull",
						"/data/local/tmp/" + guiIndex + ".xml", 
						guiPagesPath},
				removeUIXMLInDevice = {"\"" + adbPath + "\"", "shell",
						"rm", "/data/local/tmp/" + guiIndex + ".xml"},
				screenshotCmd = {"\"" + adbPath + "\"", "shell", 
						"screencap", "-p", "/data/local/tmp/" + 
						guiIndex +".png"},
				downloadScreenshotCmd = {"\"" + adbPath + "\"", "pull",
						"/data/local/tmp/" + guiIndex + ".png",
						guiPagesPath},
				removeScreenshotInDevice = {"\"" + adbPath + "\"", "shell",
						"rm", "/data/local/tmp/" + guiIndex + ".png"};
		new File(
				System.getProperty("user.dir") +
				slash + guiPagesPath).mkdirs();
		try {
			CommandHelper.executeCommand(dumpUIXMLCmd);
			CommandHelper.executeCommand(screenshotCmd);
			CommandHelper.executeCommand(downloadUIXMLCmd);
			CommandHelper.executeCommand(downloadScreenshotCmd);
			CommandHelper.executeCommand(removeUIXMLInDevice);
			CommandHelper.executeCommand(removeScreenshotInDevice);
		} 
		catch(IOException e) {
			e.printStackTrace();
		}		
	}		
	
	/***
	 * Get value from /configuration/configuration.xml 
	 */
	public static String getConfigurationValue(String elementName) {
		String value = null;
		File pathConfigFile = new File("configuration/configuration.xml");
		Element element = null, rootElement = null;
		try {		
			Document document = (new SAXReader()).read(pathConfigFile);
			rootElement = (Element)document.getRootElement().clone();
		}
		catch(DocumentException e) {
			e.printStackTrace();
		}		
		for(int i = 0 ; i<rootElement.elements().size() ; i++) {
			element = (Element)rootElement.elements().get(i);
			if(element.getName().equals(elementName)) {
				value = element.getText();
				break;
			}
		}		
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getTextXMLValue(Document targetDocument, String bounds) {
		//TODO UT
		//TODO add value using number of times
		ArrayList<String> valueList = new ArrayList<String>();					
		List<Element> elementList = null;		
		try {		
			File textXMLFile = new File("configuration/text.xml");
			Document document = (new SAXReader()).read(textXMLFile);
			Element rootElement = document.getRootElement().createCopy();
			elementList = rootElement.elements();
		}
		catch(DocumentException e) {
			e.printStackTrace();
		}		
		for(Element valueElement : elementList) {
			String xpathValue = valueElement.attribute(TextXMLType.XPath).getText();
			Node tempNode = targetDocument.selectSingleNode(xpathValue);
			String tempBounds = null;
			if(tempNode != null) {
				Element tempElement = (Element) tempNode;
				if(isEditText(tempElement)) {
					tempBounds = tempElement.attribute(NodeAttribute.Bounds).getText();
					if(tempBounds.compareTo(bounds) == 0) {
						valueList.add(valueElement.getText());
					}
				}				
			}
		}		
		return valueList;
	}
	
	protected void startParsingXML() {	
		//TODO UT
		final String dotXML = ".xml";
		ArrayList<AndroidEvent> androidEventList =
				new ArrayList<AndroidEvent>();
		SAXReader reader = new SAXReader();
		Element element = null;
		try {			
			document = reader.read(guiPagesPath+ slash + guiIndex + dotXML);
		} 
		catch(DocumentException e) {
			LoggerHelper.warning("Can't Read The UI XML File.");
			e.printStackTrace();
		}
		//root: <hierarchy
		element = document.getRootElement().createCopy();
		element = (Element)element.elementIterator().next();
		androidEventList = getTotalEventList(element);
		tempGUIState = new TempGUIState(document, androidEventList);
	}
	
	public static ElementAndSiblings getNextElementAndSiblings(
			Element element, ArrayList<List<?>> siblingElements)
			throws ArithmeticException {
		List<?> elements = null;
		int siblingSize = 0, elementsSize = 0;
		if(element.elementIterator().hasNext()) {
			if(element.elements().size()>1) {	
				siblingElements.add(element.elements());
			}
			element = (Element)element.elementIterator().next();
		}			
		else if(siblingElements.size()>0) {					
			siblingSize = siblingElements.size();
			siblingElements.get(siblingSize-1).remove(0);
			elementsSize = siblingElements.get(siblingSize-1).size();
			elements = siblingElements.get(siblingSize-1);
			element = (Element)elements.get(0);
			if(elementsSize==1) {
				siblingElements.remove(siblingSize-1);
			}
		}
		else {
			//Should be the end of XML
			throw new ArithmeticException();
		}
		return new ElementAndSiblings(element, siblingElements);
	}
	
	@SuppressWarnings("unchecked")
	protected ArrayList<AndroidEvent> getTotalEventList(Element childElement) {
		//TODO UT
		ArrayList<AndroidEvent> androidEventList = new ArrayList<AndroidEvent>();
		ArrayList<List<?>> siblingElements = new ArrayList<List<?>>();
		String bounds = childElement.attribute(NodeAttribute.Bounds).getText();		
		while(true) {
			androidEventList.addAll(getEventList(childElement));
			if(!childElement.elementIterator().hasNext() &&
					siblingElements.isEmpty()) {
				break;
			}
			ElementAndSiblings elementAndSibling = null;
			try {
				elementAndSibling = getNextElementAndSiblings(
						childElement, 
						(ArrayList<List<?>>)siblingElements.clone());
			} 
			catch(ArithmeticException e) {
				//End of XML
				e.printStackTrace();
			}
			childElement = elementAndSibling.element;
			siblingElements = elementAndSibling.siblingElements;
		}
		addSwipeEvent(androidEventList, bounds, SwipeType.Horizontal);
		androidEventList.add(new BackKeyEvent());
		androidEventList.add(new MenuKeyEvent());
		return androidEventList;
	}
	
	protected ArrayList<AndroidEvent> getEventList(Element element) {
		//TODO UT
		ArrayList<AndroidEvent> eventList = new ArrayList<AndroidEvent>();
		String bounds = element.attribute(NodeAttribute.Bounds).getText();
		if(isTrueOfAttribute(element, NodeAttribute.Checkable)) {			
			//TODO delete addCheckEvent(eventList, element, bounds);
			addClickEvent(eventList, element, bounds);
		}			
		if(isTrueOfAttribute(element, NodeAttribute.Clickable)) {
			addClickEvent(eventList, element, bounds);
		}
		if(isTrueOfAttribute(element, NodeAttribute.Scrollable)) {
			addSwipeEvent(eventList, bounds, SwipeType.Four);
		}
		if(isTrueOfAttribute(element, NodeAttribute.LongClickable)) {
			addLongClickEvent(eventList, element, bounds);
		}
		if(isEditText(element)) {
			addEditTextEvent(eventList, element, bounds);
		}
		return eventList;
	}
	
	protected void addEditTextEvent(
			ArrayList<AndroidEvent> eventList,
			Element element,
			String bounds) {
		//TODO UT
		String textValue = element.attribute(NodeAttribute.Text).getText(),
				tempLabel = "index=" + element.attribute(NodeAttribute.Index).getText();
		int backspaceCount = textValue.length();
		Point rightPoint = PointHelper.getRightPoint(
				getUpperLeftPoint(bounds), getLowerRightPoint(bounds));
		ArrayList<String> valueList = getTextXMLValue(document, bounds);
		for(String value : valueList) {
			eventList.add(new EditTextEvent(rightPoint, backspaceCount, value, tempLabel));
		}
	}
	
	//TODO delete it
	protected void addCheckEvent(ArrayList<AndroidEvent> eventList, Element element, String bounds) {
		//TODO UT
		Point centerPoint = PointHelper.getCenterPoint(
				getUpperLeftPoint(bounds), getLowerRightPoint(bounds));
		String tempLabel = getTempLabel(element);
		eventList.add(new CheckEvent(centerPoint, tempLabel));
	}
	
	protected void addClickEvent(ArrayList<AndroidEvent> eventList, Element element, String bounds) {
		//TODO UT
		boolean isListView = false, isGridView = false;
		isListView = isAttributeTextEqual(element, NodeAttribute.Class, NodeAttribute.ListView);
		isGridView = isAttributeTextEqual(element, NodeAttribute.Class, NodeAttribute.GridView); 
		if(isListView || isGridView) {
			for(Object childElement : element.elements()) {
				String childBounds = ((Element)childElement).
						attribute(NodeAttribute.Bounds).getText();
				Point centerPoint = PointHelper.getCenterPoint(
								getUpperLeftPoint(childBounds),
								getLowerRightPoint(childBounds));
				String tempLabel = getTempLabel(element);
				eventList.add(new ClickEvent(centerPoint, tempLabel));
			}
		}
		else {
			Point centerPoint = PointHelper.getCenterPoint(
					getUpperLeftPoint(bounds), getLowerRightPoint(bounds));
			String tempLabel = getTempLabel(element);
			eventList.add(new ClickEvent(centerPoint, tempLabel));
		}
	}
	
	protected String getTempLabel(Element element) {
		String tempLabel = element.attribute(NodeAttribute.Text).getText();
		if(tempLabel.compareTo("") == 0) {
			tempLabel = "index=" + element.attribute(NodeAttribute.Index).getText();
		}
		return tempLabel;
	}
	
	protected void addSwipeEvent(ArrayList<AndroidEvent> eventList, String bounds, String type) {
		//TODO UT
		Point leftPoint = PointHelper.getLeftPoint(
				getUpperLeftPoint(bounds), getLowerRightPoint(bounds)),
		rightPoint = PointHelper.getRightPoint(
				getUpperLeftPoint(bounds), getLowerRightPoint(bounds)),
		upPoint = PointHelper.getUpPoint(
				getUpperLeftPoint(bounds), getLowerRightPoint(bounds)),
		downPoint = PointHelper.getDownPoint(
				getUpperLeftPoint(bounds), getLowerRightPoint(bounds));
		eventList.add(new SwipeEvent(leftPoint, rightPoint));
		eventList.add(new SwipeEvent(rightPoint, leftPoint));
		if(type == SwipeType.Four) {
			eventList.add(new SwipeEvent(upPoint, downPoint));
			eventList.add(new SwipeEvent(downPoint, upPoint));
		}		
	}
	
	
	protected void addLongClickEvent(ArrayList<AndroidEvent> eventList, Element element, String bounds) {
		//TODO UT
		Point centerPoint = PointHelper.getCenterPoint(
				getUpperLeftPoint(bounds), getLowerRightPoint(bounds));
		String tempLabel = "index=" + element.attribute(NodeAttribute.Index).getText();
		eventList.add(new LongClickEvent(centerPoint, tempLabel));
	}
	
	protected static boolean isEditText(Element element) {
		return isAttributeTextEqual(
				element, 
				NodeAttribute.Class, 
				NodeAttribute.EditText);
	}
	
	
	protected static boolean isAttributeTextEqual(
			Element element, String attribute, String text) {
		return element.attribute(attribute).getText().equals(text);
	}
	
	protected boolean isTrueOfAttribute(Element element, String attribute) {
		final String triggerable = "true";
		return isAttributeTextEqual(element, attribute, triggerable);
	}
	
	
	protected Point getUpperLeftPoint(String bounds) {
		final int leftOffset = 1;
		String leftStringX, leftStringY;
		int leftX,leftY;		
		leftStringX = bounds.substring(1,bounds.indexOf(","));
		leftX = Integer.parseInt(leftStringX);
		leftStringY = bounds.substring(
				bounds.indexOf(",")+leftOffset, bounds.indexOf("]["));
		leftY = Integer.parseInt(leftStringY);
		Point leftPoint = new Point(leftX, leftY);
		return leftPoint;
	}
	
	
	protected Point getLowerRightPoint(String bounds) {
		final int  boudnsOffset = 2, yOffset = 1;
		String tempString, rightStringX, rightStringY;
		int rightX,rightY;
		tempString = bounds.substring(bounds.indexOf("][")+boudnsOffset);
		rightStringX = tempString.substring(0, tempString.indexOf(","));
		rightX = Integer.parseInt(rightStringX);
		rightStringY = tempString.substring(
				tempString.indexOf(",")+yOffset, tempString.indexOf("]"));
		rightY = Integer.parseInt(rightStringY);		
		Point rightPoint = new Point(rightX, rightY);
		return rightPoint;
	}
	
	
	public String getTimeStampClone() {
		return new String(timeStamp);
	}
	
}
