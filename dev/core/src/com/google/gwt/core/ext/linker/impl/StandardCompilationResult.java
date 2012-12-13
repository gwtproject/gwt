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
package com.google.gwt.core.ext.linker.impl;

import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.SelectionProperty;
import com.google.gwt.core.ext.linker.SoftPermutation;
import com.google.gwt.core.ext.linker.StatementRanges;
import com.google.gwt.core.ext.linker.SymbolData;
import com.google.gwt.dev.jjs.PermutationResult;
import com.google.gwt.dev.util.DiskCache;
import com.google.gwt.dev.util.Util;
import com.google.gwt.dev.util.collect.Lists;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The standard implementation of {@link CompilationResult}.
 */
public class StandardCompilationResult extends CompilationResult {

  private static final class MapComparator implements
      Comparator<SortedMap<SelectionProperty, String>> {
    public int compare(SortedMap<SelectionProperty, String> arg0,
        SortedMap<SelectionProperty, String> arg1) {
      int diff = arg0.size() - arg1.size();
      if (diff != 0) {
        return diff;
      }

      Iterator<String> i0 = arg0.values().iterator();
      Iterator<String> i1 = arg1.values().iterator();

      StringBuffer sb0 = new StringBuffer();
      StringBuffer sb1 = new StringBuffer();

      while (i0.hasNext()) {
        assert i1.hasNext();
        sb0.append(i0.next());
        sb1.append(i1.next());
      }
      assert !i1.hasNext();

      return sb0.toString().compareTo(sb1.toString());
    }
  }

  private static final SoftPermutation[] EMPTY_SOFT_PERMUTATION_ARRAY = {};

  /**
   * Smaller maps come before larger maps, then we compare the concatenation of
   * every value.
   */
  public static final Comparator<SortedMap<SelectionProperty, String>> MAP_COMPARATOR = new MapComparator();

  private static final DiskCache diskCache = DiskCache.INSTANCE;

  private long[] jsToken;

  private SortedSet<SortedMap<SelectionProperty, String>> propertyValues = new TreeSet<SortedMap<SelectionProperty, String>>(
      MAP_COMPARATOR);

  private List<SoftPermutation> softPermutations = Lists.create();

  private StatementRanges[] statementRanges;

  private String strongName;

  private long symbolToken;

  private int permutationId;

  public StandardCompilationResult(PermutationResult permutationResult) {
    super(StandardLinkerContext.class);
    byte[][] js = permutationResult.getJs();
    this.strongName = Util.computeStrongName(js);
    byte[] serializedSymbolMap = permutationResult.getSerializedSymbolMap();
    this.statementRanges = permutationResult.getStatementRanges();
    this.permutationId = permutationResult.getPermutation().getId();
    this.jsToken = new long[js.length];
    for (int i = 0; i < jsToken.length; ++i) {
      jsToken[i] = diskCache.writeByteArray(js[i]);
    }
    this.symbolToken = diskCache.writeByteArray(serializedSymbolMap);
  }

  /**
   * Record a particular permutation of SelectionProperty values that resulted
   * in the compilation.
   */
  public void addSelectionPermutation(Map<SelectionProperty, String> values) {
    SortedMap<SelectionProperty, String> map = new TreeMap<SelectionProperty, String>(
        StandardLinkerContext.SELECTION_PROPERTY_COMPARATOR);
    map.putAll(values);
    propertyValues.add(Collections.unmodifiableSortedMap(map));
  }

  public void addSoftPermutation(Map<SelectionProperty, String> propertyMap) {
    softPermutations = Lists.add(softPermutations, new StandardSoftPermutation(
        softPermutations.size(), propertyMap));
  }

  @Override
  public String[] getJavaScript() {
    String[] js = new String[jsToken.length];
    for (int i = 0; i < jsToken.length; ++i) {
      js[i] = diskCache.readString(jsToken[i]);
    }
    return js;
  }

  @Override
  public int getPermutationId() {
    return permutationId;
  }

  @Override
  public SortedSet<SortedMap<SelectionProperty, String>> getPropertyMap() {
    return Collections.unmodifiableSortedSet(propertyValues);
  }

  @Override
  public SoftPermutation[] getSoftPermutations() {
    return softPermutations.toArray(new SoftPermutation[softPermutations.size()]);
  }

  @Override
  public StatementRanges[] getStatementRanges() {
    return statementRanges;
  }

  @Override
  public String getStrongName() {
    return strongName;
  }

  @Override
  public SymbolData[] getSymbolMap() {
    return diskCache.readObject(symbolToken, SymbolData[].class);
  }

  /**
   * Empty constructor for externalization.
   */
  public  StandardCompilationResult() {
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeInt(jsToken.length);
    for (long tok : jsToken) {
      out.writeLong(tok);
    }
    Util.serializeCollection(propertyValues, out);
    Util.serializeCollection(softPermutations, out);
    out.writeInt(statementRanges.length);
    for (StatementRanges rng : statementRanges) {
      out.writeObject(rng);
    }
    Util.serializeString(strongName, out);
    out.writeLong(symbolToken);
    out.writeInt(permutationId);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    int sz = in.readInt();
    jsToken = new long[sz];
    for (int i = 0; i < sz; i++) {
      jsToken[i] = in.readLong();
    }
    propertyValues = (SortedSet) Util.deserializeObjectSet(in, SortedMap.class, TreeSet.class);
    softPermutations = Util.deserializeObjectList(in);
    sz = in.readInt();
    statementRanges = new StatementRanges[sz];
    for (int i = 0; i < sz; i++) {
      statementRanges[i] = (StatementRanges) in.readObject();
    }
    strongName = Util.deserializeString(in);
    symbolToken = in.readLong();
    permutationId = in.readInt();
  }
}
