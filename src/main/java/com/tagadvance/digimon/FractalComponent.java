package com.tagadvance.digimon;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class FractalComponent extends JComponent {

	/**
	 * default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	private static final int PREFERRED_WIDTH = 640, PREFERRED_HEIGHT = 480;

	private final FractalPainter painter;

	public FractalComponent(FractalPainter painter) {
		super();
		this.painter = painter;
	}

	{
		setDoubleBuffered(true);
		setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		painter.paintCustom(g);
	}

}
