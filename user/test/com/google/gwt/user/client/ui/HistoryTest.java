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
package com.google.gwt.user.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.ArrayList;

/**
 * Tests for the history system.
 * 
 * TODO: find a way to test unescaping of the initial hash value.
 */
public class HistoryTest extends GWTTestCase {

  private static native String getCurrentLocationHash() /*-{
    var href = $wnd.location.href;

    splitted = href.split("#");
    if (splitted.length != 2) {
      return null;
    }

    hashPortion = splitted[1];

    return hashPortion;
  }-*/;

  private HandlerRegistration handlerRegistration;
  private Timer timer;

  @Override
  public String getModuleName() {
    return "com.google.gwt.user.User";
  }

  /* Tests against issue #572: Double unescaping of history tokens. */
  public void testDoubleEscaping() {
    final String escToken = "%24%24%24";

    delayTestFinish(5000);
    addHistoryListenerImpl(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        assertEquals(escToken, event.getValue());
        finishTest();
      }
    });
    History.newItem(escToken);
  }

  /*
   * Tests against issue #879: Ensure that empty history tokens do not add
   * additional characters after the '#' symbol in the URL.
   */
  public void testEmptyHistoryTokens() {
    delayTestFinish(5000);

    addHistoryListenerImpl(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();

        if (historyToken == null) {
          fail("historyToken should not be null");
        }

        if (historyToken.equals("foobar")) {
          History.newItem("");
        } else {
          assertEquals(0, historyToken.length());
          finishTest();
        }
      }
    });

    // We must first start out with a non-blank history token. Adding a blank
    // history token in the initial state will not cause an onHistoryChanged
    // event to fire.
    History.newItem("foobar");
  }

  /**
   * Verify that no events are issued via newItem if there were not reqeuested.
   */
  public void testNoEvents() {
    delayTestFinish(5000);
    addHistoryListenerImpl(new ValueChangeHandler<String>() {
      {
        timer = new Timer() {
          public void run() {
            finishTest();
          }
        };
        timer.schedule(500);
      }

      public void onValueChange(ValueChangeEvent<String> event) {
        fail("onValueChanged should not have been called");
      }
    });
    History.newItem("testNoEvents", false);
  }

  /*
   * Ensure that non-url-safe strings (such as those containing spaces) are
   * encoded/decoded correctly, and that programmatic 'back' works.
   */
  @DoNotRunWith(Platform.HtmlUnitUnknown)
  public void testHistory() {
    /*
     * Sentinel token which should only be seen if tokens are lost during the
     * rest of the test. Without this, History.back() might send the browser too
     * far back, i.e. back to before the web app containing our test module.
     */
    History.newItem("if-you-see-this-then-history-went-back-too-far");

    delayTestFinish(10000);
    addHistoryListenerImpl(new ValueChangeHandler<String>() {
      private int state = 0;

      public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();
        switch (state) {
          case 0: {
            if (!historyToken.equals("foo bar")) {
              fail("Expecting token 'foo bar', but got: " + historyToken);
            }

            state = 1;
            History.newItem("baz");
            break;
          }

          case 1: {
            if (!historyToken.equals("baz")) {
              fail("Expecting token 'baz', but got: " + historyToken);
            }

            state = 2;
            History.back();
            break;
          }

          case 2: {
            if (!historyToken.equals("foo bar")) {
              fail("Expecting token 'foo bar' after History.back(), but got: " + historyToken);
            }
            finishTest();
            break;
          }
        }
      }
    });
    
    /*
     * Delay kicking off the history transitions, so the browser has time to process
     * the initial sentinel token
     */
    new Timer() {
      @Override
      public void run() {
        History.newItem("foo bar");
      }
    }.schedule(5000);
  }

  /**
   * Verify that {@link HistoryListener#onHistoryChanged(String)} is only
   * called once per {@link History#newItem(String)}. 
   */
  public void testHistoryChangedCount() {
    delayTestFinish(5000);
    timer = new Timer() {
      private int count = 0;
      
      public void run() {
        if (count++ == 0) {
          // verify that duplicates don't issue another event
          History.newItem("testHistoryChangedCount");
          timer.schedule(500);
        } else {
          finishTest();
        }
      }
    };
    addHistoryListenerImpl(new ValueChangeHandler<String>() {
      final ArrayList<Object> counter = new ArrayList<Object>();

      public void onValueChange(ValueChangeEvent<String> event) {
        counter.add(null);
        if (counter.size() != 1) {
          fail("onHistoryChanged called multiple times");
        }
        // wait 500ms to see if we get called multiple times
        timer.schedule(500);
      }
    });
    History.newItem("testHistoryChangedCount");
  }

  public void testTokenEscaping() {
    final String shouldBeEncoded = "% ^[]|\"<>{}\\";
    final String shouldBeEncodedAs = "%25%20%5E%5B%5D%7C%22%3C%3E%7B%7D%5C";

    delayTestFinish(5000);
    addHistoryListenerImpl(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        assertEquals(shouldBeEncodedAs, getCurrentLocationHash());
        assertEquals(shouldBeEncoded, event.getValue());
        finishTest();
      }
    });
    History.newItem(shouldBeEncoded);
  }

  /*
   * HtmlUnit reports:
   *   expected=abc;,/?:@&=+$-_.!~*()ABC123foo
   *   actual  =abc;,/?:@&=%20$-_.!~*()ABC123foo
   */
  @DoNotRunWith(Platform.HtmlUnitBug)
  public void testTokenNonescaping() {
    final String shouldNotChange = "abc;,/?:@&=+$-_.!~*()ABC123foo";

    delayTestFinish(5000);
    addHistoryListenerImpl(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        assertEquals(shouldNotChange, getCurrentLocationHash());
        assertEquals(shouldNotChange, event.getValue());
        finishTest();
      }
    });
    History.newItem(shouldNotChange);
  }

  /*
   * Test against issue #2500. IE6 has a bug that causes it to not report any
   * part of the current fragment after a '?' when read from location.hash; make
   * sure that on affected browsers, we're not relying on this.
   */
  public void testTokenWithQuestionmark() {
    delayTestFinish(5000);
    final String token = "foo?bar";

    addHistoryListenerImpl(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        String historyToken = event.getValue();
        if (historyToken == null) {
          fail("historyToken should not be null");
        }
        assertEquals(token, historyToken);
        finishTest();
      }
    });
    History.newItem(token);
  }

  /**
   * Test that using an empty history token works properly. There have been
   * problems (see issue 2905) with this in the past on Safari.
   * <p>
   * Seems like a HtmlUnit bug. Need more investigation.
   */
  @DoNotRunWith(Platform.HtmlUnitBug)
  public void testEmptyHistoryToken() {
    final ArrayList<Object> counter = new ArrayList<Object>();

    addHistoryListenerImpl(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        counter.add(new Object());
        assertFalse("Browser is borked by empty history token", isBorked());
      }
    });

    History.newItem("x");
    History.newItem("");

    assertEquals("Expected two history events", 2, counter.size());
  }

  // Used by testEmptyHistoryToken() to catch a bizarre failure mode on Safari.
  private static boolean isBorked() {
    Element e = Document.get().createDivElement();
    e.setInnerHTML("string");
    return e.getInnerHTML().length() == 0;
  }

  @Override
  protected void gwtTearDown() throws Exception {
    if (handlerRegistration != null) {
      handlerRegistration.removeHandler();
      handlerRegistration = null;
    }
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  private void addHistoryListenerImpl(ValueChangeHandler<String> handler) {
    this.handlerRegistration = History.addValueChangeHandler(handler);
  }  
}
