/*
 * Copyright 2012 Google Inc.
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
package com.google.gwt.i18n.shared;

import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * Methods for properly formatting lists in a locale-specific way.
 */
public interface ListPatterns {

  /**
   * Format an entry in a list.  This method is called repeatedly, starting from
   * the end of the list.  An example with 4 items in {@code val[0..3]}:
   * <ul>
   * <li>{@code String result = formatEntry(2, 4, val[2], val[3]);}
   * <li>{@code result = formatEntry(1, 4, val[1], result);}
   * <li>{@code result = formatEntry(0, 4, val[0], result);}
   * </ul>
   * Lists with 0-1 elements never call formatEntry (so the caller is responsible
   * for handling the empty list case).
   * 
   * @param index the index into the list, {@code 0} ... {@code n-2}
   * @param count the total number of elements in the list
   * @param left the element at index {@code index}
   * @param formattedTail the rightmost portion of the formatted list, or the last
   *     element of the list for the first call
   * @return a formatted list of all results so far
   */
  String formatEntry(int index, int count, String left, String formattedTail);

  /**
   * Format an entry in a {@link SafeHtml} list.  Exactly the same as
   * {@link #formatEntry(int, int, String, String)} except the individual
   * entries and the returned result is {@link SafeHtml}.
   * 
   * @param index
   * @param count
   * @param left
   * @param formattedTail
   * @return
   */
  SafeHtml formatEntry(int index, int count, SafeHtml left, SafeHtml formattedTail);
}
