/*
 * Copyright 2010 Google Inc.
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
package org.hibernate.jsr303.tck.tests.constraints.groups;

import org.hibernate.jsr303.tck.tests.constraints.groups.GroupSequenceContainingDefaultValidatorFactory.GroupSequenceContainingDefaultValidator;
import org.hibernate.jsr303.tck.tests.constraints.groups.GroupSequenceWithNoImplicitDefaultGroupValidatorFactory.TestValidator;
import org.hibernate.jsr303.tck.util.TckCompileTestCase;

import java.util.regex.Pattern;

import javax.validation.GroupDefinitionException;

/**
 * Test wrapper for {@link DefaultGroupRedefinitionTest} tests that are meant to
 * fail to compile.
 */
public class DefaultGroupRedefinitionCompileTest extends TckCompileTestCase {

  public void testGroupSequenceContainingDefault() {
    assertValidatorFailsToCompile(
        GroupSequenceContainingDefaultValidator.class,
        GroupDefinitionException.class, Pattern.compile("Unable to create a validator for "
        + "org.hibernate.jsr303.tck.tests.constraints.groups."
        + "DefaultGroupRedefinitionTest.AddressWithDefaultInGroupSequence "
        + "because 'Default.class' cannot appear in default group "
        + "sequence list.", Pattern.LITERAL));
  }

  public void testGroupSequenceWithNoImplicitDefaultGroup() {
    assertValidatorFailsToCompile(TestValidator.class,
        GroupDefinitionException.class, Pattern.compile("Unable to create a validator for "
            + "org.hibernate.jsr303.tck.tests.constraints.groups."
            + "DefaultGroupRedefinitionTest.AddressWithDefaultInGroupSequence "
            + "because 'Default.class' cannot appear in default group "
            + "sequence list.", Pattern.LITERAL));
  }
}
