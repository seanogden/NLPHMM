package edu.cornell.cs.nlp.util.math;

/**
 * Routines for some approximate math functions.
 *
 * @author Dan Klein, Berkeley
 * @author Teg Grenager
 */
public class SloppyMath {

	public static double abs(double x) {
		if (x > 0) {
			return x;
		}
		return -1.0 * x;
	}

	public static double exp(double logX) {
		// if x is very near one, use the linear approximation
		if (abs(logX) < 0.001) {
			return 1 + logX;
		}
		return Math.exp(logX);
	}

	public static double logAdd(double logX, double logY) {
		// make a the max
		if (logY > logX) {
			final double temp = logX;
			logX = logY;
			logY = temp;
		}
		// now a is bigger
		if (logX == Double.NEGATIVE_INFINITY) {
			return logX;
		}
		final double negDiff = logY - logX;
		if (negDiff < -20) {
			return logX;
		}
		return logX + java.lang.Math.log(1.0 + java.lang.Math.exp(negDiff));
	}

	public static double logAdd(double[] logV) {
		double max = Double.NEGATIVE_INFINITY;
		double maxIndex = 0;
		for (int i = 0; i < logV.length; i++) {
			if (logV[i] > max) {
				max = logV[i];
				maxIndex = i;
			}
		}
		if (max == Double.NEGATIVE_INFINITY) {
			return Double.NEGATIVE_INFINITY;
		}
		// compute the negative difference
		final double threshold = max - 20;
		double sumNegativeDifferences = 0.0;
		for (int i = 0; i < logV.length; i++) {
			if (i != maxIndex && logV[i] > threshold) {
				sumNegativeDifferences += Math.exp(logV[i] - max);
			}
		}
		if (sumNegativeDifferences > 0.0) {
			return max + Math.log(1.0 + sumNegativeDifferences);
		} else {
			return max;
		}
	}

	public static double max(double x, double y) {
		if (x > y) {
			return x;
		}
		return y;
	}

	public static double max(int x, int y) {
		if (x > y) {
			return x;
		}
		return y;
	}

	public static double min(double x, double y) {
		if (x > y) {
			return y;
		}
		return x;
	}

	public static double min(int x, int y) {
		if (x > y) {
			return y;
		}
		return x;
	}

}
