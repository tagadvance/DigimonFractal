package com.tagadvance.digimon;

import static java.awt.Color.WHITE;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

import java.awt.Container;

import javax.swing.JComponent;
import javax.swing.JFrame;
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
		container.setBackground(WHITE);
		double seed = 2;
		System.out.printf("%f%n", seed);
		FractalPainter painter = new FractalPainter(seed);
		JComponent fractalComponent = new FractalComponent(painter);
		container.add(fractalComponent);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

}
