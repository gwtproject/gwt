/*
 * Copyright 2026 Google Inc.
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests for the server-side {@link StringQuoter#tryParseDate(String)}.
 */
public class StringQuoterJreTest extends TestCase {

  private static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSz";
  private static final String RFC2822_PATTERN = "EEE, d MMM yyyy HH:mm:ss Z";

  public void testTryParseDateMillis() {
    Date d = new Date(1234567890123L);
    assertEquals(d, StringQuoter.tryParseDate(Long.toString(d.getTime())));
  }

  public void testTryParseDateIso8601() {
    SimpleDateFormat fmt = new SimpleDateFormat(ISO8601_PATTERN, Locale.getDefault());
    Date d = new Date(1234567890123L);
    assertEquals(d, StringQuoter.tryParseDate(fmt.format(d)));
  }

  public void testTryParseDateZuluSuffix() throws Exception {
    SimpleDateFormat fmt = new SimpleDateFormat(ISO8601_PATTERN, Locale.getDefault());
    Date expected = fmt.parse("2024-01-15T10:30:00.000+0000");
    assertEquals(expected, StringQuoter.tryParseDate("2024-01-15T10:30:00.000Z"));
  }

  public void testTryParseDateRfc2822() {
    // RFC 2822 has second resolution.
    SimpleDateFormat fmt = new SimpleDateFormat(RFC2822_PATTERN, Locale.getDefault());
    Date d = new Date(1234567890000L);
    assertEquals(d, StringQuoter.tryParseDate(fmt.format(d)));
  }

  public void testTryParseDateUnparseable() {
    assertNull(StringQuoter.tryParseDate("not a date"));
  }

  /**
   * SimpleDateFormat is not thread-safe; sharing a single instance across
   * request threads on the server produced either ParseExceptions (returned as
   * null) or silently wrong Dates. Hammer tryParseDate from many threads and
   * make sure every call returns the expected value.
   */
  public void testTryParseDateConcurrent() throws Exception {
    SimpleDateFormat fmt = new SimpleDateFormat(ISO8601_PATTERN, Locale.getDefault());
    final Date expected = new Date(1234567890123L);
    final String input = fmt.format(expected);

    final int threads = 16;
    final int perThread = 2000;
    final CountDownLatch start = new CountDownLatch(1);
    final AtomicInteger nulls = new AtomicInteger();
    final AtomicInteger wrong = new AtomicInteger();
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    try {
      for (int i = 0; i < threads; i++) {
        pool.submit(new Runnable() {
          @Override
          public void run() {
            try {
              start.await();
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              return;
            }
            for (int j = 0; j < perThread; j++) {
              Date r = StringQuoter.tryParseDate(input);
              if (r == null) {
                nulls.incrementAndGet();
              } else if (!expected.equals(r)) {
                wrong.incrementAndGet();
              }
            }
          }
        });
      }
      start.countDown();
      pool.shutdown();
      assertTrue("threads did not finish in time", pool.awaitTermination(60, TimeUnit.SECONDS));
    } finally {
      pool.shutdownNow();
    }
    assertEquals("parses returned null", 0, nulls.get());
    assertEquals("parses returned wrong date", 0, wrong.get());
  }
}
