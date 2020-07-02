package com.ryanquey.podcast.helpers;

import java.lang.reflect.*;
import java.lang.IllegalAccessException;

public class DataClassesHelpers {
	// copies from objA to objB
	// https://stackoverflow.com/a/8132962/6952495
  static public void copyMatchingFields (Object objA, Object objB) {
    Class objAClass = objA.getClass();
    Class objBClass = objB.getClass();
    Method[] methods = objAClass.getMethods();

    for (int i = 0; i < methods.length; i++) {
      String methodName = methods[i].getName();

      try {
        if (methodName.startsWith("get")){
          Class returnType = methods[i].getReturnType();
          Method setter = objBClass.getMethod(methodName.replaceFirst("get", "set"), returnType);
          Method getter = methods[i];

          // get from objA and then set it on objB
          setter.invoke(objB, getter.invoke(objA, null));
        }

      } catch (IllegalAccessException e) {
      } catch (InvocationTargetException e) {
      } catch (NoSuchMethodException e) {
        // TODO: handle exception...or do nothing probably
      } catch (IllegalArgumentException e) {
        // TODO: handle exception...or do nothing probably
      }
    }
  }
}
