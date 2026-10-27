package com.avi.skram.utils;

import java.awt.*;

public class MathUtils {

  public static double linearRescale(double currentValue, double min, double max, double newMin, double newMax) {
    double currentRescale = (currentValue - min) / (max - min);
    return Math.max(Math.min(newMin * (1 - currentRescale) + newMax * currentRescale, newMax), newMin);
  }

  public static int getColorValue(Color color) {
    return (color.getAlpha() << 24) | (color.getRed() << 16) | (color.getGreen() << 8) | (color.getBlue());
  }

}
