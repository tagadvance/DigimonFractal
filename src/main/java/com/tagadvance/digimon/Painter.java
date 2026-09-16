package com.tagadvance.digimon;

import java.awt.Graphics;

/** Something that draws onto a caller-supplied {@link Graphics}. */
public interface Painter {

  /** Draws onto {@code g}, which the caller owns and disposes. */
  void paintCustom(Graphics g);

}
