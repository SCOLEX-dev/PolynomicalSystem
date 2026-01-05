package test.com.project.unit.models;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import com.models.Point;

public class PointTest {

    @Test
    void testPointCreation() {
        double t = 10.5;
        double p = 20.3;
        Point point = new Point(t, p);

        assertEquals(t, point.t, 0.000001);
        assertEquals(p, point.p, 0.000001);
    }

    @Test
    void testPointWithNegativeValues() {
        Point point = new Point(-10.5, -20.3);

        assertEquals(-10.5, point.t, 0.000001);
        assertEquals(-20.3, point.p, 0.000001);
    }

    @Test
    void testPointWithZeroValues() {
        Point point = new Point(0, 0);

        assertEquals(0, point.t, 0.000001);
        assertEquals(0, point.p, 0.000001);
    }

    @Test
    void testPointPrecision() {
        Point point = new Point(1.23456789, 9.87654321);

        assertEquals(1.23456789, point.t, 0.00000001);
        assertEquals(9.87654321, point.p, 0.00000001);
    }

    @Test
    void testPointEqualsAndHashCode() {
        Point point1 = new Point(10.0, 20.0);
        Point point2 = new Point(10.0, 20.0);
        Point point3 = new Point(10.1, 20.0);
        Point point4 = new Point(10.0, 20.1);

        // Проверяем equals
        assertEquals(point1, point2);
        assertNotEquals(point1, point3);
        assertNotEquals(point1, point4);
        assertNotEquals(point1, null);
        assertNotEquals(point1, new Object());

        // Проверяем hashCode
        assertEquals(point1.hashCode(), point2.hashCode());
        assertNotEquals(point1.hashCode(), point3.hashCode());
    }

    @Test
    void testPointToString() {
        Point point = new Point(10.5, 20.3);
        String str = point.toString();

        assertNotNull(str);
        assertTrue(str.contains("t=10.50"));
        assertTrue(str.contains("p=20.30"));
    }
}