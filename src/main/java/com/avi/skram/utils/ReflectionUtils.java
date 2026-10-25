package com.avi.skram.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {

  public static <T> T getOrDefault(Object target, String fieldName, T defaultValue) {
    try {
      Object obj = getFieldValue(target, fieldName);
      return (T) obj;
    } catch (Exception e) {
      System.out.println(e);
      return defaultValue;
    }
  }

  /**
   * Gets the value of any field (public, protected, package-private, or private)
   * from an object, walking up the class hierarchy if necessary.
   */
  public static Object getFieldValue(Object target, String fieldName) throws NoSuchFieldException, IllegalAccessException {
    if (target == null || fieldName == null) {
      throw new IllegalArgumentException("Target object and field name cannot be null");
    }

    Field field = getDeclaredFieldStructure(target.getClass(), fieldName);
    field.setAccessible(true); // Suppresses Java language access checks
    return field.get(target);
  }

  /**
   * Sets the value of any field, walking up the class hierarchy if necessary.
   */
  public static void setFieldValue(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
    if (target == null || fieldName == null) {
      throw new IllegalArgumentException("Target object and field name cannot be null");
    }

    Field field = getDeclaredFieldStructure(target.getClass(), fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }

  /**
   * Helper method to recursively look up fields in the current class and its superclasses.
   */
  private static Field getDeclaredFieldStructure(Class<?> clazz, String fieldName) throws NoSuchFieldException {
    Class<?> currentClass = clazz;
    while (currentClass != null) {
      try {
        return currentClass.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        // Not found in current class level, move up to the superclass
        currentClass = currentClass.getSuperclass();
      }
    }
    throw new NoSuchFieldException("Field '" + fieldName + "' not found in hierarchy of " + clazz.getName());
  }
}
