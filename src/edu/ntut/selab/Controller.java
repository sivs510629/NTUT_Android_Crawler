package edu.ntut.selab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import edu.ntut.selab.data.NodeAttribute;
import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.ElementAndSiblings;
import edu.ntut.selab.data.EventAndStateID;
import edu.ntut.selab.data.GUIState;
import edu.ntut.selab.data.StateResult;
import edu.ntut.selab.data.TempGUIState;
import edu.ntut.selab.event.AndroidEvent;
import edu.ntut.selab.event.BackKeyEvent;

public class Controller {
	protected boolean isStartState = true,
			rollbacked = false,
			doesGoToNewActivity = false;
	protected ArrayList<TempGUIState> tempStateList = new ArrayList<TempGUIState>(),
			oldTempStateList = new ArrayList<TempGUIState>();
	protected ArrayList<GUIState> guiStateList = new ArrayList<GUIState>();
	protected EventGenerator eventGenerator = new EventGenerator();
	protected TempGUIState oldTempGUIState = null, tempGUIState = null;
	protected String xmlReaderTimestamp = null, 
			oldCurrentActivityName = null,
			currentActivityName = null;
	private final String notChanged = "Not Changed",
			hasChanged = "Changed",
			continueToRun = "Continue To Run";
	protected GUIState oldGUIState = null;
	protected AndroidEvent oldEvent = null;
	protected ArrayList<AndroidEvent> oldEventList = null,eventList = null,
			executedEvent = null;
	protected ArrayList<Integer> stateCountList = new ArrayList<Integer>();
	protected ArrayList<String> activityNameList = new ArrayList<String>();
 	protected int stateCount = 0;
	public Controller() {		
	}
	
	public StateResult startToRun() {
		XMLReader xmlReader = new XMLReader();
		xmlReaderTimestamp = xmlReader.getTimeStampClone();
		//killADBServer();
		restartApp();
		while(true) {
			boolean activityChanged = true,
					visitedState = false,
					anotherApp = false;
			turnOffSoftKeyboard();
			if(!rollbacked && !doesGoToNewActivity) {				
				tempGUIState = xmlReader.read();
				activityChanged = isActivityChanged();
			}
			anotherApp = isOtherApp(tempGUIState);
			visitedState = isInGUIStateList(tempGUIState);
			if(anotherApp && isBackKeyEvent(oldEvent)) {
				tempGUIState.getEventList().clear();
			}
			else if(anotherApp) {
				setAnotherAppThreshold();
			}
			if(!activityChanged/*!visitedState*/ || isStartState || rollbacked || visitedState) {
				if(rollbacked) {
					rollbacked = false;
				}
				else if(!visitedState) {	
					newState();
				}
				else {
					visitedState();				
				}
				isStartState = false;
				
				if(!tempGUIState.getEventList().isEmpty()) {
					executeEvent();
				}
				else if(!oldTempStateList.isEmpty()) {
					pickOldState();
				}
				else if(!tempStateList.isEmpty()){					
					pickANewActivity();
				}
				else {
					//stateCountList.add(stateCount);
					//addStateCount();
					break;
				}
			}
			else {			
				newActivity();
			}
		}		
		stopApp();
		//killADBServer();
		
		return new StateResult(guiStateList, activityNameList);			
	}	
	
	protected void setAnotherAppThreshold() {		
		try {
			int threshold = Integer.parseInt(XMLReader.getConfigurationValue(
					ConfigurationType.AnotherAppDepthThresold));
			if(threshold<getAnotherAppStateDepth()) {
				tempGUIState.getEventList().clear();
			}			
		}
		catch(NumberFormatException e) {
			LoggerHelper.warning("<<Configuration Threshold Error>>");
		}
	}
	
	protected int getAnotherAppStateDepth() {
		boolean tempIsAnotherApp = true;
		Document content = null;
		int depth = 0,
			targetID = getStateIDByContent(oldTempGUIState.contentClone()); 
		ArrayList<Integer>	pathList = getPathOfTargetID(targetID);
		while(tempIsAnotherApp) {
			depth++;			
			targetID = pathList.get(pathList.size()-1);
			pathList.remove(pathList.size()-1);
			int index = getIndexOfGUIStateListByStateID(targetID);
			content = guiStateList.get(index).contentClone();
			tempIsAnotherApp = isOtherApp(content);
		}
		return depth;			
	}
	
	protected boolean isBackKeyEvent(AndroidEvent event) {
		String label = event.getReportLabel(),
				backEventLabel = (new BackKeyEvent()).getReportLabel();
		return backEventLabel.compareTo(label) == 0;
	}
	
	/***
	 * For startToRun()
	 */
	protected void turnOffSoftKeyboard() {
		String[] command = {"\"" +
				XMLReader.getConfigurationValue(ConfigurationType.ADB) +
				"\"", "shell", "dumpsys", "input_method", "|", "grep",
				"\"mInputShown=true\""};
		String feedBack = null;
		try {
			feedBack = CommandHelper.executeAndGetFeedBack(command);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		if(feedBack != null) {
			(new BackKeyEvent()).execute();
		}
	}
	
	/***
	 * For startToRun()
	 */
	protected void newState() {
		//TODO UT
		System.out.println("<<new state>>");
		
		oldCurrentActivityName = getCurrentActivityName();
		//TODO duplicate 1
		if(oldGUIState != null) {
			EventAndStateID eventAndNextState = null;
			if(!isBackKeyEvent(oldEvent) || 
					!isOtherApp(tempGUIState)) {/*tempGUIState.getEventList().isEmpty()) {*/				
				eventAndNextState = new EventAndStateID(getStateID(tempGUIState), oldEvent);
			}
			else {
				eventAndNextState =	new EventAndStateID(StateIDHelper.FinalNodeID, oldEvent);
			}
			oldGUIState.addEventAndNextState(eventAndNextState);
		}
		//duplicate 1
		if(oldGUIState != null) {
			setEventAndBeforeStateID(oldGUIState.getID(), oldEvent);
		}
		doesGoToNewActivity = false;
		int parentID = StateIDHelper.InitialNodeID;
		if(tempGUIState.getEventAndBeforeStateID() != null) {
			parentID = tempGUIState.getEventAndBeforeStateID().
					stateID();
		}
		if(oldTempGUIState != null && !oldTempGUIState.getEventList().isEmpty()
				&& !isInOldTempGUIStateList(oldTempGUIState)) {
			oldTempStateList.add(oldTempGUIState);
		}
		oldTempGUIState = tempGUIState;
		oldTempStateList.add(tempGUIState);
		int stateID = StateIDHelper.FinalNodeID;
		//when eventList is empty, the tempGUIState is another App. 
		if(!tempGUIState.getEventList().isEmpty() || !isBackKeyEvent(oldEvent)) {
			stateID = getStateID(tempGUIState);
		}
		GUIState guiState = new GUIState(
				stateID, tempGUIState.contentClone(), parentID);
		oldGUIState = guiState;
		guiStateList.add(guiState);	
		activityNameList.add(oldCurrentActivityName);
		//stateCount++;
	}
	
	/***
	 * For startToRun()
	 */
	protected void visitedState() {
		//TODO UT
		System.out.println("<<visited state>>");
		//TODO duplicate 1
		if(oldGUIState != null /*&& !doesGoToNewActivity*//*isStartState*/) {
			int nextStateID = 
					getStateIDByContent(tempGUIState.contentClone());
			EventAndStateID eventAndNextState = null;
			if(!isBackKeyEvent(oldEvent) || 
					!isOtherApp(tempGUIState)) {/*tempGUIState.getEventList().isEmpty()) {*/				
				eventAndNextState = new EventAndStateID(nextStateID, oldEvent);
			}
			else {
				eventAndNextState =	new EventAndStateID(StateIDHelper.FinalNodeID, oldEvent);
			}
			oldGUIState.addEventAndNextState(eventAndNextState);
		}
		//duplicate 1
		deleteDuplicateGUIPages();
		rollback(oldGUIState.getID());
		rollbacked = false;
	}
	
	/***
	 * For startToRun()
	 */
	protected void executeEvent() {
		//TODO UT
		oldEvent = tempGUIState.getEventList().get(0);
		turnOffSoftKeyboard();
		eventGenerator.run(oldEvent);
		tempGUIState.getEventList().remove(0);
		oldEventList = tempGUIState.getEventListClone();
	}
	
	/*
	protected void cleanOldTempStateList() {
		for(int i = 0 ; i<oldTempStateList.size() ; i++) {
			if(oldTempStateList.get(i).getEventList().isEmpty()) {
				oldTempStateList.remove(i);
				i--;
			}
		}
	}
	*/
	protected void pickOldState() {
		System.out.println("<<pick old state>>");
		tempGUIState = oldTempStateList.get(0);
		oldTempStateList.remove(0);
		oldTempGUIState = tempGUIState;		
		restartApp();
		int stateID = getStateIDByContent(tempGUIState.contentClone());
		if(stateID != 0) {
			goToTargetState(stateID);
		}				
		int index = getIndexOfGUIStateListByStateID(stateID);
		oldGUIState = guiStateList.get(index);
		oldCurrentActivityName = getCurrentActivityName();
		isStartState = true;
		rollbacked = true;
	}
	
	/*
	protected void addStateCount() {
		int index = getIndexOfActivityNameList(oldCurrentActivityName);
		if(isOtherApp(tempGUIState) && isBackKeyEvent(oldEvent)) {
			return;
		}
		addActivityNameToActivityNameList(index, oldCurrentActivityName);
		if(index>=0) {
			stateCountList.set(index, stateCountList.get(index) + stateCount);
		}
		else {
			stateCountList.add(stateCount);
		}
	}
	*/
	
	/***
	 * For addStateCount()
	 */
	protected void addActivityNameToActivityNameList(int indexOfActivityList, String activityName) {
		if(indexOfActivityList<0) {
			activityNameList.add(activityName);
		}
	}
	
	protected int getIndexOfActivityNameList(String activityName) {
		for(int i = 0 ; i<activityNameList.size() ; i++) {
			if(activityName.compareTo(activityNameList.get(i)) == 0) {
				return i;
			}
		}
		int notExist = -1;
		return notExist;
	}
	
	/***
	 * For startToRun()
	 */
	protected void pickANewActivity() {
		//TODO UT		
		System.out.println("<<pick a new activity>>");
		//stateCountList.add(stateCount);
		//addStateCount();
		//stateCount = 0;
		tempGUIState = tempStateList.get(0);
		EventAndStateID eventAndBeforeState = 
				tempGUIState.getEventAndBeforeStateID();
		tempStateList.remove(0);
		int beforeStateID = eventAndBeforeState.stateID();
		restartApp();
		goToTargetState(beforeStateID);
		oldEvent = eventAndBeforeState.event();
		turnOffSoftKeyboard();
		eventGenerator.run(eventAndBeforeState.event());
		int index = getIndexOfGUIStateListByStateID(beforeStateID);
		oldGUIState = guiStateList.get(index);
		oldCurrentActivityName = getCurrentActivityName();
		isStartState = true;
		doesGoToNewActivity = true;
	}
	
	/***
	 * For startToRun()
	 */
	protected void newActivity() {
		//TODO UT
		System.out.println("<<new activity>>");
		setEventAndBeforeStateID(oldGUIState.getID(), oldEvent);
		boolean inTempStateList = isInTempGUIStateList(tempGUIState),
				inGUIStateList = isInGUIStateList(tempGUIState);
		if(/*!inTempStateList && */!inGUIStateList) {
			tempStateList.add(tempGUIState);			
		}
		else if(inGUIStateList) {
			int stateID = getStateIDByContent(tempGUIState.contentClone());
			EventAndStateID eventAndNextState = 
					new EventAndStateID(stateID, oldEvent);
			oldGUIState.addEventAndNextState(eventAndNextState);
			deleteDuplicateGUIPages();
		}
		/*
		else if(inTempStateList) {
			int stateID = getStateIDFromTempGUIStateList(tempGUIState);
			EventAndStateID eventAndNextState = 
					new EventAndStateID(stateID, oldEvent);
			oldGUIState.addEventAndNextState(eventAndNextState);
			deleteDuplicateGUIPages();
		}*/
		rollback(oldGUIState.getID());
	}
	
	protected void deleteDuplicateGUIPages() {
		//TODO UT
		//debug to comment it
		/*
		File tempFile = new File(tempGUIState.contentClone().getName());
		tempFile.delete();
		tempFile = new File(tempFile.getAbsolutePath().replace(".xml", ".png"));
		tempFile.delete();
		*/
		//debug to comment it
		
	}
	
	protected boolean isOtherApp(Document content) {
		String packageName = XMLReader.getConfigurationValue(ConfigurationType.PackageName),
				tempPackageName = getPackageName(content);
		return !tempPackageName.equals(packageName);
	}
	
	protected boolean isOtherApp(TempGUIState tempState) {
		String packageName = XMLReader.getConfigurationValue(ConfigurationType.PackageName),
				tempPackageName = getPackageName(tempState);
		return !tempPackageName.equals(packageName);			
	}
	
	protected boolean isInOtherPackageList(String packageName, ArrayList<String> otherPackageList) {
		for(int i=0 ; i<otherPackageList.size() ; i++) {
			if(otherPackageList.get(i).equals(packageName)) {
				return true;
			}
		}
		return false;
	}
	
	protected String getPackageName(Document content) {
		Element element = content.getRootElement();
		ArrayList<List<?>> siblingElements = new ArrayList<List<?>>();
		element = XMLReader.getNextElementAndSiblings(element, siblingElements).element;
		return element.attribute(NodeAttribute.Package).getText();
	}
	
	protected String getPackageName(TempGUIState tempState) {
		Document content = tempState.contentClone();
		Element element = content.getRootElement();
		ArrayList<List<?>> siblingElements = new ArrayList<List<?>>();
		element = XMLReader.getNextElementAndSiblings(element, siblingElements).element;
		return element.attribute(NodeAttribute.Package).getText();
	}
	
	protected void setEventAndBeforeStateID(int beforeStateID, AndroidEvent beforeEvent) {
		EventAndStateID eventAndBeforeStateID = 
				new EventAndStateID(beforeStateID, beforeEvent);
		tempGUIState.setEventAndBeforeStateID(eventAndBeforeStateID);
	}	
	
	protected boolean isActivityChanged() {
		//TODO UT
		if(oldCurrentActivityName == null) {
			return true;
		}
		currentActivityName = getCurrentActivityName();
		if(oldCurrentActivityName.compareTo(currentActivityName) == 0) {
			return false;
		}
		else {
			return true;
		}
	}
	
	protected String getCurrentActivityName() {
		//TODO UT
		String[] command = {"\"" +
				XMLReader.getConfigurationValue(ConfigurationType.ADB) +
				"\"", "shell", "dumpsys", "window", "windows", "|", "grep",
				"\"mFocusedApp\""};
		String feedBack = null;
		try {
			feedBack = CommandHelper.executeAndGetFeedBack(command);
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
		return feedBack.substring(feedBack.indexOf("/")+1, 
				feedBack.indexOf("}}}"));
	}
	
	/***
	 * 
	 * If root elements (hierarchy) are different,
	 * activity is changed.
	 * 
	 * If elements class (ex. android.widget.FrameLayout) or
	 * index are different,
	 * activity is changed.
	 * 
	 */
	//TODO delete unused
	protected boolean isActivityChanged(TempGUIState oldState,
			TempGUIState newState) {
		if(oldState == null || newState == null) {
			return true;
		}
		boolean changed = false;
		Element oldElement = oldState.contentClone().getRootElement(), 
				newElement = newState.contentClone().getRootElement();
		String oldIndex = null, newIndex = null, oldClass = null,
				newClass = null;
		ArrayList<List<?>> oldSiblingElements = new ArrayList<List<?>>(),
				newSiblingElements = new ArrayList<List<?>>();
		//Check root element changed \|/
		if(!isAttributeEqual(oldElement, newElement)) {
			return true;
		}		
		oldElement = (Element)oldElement.elementIterator().next();
		newElement = (Element)newElement.elementIterator().next();
		while(true) {			
			oldIndex = 
					oldElement.attribute(NodeAttribute.Index).getText();
			oldClass = 
					oldElement.attribute(NodeAttribute.Class).getText();
			newIndex = 
					newElement.attribute(NodeAttribute.Index).getText();
			newClass = 
					newElement.attribute(NodeAttribute.Class).getText();
			if(!oldIndex.equals(newIndex) || !oldClass.equals(newClass)) {
				changed = true;
				break;
			}	
			ElementAndSiblings oldES = 
							new ElementAndSiblings(oldElement, 
							oldSiblingElements),
					newES = new ElementAndSiblings(newElement,
							newSiblingElements);
			String whatNext = hasNextElement(oldES, newES); 
			if(!whatNext.equals(continueToRun)) {
				if(whatNext.equals(hasChanged)) {
					changed = true;
				}
				break;
			}			
			ElementAndSiblings elementAndSibling = null;
			try {
				elementAndSibling = XMLReader.
						getNextElementAndSiblings(oldElement,
								oldSiblingElements);
				oldElement = elementAndSibling.element;
				oldSiblingElements = elementAndSibling.siblingElements;
				elementAndSibling = XMLReader.
						getNextElementAndSiblings(newElement,
								newSiblingElements);
				newElement = elementAndSibling.element;
				newSiblingElements = elementAndSibling.siblingElements;
			}
			catch(ArithmeticException e) {
				//End of XML
				e.printStackTrace();
			}
		}
		return changed;
	}
	
	protected String hasNextElement(ElementAndSiblings oldES,
			ElementAndSiblings newES) {
		Element oldElement = oldES.element, newElement = newES.element;
		ArrayList<List<?>> oldSiblings = oldES.siblingElements,
				newSiblings = newES.siblingElements;
		if((!oldElement.elementIterator().hasNext() &&
				oldSiblings.isEmpty()) && 
						(!newElement.elementIterator().hasNext() &&
								newSiblings.isEmpty())) {
				return notChanged;
			}
		else if((!oldElement.elementIterator().hasNext() &&
				oldSiblings.isEmpty()) || 
						(!newElement.elementIterator().hasNext() &&
								newSiblings.isEmpty())){
			return hasChanged;
		} 
		else{
			return continueToRun;
		}		
	}
	
	protected int getStateIDFromTempGUIStateList(TempGUIState tempState) {
		Document oldContent = null, newContent = null;
		for(int i=0 ; i<tempStateList.size() ; i++) {
			newContent = tempState.contentClone();
			oldContent = tempStateList.get(i).contentClone();
			if(!isStateChanged(oldContent, newContent)) {
				return getStateID(tempStateList.get(i));
			}
		}
		int undefine = -1;
		return undefine;
	}
	
	protected boolean isInOldTempGUIStateList(TempGUIState tempState) {
		boolean result = false;
		Document oldContent = null, newContent = null;
		for(int i=0 ; i<oldTempStateList.size() ; i++) {
			newContent = tempState.contentClone();
			oldContent = oldTempStateList.get(i).contentClone();
			if(!isStateChanged(oldContent, newContent)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	protected boolean isInTempGUIStateList(TempGUIState tempState) {
		boolean result = false;
		Document oldContent = null, newContent = null;
		for(int i=0 ; i<tempStateList.size() ; i++) {
			newContent = tempState.contentClone();
			oldContent = tempStateList.get(i).contentClone();
			if(!isStateChanged(oldContent, newContent)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	protected boolean isInGUIStateList(TempGUIState tempState) {
		boolean result = false;
		Document oldContent = null, newContent = null;
		for(int i=0 ; i<guiStateList.size() ; i++) {
			newContent = tempState.contentClone();
			oldContent = guiStateList.get(i).contentClone();
			if(!isStateChanged(oldContent, newContent)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	protected boolean isStateChanged(Document oldContent, Document newContent) {
		if(oldContent == null || newContent == null) {
			return true;
		}
		Element oldElement = ((Document)oldContent.clone()).getRootElement(),
				newElement = ((Document)newContent.clone()).getRootElement();
		boolean changed = false;
		ArrayList<List<?>> oldSiblingElements = new ArrayList<List<?>>(),
				newSiblingElements = new ArrayList<List<?>>();
		while(true) {		
			if(!isAttributeEqual(oldElement, newElement)) {
				changed = true;
				break;
			}
			ElementAndSiblings oldES = 
						new ElementAndSiblings(oldElement, 
								oldSiblingElements),
					newES = new ElementAndSiblings(newElement,
							newSiblingElements);
			String whatNext = hasNextElement(oldES, newES); 
			if(!whatNext.equals(continueToRun)) {
				if(whatNext.equals(hasChanged)) {
					changed = true;
				}
				break;
			}			
			ElementAndSiblings elementAndSibling = null;
			try {
				elementAndSibling = XMLReader.
						getNextElementAndSiblings(oldElement, oldSiblingElements);
				oldElement = elementAndSibling.element;
				oldSiblingElements = elementAndSibling.siblingElements;
				elementAndSibling = XMLReader.
						getNextElementAndSiblings(newElement, newSiblingElements);
				newElement = elementAndSibling.element;
				newSiblingElements = elementAndSibling.siblingElements;
			}
			catch(ArithmeticException e) {
				//End of XML
				e.printStackTrace();
			}			
		}		
		return changed;
	}
	
	protected boolean isAttributeEqual(Element oldElement, Element newElement) {
		int oldAttrSize = oldElement.attributes().size(), 
				newAttrSize = newElement.attributes().size();
		if(oldAttrSize != newAttrSize) {
			return false;
		}
		String oldValue = null, newValue= null;
		for(int i = 0 ; i<oldAttrSize ; i++) {
			if(((Attribute)oldElement.attributes().get(i))
					.getName().equals(NodeAttribute.Focused)) {
				continue;
			}
			oldValue = ((Node) oldElement.attributes().get(i)).getText();
			newValue = ((Node) newElement.attributes().get(i)).getText();
			if(!newValue.equals(oldValue)) {
				return false;
			}
		}
		return true;		
	}
	
	protected void rollback(
			int targetStateID) {
		rollbacked = true;
		tempGUIState = oldTempGUIState;
		restartApp();
		goToTargetState(targetStateID);
	}
	
	protected void restartApp() {
		stopApp();
		clearAppData();
		String[] command = {"\"" +
				XMLReader.getConfigurationValue(ConfigurationType.ADB) +
				"\"", "shell", "input", "keyevent", "KEYCODE_HOME"};
		try {
			CommandHelper.executeCommand(command);
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		startApp();
	}
	
	protected void stopApp() {
		long waitingTime = 0;
		try {
			waitingTime = TimeHelper.getWaitingTime(ConfigurationType.StopAppWaitingTime);
		} 
		catch(NumberFormatException e) {
			e.printStackTrace();
		}
		String packageName = XMLReader.getConfigurationValue(
						ConfigurationType.PackageName),
				adb = XMLReader.getConfigurationValue(
						ConfigurationType.ADB);
		String[] stopCmd = {"\"" + adb + "\"", "shell", "am", "force-stop", packageName};
		try {
			CommandHelper.executeCommand(stopCmd);
			TimeHelper.sleep(waitingTime);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
	}	
	
	protected void startApp() {
		long waitingTime = 0;
		try {
			waitingTime = TimeHelper.getWaitingTime(ConfigurationType.StartAppWaitingTime);
		} 
		catch(NumberFormatException e) {
			e.printStackTrace();
		}
		String packageName = XMLReader.getConfigurationValue(
						ConfigurationType.PackageName),
				activityName = XMLReader.getConfigurationValue(
						ConfigurationType.LaunchableActivity),
				adb = XMLReader.getConfigurationValue(
						ConfigurationType.ADB);
		String[] startCmd = {"\"" + adb + "\"", "shell", "am", "start", "-n", 
						packageName + "/" +activityName};
		try {
			CommandHelper.executeCommand(startCmd);
			TimeHelper.sleep(waitingTime);
		} 
		catch(InterruptedException e) {
			e.printStackTrace();
		} 
		catch(IOException e) {
			e.printStackTrace();
		}	
	}	
	
	protected void goToTargetState(int targetStateID) {
		ArrayList<Integer> pathOfTargetState = getPathOfTargetID(targetStateID);
		AndroidEvent nextEvent = null;
		for(int i = 1 ; i<pathOfTargetState.size() ; i++) {
			try {
				nextEvent = getEventToNextState(
						pathOfTargetState.get(i-1), pathOfTargetState.get(i));
				turnOffSoftKeyboard();
				eventGenerator.run(nextEvent);
			}
			catch(NullPointerException e) {
			}	
		}
	}
	
	/***
	 * The order is from root state to target state,
	 * so the "arraylist.get(0) = root state"
	 */
 	protected ArrayList<Integer> getPathOfTargetID(int targetStateID) {
		ArrayList<Integer> pathList = new ArrayList<Integer>();
		int index = -1, stateID = -1;
		int parentID = -1;
		pathList.add(targetStateID);
		stateID = targetStateID;		
		while(true) {
			index = getIndexOfGUIStateListByStateID(stateID);
			try {
				parentID = guiStateList.get(index).getParentID();
			}
			catch(IndexOutOfBoundsException e) {
				System.out.println(
						"<<guiStateList index out of range in rollback algorithm!!!>>");
				System.out.println("<<Index= "+index+" >>");
				e.printStackTrace();
			}
			if(parentID >= 0){
				pathList.add(0, parentID);
			}
			if(parentID == -1) {
				break;
			}
			stateID = parentID;
		}
		return pathList;
	}
	
	protected int getIndexOfGUIStateListByStateID(int targetStateID) {
		return getIndexOfGUIStateList(targetStateID, guiStateList);
	}
	
	public static int getIndexOfGUIStateList(int targetStateID, ArrayList<GUIState> guiStateList) {
		int index = -1;
		for(int i = 0 ; i<guiStateList.size() ; i++) {
			if(guiStateList.get(i).getID() == targetStateID) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	protected int getStateIDByContent(Document stateContent) {
		int stateID = -1;
		for(int i = 0 ; i<guiStateList.size() ; i++) {
			if(!isStateChanged(guiStateList.get(i).contentClone(), stateContent)) {
				stateID = guiStateList.get(i).getID();
				break;
			}
		}
		return stateID;
	}
	
	protected AndroidEvent getEventToNextState(int currentStateID,
			int nextStateID) throws NullPointerException {
		AndroidEvent tempEvent = null;
		int currentIndex = getIndexOfGUIStateListByStateID(currentStateID),
			eventListSize = 0, 
			tempStateID = -1;
		ArrayList<EventAndStateID> eventAndNextStateList = 
				guiStateList.get(currentIndex).getEventAndNextStateList();
		eventListSize = eventAndNextStateList.size();
		for(int i = 0 ; i<eventListSize ; i++) {
			tempStateID = eventAndNextStateList.get(i).stateID();
			if(tempStateID == nextStateID) {
				tempEvent = eventAndNextStateList.get(i).event();
				break;
			}
		}
		if(tempEvent == null) {
			throw new NullPointerException();
		}
		return tempEvent;
	}	
	
	public String xmlReaderTimestamp() {
		return xmlReaderTimestamp;
	}
	
	protected void killADBServer() {
		String adb = XMLReader.getConfigurationValue(ConfigurationType.ADB);
		String[] command = {"\"" + adb + "\"", "kill-server"};
		try {
			CommandHelper.executeCommand(command);
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void clearAppData() {
		String adb = XMLReader.getConfigurationValue(ConfigurationType.ADB),
				packageName = XMLReader.getConfigurationValue(ConfigurationType.PackageName);
		String[] command = {"\"" + adb + "\"", "shell", "pm", "clear" ,packageName};
		try {
			CommandHelper.executeCommand(command);
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	protected int getStateID(TempGUIState tempState) {
		String name = tempState.contentClone().getName();
		name = name.substring(name.indexOf(xmlReaderTimestamp) + xmlReaderTimestamp.length()+1, name.indexOf(".xml"));
		return Integer.parseInt(name);
	}
}
