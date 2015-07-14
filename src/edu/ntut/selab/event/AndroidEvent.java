package edu.ntut.selab.event;

/***
 * 
 * @author Roger
 * 
 * A kind of data structure, 
 * store the events of Android.
 */
public interface AndroidEvent {
	public void execute();
	public String getReportLabel();
	public AndroidEvent clone();
}