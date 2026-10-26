package com.avi.skram.ui;

import com.avi.skram.utils.ReflectionUtils;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ToastUtils {

  public static boolean hasGenericMessageToast(ToastManager toastManager, String toastName) {
    // We want to check both queued AND visible toasts here.
    Deque<Toast> queuedToasts = ReflectionUtils.getOrDefault(toastManager, "queued", new ArrayDeque<>());
    for (Toast toast : queuedToasts) {
      if (!(toast instanceof GenericMessageToast gmt)) continue;

      if (gmt.toastName.equals(toastName)) {
        return true;
      }
    }

    // Visible toasts rely on an inner private class ToastManager.ToastInstance.
    // We have to do some black magic to refer to it.
    try {
      Class<?> toastInstance = Class.forName(ToastManager.class.getName() + "$ToastInstance");
      java.lang.reflect.Method method = toastInstance.getDeclaredMethod("getToast");
      method.setAccessible(true);

      List<?> visibleToasts = ReflectionUtils.getOrDefault(toastManager, "visibleToasts", new ArrayList<>());
      for (Object visibleToast : visibleToasts) {
        if (!(method.invoke(visibleToast) instanceof GenericMessageToast gmt)) continue;

        if (gmt.toastName.equals(toastName)) {
          return true;
        }
      }

    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      System.out.println(e);
    }

    return false;
  }
}
