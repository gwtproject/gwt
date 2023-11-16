/*
 * Copyright 2019 Google Inc.
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

package java.util.concurrent.atomic;

/**
 * GWT emulation of AtomicReference.
 *
 * @param <V> The type of object referred to by this reference
 */
public class AtomicReference<V> {

  private V value;

  public AtomicReference() {
  }

  public AtomicReference(V initialValue) {
    value = initialValue;
  }

  public final boolean compareAndSet(V expect, V update) {
    if (value == expect) {
      value = update;
      return true;
    } else {
      return false;
    }
  }

  public final V get() {
    return value;
  }

  public final V getAndSet(V newValue) {
    V oldValue = value;
    value = newValue;
    return oldValue;
  }

  public final void lazySet(V newValue) {
    set(newValue);
  }

  public final void set(V newValue) {
    value = newValue;
  }

  public final boolean weakCompareAndSet(V expect, V update) {
    return compareAndSet(expect, update);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}
