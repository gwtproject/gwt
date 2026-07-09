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
package com.google.gwt.resources.css.ast;

import com.google.gwt.resources.css.ast.CssProperty.DotPathValue;
import com.google.gwt.resources.css.ast.CssProperty.ListValue;
import com.google.gwt.resources.css.ast.CssProperty.StringValue;
import com.google.gwt.resources.css.ast.CssProperty.Value;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Represents a sprited image. This is basically a normal CssRule, except for
 * one well-known property {@value IMAGE_PROPERTY_NAME}, which
 * specifies the name of an ImageResource accessor.
 */
public class CssSprite extends CssRule implements CssSubstitution {

  public static final String IMAGE_PROPERTY_NAME = "gwt-image";

  /**
   * A facade for the underlying CssProperty list maintained by CssRule. We
   * override the add and set methods to intercept the
   * {@value IMAGE_PROPERTY_NAME} property.
   */
  private class SpritePropertyList implements List<CssProperty> {
    private final List<CssProperty> source;

    public SpritePropertyList(List<CssProperty> source) {
      this.source = source;
    }

    @Override
    public boolean add(CssProperty o) {
      if (!processProperty(o)) {
        return source.add(o);
      } else {
        return false;
      }
    }

    @Override
    public void add(int index, CssProperty element) {
      if (!processProperty(element)) {
        source.add(index, element);
      }
    }

    @Override
    public boolean addAll(Collection<? extends CssProperty> c) {
      return source.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends CssProperty> c) {
      return source.addAll(index, c);
    }

    @Override
    public void clear() {
      source.clear();
    }

    @Override
    public boolean contains(Object o) {
      return source.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      return source.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
      return source.equals(o);
    }

    @Override
    public CssProperty get(int index) {
      return source.get(index);
    }

    @Override
    public int hashCode() {
      return source.hashCode();
    }

    @Override
    public int indexOf(Object o) {
      return source.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
      return source.isEmpty();
    }

    @Override
    public Iterator<CssProperty> iterator() {
      return source.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
      return source.lastIndexOf(o);
    }

    @Override
    public ListIterator<CssProperty> listIterator() {
      return source.listIterator();
    }

    @Override
    public ListIterator<CssProperty> listIterator(int index) {
      return source.listIterator(index);
    }

    @Override
    public CssProperty remove(int index) {
      return source.remove(index);
    }

    @Override
    public boolean remove(Object o) {
      return source.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      return source.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      return source.retainAll(c);
    }

    @Override
    public CssProperty set(int index, CssProperty element) {
      if (!processProperty(element)) {
        return source.set(index, element);
      } else {
        return source.remove(index);
      }
    }

    @Override
    public int size() {
      return source.size();
    }

    @Override
    public List<CssProperty> subList(int fromIndex, int toIndex) {
      return source.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
      return source.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
      return source.toArray(a);
    }
  }

  private DotPathValue resourceFunction;

  @Override
  public List<CssProperty> getProperties() {
    return new SpritePropertyList(super.getProperties());
  }

  public DotPathValue getResourceFunction() {
    return resourceFunction;
  }

  @Override
  public boolean isStatic() {
    return true;
  }

  public void setResourceFunction(DotPathValue resourceFunction) {
    this.resourceFunction = resourceFunction;
  }

  @Override
  public void traverse(CssVisitor visitor, Context context) {
    if (visitor.visit(this, context)) {
      visitor.acceptWithInsertRemove(selectors);
      visitor.acceptWithInsertRemove(getProperties());
    }
    visitor.endVisit(this, context);
  }

  private boolean processProperty(CssProperty property) {
    if (IMAGE_PROPERTY_NAME.equals(property.getName())) {
      setImageProperty(property.getValues());
      return true;
    }
    return false;
  }

  private void setImageProperty(Value value) {
    StringValue stringValue;
    ListValue listValue;

    if ((stringValue = value.isStringValue()) != null) {
      String s = stringValue.getValue();

      // Allow the user to use both raw idents and quoted strings
      if (s.startsWith("\"")) {
        s = s.substring(1, s.length() - 1);
      }

      resourceFunction = new DotPathValue(s);

    } else if ((listValue = value.isListValue()) != null) {
      List<Value> values = listValue.getValues();
      if (values.size() == 1) {
        setImageProperty(values.get(0));
      }

    } else {
      throw new IllegalArgumentException("The " + IMAGE_PROPERTY_NAME
          + " property of @sprite must have exactly one value");
    }
  }
}
