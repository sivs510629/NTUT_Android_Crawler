package edu.ntut.selab;

import edu.ntut.selab.data.Point;

public class PointHelper {
	//TODO UT
	public static int getAverage(int a, int b) {
		return (a+b)/2;
	}
	
	public static Point getCenterPoint(Point a, Point b) {
		Integer x = null, y = null;
		x = getAverage(a.x(), b.x());
		y = getAverage(a.y(), b.y());
		return new Point(x, y);
	}
	
	public static Point getLeftPoint(Point upperLeftPoint, Point lowerRightPoint) {
		final int offset = 1;
		Integer leftX = upperLeftPoint.x()+offset,
				leftY = PointHelper.getAverage(
						lowerRightPoint.y(),
						upperLeftPoint.y());
		return new Point(leftX, leftY);		
	}
	
	public static Point getRightPoint(Point upperLeftPoint, Point lowerRightPoint) {
		final int offset = 1;
		Integer rightX = lowerRightPoint.x()-offset,
				rightY = PointHelper.getAverage(
						lowerRightPoint.y(),
						upperLeftPoint.y());
		return new Point(rightX, rightY);
	}
	
	public static Point getUpPoint(Point upperLeftPoint, Point lowerRightPoint) {
		final int offset = 1;
		Integer upX = PointHelper.getAverage(
						lowerRightPoint.x(),
						upperLeftPoint.x()),
				upY = upperLeftPoint.y()+offset;
		return new Point(upX, upY);
	}
	
	public static Point getDownPoint(Point upperLeftPoint, Point lowerRightPoint) {
		final int offset = 1;
		Integer downX = PointHelper.getAverage(
						lowerRightPoint.x(),
						upperLeftPoint.x()),
				downY = lowerRightPoint.y()-offset;
		return new Point(downX, downY);
	}
}
