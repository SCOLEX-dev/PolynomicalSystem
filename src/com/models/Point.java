package com.models;

public class Point {
    public final double t;
    public final double p;

    public Point(double t, double p) {
        this.t = t;
        this.p = p;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Point point = (Point) obj;
        return Math.abs(t - point.t) < 0.000001 &&
                Math.abs(p - point.p) < 0.000001;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(t) * 31 + Double.hashCode(p);
    }

    @Override
    public String toString() {
        return String.format("Point{t=%.2f, p=%.2f}", t, p);
    }
}