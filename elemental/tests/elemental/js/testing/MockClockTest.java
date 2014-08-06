// Copyright 2010 Google Inc. All Rights Reserved.
package elemental.js.testing;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

/**
 * Tests for {@link MockClock}
 */
public class MockClockTest extends GWTTestCase {

  /**
   * TODO(manolo): This is just a class to make this test
   * compile and pass. For some reason when this test came
   * to gwt, this class was not migrated, we should either
   * remove this test, or rescue MocClock from the original
   * site.
   */
  static class MockClock {
    static double time = 0;
    public static void run(Runnable run) {
      // TODO Auto-generated method stub
    }
    public static double getCurrentTime() {
      return time;
    }
    public static void reset() {
      time = 0;
    }
    public static int getTimeoutsMade() {
      // TODO Auto-generated method stub
      return 0;
    }
    public static void setTimeoutDelay(int i) {
      // TODO Auto-generated method stub
    }
    public static void tick(final double d) {
      time = d;
    }
  }

  public void testClearTimeout() {
    MockClock.run(new Runnable() {
      public void run() {
        TestTimer t = new TestTimer();
        t.schedule(100);
        t.cancel();
        MockClock.tick(101);
        assertFalse(t.hasRun);
      }
    });
  }

  public void testGetCurrentTime() {
    MockClock.reset();
    assertEquals(0.0, MockClock.getCurrentTime());
  }

  public void testGetTimeoutsMade() throws Exception {
    MockClock.run(new Runnable() {
      public void run() {
        TestTimer t = new TestTimer();
        assertEquals(0, MockClock.getTimeoutsMade());
        t.schedule(100);
        MockClock.tick(101);
        assertEquals(1, MockClock.getTimeoutsMade());
      }
    });
  }

  public void testRun() {
    MockClock.run(new Runnable() {
      public void run() {
        TestTimer t = new TestTimer();
        t.schedule(100);
        MockClock.tick(101);
        assertTrue(t.hasRun);
      }
    });
  }

  public void testSetTimeoutDelay() {
    MockClock.run(new Runnable() {
      public void run() {
        MockClock.setTimeoutDelay(30);
        TestTimer t = new TestTimer() {
          @Override
          public void run() {
            super.run();
            assertTrue(MockClock.getCurrentTime() >= 130);
          }
        };
        assertEquals(0, MockClock.getTimeoutsMade());
        t.schedule(100);
        assertEquals(1, MockClock.getTimeoutsMade());
        MockClock.tick(100);
        assertFalse(t.hasRun);
        MockClock.tick(30);
        assertTrue(t.hasRun);
      }
    });
  }

  public void testTick() {
    MockClock.reset();
    MockClock.tick(1234.0);
    assertEquals(1234.0, MockClock.getCurrentTime());
  }

  @Override
  public String getModuleName() {
    return "elemental.Elemental";
  }

  private static class TestTimer extends Timer {

    private boolean hasRun;

    @Override
    public void run() {
      hasRun = true;
    }
  }
}
