package edu.ntut.selab.data;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

/***
 * 
 * @author Roger
 * Only for XMLReader.getNextElementAndSiblings(
 * 		Element element, ArrayList<List<?>> siblingElements) 
 */
public class ElementAndSiblings {
	public Element element = null;
	public ArrayList<List<?>> siblingElements = new ArrayList<List<?>>();
	
	public ElementAndSiblings(Element element, ArrayList<List<?>> siblingElements) {
		this.element = element;
		this.siblingElements = siblingElements;
	}
}
