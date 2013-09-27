/*
 * Copyright 2013 Google Inc.
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
package com.google.gwt.dev.jjs.impl.codesplitter;

import com.google.gwt.dev.jjs.ast.JRunAsync;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;

import java.util.Collection;

/**
 * A read-only class that holds some information about the result of the
 * partition process.
 */
public class FragmentPartitioningResult {
  private final int[] runAsyncIdToFragment;
  private final int numFragments;
  private final int lastInitialFragment;

  FragmentPartitioningResult(Collection<Fragment> fragments, int numberOfRunAsyncs) {
    numFragments = fragments.size();
    // runAsync ids start from 1.
    this.runAsyncIdToFragment = new int[numberOfRunAsyncs + 1];
    int lastInitial = -1;
    for (Fragment fragment : fragments) {
      // Fragments are assumed ordered by increasing ids.
      if (fragment.getType() == Fragment.Type.INITIAL) {
        Preconditions.checkState(lastInitial < fragment.getFragmentId());
        lastInitial = fragment.getFragmentId();
      }
      for (JRunAsync runAsync : fragment.getRunAsyncs()) {
        runAsyncIdToFragment[runAsync.getRunAsyncId()] = fragment.getFragmentId();
      }
    }
    this.lastInitialFragment = lastInitial;
  }

  /**
   * @return Fragment index from a splitpoint number.
   */
  public int getFragmentForRunAsync(int splitpoint) {
    return runAsyncIdToFragment[splitpoint];
  }

  /**
   * @return a fragment that is guaranteed to be loaded before thisFragmen and thatFragment
   */
  public int getSafeSharedFragment(int thisFragment, int thatFragment) {
    if (thisFragment == thatFragment) {
      return thisFragment;
    }

    // If none of the fragments is initial, move to leftovers
    if (thisFragment > lastInitialFragment && thatFragment > lastInitialFragment) {
      return getLeftoverFragmentId();
    }
    // Return the one that occurs first in the initial load sequence.
    return thisFragment < thatFragment ? thisFragment : thatFragment;
  }


  /**
   * @return Fragment number of the left over fragment.
   */
  public int getLeftoverFragmentId() {
    return getNumFragments() - 1;
  }

  /**
   * @return Total number of code fragments in the compilation (initial + exclusives + leftovers).
   */
  public int getNumFragments() {
    return numFragments;
  }
}
