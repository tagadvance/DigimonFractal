package com.tagadvance.digimon;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

final class FractalPainterTest {

  @ParameterizedTest
  @ValueSource(doubles = {1, 1.5, 2})
  @DisplayName("accepts any seed in [1, 2]")
  void seedInRange(final double seed) {
    final var painter = new FractalPainter(seed);

    assertEquals(seed, painter.getSeed());
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.999, 2.001, Double.NaN})
  @DisplayName("rejects a seed outside [1, 2]")
  void seedOutOfRange(final double seed) {
    assertThrows(IllegalArgumentException.class, () -> new FractalPainter(seed));
  }

  @Test
  @DisplayName("rejects a negative vertical offset and keeps the old one")
  void negativeVerticalOffset() {
    final var painter = new FractalPainter(1);
    painter.setVerticalOffset(42);

    assertThrows(IllegalArgumentException.class, () -> painter.setVerticalOffset(-1));
    assertEquals(42, painter.getVerticalOffset());
  }

  @Test
  @DisplayName("draws the baseline at the vertical offset")
  void baselineAtVerticalOffset() {
    final var image = new BufferedImage(512, 200, BufferedImage.TYPE_INT_RGB);
    final var painter = new FractalPainter(1);
    painter.setVerticalOffset(150);

    paint(painter, image);

    assertTrue(IntStream.range(100, 412).anyMatch(x -> image.getRGB(x, 150) != 0xFF000000));
  }

  @Test
  @DisplayName("displaces the coastline off the baseline")
  void coastlineDisplacesFromBaseline() {
    final var image = new BufferedImage(512, 200, BufferedImage.TYPE_INT_RGB);
    final var painter = new FractalPainter(2);

    paint(painter, image);

    // Which side a given run lands on is random, so only assert that it left the baseline row.
    final int offset = painter.getVerticalOffset();
    assertTrue(
        anyPainted(image, 0, offset) || anyPainted(image, offset + 1, image.getHeight()),
        "nothing drawn off the baseline");
  }

  private static void paint(final Painter painter, final BufferedImage image) {
    final var g = image.getGraphics();
    try {
      painter.paintCustom(g);
    } finally {
      g.dispose();
    }
  }

  private static boolean anyPainted(final BufferedImage image, final int fromY, final int toY) {
    return IntStream.range(fromY, toY)
        .anyMatch(
            y ->
                IntStream.range(0, image.getWidth())
                    .anyMatch(x -> image.getRGB(x, y) != 0xFF000000));
  }

}
