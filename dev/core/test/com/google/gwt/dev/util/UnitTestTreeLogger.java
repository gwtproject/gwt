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
package com.google.gwt.dev.util;

import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.thirdparty.guava.common.collect.HashMultimap;
import com.google.gwt.thirdparty.guava.common.collect.Multimap;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A {@link TreeLogger} implementation that can be used during JUnit tests to
 * check for a specified sequence of log events.
 */
public class UnitTestTreeLogger extends TreeLogger {

  /**
   * Simplifies the creation of a {@link UnitTestTreeLogger} by providing
   * convenience methods for specifying the expected log events.
   */
  public static class Builder {

    private final List<LogEntry> expected = new ArrayList<LogEntry>();
    private EnumSet<Type> loggableTypes = EnumSet.allOf(TreeLogger.Type.class);

    public Builder() {
    }

    public UnitTestTreeLogger createLogger() {
      return new UnitTestTreeLogger(expected, loggableTypes);
    }

    public void expect(TreeLogger.Type type, String msg, Class<? extends Throwable> caught) {
      expected.add(new LogEntry(type, msg, caught));
    }

    public void expect(TreeLogger.Type type, Pattern msgPattern,
        Class<? extends Throwable> caught) {
      expected.add(new LogEntry(type, msgPattern, caught));
    }

    public void expectDebug(String msg, Class<? extends Throwable> caught) {
      expect(TreeLogger.DEBUG, msg, caught);
    }

    public void expectError(String msg, Class<? extends Throwable> caught) {
      expect(TreeLogger.ERROR, msg, caught);
    }

    public void expectError(Pattern msgPattern, Class<? extends Throwable> caught) {
      expect(TreeLogger.ERROR, msgPattern, caught);
    }

    public void expectInfo(String msg, Class<? extends Throwable> caught) {
      expect(TreeLogger.INFO, msg, caught);
    }

    public void expectSpam(String msg, Class<? extends Throwable> caught) {
      expect(TreeLogger.SPAM, msg, caught);
    }

    public void expectTrace(String msg, Class<? extends Throwable> caught) {
      expect(TreeLogger.TRACE, msg, caught);
    }

    public void expectWarn(String msg, Class<? extends Throwable> caught) {
      expect(TreeLogger.WARN, msg, caught);
    }

    /**
     * Sets the loggable types based on an explicit set.
     */
    public void setLoggableTypes(EnumSet<TreeLogger.Type> loggableTypes) {
      this.loggableTypes = loggableTypes;
    }

    /**
     * Sets the loggable types based on a lowest log level.
     */
    public void setLowestLogLevel(TreeLogger.Type lowestLogLevel) {
      loggableTypes.clear();
      for (Type type : TreeLogger.Type.values()) {
        if (!type.isLowerPriorityThan(lowestLogLevel)) {
          loggableTypes.add(type);
        }
      }
    }
  }

  /**
   * Represents a log event to check for.
   */
  private static class LogEntry {
    private final Class<? extends Throwable> caught;
    private String msg;
    private Pattern msgPattern;
    private final Type type;

    public LogEntry(TreeLogger.Type type, String msg, Class<? extends Throwable> caught) {
      assert (type != null);
      this.type = type;
      this.msg = msg;
      this.caught = caught;
    }

    public LogEntry(TreeLogger.Type type, Pattern msgPattern, Class<? extends Throwable> caught) {
      assert (type != null);
      this.type = type;
      this.msgPattern = msgPattern;
      this.caught = caught;
    }

    public LogEntry(Type type, String msg, Throwable caught) {
      this(type, msg, (caught == null) ? null : caught.getClass());
    }

    public Class<? extends Throwable> getCaught() {
      return caught;
    }

    public String getMessage() {
      return msg;
    }

    public Pattern getMessagePattern() {
      return msgPattern;
    }

    public Type getType() {
      return type;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(type.getLabel());
      sb.append(": ");
      if (getMessage() != null) {
        sb.append(getMessage());
      } else {
        sb.append("like " + getMessagePattern().pattern());
      }
      Class<? extends Throwable> t = getCaught();
      if (t != null) {
        sb.append("; ");
        sb.append(t.getName());
      }
      return sb.toString();
    }

    // Test whether this log entry matches {@code other} (not symmetrical, i.e. a matches b does not
    // imply b matches a)
    //
    // NOTE: DO NOT IMPLEMENT EQUAL. The test harness relies on equal() being reference equality.
    private boolean matches(LogEntry other) {
      if (!type.equals(other.type)) {
        return false;
      }
      if (msg != null) {
        if (!msg.equals(other.msg)) {
          return false;
        }
      } else {
        if (!getMessagePattern().matcher(other.msg).matches()) {
          return false;
        }
      }
      if ((caught == null) != (other.caught == null)) {
        return false;
      }
      if (caught != null && !caught.isAssignableFrom(other.caught)) {
        return false;
      }
      return true;
    }
  }

  private static void assertCorrectLogEntry(LogEntry expected, LogEntry actual) {
    Assert.assertEquals("Log types do not match", expected.getType(), actual.getType());
    if (expected.getMessage() != null) {
      Assert.assertEquals("Log messages do not match", expected.getMessage(), actual.getMessage());
    } else {
      Assert.assertTrue("Log message '" + actual.getMessage() + "' does not match pattern "
          + expected.getMessagePattern().pattern(),
          expected.getMessagePattern().matcher(actual.getMessage()).matches());
    }
    if (expected.getCaught() == null) {
      Assert.assertNull("Actual log exception type should have been null", actual.getCaught());
    } else {
      Assert.assertNotNull("Actual log exception type should not have been null", actual
          .getCaught());
      Assert.assertTrue("Actual log exception type (" + actual.getCaught().getName()
          + ") cannot be assigned to expected log exception type ("
          + expected.getCaught().getName() + ")", expected.getCaught().isAssignableFrom(
          actual.getCaught()));
    }
  }

  private final List<LogEntry> actualEntries = new ArrayList<LogEntry>();
  private final List<LogEntry> expectedEntries = new ArrayList<LogEntry>();

  private final EnumSet<TreeLogger.Type> loggableTypes;

  public UnitTestTreeLogger(List<LogEntry> expectedEntries, EnumSet<TreeLogger.Type> loggableTypes) {
    this.expectedEntries.addAll(expectedEntries);
    this.loggableTypes = loggableTypes;

    // Sanity check that all expected entries are actually loggable.
    for (LogEntry entry : expectedEntries) {
      Type type = entry.getType();
      Assert.assertTrue("Cannot expect an entry of a non-loggable type!", isLoggable(type));
      loggableTypes.add(type);
    }
  }

  /**
   * Asserts that all expected log entries were logged in the correct order and
   * no other entries were logged.
   */
  public void assertCorrectLogEntries() {
    if (expectedEntries.size() != actualEntries.size()) {
      Assert.fail("Wrong log count: expected=" + expectedEntries + ", actual=" + actualEntries);
    }

    assertMatches(expectedEntries, actualEntries);
  }

  private void assertMatches(Iterable<LogEntry> expectedEntries, Iterable<LogEntry> actualEntries) {
    Multimap<LogEntry, LogEntry> matches = HashMultimap.create();
    for (LogEntry expectedEntry : expectedEntries) {
      for (LogEntry actualEntry : actualEntries) {
        if (expectedEntry.matches(actualEntry)) {
          matches.put(expectedEntry, actualEntry);
        }
      }
    }

    // Assure only one match per expected entry.
    for (Entry<LogEntry, Collection<LogEntry>> entry : matches.asMap().entrySet()) {
      Assert.assertTrue(entry.getKey() + " matches multiple actual entries " + entry.getValue(),
          entry.getValue().size() == 1);
    }

    Set<LogEntry> unmatchedActualEntries =
        Sets.difference(Sets.newHashSet(actualEntries), Sets.newHashSet(matches.values()));
    Set<LogEntry> unmatchedExpectedEntries =
        Sets.difference(Sets.newHashSet(expectedEntries), Sets.newHashSet(matches.keySet()));
    // This is a HACK to get a nice printout, entries are only equals when they are empty.
    Assert.assertEquals(unmatchedExpectedEntries, unmatchedActualEntries);
  }

  /**
   * A more loose check than {@link #assertCorrectLogEntries} that just checks
   * to see that the expected log messages are somewhere in the actual logged
   * messages.
   */
  public void assertLogEntriesContainExpected() {
    for (LogEntry expectedEntry : expectedEntries) {
      boolean found = false;
      for (LogEntry actualEntry : actualEntries) {
        if (expectedEntry.matches(actualEntry)) {
          found = true;
          break;
        }
      }
      Assert.assertTrue("No match for expected=" + expectedEntry + " in actual=" + actualEntries,
          found);
    }
  }

  @Override
  public TreeLogger branch(Type type, String msg, Throwable caught, HelpInfo helpInfo) {
    log(type, msg, caught);
    return this;
  }

  @Override
  public boolean isLoggable(Type type) {
    return loggableTypes.contains(type);
  }

  @Override
  public void log(Type type, String msg, Throwable caught, HelpInfo helpInfo) {
    if (!isLoggable(type)) {
      return;
    }
    LogEntry actualEntry = new LogEntry(type, msg, caught);
    actualEntries.add(actualEntry);
  }
}
