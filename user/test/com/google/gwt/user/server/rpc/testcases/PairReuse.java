package com.google.gwt.user.server.rpc.testcases;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Tests a tricky use of generics.
 */
public class PairReuse implements RemoteService {

  /**
   * Creates the argument we will use to calling {@link #send}.
   */
  public static Rect makeRect(int x, int y, int w, int h) {

    Rect r = new Rect();

    r.first = new XY();
    r.first.first = new Int();
    r.first.first.val = x;
    r.first.second = new Int();
    r.first.second.val = y;

    r.second = new WH();
    r.second.first = new Int();
    r.second.first.val = w;
    r.second.second = new Int();
    r.second.second.val = h;

    return r;
  }

  /**
   * A dummy RPC call that sends one argument to the server.
   */
  public static void send(Rect r) {
  }

  /**
   * A pair class that's used in three different ways.
   */
  public static class Pair<T1 extends IsSerializable, T2 extends IsSerializable>
    implements IsSerializable {
    public T1 first;
    public T2 second;

    @Override
    public String toString() {
      return "(" + first + ", " + second + ")";
    }
  }

  /**
   * An IsSerializable int.
   */
  public static class Int implements IsSerializable {
    public int val;
  }

  /**
   * The (x,y) coordinates.
   */
  public static class XY extends Pair<Int, Int> {}

  /**
   * The width and height.
   */
  public static class WH extends Pair<Int, Int> {}

  /**
   * A rectangle.
   */
  public static class Rect extends Pair<XY, WH> {}
}
