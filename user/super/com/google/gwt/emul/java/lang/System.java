/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package java.lang;

import static javaemul.internal.InternalPreconditions.checkArrayType;
import static javaemul.internal.InternalPreconditions.checkNotNull;
import static javaemul.internal.InternalPreconditions.isTypeChecked;

import java.io.PrintStream;
import java.util.function.Supplier;

import javaemul.internal.ArrayHelper;
import javaemul.internal.ConsoleLogger;
import javaemul.internal.HashCodes;
import javaemul.internal.InternalPreconditions;
import javaemul.internal.JsUtils;

import jsinterop.annotations.JsMethod;

/**
 * General-purpose low-level utility methods. GWT only supports a limited subset
 * of these methods due to browser limitations. Only the documented methods are
 * available.
 */
public final class System {

  /**
   * Does nothing in web mode. To get output in web mode, subclass PrintStream
   * and call {@link #setErr(PrintStream)}.
   */
  public static PrintStream err = new PrintStream(null);

  /**
   * Does nothing in web mode. To get output in web mode, subclass
   * {@link PrintStream} and call {@link #setOut(PrintStream)}.
   */
  public static PrintStream out = new PrintStream(null);

  public static void arraycopy(Object src, int srcOfs, Object dest, int destOfs, int len) {
    checkNotNull(src, "src");
    checkNotNull(dest, "dest");

    Class<?> srcType = src.getClass();
    Class<?> destType = dest.getClass();
    checkArrayType(srcType.isArray(), "srcType is not an array");
    checkArrayType(destType.isArray(), "destType is not an array");

    Class<?> srcComp = srcType.getComponentType();
    Class<?> destComp = destType.getComponentType();
    checkArrayType(arrayTypeMatch(srcComp, destComp), "Array types don't match");

    int srclen = ArrayHelper.getLength(src);
    int destlen = ArrayHelper.getLength(dest);
    if (srcOfs < 0 || destOfs < 0 || len < 0 || srcOfs + len > srclen || destOfs + len > destlen) {
      throw new IndexOutOfBoundsException();
    }

    /*
     * If the arrays are not references or if they are exactly the same type, we
     * can copy them in native code for speed. Otherwise, we have to copy them
     * in Java so we get appropriate errors.
     */
    if (isTypeChecked() && !srcComp.isPrimitive() && !srcType.equals(destType)) {
      // copy in Java to make sure we get ArrayStoreExceptions if the values
      // aren't compatible
      Object[] srcArray = (Object[]) src;
      Object[] destArray = (Object[]) dest;
      if (src == dest && srcOfs < destOfs) {
        // TODO(jat): how does backward copies handle failures in the middle?
        // copy backwards to avoid destructive copies
        srcOfs += len;
        for (int destEnd = destOfs + len; destEnd-- > destOfs;) {
          destArray[destEnd] = srcArray[--srcOfs];
        }
      } else {
        for (int destEnd = destOfs + len; destOfs < destEnd;) {
          destArray[destOfs++] = srcArray[srcOfs++];
        }
      }
    } else if (len > 0) {
      ArrayHelper.copy(src, srcOfs, dest, destOfs, len);
    }
  }

  public static long currentTimeMillis() {
    return (long) JsUtils.getTime();
  }

  /**
   * Has no effect; just here for source compatibility.
   *
   * @skip
   */
  public static void gc() {
  }

  /**
   * The compiler replaces getProperty by the actual value of the property.
   */
  @JsMethod(name = "$getDefine", namespace = "nativebootstrap.Util")
  public static native String getProperty(String key);

  /**
   * The compiler replaces getProperty by the actual value of the property.
   */
  @JsMethod(name = "$getDefine", namespace = "nativebootstrap.Util")
  public static native String getProperty(String key, String def);

  public static int identityHashCode(Object o) {
    return HashCodes.getIdentityHashCode(o);
  }

  public static void setErr(PrintStream err) {
    System.err = err;
  }

  public static void setOut(PrintStream out) {
    System.out = out;
  }

  public static Logger getLogger(String name) {
    return new LoggerImpl(name);
  }

  private static boolean arrayTypeMatch(Class<?> srcComp, Class<?> destComp) {
    if (srcComp.isPrimitive()) {
      return srcComp.equals(destComp);
    } else {
      return !destComp.isPrimitive();
    }
  }

  /**
   * An emulation of the java.lang.System.Logger interface, added in JDK 9.
   */
  public interface Logger {
    /**
     * An emulation of the java.lang.System.Logger.Level enum, added in JDK 9.
     */
    enum Level {
      ALL(Integer.MIN_VALUE),
      TRACE(400),
      DEBUG(500),
      INFO(800),
      WARNING(900),
      ERROR(1000),
      OFF(Integer.MAX_VALUE);

      private final int severity;

      Level(int severity) {
        this.severity = severity;
      }

      public String getName() {
        return name();
      }

      public int getSeverity() {
        return severity;
      }
    }

    String getName();

    boolean isLoggable(Level level);

    default void log(Level level, Object obj) {
      InternalPreconditions.checkNotNull(obj);
      log(level, obj::toString, null);
    }

    default void log(Level level, String msg) {
      log(level, msg, null);
    }

    default void log(Level level, String msg, Throwable thrown) {
      log(level, () -> msg, thrown);
    }

    default void log(Level level, Supplier<String> msgSupplier) {
      log(level, msgSupplier, null);
    }

    void log(Level level, Supplier<String> msgSupplier, Throwable thrown);
  }

  private static class LoggerImpl implements Logger {

    private static final boolean ALL_ENABLED;
    private static final boolean INFO_ENABLED;
    private static final boolean WARNING_ENABLED;
    private static final boolean ERROR_ENABLED;

    static {
      // '==' instead of equals makes it compile out faster.

      String level = System.getProperty("jre.logging.logLevel");
      if (level != "ALL"
          && level != "INFO"
          && level != "WARNING"
          && level != "SEVERE" // alias for j.u.l compatibility
          && level != "ERROR"
          && level != "OFF") {
        throw new AssertionError("Undefined value for jre.logging.logLevel: '" + level + "'");
      }

      ALL_ENABLED = level == "ALL";
      INFO_ENABLED = level == "ALL" || level == "INFO";
      WARNING_ENABLED = level == "ALL" || level == "INFO" || level == "WARNING";
      ERROR_ENABLED = level == "ALL" || level == "INFO" || level == "WARNING"
          || level == "SEVERE" || level == "ERROR";
    }

    private final String name;

    private LoggerImpl(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public boolean isLoggable(Level level) {
      InternalPreconditions.checkNotNull(level);
      if (ALL_ENABLED) {
        return level != Level.OFF;
      } else if (INFO_ENABLED) {
        return level == Level.INFO || level == Level.WARNING || level == Level.ERROR;
      } else if (WARNING_ENABLED) {
        return level == Level.WARNING || level == Level.ERROR;
      } else if (ERROR_ENABLED) {
        return level == Level.ERROR;
      } else {
        return false;
      }
    }

    @Override
    public void log(Level level, Supplier<String> msgSupplier, Throwable thrown) {
      InternalPreconditions.checkNotNull(msgSupplier);
      if (!isLoggable(level)) {
        return;
      }
      ConsoleLogger consoleLogger = ConsoleLogger.createIfSupported();
      if (consoleLogger == null) {
        return;
      }
      String consoleLogLevel = toConsoleLogLevel(level);
      // XXX: use 'name' here? Fwiw, j.u.l.SimpleConsoleLogHandler ignores it as well.
      consoleLogger.log(consoleLogLevel, msgSupplier.get());
      if (thrown != null) {
        consoleLogger.log(consoleLogLevel, thrown);
      }
    }

    private String toConsoleLogLevel(Level level) {
      switch (level) {
        case ERROR:
          return "error";
        case WARNING:
          return "warn";
        case INFO:
          return "info";
        default:
          return "log";
      }
    }
  }
}
