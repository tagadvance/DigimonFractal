package com.tagadvance.digimon;

import static java.awt.Color.BLACK;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Tag <tagadvance@gmail.com>
 *
 */
public class DigimonFractal implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new DigimonFractal());
	}

	public void run() {
		String title = "Digimon Fractal";
		JFrame window = new JFrame(title);
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
		Container container = window.getContentPane();
		container.setBackground(BLACK);
		double seed = 2;
		System.out.printf("%f%n", seed);
		Painter painter = new FractalPainter(seed);
		JComponent fractalComponent = createComponent(painter);
		container.add(fractalComponent);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	public JComponent createComponent(Painter painter) {
		int width = 640, height = 240;
		BufferedImage bufferedImage = new BufferedImage(width, height,
				TYPE_INT_RGB);
		Graphics g = bufferedImage.getGraphics();
		painter.paintCustom(g);
		g.dispose();
		Icon icon = new ImageIcon(bufferedImage);
		return new JLabel(icon);
	}

}
