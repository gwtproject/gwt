package com.google.gwt.dev.cfg;

import java.util.List;

/**
 * An immutable library group.<br />
 *
 * Makes a good starter value for libraryGroup variables in that it allows read calling code without
 * having to check for a null but will force write calling code to be guarded a condition that
 * verifies that values even should be inserted.
 */
public class ImmutableLibraryGroup extends LibraryGroup {

  public ImmutableLibraryGroup() {
  }

  @Override
  public LibraryGroup createSubgroup(List<String> libraryNames) {
    throw new UnsupportedOperationException();
  }
}
