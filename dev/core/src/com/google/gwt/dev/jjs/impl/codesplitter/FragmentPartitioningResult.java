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
  private final int fragmentCount;
  private final int lastInitialFragment;

  FragmentPartitioningResult(Collection<Fragment> fragments, int runAsyncCount) {
    checkFragmentNumberingAssumtions(fragments);

    fragmentCount = fragments.size();
    // runAsync ids start from 1.
    this.runAsyncIdToFragment = new int[runAsyncCount + 1];
    int lastInitialFragmentId = -1;
    for (Fragment fragment : fragments) {
      // Fragments are assumed ordered by increasing ids.
      if (fragment.getType() == Fragment.Type.INITIAL) {
        Preconditions.checkState(lastInitialFragmentId < fragment.getFragmentId());
        lastInitialFragmentId = fragment.getFragmentId();
      }
      for (JRunAsync runAsync : fragment.getRunAsyncs()) {
        runAsyncIdToFragment[runAsync.getRunAsyncId()] = fragment.getFragmentId();
      }
    }
    this.lastInitialFragment = lastInitialFragmentId;
  }

  /**
   * @return the fragmentId for a fragment that is guaranteed to be loaded before
   * thisFragmentId and thatFragmentId
   */
  public int getCommonAncestorFragmentId(int thisFragmentId, int thatFragmentId) {
    if (thisFragmentId == thatFragmentId) {
      return thisFragmentId;
    }

    // If none of the fragments is initial, move to leftovers
    if (thisFragmentId > lastInitialFragment && thatFragmentId > lastInitialFragment) {
      return getLeftoverFragmentId();
    }
    // Return the one that occurs first in the initial load sequence.
    return Math.min(thisFragmentId, thatFragmentId);
  }

  /**
   * @return Fragment index from a splitpoint number.
   */
  public int getFragmentForRunAsync(int splitpoint) {
    return runAsyncIdToFragment[splitpoint];
  }

  /**
   * @return Fragment number of the left over fragment.
   */
  public int getLeftoverFragmentId() {
    return getFragmentCount() - 1;
  }

  /**
   * @return Total number of code fragments in the compilation (initial + exclusives + leftovers).
   */
  public int getFragmentCount() {
    return fragmentCount;
  }

  private void checkFragmentNumberingAssumtions(Collection<Fragment> fragments) {
    int lastSeenId = -1;
    Fragment.Type lastTypeSeen = Fragment.Type.INITIAL;
    boolean leftOversSeen = false;
    int leftOverId = -1;
    for (Fragment fragment : fragments) {

      // Fragments appear in the fragment list in ascending id order. first one being 0.
      Preconditions.checkState(fragment.getFragmentId() == lastSeenId + 1);
      lastSeenId = fragment.getFragmentId();

      // Fragments appear in the following order, first INTIALs, then EXCLUSIVEs and last 1
      // NOT_EXCLUSIVE
      Preconditions.checkState(fragment.getType().ordinal() >= lastTypeSeen.ordinal());
      lastTypeSeen = fragment.getType();

      if (fragment.getType() == Fragment.Type.NOT_EXCLUSIVE) {
        com.google.gwt.dev.util.Preconditions.checkState(!leftOversSeen);
        leftOversSeen = true;
        leftOverId = fragment.getFragmentId();
      }
    }
    // Lastly check that the left over is the last one if any.
    Preconditions.checkState(leftOverId == -1 || leftOverId == lastSeenId);
  }
}
