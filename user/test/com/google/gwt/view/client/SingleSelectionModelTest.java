/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.view.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;

import java.util.Locale;

/**
 * Tests for {@link SingleSelectionModel}.
 */
public class SingleSelectionModelTest extends AbstractSelectionModelTest {

  /**
   * Test that deselecting a value other than the pending selection does not
   * cause the pending selection to be lost.
   */
  public void testDeselectWhileSelectionPending() {
    SingleSelectionModel<String> model = createSelectionModel(null);
    model.setSelected("test", true);
    model.setSelected("other", false);
    assertTrue(model.isSelected("test"));
    assertEquals("test", model.getSelectedObject());
  }

  public void testGetSelectedObject() {
    SingleSelectionModel<String> model = createSelectionModel(null);
    assertNull(model.getSelectedObject());

    model.setSelected("test", true);
    assertEquals("test", model.getSelectedObject());
    assertEquals("test", model.getSelectedSet().iterator().next());

    model.setSelected("test", false);
    assertNull(model.getSelectedObject());
    assertEquals(0, model.getSelectedSet().size());
  }

  public void testSelectedChangeEvent() {
    SingleSelectionModel<String> model = createSelectionModel(null);
    SelectionChangeEvent.Handler handler = new SelectionChangeEvent.Handler() {
      @Override
      public void onSelectionChange(SelectionChangeEvent event) {
        finishTest();
      }
    };
    model.addSelectionChangeHandler(handler);

    delayTestFinish(2000);
    model.setSelected("test", true);
  }

  public void testNoDuplicateChangeEvent() {
    delayTestFinish(2000);
    final SingleSelectionModel<String> model = createSelectionModel(null);
    final MockSelectionChangeHandler handler = new AssertOneSelectionChangeEventOnlyHandler();

    model.addSelectionChangeHandler(handler);
    model.setSelected("test", true);
    // selection events fire at the end of current event loop (finally command)
    handler.assertEventFired(false);

    Scheduler.get().scheduleDeferred(new Command() {
      @Override
      public void execute() {
        handler.assertEventFired(true);
        // No further selection events should be fired
        model.addSelectionChangeHandler(new FailingSelectionChangeEventHandler());
        model.setSelected("test", true);
        model.setSelected("test", true);
      }
    });

    new Timer() {
      @Override
      public void run() {
        finishTest();
      }
    }.schedule(1000);
  }

  public void testNoDuplicateChangeEvent2() {
    delayTestFinish(2000);
    SingleSelectionModel<String> model = createSelectionModel(null);

    // no event at all should be fired, as selection events fire at the end of current event loop
    // and at that point no state has been effectively changed.
    model.addSelectionChangeHandler(new FailingSelectionChangeEventHandler());
    model.setSelected("test", true);
    model.setSelected("test", false);
    model.setSelected("test", false);
    model.setSelected("test", false);

    new Timer() {
      @Override
      public void run() {
        finishTest();
      }
    }.schedule(1000);
  }

  public void testSetSelected() {
    SingleSelectionModel<String> model = createSelectionModel(null);
    assertFalse(model.isSelected("test0"));

    model.setSelected("test0", true);
    assertTrue(model.isSelected("test0"));
    assertEquals("test0", model.getSelectedSet().iterator().next());

    model.setSelected("test1", true);
    assertTrue(model.isSelected("test1"));
    assertFalse(model.isSelected("test0"));
    assertEquals("test1", model.getSelectedSet().iterator().next());
    assertEquals(1, model.getSelectedSet().size());

    model.setSelected("test1", false);
    assertFalse(model.isSelected("test1"));
    assertFalse(model.isSelected("test0"));
    assertEquals(0, model.getSelectedSet().size());

    model.setSelected("test2", true);
    assertEquals(1, model.getSelectedSet().size());
    assertEquals("test2", model.getSelectedSet().iterator().next());
  }

  public void testSetSelectedNull() {
    SingleSelectionModel<String> model = createSelectionModel(null);

    model.setSelected("test", true);
    assertTrue(model.isSelected("test"));

    // Null cannot be selected, but it deselects the current item.
    model.setSelected(null, true);
    assertNull(model.getSelectedObject());
    assertFalse(model.isSelected("test"));
    assertFalse(model.isSelected(null));
    assertEquals(0, model.getSelectedSet().size());
  }

  public void testSetSelectedWithKeyProvider() {
    ProvidesKey<String> keyProvider = new ProvidesKey<String>() {
      @Override
      public Object getKey(String item) {
        return item.toUpperCase(Locale.ROOT);
      }
    };
    SingleSelectionModel<String> model = createSelectionModel(keyProvider);
    assertFalse(model.isSelected("test0"));

    model.setSelected("test0", true);
    assertTrue(model.isSelected("test0"));
    assertTrue(model.isSelected("TEST0"));

    model.setSelected("test1", true);
    assertTrue(model.isSelected("test1"));
    assertTrue(model.isSelected("TEST1"));
    assertFalse(model.isSelected("test0"));

    model.setSelected("test1", false);
    assertFalse(model.isSelected("test1"));
    assertFalse(model.isSelected("test0"));
  }

  @Override
  protected SingleSelectionModel<String> createSelectionModel(ProvidesKey<String> keyProvider) {
    return new SingleSelectionModel<String>(keyProvider);
  }
}
