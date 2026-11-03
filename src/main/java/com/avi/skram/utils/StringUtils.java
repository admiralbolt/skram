package com.avi.skram.utils;

import java.util.stream.Collectors;

public class StringUtils {

  // I'm shocked that I have to write this method.
  public static String capitalize(String str) {
    if (str == null || str.isEmpty()) return str;

    return Character.toUpperCase(str.charAt(0)) + str.substring(1);
  }

  public static String snakeToTitle(String name) {
    return java.util.Arrays.stream(name.split("_"))
      .map(StringUtils::capitalize)
      .collect(Collectors.joining(" "));
  }

}
