package com.avi.skram.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;

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
   * Invokes a private, protected, or otherwise non-public instance method, searching the target's class
   * and its superclasses. Overloads are matched against the supplied argument types.
   */
  public static Object invokeMethod(Object target, String methodName, Object... args)
    throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    if (target == null || methodName == null || args == null) {
      throw new IllegalArgumentException("Target object, method name, and arguments cannot be null");
    }

    Method method = getDeclaredMethodStructure(target.getClass(), methodName, args);
    method.setAccessible(true);
    return method.invoke(target, args);
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

  /**
   * Finds the most specific matching method in the class hierarchy.
   */
  private static Method getDeclaredMethodStructure(Class<?> clazz, String methodName, Object[] args)
    throws NoSuchMethodException {
    Method bestMatch = null;
    int bestScore = Integer.MAX_VALUE;
    int bestClassDepth = Integer.MAX_VALUE;
    boolean ambiguous = false;
    int classDepth = 0;

    for (Class<?> currentClass = clazz; currentClass != null; currentClass = currentClass.getSuperclass(), classDepth++) {
      for (Method method : currentClass.getDeclaredMethods()) {
        if (!method.getName().equals(methodName) || method.getParameterCount() != args.length) continue;

        int score = getMethodMatchScore(method, args);
        if (score < 0) continue;

        if (score < bestScore || (score == bestScore && classDepth < bestClassDepth)) {
          bestMatch = method;
          bestScore = score;
          bestClassDepth = classDepth;
          ambiguous = false;
        } else if (score == bestScore && classDepth == bestClassDepth && !method.equals(bestMatch)) {
          ambiguous = true;
        }
      }
    }

    if (bestMatch == null || ambiguous) {
      throw new NoSuchMethodException(
        "No unambiguous method '" + methodName + "' matching the supplied arguments in " + clazz.getName()
      );
    }
    return bestMatch;
  }

  private static int getMethodMatchScore(Method method, Object[] args) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    int score = 0;

    for (int i = 0; i < parameterTypes.length; i++) {
      Class<?> parameterType = wrapPrimitive(parameterTypes[i]);
      Object arg = args[i];

      if (arg == null) {
        if (parameterTypes[i].isPrimitive()) return -1;
        score += 1;
      } else {
        Class<?> argumentType = arg.getClass();
        if (!parameterType.isAssignableFrom(argumentType)) return -1;
        score += getTypeDistance(argumentType, parameterType);
      }
    }

    return score;
  }

  private static int getTypeDistance(Class<?> from, Class<?> to) {
    if (from.equals(to)) return 0;

    Deque<Class<?>> types = new ArrayDeque<>();
    Deque<Integer> distances = new ArrayDeque<>();
    types.add(from);
    distances.add(0);

    while (!types.isEmpty()) {
      Class<?> current = types.removeFirst();
      int distance = distances.removeFirst();
      if (current.equals(to)) return distance;

      Class<?> superclass = current.getSuperclass();
      if (superclass != null) {
        types.addLast(superclass);
        distances.addLast(distance + 1);
      }
      for (Class<?> implementedInterface : current.getInterfaces()) {
        types.addLast(implementedInterface);
        distances.addLast(distance + 1);
      }
    }

    return Integer.MAX_VALUE / 2;
  }

  private static Class<?> wrapPrimitive(Class<?> type) {
    if (!type.isPrimitive()) return type;
    if (type == boolean.class) return Boolean.class;
    if (type == byte.class) return Byte.class;
    if (type == short.class) return Short.class;
    if (type == int.class) return Integer.class;
    if (type == long.class) return Long.class;
    if (type == float.class) return Float.class;
    if (type == double.class) return Double.class;
    if (type == char.class) return Character.class;
    return type;
  }
}
