package com.google.gwt.user.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Widget;

public class IsWidgetIteratorAdaptor implements Iterator<IsWidget> {

  private final Iterator<Widget> backing;

  public IsWidgetIteratorAdaptor(Iterator<Widget> backing) {
    this.backing = backing;
  }

  @Override
  public boolean hasNext() {
    return backing.hasNext();
  }

  @Override
  public IsWidget next() {
    return (IsWidget) backing.next();
  }

  @Override
  public void remove() {
    backing.remove();
  }

}
