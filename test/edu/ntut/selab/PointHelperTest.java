package edu.ntut.selab;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ntut.selab.data.Point;

public class PointHelperTest {

	@Test
	public void testGetAverage() {
		assertEquals(4, PointHelper.getAverage(3, 5));
	}

	@Test
	public void testGetCenterPoint() {
		Point centerPoint = new Point(4, 6),
				upperLeftPoint = new Point(1, 3),
				lowerRightPoint = new Point(7, 9);
		assertEquals(centerPoint.x(), PointHelper.getCenterPoint(upperLeftPoint, lowerRightPoint).x());
		assertEquals(centerPoint.y(), PointHelper.getCenterPoint(upperLeftPoint, lowerRightPoint).y());
	}

	@Test
	public void testGetLeftPoint() {
		Point leftPoint = new Point(2, 6),
				upperLeftPoint = new Point(1, 3),
				lowerRightPoint = new Point(7, 9);
		assertEquals(leftPoint.x(), PointHelper.getLeftPoint(upperLeftPoint, lowerRightPoint).x());
		assertEquals(leftPoint.y(), PointHelper.getLeftPoint(upperLeftPoint, lowerRightPoint).y());
	}

	@Test
	public void testGetRightPoint() {
		Point rightPoint = new Point(6, 6),
				upperLeftPoint = new Point(1, 3),
				lowerRightPoint = new Point(7, 9);
		assertEquals(rightPoint.x(), PointHelper.getRightPoint(upperLeftPoint, lowerRightPoint).x());
		assertEquals(rightPoint.y(), PointHelper.getRightPoint(upperLeftPoint, lowerRightPoint).y());
	}

	@Test
	public void testGetUpPoint() {
		Point upPoint = new Point(4, 4),
				upperLeftPoint = new Point(1, 3),
				lowerRightPoint = new Point(7, 9);
		assertEquals(upPoint.x(), PointHelper.getUpPoint(upperLeftPoint, lowerRightPoint).x());
		assertEquals(upPoint.y(), PointHelper.getUpPoint(upperLeftPoint, lowerRightPoint).y());
	}

	@Test
	public void testGetDownPoint() {
		Point downPoint = new Point(4, 8),
				upperLeftPoint = new Point(1, 3),
				lowerRightPoint = new Point(7, 9);
		assertEquals(downPoint.x(), PointHelper.getDownPoint(upperLeftPoint, lowerRightPoint).x());
		assertEquals(downPoint.y(), PointHelper.getDownPoint(upperLeftPoint, lowerRightPoint).y());
	}

}
