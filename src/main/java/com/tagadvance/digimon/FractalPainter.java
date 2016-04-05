package com.tagadvance.digimon;

import java.awt.Color;
import java.awt.Graphics;

/**
 * 
 * @author Tag <tagadvance@gmail.com>
 *
 */
public class FractalPainter implements Painter {
	
	private static final int MINIMUM_SEED = 1, MAXIMUM_SEED = 2;
	private static final int MAXIMUM_DEPTH = 9;

	@SuppressWarnings("unused")
	private static final int NORTH = -1, SOUTH = -2;

	private double seed;
	private double mutableSeed;

	public FractalPainter(double seed) {
		super();
		this.setSeed(seed);
	}

	public double getSeed() {
		return seed;
	}

	public void setSeed(double seed) {
		if (seed < MINIMUM_SEED || seed > MAXIMUM_SEED) {
			String format = "%f < %d || %1$f > %d";
			String message = String.format(format, seed, MINIMUM_SEED, MAXIMUM_SEED);
			throw new IllegalArgumentException(message);
		}
		this.seed = this.mutableSeed = seed;
	}

	public void paintCustom(Graphics g) {
		// what does this do?
		// screen 1,2,1,1
		mutableSeed = seed;
		mutableSeed = (mutableSeed - 1) / 10 + 1;
		mutableSeed = Math.sqrt(mutableSeed * mutableSeed - 1);
		double x0 = 100, x1 = 412, y0 = 0, y1 = 0;
		fractal(g, x0, x1, y0, y1, 1);
		line(g, 100, 50, 412, 50, 0xFF, 0xFFFF);
	}

	public void fractal(Graphics g, double x0, double x1, double y0, double y1, int depth) {
		double l = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
		if (Double.isNaN(l)) {
			return;
		}
		if (l < 2 || depth++ >= MAXIMUM_DEPTH) {
			line(g, x0, y0 / 3 + 50, x1, y1 / 3 + 50, 0xFF, 0xFFFF);
			return;
		}
		double r = Math.random() + Math.random() + Math.random() + SOUTH;
		double x2 = (x0 + x1) / 2 + mutableSeed * (y1 - y0) * r;
		double y2 = (y0 + y1) / 2 + mutableSeed * (x0 - x1) * r;
		fractal(g, x0, x2, y0, y2, depth);
		fractal(g, x2, x1, y2, y1, depth);
	}

	/**
	 * 
	 * @param g
	 * @param x0
	 * @param x1
	 * @param y0
	 * @param y1
	 * @param mystery1 opacity?
	 * @param mystery2 color?
	 */
	public void line(Graphics g, double x0, double x1, double y0, double y1, int mystery1, int mystery2) {
		double[] doubles = { x0, y0, x1, y1 };
		for (double d : doubles) {
			if (Double.isNaN(d)) {
				return;
			}
		}

		int white = 0xFFFFFF;
		int rgb = (int) (Math.random() * white);
		Color color = new Color(rgb);
		g.setColor(color);

		// System.out.printf("%f, %f, %f, %f%n", x0, y0, x1, y1);
		g.drawLine((int) x0, (int) x1, (int) y0, (int) y1);
	}

}
