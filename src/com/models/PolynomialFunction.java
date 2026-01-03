package com.models;

import java.io.Serializable;

public class PolynomialFunction implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final double[] coefficients;

    public PolynomialFunction(String name, double[] coefficients) {
        if (coefficients == null || coefficients.length != 5) {
            throw new IllegalArgumentException("Коэффициентов должно быть ровно 5");
        }
        this.name = name;
        this.coefficients = coefficients.clone();
    }

    public double calculate(double x) {
        double result = coefficients[0];
        double power = x;

        for (int i = 1; i < coefficients.length; i++) {
            result += coefficients[i] * power;
            power *= x;
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public double[] getCoefficients() {
        return coefficients.clone();
    }

    public String getCoefficientsInfo() {
        return String.format(
                "y = %.8f + %.8f*x + %.8f*x² + %.8f*x³ + %.8f*x⁴\n" +
                        "a = %.8f\nb = %.8f\nc = %.8f\nd = %.8f\ne = %.8f",
                coefficients[0], coefficients[1], coefficients[2],
                coefficients[3], coefficients[4],
                coefficients[0], coefficients[1], coefficients[2],
                coefficients[3], coefficients[4]
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        PolynomialFunction that = (PolynomialFunction) obj;
        if (!name.equals(that.name)) return false;

        for (int i = 0; i < coefficients.length; i++) {
            if (Math.abs(coefficients[i] - that.coefficients[i]) > 0.000001) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        for (double coeff : coefficients) {
            long bits = Double.doubleToLongBits(coeff);
            result = 31 * result + (int)(bits ^ (bits >>> 32));
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("PolynomialFunction{name='%s', coefficients=%s}",
                name, java.util.Arrays.toString(coefficients));
    }
}