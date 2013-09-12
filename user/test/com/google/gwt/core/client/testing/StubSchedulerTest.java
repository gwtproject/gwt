/*
 * Copyright 2013 Google Inc.
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

package com.google.gwt.core.client.testing;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Tests for {@link StubScheduler}.
 */
public class StubSchedulerTest extends TestCase {

  private StubScheduler scheduler;
  private List<String> events;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    scheduler = new StubScheduler();
    events = new ArrayList<String>();
  }

  @Override
  protected void tearDown() throws Exception {
    checkEvents();
    super.tearDown();
  }

  public void testMixedCommands() {
    scheduler.scheduleEntry(new FakeRepeatingCommand("repeating", 2, "repeating child"));
    scheduler.scheduleDeferred(new FakeScheduledCommand("scheduled", "scheduled child"));
    checkEvents();

    assertTrue(scheduler.executeCommands());
    checkEvents("repeating", "scheduled");

    assertTrue(scheduler.executeCommands());
    checkEvents("repeating child", "repeating", "scheduled child");

    assertTrue(scheduler.executeCommands());
    checkEvents("repeating child", "repeating child");

    assertFalse(scheduler.executeCommands());
    checkEvents("repeating child");

    checkCommands(scheduler.getRepeatingCommands());
    checkCommands(scheduler.getScheduledCommands());
  }

  public void testRepeatingCommands() {
    scheduler.scheduleEntry(new FakeRepeatingCommand("entry1", 1, null));
    scheduler.scheduleFinally(new FakeRepeatingCommand("finally1", 2, null));
    scheduler.scheduleFixedDelay(new FakeRepeatingCommand("delay1", 1, null), 42);
    scheduler.scheduleFixedPeriod(new FakeRepeatingCommand("period1", 2, "period1 child"), 42);
    scheduler.scheduleIncremental(new FakeRepeatingCommand("incremental1", 1, null));

    scheduler.scheduleEntry(new FakeRepeatingCommand("entry2", 2, null));
    scheduler.scheduleFinally(new FakeRepeatingCommand("finally2", 1, "finally2 child"));
    scheduler.scheduleFixedDelay(new FakeRepeatingCommand("delay2", 2, null), 42);
    scheduler.scheduleFixedPeriod(new FakeRepeatingCommand("period2", 1, null), 42);
    scheduler.scheduleIncremental(new FakeRepeatingCommand("incremental2", 2, null));

    scheduler.scheduleDeferred(new FakeScheduledCommand("scheduled", null)); // ignored
    checkEvents();

    checkCommands(scheduler.getRepeatingCommands(),
        "entry1", "finally1", "delay1", "period1", "incremental1",
        "entry2", "finally2", "delay2", "period2", "incremental2");
    assertTrue(scheduler.executeRepeatingCommands());
    checkEvents(
        "entry1", "finally1", "delay1", "period1", "incremental1",
        "entry2", "finally2", "delay2", "period2", "incremental2");

    checkCommands(scheduler.getRepeatingCommands(),
        "finally1", "period1 child", "period1",
        "entry2", "finally2 child", "delay2", "incremental2");
    assertTrue(scheduler.executeRepeatingCommands());
    checkEvents(
        "finally1", "period1 child", "period1",
        "entry2", "finally2 child", "delay2", "incremental2");

    checkCommands(scheduler.getRepeatingCommands(),
        "period1 child", "period1 child");
    assertTrue(scheduler.executeRepeatingCommands());
    checkEvents("period1 child", "period1 child");

    checkCommands(scheduler.getRepeatingCommands(),
        "period1 child");
    assertFalse(scheduler.executeRepeatingCommands());
    checkEvents("period1 child");

    checkCommands(scheduler.getRepeatingCommands());
  }

  public void testScheduledCommands() {
    scheduler.scheduleDeferred(new FakeScheduledCommand("deferred1", "deferred1 child"));
    scheduler.scheduleEntry(new FakeScheduledCommand("entry1", null));
    scheduler.scheduleFinally(new FakeScheduledCommand("finally1", null));

    scheduler.scheduleFinally(new FakeScheduledCommand("finally2", null));
    scheduler.scheduleEntry(new FakeScheduledCommand("entry2", "entry2 child"));
    scheduler.scheduleDeferred(new FakeScheduledCommand("deferred2", null));

    scheduler.scheduleEntry(new FakeRepeatingCommand("repeating", 1, null));  // ignored
    checkEvents();

    checkCommands(scheduler.getScheduledCommands(),
        "deferred1", "entry1", "finally1", "finally2", "entry2", "deferred2");
    assertTrue(scheduler.executeScheduledCommands());
    checkEvents("deferred1", "entry1", "finally1", "finally2", "entry2", "deferred2");

    checkCommands(scheduler.getScheduledCommands(), "deferred1 child", "entry2 child");
    assertFalse(scheduler.executeScheduledCommands());
    checkEvents("deferred1 child", "entry2 child");

    checkCommands(scheduler.getScheduledCommands());
  }

  private void checkEvents(String... expected) {
    assertEquals(Arrays.asList(expected), events);
    events.clear();
  }

  private void checkCommands(List<?> actual, String... expected) {
    List<String> actualStrings = new ArrayList<String>();
    for (Object command : actual) {
      actualStrings.add(command.toString());
    }
    assertEquals(Arrays.asList(expected), actualStrings);
  }

  private class FakeRepeatingCommand implements RepeatingCommand {

    private final String id;
    private final int repeatCount;
    @Nullable private final String scheduledEntryId;

    private int executionCount;

    public FakeRepeatingCommand(String id, int repeatCount, @Nullable String scheduledEntryId) {
      this.id = id;
      this.repeatCount = repeatCount;
      this.scheduledEntryId = scheduledEntryId;
    }

    @Override
    public boolean execute() {
      events.add(id);
      if (scheduledEntryId != null) {
        scheduler.scheduleEntry(new FakeRepeatingCommand(scheduledEntryId, repeatCount, null));
      }

      assertTrue(executionCount < repeatCount);
      return (++executionCount < repeatCount);
    }

    @Override
    public String toString() {
      return id;
    }
  }

  private class FakeScheduledCommand implements ScheduledCommand {

    private final String id;
    @Nullable private final String scheduledEntryId;

    private int executionCount;

    public FakeScheduledCommand(String id, @Nullable String scheduledEntryId) {
      this.id = id;
      this.scheduledEntryId = scheduledEntryId;
    }

    @Override
    public void execute() {
      events.add(id);
      if (scheduledEntryId != null) {
        scheduler.scheduleEntry(new FakeScheduledCommand(scheduledEntryId, null));
      }

      assertEquals(1, ++executionCount);
    }

    @Override
    public String toString() {
      return id;
    }
  }
}
