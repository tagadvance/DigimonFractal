package com.tagadvance.digimon;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * 
 * @author Tag <tagadvance@gmail.com>
 *
 */
public class CanvasComponent extends JComponent {

	/**
	 * default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	private BufferedImage image;

	public CanvasComponent(BufferedImage image) {
		super();
		this.image = image;

		int width = image.getWidth(), height = image.getHeight();
		Dimension preferredSize = new Dimension(width, height);
		setPreferredSize(preferredSize);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		int x = 0, y = 0;
		g.drawImage(image, x, y, null);
	}

}
