// CHECKSTYLE_OFF: Copyrighted to members of JCP JSR-166 Expert Group.
/*
 * This file is a modified version of
 * http://gee.cs.oswego.edu/cgi-bin/viewcvs.cgi/jsr166/src/main/java/util/concurrent/ScheduledFuture.java?revision=1.6
 * which contained the following notice:
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */
// CHECKSTYLE_ON

package java.util.concurrent;

/**
 * Emulation of ScheduleFuture.
 *
 * @param <V> value type returned by the future.
 */
public interface ScheduledFuture<V> extends Delayed, Future<V> {
}
