package com.tagadvance.digimon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * 
 * @author Tag <tagadvance@gmail.com>
 *
 */
public class FractalComponent extends JComponent {

	private static final long serialVersionUID = 1L;

	private static final int PREFERRED_WIDTH = 640, PREFERRED_HEIGHT = 480;
	
	private static final int MAXIMUM_DEPTH = 9;
	
	@SuppressWarnings("unused")
	private static final int NORTH = -1, SOUTH = -2;

	private double seed;

	public FractalComponent(double seed) {
		super();
		this.seed = seed;
	}

	{
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintFractal(g);
	}

	public void paintFractal(Graphics g) {
		// what does this do?
		// screen 1,2,1,1
		seed = (seed - 1) / 10 + 1;
		seed = Math.sqrt(seed * seed - 1);
		double x0 = 100, x1 = 412, y0 = 0, y1 = 0;
		fractal(g, x0, x1, y0, y1, 1);
		line(g, 100, 50, 412, 50, 0xFF, 0xFFFF);
	}

	public void fractal(Graphics g, double x0, double x1, double y0, double y1,
			int depth) {
		double l = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0));
		if (Double.isNaN(l)) {
			return;
		}
		if (l < 2 || depth++ >= MAXIMUM_DEPTH) {
			line(g, x0, y0 / 3 + 50, x1, y1 / 3 + 50, 0xFF, 0xFFFF);
			return;
		}
		double r = Math.random() + Math.random() + Math.random() + SOUTH;
		double x2 = (x0 + x1) / 2 + seed * (y1 - y0) * r;
		double y2 = (y0 + y1) / 2 + seed * (x0 - x1) * r;
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
	public void line(Graphics g, double x0, double x1, double y0, double y1,
			int mystery1, int mystery2) {
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
