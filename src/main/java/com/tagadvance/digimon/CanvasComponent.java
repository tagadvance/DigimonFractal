package com.tagadvance.digimon;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Serial;
import javax.swing.JComponent;

/**
 * Shows a {@link BufferedImage} at its natural size. The image is shared, not copied: draw into it
 * and call {@link #repaint()} to show the result.
 */
public final class CanvasComponent extends JComponent {

  @Serial private static final long serialVersionUID = 1L;

  // Swing components are Serializable in name only; nothing here is ever serialized.
  private final transient BufferedImage image;

  public CanvasComponent(final BufferedImage image) {
    super();
    this.image = image;

    final int width = image.getWidth(), height = image.getHeight();
    final var preferredSize = new Dimension(width, height);
    setPreferredSize(preferredSize);
  }

  @Override
  protected void paintComponent(final Graphics g) {
    super.paintComponent(g);
    final int x = 0, y = 0;
    g.drawImage(image, x, y, null);
  }

}
