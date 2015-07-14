package edu.ntut.selab;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import edu.ntut.selab.data.ConfigurationType;
import edu.ntut.selab.data.EventAndStateID;
import edu.ntut.selab.data.GUIState;
import edu.ntut.selab.data.StateResult;
import edu.ntut.selab.event.AndroidEvent;

public class ReportGenerator {
	protected String xmlReaderTimestamp = null,
			imagePath = null;
	ArrayList<GUIState> guiStateList = null;
	//ArrayList<Integer> stateCountList = null;
	ArrayList<String> activityNameList = null,
			typeList = null;
	public ReportGenerator(String xmlReaderTimestamp, StateResult result) {
		this.xmlReaderTimestamp = xmlReaderTimestamp;
		this.guiStateList = result.guiStateList;
		this.activityNameList = result.activityNameList;
		this.typeList = getTypeOfActivityNameList();
		copyInitialAndFinalNodePNG();
		generateReport();
	}
	
	protected void generateReport() {
		File dotFile = new File("gui_pages/" + xmlReaderTimestamp + "/result.dot");
		PrintWriter writer = null;
		try {
			 writer = new PrintWriter(dotFile, "UTF-8");
		} 
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		writer.println("digraph g {");
		writer.println("graph [color=red];");
		imagePath=dotFile.getParentFile().getAbsolutePath();
		writer.println("imagepath = \"" +	imagePath +	"\";");
		createrActivityBounds(writer);
		createGraph(writer);
		writer.println("}");
		writer.close();
		createResultFile(dotFile);
		//New Report 
		createActivityDOTFile();
		createActivitySVGFile();
		createHierarchyResultDOTFile();
		createHierarchyResultSVGFile();
	}	
	
	protected void copyInitialAndFinalNodePNG() {
		Path initialNodeSourcePath = new File("documents/initial_node.png").toPath(),
				finalNodePath = new File("documents/final_node.png").toPath(),
				initialNodeTargetPath = new File(
						"gui_pages/" + xmlReaderTimestamp + "/" + 
						StateIDHelper.InitialNodeID + ".png").toPath(),
				finalNodeTargetPath = new File(
						"gui_pages/" + xmlReaderTimestamp + "/" +
						StateIDHelper.FinalNodeID + ".png").toPath();
		CopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;
		try {
			Files.copy(initialNodeSourcePath, initialNodeTargetPath, copyOption);
			Files.copy(finalNodePath, finalNodeTargetPath, copyOption);
		}
		catch(IOException e) {
			e.printStackTrace();
		}		
	}
	
	protected ArrayList<String> getTypeOfActivityNameList() {
		ArrayList<String> typeList = new ArrayList<String>();
		for(String activityName : activityNameList) {
			int index = getIndexOfTypeList(typeList, activityName);
			if(index < 0) {
				typeList.add(activityName);
			}
		}
		return typeList;
	}
	
	protected int getIndexOfTypeList(ArrayList<String> typeList, String activityName) {
		for(int i = 0 ; i<typeList.size() ; i++) {
			if(typeList.get(i).compareTo(activityName) == 0) {
				return i;
			}
		}
		int notFound = -1;
		return notFound;
	}
	
	protected void createActivityDOTFile() {
		File stateDOTFile = null;
		PrintWriter writer = null;
		
		for(int index = 0 ; index<typeList.size() ; index++) {
			int id = getFirstIDByActivityName(typeList.get(index)); 
			if(id != StateIDHelper.FinalNodeID) {
				stateDOTFile = new File(
						"gui_pages/" + xmlReaderTimestamp + 
						"/activity_" + index + ".dot");
				try {
					writer = new PrintWriter(stateDOTFile, "UTF-8");
				} 
				catch(FileNotFoundException e) {
					e.printStackTrace();
				}
				catch(UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				writer.println("digraph activity_" + index + "{");
				writer.println("imagepath=\"" +	imagePath +	"\";");
				for(int i = 0 ; i<guiStateList.size() ; i++) {
					id = guiStateList.get(i).getID();
					if(id != StateIDHelper.FinalNodeID && 
							!isOutOfActivityBound(id, typeList.get(index))) {
						int incoming = getIncomingOfState(id),
								outgoing = getOutgoingOfState(i),
								loop = getLoopOfState(i);
						writer.println("state_" + id + 
								"[label=<<table border=\"0\" cellborder=\"0\" " +
								"cellspacing=\"0\" cellpadding=\"0\">" +
								"<tr><td>state_" + id + "</td></tr>" + 
								"<tr><td>incoming:" + incoming + "</td></tr>" +
								"<tr><td>outgoing:" + outgoing + "</td></tr>" +
								"<tr><td>loop:" + loop + "</td></tr>" +
								"<tr><td width=\"130px\" height=\"230px\" " +
								"fixedsize=\"true\"><img src=\"" + id + ".png\"/>" +
								"</td></tr></table>>,shape=box,margin=0,URL=\"" +
								id + ".png\"];");
					}
				}
				createStateConnection(writer, index);
				writer.println("}");
				writer.close();
			}			
			
		}
		/*
		 * int activityCount = 0;
		for(int indexOfList = 0 ; indexOfList<guiStateList.size() ; indexOfList++) {
			int id = guiStateList.get(indexOfList).getID(); 
			if(indexOfList>=stateCount) {
				if(id != StateIDHelper.FinalNodeID) {
					stateDOTFile = new File(
							"gui_pages/" + xmlReaderTimestamp + 
							"/activity_" + activityCount + ".dot");
					try {
						 writer = new PrintWriter(stateDOTFile, "UTF-8");
					} 
					catch(FileNotFoundException e) {
						e.printStackTrace();
					}
					catch(UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					writer.println("digraph activity_" + activityCount + "{");
					writer.println("imagepath=\"" +	imagePath +	"\";");
				}
				oldStateCount = stateCount;
				stateCount += stateCountList.get(activityCount++);			
			}
			if(id != StateIDHelper.FinalNodeID) {
				int incoming = getIncomingOfState(id),
						outgoing = getOutgoingOfState(indexOfList),
						loop = getLoopOfState(indexOfList);
				writer.println("state_" + id + 
						"[label=<<table border=\"0\" cellborder=\"0\" " +
						"cellspacing=\"0\" cellpadding=\"0\">" +
						"<tr><td>state_" + id + "</td></tr>" + 
						"<tr><td>incoming:" + incoming + "</td></tr>" +
						"<tr><td>outgoing:" + outgoing + "</td></tr>" +
						"<tr><td>loop:" + loop + "</td></tr>" +
						"<tr><td width=\"130px\" height=\"230px\" " +
						"fixedsize=\"true\"><img src=\"" + id + ".png\"/>" +
						"</td></tr></table>>,shape=box,margin=0,URL=\"" +
						id + ".png\"];");
			}
			if(indexOfList == stateCount-1) {
				createStateConnection(writer, oldStateCount, stateCount);
				writer.println("}");
				writer.close();
			}
		}
		*/
	}
	
	protected int getFirstIDByActivityName(String activityName) {
		for(int i = 0 ; i<guiStateList.size() ; i++) {
			if(activityName.compareTo(activityNameList.get(i)) == 0) {
				return guiStateList.get(i).getID();
			}
		}
		int notFound = -1;
		return notFound;
	}
	
	/***
	 * Get the incoming transactions of stateID
	 */
	protected int getIncomingOfState(int stateID) {
		int incoming = 0;
		for(int indexOfList = 0 ; indexOfList<guiStateList.size() ; indexOfList++) {
			int eventListSize = 
					guiStateList.get(indexOfList).getEventAndNextStateList().size();
			ArrayList<EventAndStateID> eventAndNextStateList =
					guiStateList.get(indexOfList).getEventAndNextStateList();
			for(int indexOfEventList = 0 ; indexOfEventList<eventListSize ; indexOfEventList++) {
				int nextStateID = eventAndNextStateList.get(indexOfEventList).stateID();
				if(stateID == nextStateID) {
					incoming++;
				}
			}
		}
		return incoming;
	}
	
	/***
	 * Get the outgoing transactions of index of guiStateList
	 */
	protected int getOutgoingOfState(int indexOfStateList) {
		return guiStateList.get(indexOfStateList).
				getEventAndNextStateList().size();
	}
	
	/***
	 * Get the loop transactions of index of guiStateList
	 */
	protected int getLoopOfState(int indexOfStateList) {
		int loop = 0,
				eventListSize = 
						guiStateList.get(indexOfStateList).
						getEventAndNextStateList().size(),
				stateID = guiStateList.get(indexOfStateList).getID();
		ArrayList<EventAndStateID> eventAndNextStateList =
				guiStateList.get(indexOfStateList).getEventAndNextStateList();
		for(int indexOfEventList = 0 ; indexOfEventList<eventListSize ; indexOfEventList++) {
			int nextStateID = eventAndNextStateList.get(indexOfEventList).stateID();
			if(stateID == nextStateID) {
				loop++;
			}
		}		
		return loop;
	}
	
	protected void createStateConnection(PrintWriter writer, int indexOfTypeList) {
		for(int i = 0 ; i<guiStateList.size() ; i++) {
			if(activityNameList.get(i).compareTo(
					typeList.get(indexOfTypeList)) == 0) {
				int nextStateSize = 
						guiStateList.get(i).getEventAndNextStateList().size(),
						currentID = guiStateList.get(i).getID();
				for(int j = 0 ; j<nextStateSize ; j++) {
					EventAndStateID eventAndNextState = 
							guiStateList.get(i).getEventAndNextStateList().get(j);
					int nextID = eventAndNextState.stateID();
					String label =  eventAndNextState.event().getReportLabel();
					if(nextID == StateIDHelper.FinalNodeID) {
						writer.println("final [label=\"\"," +
								"shape=plaintext,width=0.7,height=0.7," +
								"image=\"-2.png\",fixedsize=true];");
						writer.print("state_" + currentID +  " -> final");
						writer.println(" [label = \"   " + label + "   \"," +
								"fontcolor = red,color = red];");
					}
					else if(!isOutOfActivityBound(nextID, typeList.get(indexOfTypeList))) {
						writer.print("state_" + currentID + " -> state_" + nextID);					
						writer.println(" [label = \"   " + label + "   \"];");
					}
					else {
						writer.println("activity_" + getActivityCount(nextID) +
								" [fontcolor = red,color = red];");
						writer.print("state_" + currentID +  " -> activity_" + 
								getActivityCount(nextID));
						writer.println(" [label = \"   " + label + "   \"," +
								"fontcolor = red,color = red];");
					}
				}
			}			
		}
		createStateConnectionFromAnotherActivity(writer, indexOfTypeList);
			
			/*
			int nextStateSize = 
					guiStateList.get(i).getEventAndNextStateList().size(),
				currentID = guiStateList.get(i).getID();
			for(int j = 0 ; j<nextStateSize ; j++) {
				EventAndStateID eventAndNextState = 
						guiStateList.get(i).getEventAndNextStateList().get(j);
				int nextID = eventAndNextState.stateID();
				String label =  eventAndNextState.event().getReportLabel();
				if(nextID == StateIDHelper.FinalNodeID) {
					writer.println("final [label=\"\"," +
							"shape=plaintext,width=0.7,height=0.7," +
							"image=\"-2.png\",fixedsize=true];");
					writer.print("state_" + currentID +  " -> final");
					writer.println(" [label = \"   " + label + "   \"," +
							"fontcolor = red,color = red];");
				}
				else if(!isOutOfActivityBound(nextID, oldStateCount, stateCount)) {
					writer.print("state_" + currentID + " -> state_" + nextID);					
					writer.println(" [label = \"   " + label + "   \"];");
				}
				else {
					writer.println("activity_" + getActivityCount(nextID) +
							" [fontcolor = red,color = red];");
					writer.print("state_" + currentID +  " -> activity_" + 
							getActivityCount(nextID));
					writer.println(" [label = \"   " + label + "   \"," +
							"fontcolor = red,color = red];");
				}
			}
			
		}
		//createStateConnectionFromAnotherActivity(writer, oldStateCount, stateCount);
		*/
	}
	
	protected int getIndexOfGuiStateList(int id) {
		for(int index = 0 ; index<guiStateList.size() ; index++) {
			if(id == guiStateList.get(index).getID()) {
				return index;
			}
		}
		int notFound = -1;
		return notFound;
	}
	
	/***
	 * Create another activity connection incoming transitions 
	 */
	
	protected void createStateConnectionFromAnotherActivity(PrintWriter writer, int indexOfTypeList) {
		int stateID = 0, activityCount = 0, nextStateID = 0;
		ArrayList<EventAndStateID> eventAndNextStateList = null;
		for(int i = 0 ; i<guiStateList.size() ; i++) {
			stateID = guiStateList.get(i).getID();			
			if(isOutOfActivityBound(stateID, typeList.get(indexOfTypeList))) {
				activityCount = getActivityCount(stateID);
				eventAndNextStateList = 
						guiStateList.get(i).getEventAndNextStateList();
				for(int j = 0 ; j<eventAndNextStateList.size() ; j++) {
					nextStateID = eventAndNextStateList.get(j).stateID();
					if(!isOutOfActivityBound(nextStateID, typeList.get(indexOfTypeList))) {
						String label = eventAndNextStateList.get(j).event().getReportLabel();
						writeStateConnectionFormAnotherAcitvity(writer, activityCount, nextStateID, label);
					}
				}
			}
		}
		/*
		for(int i = stateCount ; i<guiStateList.size() ; i++) {
			stateID = guiStateList.get(i).getID();
			activityCount = getActivityCount(stateID);
			eventAndNextStateList = 
					guiStateList.get(i).getEventAndNextStateList();
			for(int j = 0 ; j<eventAndNextStateList.size() ; j++) {
				nextStateID = eventAndNextStateList.get(j).stateID();
				if(!isOutOfActivityBound(nextStateID, oldStateCount, stateCount)) {
					String label = eventAndNextStateList.get(j).event().getReportLabel();
					writeStateConnectionFormAnotherAcitvity(writer, activityCount, nextStateID, label);
				}
			}
		}
		*/
	}
	
	
	protected void writeStateConnectionFormAnotherAcitvity(
			PrintWriter writer, int activityCount, int nextStateID, String label) {
		writer.println("activity_" + activityCount +
				" [fontcolor = red,color = red];");
		if(nextStateID == StateIDHelper.FinalNodeID) {
			writer.println("final [label=\"\"," +
					"shape=plaintext,width=0.7,height=0.7," +
					"image=\"-2.png\",fixedsize=true];");
			writer.print("activity_" + activityCount +  " -> final");
		}
		else {
			writer.print("activity_" + activityCount +  " -> state_" + 
					nextStateID);
		}
		writer.println(" [label = \"   " + label + "   \"," +
				"fontcolor = red,color = red];");
	}
	
	
	protected boolean isOutOfActivityBound(int stateID, String activityName) {
		String nextActivityName = activityNameList.get(getIndexOfGuiStateList(stateID));
		if(nextActivityName.compareTo(activityName) == 0) {
			return false;
		}
		else {
			return true;
		}
		/*
		
		for(int i = oldStateCount ; i<stateCount ; i++) {
			if(stateID == guiStateList.get(i).getID()) {
				return false;
			}
		}
		return true;
		*/
	}
	
	protected void createActivitySVGFile() {
		int activityCount = typeList.size();
		for(int i = 0 ; i<activityCount ; i++) {
			File dotFile = new File(imagePath + "/activity_" + i + ".dot");
			createSVGFile(dotFile);
		}		
	}
	
	protected void createSVGFile(File dotFile) {
		String dot = "\"" + 
				XMLReader.getConfigurationValue(ConfigurationType.Graphviz) +
				"\"";
		String[] cmd = {dot, "-Tsvg", "\"" + dotFile.getPath() +
				"\"", "-o", "\"" +
				dotFile.getPath().replace(".dot", ".svg") + "\""}; 
		try {
			CommandHelper.executeCommand(cmd);
		} 
		catch(IOException e) {
			LoggerHelper.warning("File not found");
			e.printStackTrace();
		}
	}
	
	protected void createHierarchyResultDOTFile() {
		//int activityCount = typeList.size();
		File dotFile = new File(imagePath + "/hierarchy_result.dot");
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(dotFile, "UTF-8");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		writer.println("digraph result {");
		writer.println("rankdir=LR;");
		writer.println("imagepath=\"" +	imagePath +	"\";");
		writer.println("initial [label=\"\",shape=plaintext," +
						"fixedsize=true,width=0.7,height=0.7,image=\"" +
						StateIDHelper.InitialNodeID + ".png\"];");
		int id = 0;//, stateCount = 0;
		for(int i = 0 ; i<typeList.size() ; i++) {
			//for(int j = 0 ; j<guiStateList.size() ; j++) {
				id = getFirstIDByActivityName(typeList.get(i));
				if(id == StateIDHelper.FinalNodeID) {
					writer.println("final [label=\"\"," +
							"shape=plaintext,width=0.7,height=0.7," +
							"image=\"-2.png\",fixedsize=true];");
				}
				else {
					int incoming = getIncomingOfActivity(i),
							outgoing = getOutgoingOfActivity(i);
					writer.println("activity_" + i + 
							"[label=<<table border=\"0\" cellborder=\"0\" " +
							"cellspacing=\"0\" cellpadding=\"0\">" +
							"<tr><td>activity_" + i + "</td></tr>" +
							"<tr><td>incoming:" + incoming + "</td></tr>" +
							"<tr><td>outgoing:" + outgoing + "</td></tr>" +
							"<tr><td width=\"130px\" height=\"230px\"" +
							" fixedsize=\"true\"><img src=\"" + id + ".png\"/>" +
							"</td></tr></table>>,shape=box,margin=0,URL=\"activity_" +
							i + ".svg\"];");
				}		
				
			//}
			/*
			if(id == StateIDHelper.FinalNodeID) {
				writer.println("final [label=\"\"," +
						"shape=plaintext,width=0.7,height=0.7," +
						"image=\"-2.png\",fixedsize=true];");
			}
			else {
				int incoming = getIncomingOfActivity(i),
						outgoing = getOutgoingOfActivity(i);
				writer.println("activity_" + i + 
						"[label=<<table border=\"0\" cellborder=\"0\" " +
						"cellspacing=\"0\" cellpadding=\"0\">" +
						"<tr><td>activity_" + i + "</td></tr>" +
						"<tr><td>incoming:" + incoming + "</td></tr>" +
						"<tr><td>outgoing:" + outgoing + "</td></tr>" +
						"<tr><td width=\"130px\" height=\"230px\"" +
						" fixedsize=\"true\"><img src=\"" + id + ".png\"/>" +
						"</td></tr></table>>,shape=box,margin=0,URL=\"activity_" +
						i + ".svg\"];");
			}		
			
			stateCount += stateCountList.get(i);
			if(i != activityCount-1) {
				id = guiStateList.get(stateCount).getID();
			}
			*/
			
		}		
		createActivityConnection(writer);
		writer.println("}");
		writer.close();
	}
	
	/***
	 * Get the incoming transations of the activity
	 */
	protected int getIncomingOfActivity(int activityCount) {
		int incoming = 0;
		for(int indexOfList = 0 ; indexOfList<guiStateList.size() ; indexOfList++) {
			int eventListSize = 
					guiStateList.get(indexOfList).getEventAndNextStateList().size(),
				currentStateID = guiStateList.get(indexOfList).getID(),
				currentActivityCount = getActivityCount(currentStateID);
			ArrayList<EventAndStateID> eventAndNextStateList =
					guiStateList.get(indexOfList).getEventAndNextStateList();
			for(int indexOfEventList = 0 ; indexOfEventList<eventListSize ; indexOfEventList++) {
				int nextStateID = eventAndNextStateList.get(indexOfEventList).stateID(),
						nextActivityCount = getActivityCount(nextStateID);
				if(activityCount == nextActivityCount &&
						currentActivityCount != nextActivityCount) {
					incoming++;
				}
			}
		}
		return incoming;
	}
	
	/***
	 * Get the outgoing transations of the activity
	 */
	protected int getOutgoingOfActivity(int activityCount) {
		int outgoing = 0;
		for(int i = 0 ; i<guiStateList.size() ; i++) {
			String activityName = activityNameList.get(i);
			if(activityName.compareTo(typeList.get(activityCount)) == 0) {
				int eventListSize = 
						guiStateList.get(i).getEventAndNextStateList().size(),
					currentStateID = guiStateList.get(i).getID(),
					currentActivityCount = getActivityCount(currentStateID);
				ArrayList<EventAndStateID> eventAndNextStateList =
						guiStateList.get(i).getEventAndNextStateList();
				for(int indexOfEventList = 0 ; indexOfEventList<eventListSize ; indexOfEventList++) {
					int nextStateID = eventAndNextStateList.get(indexOfEventList).stateID(),
							nextActivityCount = getActivityCount(nextStateID);
					if(activityCount != nextActivityCount &&
							currentActivityCount != nextActivityCount) {
						outgoing++;
					}				
				}
			}
		}
		/*
		int outgoing = 0, stateCount = stateCountList.get(0), indexOfList = 0;
		for(int index = 1 ; index<=activityCount ; index++) {
			stateCount += stateCountList.get(index);
			indexOfList += stateCountList.get(index-1);
		}
		for(; indexOfList<stateCount ; indexOfList++) {
			int eventListSize = 
					guiStateList.get(indexOfList).getEventAndNextStateList().size(),
				currentStateID = guiStateList.get(indexOfList).getID(),
				currentActivityCount = getActivityCount(currentStateID);
			ArrayList<EventAndStateID> eventAndNextStateList =
					guiStateList.get(indexOfList).getEventAndNextStateList();
			for(int indexOfEventList = 0 ; indexOfEventList<eventListSize ; indexOfEventList++) {
				int nextStateID = eventAndNextStateList.get(indexOfEventList).stateID(),
						nextActivityCount = getActivityCount(nextStateID);
				if(activityCount != nextActivityCount &&
						currentActivityCount != nextActivityCount) {
					outgoing++;
				}
			}
		}
		*/
		return outgoing;
	}
	
	protected void createActivityConnection(PrintWriter writer) {
		//int stateCount = 0, activityCount = -1;
		writer.println("initial -> activity_0;");
		for(int i = 0 ; i<guiStateList.size() ; i++) {
			/*
			if(i>=stateCount) {
				stateCount += stateCountList.get(++activityCount);
			}
			*/
			int stateID = guiStateList.get(i).getID(),
					activityCount = getActivityCount(stateID);
			ArrayList<EventAndStateID> eventAndNextStateList = 
					guiStateList.get(i).getEventAndNextStateList();			
			for(int j = 0 ; j<eventAndNextStateList.size() ; j++) {
				int nextStateID = eventAndNextStateList.get(j).stateID(),
						nextActivityCount = getActivityCount(nextStateID); 
				if(activityCount != nextActivityCount) {
					writer.print("activity_" + activityCount + " -> ");		
					String label = eventAndNextStateList.get(j).event().getReportLabel();
					if(nextStateID != StateIDHelper.FinalNodeID) {
						writer.print("activity_" + nextActivityCount);
						writer.print(" [label = \"   <a" + activityCount +
								".s" + stateID + "," + label + ",a" + nextActivityCount +
								".s" + nextStateID + ">   \"]");
					}
					else {
						writer.print("final");
						writer.print(" [label = \"   <a" + activityCount +
								".s" + stateID + "," + label + ",final>   \"]");
					}
					writer.println(";");
				}
			}
		}		
	}
	
	protected int getActivityCount(int stateID) {
		int index = getIndexOfGuiStateList(stateID);
		String targetActivityName = activityNameList.get(index);
		for(int i = 0 ; i<typeList.size() ; i++) {
			if(typeList.get(i).compareTo(targetActivityName) == 0) {
				return i;
			}
		}
		int notFound = -1;
		return notFound;
		/*
		int stateCount = 0, activityCount = -1;
		for(int i = 0 ; i<guiStateList.size() ; i++) {
			if(i>=stateCount) {
				stateCount += stateCountList.get(++activityCount);
			}
			if(guiStateList.get(i).getID() == stateID) {
				break;
			}
		}
		return activityCount;
		*/
	}
	
	protected void createHierarchyResultSVGFile() {
		File dotFile = new File(imagePath + "/hierarchy_result.dot");
		createSVGFile(dotFile);
	}
	
	protected void createrActivityBounds(PrintWriter writer) {		
		/*
		writer.println("subgraph cluster_0 {");
		writer.println("fontcolor = red");
		writer.println("label = \"  activity_0  \"");
		*/
			
		for(int index = 0 ; index<typeList.size() ; index++) {
			/*
			writer.println("subgraph cluster_" +
					(index) + "{");
			writer.println("fontcolor = red;");
			*/
			writer.println("label = \"   activity_" + 
					index + "   \";");
			for(int i = 0 ; i<guiStateList.size() ; i++) {
				if(activityNameList.get(i).compareTo(
						typeList.get(index)) == 0)  {
					int id = guiStateList.get(i).getID();
					if(id != StateIDHelper.FinalNodeID) {
						writer.print("state_" + id);
					}
					else {
						writer.print("state_final");
					}
					writer.print(" [label=\"\",shape=box,fixedsize=true");
					writer.print(",width=1.8,height=3.2,image=\"" + id);
					writer.println(".png\",URL=\"" + id + ".png\"];");
				}
			}
			//writer.println("}");
		}
		/*
		 * int activityCount = 0;	
		for(int i = 0 ; i<guiStateList.size() ; i++) {
			if(i>=stateCount) {
				writer.println("}");
				writer.println("subgraph cluster_" +
						(++activityCount) + "{");
				writer.println("fontcolor = red");
				writer.println("label = \"   activity_" + 
						activityCount + "   \"");
				stateCount += stateCountList.get(activityCount);
			}
			int id = guiStateList.get(i).getID();
			writer.print(id + " [label=\"\",shape=box,fixedsize=true");
			writer.print(",width=1.8,height=3.2,image=\"" + id);
			writer.println(".png\",URL=\"" + id + ".png\"];");
		}
		writer.println("}");
		*/
	}
	
	protected void createGraph(PrintWriter writer) {
		for(int i = 0 ; i<guiStateList.size() ; i++) {
			int nextStateSize = guiStateList.get(i).getEventAndNextStateList().size();
			for(int j = 0 ; j<nextStateSize ; j++) {
				EventAndStateID eventAndNextState = 
						guiStateList.get(i).getEventAndNextStateList().get(j);
				writer.print("state_" + guiStateList.get(i).getID() + " -> ");
				if(eventAndNextState.stateID() != StateIDHelper.FinalNodeID) {
					writer.print("state_" + eventAndNextState.stateID());
				}
				else {
					writer.print("state_final");
				}
				AndroidEvent event =  eventAndNextState.event();
				writer.println(" [label = \"   " + event.getReportLabel() + "   \"];");
			}
		}
	}
	
	protected void createResultFile(File dotFile) {
		String dot = "\"" + 
				XMLReader.getConfigurationValue(ConfigurationType.Graphviz) +
				"\"";
		String[] cmd = {dot, "-Tsvg", "\"" + dotFile.getPath().toString() +
				"\"", "-o", "\"gui_pages/" + xmlReaderTimestamp + "/result.svg\""}; 
		try {
			CommandHelper.executeCommand(cmd);
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
