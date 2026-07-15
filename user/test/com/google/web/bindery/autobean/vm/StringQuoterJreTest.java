/*
 * Copyright 2026 GWT Project Authors
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
package com.google.web.bindery.autobean.vm;

import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests for the server-side {@link StringQuoter#tryParseDate(String)}.
 */
public class StringQuoterJreTest extends TestCase {

  // All inputs below resolve to the same instant: 2009-02-13T23:31:30.123 UTC.
  private static final Date EXPECTED = new Date(1234567890123L);

  public void testTryParseDateMillis() {
    assertEquals(EXPECTED, StringQuoter.tryParseDate("1234567890123"));
  }

  public void testTryParseDateIso8601() {
    assertEquals(EXPECTED, StringQuoter.tryParseDate("2009-02-13T23:31:30.123+0000"));
  }

  public void testTryParseDateZuluSuffix() {
    assertEquals(EXPECTED, StringQuoter.tryParseDate("2009-02-13T23:31:30.123Z"));
  }

  public void testTryParseDateRfc2822() {
    // RFC 2822 has second resolution.
    assertEquals(new Date(1234567890000L),
        StringQuoter.tryParseDate("Fri, 13 Feb 2009 23:31:30 +0000"));
  }

  public void testTryParseDateUnparseable() {
    assertNull(StringQuoter.tryParseDate("not a date"));
  }

  /**
   * SimpleDateFormat is not thread-safe; sharing a single instance across
   * request threads on the server produced either ParseExceptions (returned as
   * null), other exceptions (e.g. NumberFormatException), or silently wrong
   * Dates. Hammer tryParseDate from many threads at once and make sure every
   * call returns the expected value and no worker threw.
   */
  public void testTryParseDateConcurrent() throws Exception {
    final String input = "2009-02-13T23:31:30.123+0000";

    final int threads = 16;
    final int perThread = 2000;
    final CountDownLatch ready = new CountDownLatch(threads);
    final CountDownLatch start = new CountDownLatch(1);
    final AtomicInteger nulls = new AtomicInteger();
    final AtomicInteger wrong = new AtomicInteger();
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    try {
      List<Future<?>> futures = new ArrayList<Future<?>>();
      for (int i = 0; i < threads; i++) {
        futures.add(pool.submit(new Runnable() {
          @Override
          public void run() {
            ready.countDown();
            try {
              if (!start.await(60, TimeUnit.SECONDS)) {
                throw new AssertionError("worker was not released in time");
              }
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
            for (int j = 0; j < perThread; j++) {
              Date r = StringQuoter.tryParseDate(input);
              if (r == null) {
                nulls.incrementAndGet();
              } else if (!EXPECTED.equals(r)) {
                wrong.incrementAndGet();
              }
            }
          }
        }));
      }
      assertTrue("worker threads did not start in time", ready.await(60, TimeUnit.SECONDS));
      start.countDown();
      // get() each worker so that any exception thrown inside run() (including
      // an InterruptedException that would otherwise leave the work undone) is
      // rethrown here and fails the test instead of being swallowed.
      for (Future<?> f : futures) {
        f.get(60, TimeUnit.SECONDS);
      }
    } finally {
      pool.shutdownNow();
    }
    assertEquals("parses returned null", 0, nulls.get());
    assertEquals("parses returned wrong date", 0, wrong.get());
  }
}
