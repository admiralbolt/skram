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

  // A helper function for generating an int from a random chance when the chance value can be greater than 1.
  // In cases where we have a random chance like 1.5 (i.e. 150%), we return either 1 OR 2.
  public static int getScalingRandom(double chance) {
    int flat = (int) chance;

    return flat + ((Math.random() < (chance - flat)) ? 1 : 0);
  }

}
