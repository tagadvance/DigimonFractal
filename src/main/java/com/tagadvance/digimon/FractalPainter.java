package com.tagadvance.digimon;

import java.awt.Color;
import java.awt.Graphics;
import java.util.stream.Stream;

/**
 * A port of the coastline program that appears on screen in Digimon Adventure episode 5. See the
 * README for the original BASIC listing.
 *
 * <p>Every call to {@link #paintCustom(Graphics)} draws a different coastline: the midpoint
 * displacement and the segment colors both come from {@link Math#random()}.
 */
public final class FractalPainter implements Painter {

  private static final int MINIMUM_SEED = 1, MAXIMUM_SEED = 2;
  private static final int MAXIMUM_DEPTH = 9;

  @SuppressWarnings("unused")
  private static final int NORTH = -1, SOUTH = -2;

  private double seed;
  private double mutableSeed;
  private int verticalOffset = 100;
  private static final double verticalBias = .333;

  /**
   * @throws IllegalArgumentException if {@code seed} is outside {@code [1, 2]}
   */
  public FractalPainter(final double seed) {
    super();
    this.setSeed(seed);
  }

  public double getSeed() {
    return seed;
  }

  /**
   * The "ratio 1 to 2" the original program prompts for. Higher values displace the midpoints
   * further and give a more jagged coastline.
   *
   * @throws IllegalArgumentException if {@code seed} is outside {@code [1, 2]}
   */
  public void setSeed(final double seed) {
    // Written as a negated conjunction so that NaN fails the check too.
    if (!(seed >= MINIMUM_SEED && seed <= MAXIMUM_SEED)) {
      final String message =
          String.format("%f < %d || %1$f > %d", seed, MINIMUM_SEED, MAXIMUM_SEED);

      throw new IllegalArgumentException(message);
    }

    this.seed = this.mutableSeed = seed;
  }

  public int getVerticalOffset() {
    return verticalOffset;
  }

  /**
   * The y coordinate of the horizontal baseline the coastline is drawn around.
   *
   * @throws IllegalArgumentException if {@code verticalOffset} is negative
   */
  public void setVerticalOffset(final int verticalOffset) {
    if (verticalOffset < 0) {
      throw new IllegalArgumentException("verticalOffset must be >= 0");
    }

    this.verticalOffset = verticalOffset;
  }

  @Override
  public void paintCustom(final Graphics g) {
    // what does this do?
    // screen 1,2,1,1
    mutableSeed = seed;
    mutableSeed = (mutableSeed - 1) / 10 + 1;
    mutableSeed = Math.sqrt(mutableSeed * mutableSeed - 1);
    final double x0 = 100, x1 = 412, y0 = 0, y1 = 0;
    fractal(g, x0, x1, y0, y1, 1);
    line(g, x0, verticalOffset, x1, verticalOffset, 0xFF, 0xFFFF);
  }

  /**
   * Recursively displaces the midpoint of the segment from {@code (x0, y0)} to {@code (x1, y1)}
   * until the pieces are shorter than two pixels or nine levels deep, then draws them.
   */
  public void fractal(
      final Graphics g,
      final double x0,
      final double x1,
      final double y0,
      final double y1,
      int depth) {
    final double xDifference = x1 - x0, xDifferenceSquared = Math.pow(xDifference, 2);
    final double yDifference = y1 - y0, yDifferenceSquared = Math.pow(yDifference, 2);
    final double l = Math.sqrt(xDifferenceSquared + yDifferenceSquared);
    if (Double.isNaN(l)) {
      return;
    } else if (l < 2 || depth++ >= MAXIMUM_DEPTH) {
      line(
          g,
          x0,
          y0 * verticalBias + verticalOffset,
          x1,
          y1 * verticalBias + verticalOffset,
          0xFF,
          0xFFFF);
      return;
    }

    final double r = Math.random() + Math.random() + Math.random() + SOUTH;
    final double x2 = (x0 + x1) / 2 + mutableSeed * (y1 - y0) * r;
    final double y2 = (y0 + y1) / 2 + mutableSeed * (x0 - x1) * r;
    fractal(g, x0, x2, y0, y2, depth);
    fractal(g, x2, x1, y2, y1, depth);
  }

  /**
   * Draws a segment from {@code (x0, y0)} to {@code (x1, y1)} in a random color. A segment with a
   * NaN endpoint is skipped rather than drawn.
   *
   * @param mystery1 the original program's fifth argument, presumably a color; ignored
   * @param mystery2 the original program's sixth argument, presumably a line style; ignored
   */
  @SuppressWarnings("unused")
  public void line(
      final Graphics g,
      final double x0,
      final double y0,
      final double x1,
      final double y1,
      final int mystery1,
      final int mystery2) {
    if (Stream.of(x0, y0, x1, y1).anyMatch(d -> Double.isNaN(d))) {
      return;
    }

    // randomize line segment colors to help understand line segment behavior
    final int white = 0xFFFFFF;
    final int rgb = (int) (Math.random() * white);
    final var color = new Color(rgb);
    g.setColor(color);

    g.drawLine((int) x0, (int) y0, (int) x1, (int) y1);
  }

}
