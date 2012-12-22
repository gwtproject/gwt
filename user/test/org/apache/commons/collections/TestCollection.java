/*
 * Copyright 1999-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * Tests base {@link java.util.Collection} methods and contracts.
 * <p>
 * You should create a concrete subclass of this class to test any custom
 * {@link Collection} implementation.  At minimum, you'll have to
 * implement the {@link #makeCollection()} method.  You might want to
 * override some of the additional protected methods as well:<P>
 *
 * <B>Element Population Methods</B><P>
 *
 * Override these if your collection restricts what kind of elements are
 * allowed (for instance, if <Code>null</Code> is not permitted):
 * <UL>
 * <Li>{@link #getFullElements()}
 * <Li>{@link #getOtherElements()}
 * </UL>
 *
 * <B>Supported Operation Methods</B><P>
 *
 * Override these if your collection doesn't support certain operations:
 * <UL>
 * <LI>{@link #isAddSuppoted()}
 * <LI>{@link #isRemoveSupported()}
 * <li>{@link #areEqualElementsDistinguishable()}
 * </UL>
 *
 * <B>Fixture Methods</B><P>
 *
 * Fixtures are used to verify that the the operation results in correct state
 * for the collection.  Basically, the operation is performed against your
 * collection implementation, and an identical operation is performed against a
 * <I>confirmed</I> collection implementation.  A confirmed collection
 * implementation is something like <Code>java.util.ArrayList</Code>, which is
 * known to conform exactly to its collection interface's contract.  After the
 * operation takes place on both your collection implementation and the
 * confirmed collection implementation, the two collections are compared to see
 * if their state is identical.  The comparison is usually much more involved
 * than a simple <Code>equals</Code> test.  This verification is used to ensure
 * proper modifications are made along with ensuring that the collection does
 * not change when read-only modifications are made.<P>
 *
 * The {@link #collection} field holds an instance of your collection
 * implementation; the {@link #confirmed} field holds an instance of the
 * confirmed collection implementation.  The {@link #resetEmpty()} and
 * {@link #resetFull()} methods set these fields to empty or full collections,
 * so that tests can proceed from a known state.<P>
 *
 * After a modification operation to both {@link #collection} and
 * {@link #confirmed}, the {@link #verify()} method is invoked to compare
 * the results.  You may want to override {@link #verify()} to perform
 * additional verifications.  For instance, when testing the collection
 * views of a map, {@link TestMap} would override {@link #verify()} to make
 * sure the map is changed after the collection view is changed.
 *
 * If you're extending this class directly, you will have to provide
 * implementations for the following:
 * <UL>
 * <LI>{@link #makeConfirmedCollection()}
 * <LI>{@link #makeConfirmedFullCollection()}
 * </UL>
 *
 * Those methods should provide a confirmed collection implementation
 * that's compatible with your collection implementation.<P>
 *
 * If you're extending {@link TestList}, {@link TestSet},
 * or {@link TestBag}, you probably don't have to worry about the
 * above methods, because those three classes already override the methods
 * to provide standard JDK confirmed collections.<P>
 *
 * <B>Other notes</B><P>
 *
 * If your {@link Collection} fails one of these tests by design,
 * you may still use this base set of cases.  Simply override the
 * test case (method) your {@link Collection} fails.  For instance, the
 * {@link #testIteratorFailFast()} method is provided since most collections
 * have fail-fast iterators; however, that's not strictly required by the
 * collection contract, so you may want to override that method to do
 * nothing.<P>
 *
 * @author Rodney Waldhoff
 * @author Paul Jack
 * @author <a href="mailto:mas@apache.org">Michael A. Smith</a>
 * @version $Id: TestCollection.java,v 1.9.2.1 2004/05/22 12:14:05 scolebourne Exp $
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class TestCollection extends TestObject {

    //
    // NOTE:
    //
    // Collection doesn't define any semantics for equals, and recommends you
    // use reference-based default behavior of Object.equals.  (And a test for
    // that already exists in TestObject).  Tests for equality of lists, sets
    // and bags will have to be written in test subclasses.  Thus, there is no
    // tests on Collection.equals nor any for Collection.hashCode.
    //


    // These fields are used by reset() and verify(), and any test
    // method that tests a modification.

    /**
     *  A collection instance that will be used for testing.
     */
    protected Collection collection;

    /**
     *  Confirmed collection.  This is an instance of a collection that is
     *  confirmed to conform exactly to the java.util.Collection contract.
     *  Modification operations are tested by performing a mod on your
     *  collection, performing the exact same mod on an equivalent confirmed
     *  collection, and then calling verify() to make sure your collection
     *  still matches the confirmed collection.
     */
    protected Collection confirmed;



    /**
     *  Resets the {@link #collection} and {@link #confirmed} fields to empty
     *  collections.  Invoke this method before performing a modification
     *  test.
     */
    protected void resetEmpty() {
        this.collection = makeCollection();
        this.confirmed = makeConfirmedCollection();
    }


    /**
     *  Resets the {@link #collection} and {@link #confirmed} fields to full
     *  collections.  Invoke this method before performing a modification
     *  test.
     */
    protected void resetFull() {
        this.collection = makeFullCollection();
        this.confirmed = makeConfirmedFullCollection();
    }

    /**
     *  Specifies whether equal elements in the collection are, in fact,
     *  distinguishable with information not readily available.  That is, if a
     *  particular value is to be removed from the collection, then there is
     *  one and only one value that can be removed, even if there are other
     *  elements which are equal to it.
     *
     *  <P>In most collection cases, elements are not distinguishable (equal is
     *  equal), thus this method defaults to return false.  In some cases,
     *  however, they are.  For example, the collection returned from the map's
     *  values() collection view are backed by the map, so while there may be
     *  two values that are equal, their associated keys are not.  Since the
     *  keys are distinguishable, the values are.
     *
     *  <P>This flag is used to skip some verifications for iterator.remove()
     *  where it is impossible to perform an equivalent modification on the
     *  confirmed collection because it is not possible to determine which
     *  value in the confirmed collection to actually remove.  Tests that
     *  override the default (i.e. where equal elements are distinguishable),
     *  should provide additional tests on iterator.remove() to make sure the
     *  proper elements are removed when remove() is called on the iterator.
     **/
    protected boolean areEqualElementsDistinguishable() {
        return false;
    }

    /**
     *  Verifies that {@link #collection} and {@link #confirmed} have
     *  identical state.
     */
    protected void verify() {
        int confirmedSize = confirmed.size();
        assertEquals("Collection size should match confirmed collection's",
                     confirmedSize, collection.size());
        assertEquals("Collection isEmpty() result should match confirmed " +
                     " collection's",
                     confirmed.isEmpty(), collection.isEmpty());

        // verify the collections are the same by attempting to match each
        // object in the collection and confirmed collection.  To account for
        // duplicates and differing orders, each confirmed element is copied
        // into an array and a flag is maintained for each element to determine
        // whether it has been matched once and only once.  If all elements in
        // the confirmed collection are matched once and only once and there
        // aren't any elements left to be matched in the collection,
        // verification is a success.

        // copy each collection value into an array
        Object[] confirmedValues = new Object[confirmedSize];

        Iterator iter;

        iter = confirmed.iterator();
        int pos = 0;
        while(iter.hasNext()) {
            confirmedValues[pos++] = iter.next();
        }

        // allocate an array of boolean flags for tracking values that have
        // been matched once and only once.
        boolean[] matched = new boolean[confirmedSize];

        // now iterate through the values of the collection and try to match
        // the value with one in the confirmed array.
        iter = collection.iterator();
        while(iter.hasNext()) {
            Object o = iter.next();
            boolean match = false;
            for(int i = 0; i < confirmedSize; i++) {
                if(matched[i]) {
                    // skip values already matched
                    continue;
                }
                if(o == confirmedValues[i] ||
                   (o != null && o.equals(confirmedValues[i]))) {
                    // values matched
                    matched[i] = true;
                    match = true;
                    break;
                }
            }
            // no match found!
            if(!match) {
                fail("Collection should not contain a value that the " +
                     "confirmed collection does not have.");
            }
        }

        // make sure there aren't any unmatched values
        for(int i = 0; i < confirmedSize; i++) {
            if(!matched[i]) {
                // the collection didn't match all the confirmed values
                fail("Collection should contain all values that are in the " +
                     "confirmed collection");
            }
        }
    }


    /**
     *  Returns a confirmed empty collection.
     *  For instance, a {@link java.util.ArrayList} for lists or a
     *  {@link java.util.HashSet} for sets.
     *
     *  @return a confirmed empty collection
     */
    protected abstract Collection makeConfirmedCollection();



    /**
     *  Returns a confirmed full collection.
     *  For instance, a {@link java.util.ArrayList} for lists or a
     *  {@link java.util.HashSet} for sets.  The returned collection
     *  should contain the elements returned by {@link #getFullElements()}.
     *
     *  @return a confirmed full collection
     */
    protected abstract Collection makeConfirmedFullCollection();


    /**
     *  Returns true if the collections produced by
     *  {@link #makeCollection()} and {@link #makeFullCollection()}
     *  support the <Code>add</Code> and <Code>addAll</Code>
     *  operations.<P>
     *  Default implementation returns true.  Override if your collection
     *  class does not support add or addAll.
     */
    protected boolean isAddSupported() {
        return true;
    }


    /**
     *  Returns true if the collections produced by
     *  {@link #makeCollection()} and {@link #makeFullCollection()}
     *  support the <Code>remove</Code>, <Code>removeAll</Code>,
     *  <Code>retainAll</Code>, <Code>clear</Code> and
     *  <Code>iterator().remove()</Code> methods.
     *  Default implementation returns true.  Override if your collection
     *  class does not support removal operations.
     */
    protected boolean isRemoveSupported() {
        return true;
    }


    /**
     *  Returns an array of objects that are contained in a collection
     *  produced by {@link #makeFullCollection()}.  Every element in the
     *  returned array <I>must</I> be an element in a full collection.<P>
     *  The default implementation returns a heterogenous array of
     *  objects with some duplicates and with the null element.
     *  Override if you require specific testing elements.  Note that if you
     *  override {@link #makeFullCollection()}, you <I>must</I> override
     *  this method to reflect the contents of a full collection.
     */
    protected Object[] getFullElements() {
        ArrayList list = new ArrayList();

        list.addAll(Arrays.asList(getFullNonNullElements()));
        list.add(4, null);
        return list.toArray();
    }


    /**
     *  Returns an array of elements that are <I>not</I> contained in a
     *  full collection.  Every element in the returned array must
     *  not exist in a collection returned by {@link #makeFullCollection()}.
     *  The default implementation returns a heterogenous array of elements
     *  without null.  Note that some of the tests add these elements
     *  to an empty or full collection, so if your collection restricts
     *  certain kinds of elements, you should override this method.
     */
    protected Object[] getOtherElements() {
        return getOtherNonNullElements();
    }


    /**
     * Returns a new, empty {@link Collection} to be used for testing.
     */
    protected abstract Collection makeCollection();


    /**
     *  Returns a full collection to be used for testing.  The collection
     *  returned by this method should contain every element returned by
     *  {@link #getFullElements()}.  The default implementation, in fact,
     *  simply invokes <Code>addAll</Code> on an empty collection with
     *  the results of {@link #getFullElements()}.  Override this default
     *  if your collection doesn't support addAll.
     */
    protected Collection makeFullCollection() {
        Collection c = makeCollection();
        c.addAll(Arrays.asList(getFullElements()));
        return c;
    }


    /**
     *  Returns an empty collection for Object tests.
     */
    public Object makeObject() {
        return makeCollection();
    }


    /**
     *  Tests {@link Collection#add(Object)}.
     */
    public void testCollectionAdd() {
        if (!isAddSupported()) return;

        Object[] elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            resetEmpty();
            boolean r = collection.add(elements[i]);
            confirmed.add(elements[i]);
            verify();
            assertTrue("Empty collection changed after add", r);
            assertTrue("Collection size is 1 after first add",
                       collection.size() == 1);
        }

        resetEmpty();
        int size = 0;
        for (int i = 0; i < elements.length; i++) {
            boolean r = collection.add(elements[i]);
            confirmed.add(elements[i]);
            verify();
            if (r) size++;
            assertEquals("Collection size should grow after add",
                         size, collection.size());
            assertTrue("Collection should contain added element",
                       collection.contains(elements[i]));
        }
    }


    /**
     *  Tests {@link Collection#addAll(Collection)}.
     */
    public void testCollectionAddAll() {
        if (!isAddSupported()) return;

        resetEmpty();
        Object[] elements = getFullElements();
        boolean r = collection.addAll(Arrays.asList(elements));
        confirmed.addAll(Arrays.asList(elements));
        verify();
        assertTrue("Empty collection should change after addAll", r);
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Collection should contain added element",
                       collection.contains(elements[i]));
        }

        resetFull();
        int size = collection.size();
        elements = getOtherElements();
        r = collection.addAll(Arrays.asList(elements));
        confirmed.addAll(Arrays.asList(elements));
        verify();
        assertTrue("Full collection should change after addAll", r);
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Full collection should contain added element",
                       collection.contains(elements[i]));
        }
        assertEquals("Size should increase after addAll",
                     size + elements.length, collection.size());

        resetFull();
        size = collection.size();
        r = collection.addAll(Arrays.asList(getFullElements()));
        confirmed.addAll(Arrays.asList(getFullElements()));
        verify();
        if (r) {
            assertTrue("Size should increase if addAll returns true",
                       size < collection.size());
        } else {
            assertEquals("Size should not change if addAll returns false",
                         size, collection.size());
        }
    }


    /**
     *  If {@link #isAddSupported()} returns false, tests that add operations
     *  raise <Code>UnsupportedOperationException.
     */
    public void testUnsupportedAdd() {
        if (isAddSupported()) return;

        resetEmpty();
        try {
            collection.add(new Object());
            fail("Emtpy collection should not support add.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        try {
            collection.addAll(Arrays.asList(getFullElements()));
            fail("Emtpy collection should not support addAll.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        resetFull();
        try {
            collection.add(new Object());
            fail("Full collection should not support add.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();

        try {
            collection.addAll(Arrays.asList(getOtherElements()));
            fail("Full collection should not support addAll.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        // make sure things didn't change even if the expected exception was
        // thrown.
        verify();
    }


    /**
     *  Test {@link Collection#clear()}.
     */
    public void testCollectionClear() {
        if (!isRemoveSupported()) return;

        resetEmpty();
        collection.clear(); // just to make sure it doesn't raise anything
        verify();

        resetFull();
        collection.clear();
        confirmed.clear();
        verify();
    }


    /**
     *  Tests {@link Collection#contains(Object)}.
     */
    public void testCollectionContains() {
        Object[] elements;

        resetEmpty();
        elements = getFullElements();
        for(int i = 0; i < elements.length; i++) {
            assertTrue("Empty collection shouldn'y contain element",
                       !collection.contains(elements[i]));
        }
        // make sure calls to "contains" don't change anything
        verify();

        elements = getOtherElements();
        for(int i = 0; i < elements.length; i++) {
            assertTrue("Empty collection shouldn'y contain element",
                       !collection.contains(elements[i]));
        }
        // make sure calls to "contains" don't change anything
        verify();

        resetFull();
        elements = getFullElements();
        for(int i = 0; i < elements.length; i++) {
            assertTrue("Full collection should contain element.",
                       collection.contains(elements[i]));
        }
        // make sure calls to "contains" don't change anything
        verify();

        resetFull();
        elements = getOtherElements();
        for(int i = 0; i < elements.length; i++) {
            assertTrue("Full collection shouldn't contain element",
                       !collection.contains(elements[i]));
        }
    }


    /**
     *  Tests {@link Collection#containsAll(Collection)}.
     */
    public void testCollectionContainsAll() {
        resetEmpty();
        Collection col = new HashSet();
        assertTrue("Every Collection should contain all elements of an " +
                   "empty Collection.", collection.containsAll(col));
        col.addAll(Arrays.asList(getOtherElements()));
        assertTrue("Empty Collection shouldn't contain all elements of " +
                   "a non-empty Collection.", !collection.containsAll(col));
        // make sure calls to "containsAll" don't change anything
        verify();

        resetFull();
        assertTrue("Full collection shouldn't contain other elements",
                   !collection.containsAll(col));

        col.clear();
        col.addAll(Arrays.asList(getFullElements()));
        assertTrue("Full collection should containAll full elements",
                   collection.containsAll(col));
        // make sure calls to "containsAll" don't change anything
        verify();


        assertTrue("Full collection should containAll itself",
                   collection.containsAll(collection));

        // make sure calls to "containsAll" don't change anything
        verify();

        col = new ArrayList();
        col.addAll(Arrays.asList(getFullElements()));
        col.addAll(Arrays.asList(getFullElements()));
        assertTrue("Full collection should containAll duplicate full " +
                   "elements", collection.containsAll(col));

        // make sure calls to "containsAll" don't change anything
        verify();
    }

    /**
     *  Tests {@link Collection#isEmpty()}.
     */
    public void testCollectionIsEmpty() {
        resetEmpty();
        assertEquals("New Collection should be empty.",
                     true, collection.isEmpty());
        // make sure calls to "isEmpty() don't change anything
        verify();

        resetFull();
        assertEquals("Full collection shouldn't be empty",
                     false, collection.isEmpty());
        // make sure calls to "isEmpty() don't change anything
        verify();
    }


    /**
     *  Tests the read-only functionality of {@link Collection#iterator()}.
     */
    public void testCollectionIterator() {
        resetEmpty();
        Iterator it1 = collection.iterator();
        assertEquals("Iterator for empty Collection shouldn't have next.",
                     false, it1.hasNext());
        try {
            it1.next();
            fail("Iterator at end of Collection should throw " +
                 "NoSuchElementException when next is called.");
        } catch(NoSuchElementException e) {
            // expected
        }
        // make sure nothing has changed after non-modification
        verify();

        resetFull();
        it1 = collection.iterator();
        for (int i = 0; i < collection.size(); i++) {
            assertTrue("Iterator for full collection should haveNext",
                       it1.hasNext());
            it1.next();
        }
        assertTrue("Iterator should be finished", !it1.hasNext());

        ArrayList list = new ArrayList();
        it1 = collection.iterator();
        for (int i = 0; i < collection.size(); i++) {
            Object next = it1.next();
            assertTrue("Collection should contain element returned by " +
                       "its iterator", collection.contains(next));
            list.add(next);
        }
        try {
            it1.next();
            fail("iterator.next() should raise NoSuchElementException " +
                 "after it finishes");
        } catch (NoSuchElementException e) {
            // expected
        }
        // make sure nothing has changed after non-modification
        verify();
    }


    /**
     *  Tests removals from {@link Collection#iterator()}.
     */
    public void testCollectionIteratorRemove() {
        if (!isRemoveSupported()) return;

        resetEmpty();
        try {
            collection.iterator().remove();
            fail("New iterator.remove should raise IllegalState");
        } catch (IllegalStateException e) {
            // expected
        }
        verify();

        try {
            Iterator iter = collection.iterator();
            iter.hasNext();
            iter.remove();
            fail("New iterator.remove should raise IllegalState " +
                 "even after hasNext");
        } catch (IllegalStateException e) {
            // expected
        }
        verify();

        resetFull();
        int size = collection.size();
        Iterator iter = collection.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            iter.remove();

            // if the elements aren't distinguishable, we can just remove a
            // matching element from the confirmed collection and verify
            // contents are still the same.  Otherwise, we don't have the
            // ability to distinguish the elements and determine which to
            // remove from the confirmed collection (in which case, we don't
            // verify because we don't know how).
            //
            // see areEqualElementsDistinguishable()
            if(!areEqualElementsDistinguishable()) {
                confirmed.remove(o);
                verify();
            }

            size--;
            assertEquals("Collection should shrink by one after " +
                         "iterator.remove", size, collection.size());
        }
        assertTrue("Collection should be empty after iterator purge",
                   collection.isEmpty());

        resetFull();
        iter = collection.iterator();
        iter.next();
        iter.remove();
        try {
            iter.remove();
            fail("Second iter.remove should raise IllegalState");
        } catch (IllegalStateException e) {
            // expected
        }
    }


    /**
     *  Tests {@link Collection#remove(Object)}.
     */
    public void testCollectionRemove() {
        if (!isRemoveSupported()) return;

        resetEmpty();
        Object[] elements = getFullElements();
        for (int i = 0; i < elements.length; i++) {
            assertTrue("Shouldn't remove nonexistent element",
                       !collection.remove(elements[i]));
            verify();
        }

        Object[] other = getOtherElements();

        resetFull();
        for (int i = 0; i < other.length; i++) {
            assertTrue("Shouldn't remove nonexistent other element",
                       !collection.remove(other[i]));
            verify();
        }

        int size = collection.size();
        for (int i = 0; i < elements.length; i++) {
            resetFull();
            assertTrue("Collection should remove extant element",
                       collection.remove(elements[i]));

            // if the elements aren't distinguishable, we can just remove a
            // matching element from the confirmed collection and verify
            // contents are still the same.  Otherwise, we don't have the
            // ability to distinguish the elements and determine which to
            // remove from the confirmed collection (in which case, we don't
            // verify because we don't know how).
            //
            // see areEqualElementsDistinguishable()
            if(!areEqualElementsDistinguishable()) {
                confirmed.remove(elements[i]);
                verify();
            }

            assertEquals("Collection should shrink after remove",
                         size - 1, collection.size());
        }
    }


    /**
     *  Tests {@link Collection#removeAll(Collection)}.
     */
    public void testCollectionRemoveAll() {
        if (!isRemoveSupported()) return;

        resetEmpty();
        assertTrue("Emtpy collection removeAll should return false for " +
                   "empty input",
                   !collection.removeAll(Collections.EMPTY_SET));
        verify();

        assertTrue("Emtpy collection removeAll should return false for " +
                   "nonempty input",
                   !collection.removeAll(new ArrayList(collection)));
        verify();

        resetFull();
        assertTrue("Full collection removeAll should return false for " +
                   "empty input",
                   !collection.removeAll(Collections.EMPTY_SET));
        verify();

        assertTrue("Full collection removeAll should return false for " +
                   "other elements",
                   !collection.removeAll(Arrays.asList(getOtherElements())));
        verify();

        assertTrue("Full collection removeAll should return true for " +
                   "full elements",
                   collection.removeAll(new HashSet(collection)));
        confirmed.removeAll(new HashSet(confirmed));
        verify();

        resetFull();

        int size = collection.size();
        Object[] s = getFullElements();
        List l = new ArrayList();
        l.add(s[2]); l.add(s[3]); l.add(s[3]);

        assertTrue("Full collection removeAll should work",
                   collection.removeAll(l));
        confirmed.removeAll(l);
        verify();

        assertTrue("Collection should shrink after removeAll",
                   collection.size() < size);
        Iterator iter = l.iterator();
        while (iter.hasNext()) {
            assertTrue("Collection shouldn't contain removed element",
                       !collection.contains(iter.next()));
        }
    }


    /**
     *  Tests {@link Collection#retainAll(Collection)}.
     */
    public void testCollectionRetainAll() {
        if (!isRemoveSupported()) return;

        resetEmpty();
        List elements = Arrays.asList(getFullElements());
        List other = Arrays.asList(getOtherElements());
        Set empty = new HashSet();

        assertTrue("Empty retainAll() should return false",
                   !collection.retainAll(empty));
        verify();

        assertTrue("Empty retainAll() should return false",
                   !collection.retainAll(elements));
        verify();

        resetFull();
        assertTrue("Collection should change from retainAll empty",
                   collection.retainAll(empty));
        confirmed.retainAll(empty);
        verify();

        resetFull();
        assertTrue("Collection changed from retainAll other",
                   collection.retainAll(other));
        confirmed.retainAll(other);
        verify();

        resetFull();
        int size = collection.size();
        assertTrue("Collection shouldn't change from retainAll elements",
                   !collection.retainAll(elements));
        verify();
        assertEquals("Collection size shouldn't change", size,
                     collection.size());


        resetFull();
        HashSet set = new HashSet(elements);
        size = collection.size();
        assertTrue("Collection shouldn't change from retainAll without " +
                   "duplicate elements", !collection.retainAll(set));
        verify();
        assertEquals("Collection size didn't change from nonduplicate " +
                     "retainAll", size, collection.size());
    }


    /**
     *  Tests {@link Collection#size()}.
     */
    public void testCollectionSize() {
        resetEmpty();
        assertEquals("Size of new Collection is 0.", 0, collection.size());

        resetFull();
        assertTrue("Size of full collection should be greater than zero",
                   collection.size() > 0);
    }




    /**
     *  Tests <Code>toString</Code> on a collection.
     */
    public void testCollectionToString() {
        resetEmpty();
        assertTrue("toString shouldn't return null",
                   collection.toString() != null);

        resetFull();
        assertTrue("toString shouldn't return null",
                   collection.toString() != null);
    }


    /**
     *  If isRemoveSupported() returns false, tests to see that remove
     *  operations raise an UnsupportedOperationException.
     */
    public void testUnsupportedRemove() {
        if (isRemoveSupported()) return;

        resetEmpty();
        try {
            collection.clear();
            fail("clear should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();

        try {
            collection.remove(null);
            fail("remove should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();

        try {
            collection.removeAll(null);
            fail("removeAll should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();

        try {
            collection.retainAll(null);
            fail("removeAll should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();

        resetFull();
        try {
            Iterator iterator = collection.iterator();
            iterator.next();
            iterator.remove();
            fail("iterator.remove should raise UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        verify();

    }



    /**
     *  Returns a list of elements suitable for return by
     *  {@link #getFullElements()}.  The array returned by this method
     *  does not include null, but does include a variety of objects
     *  of different types.  Override getFullElements to return
     *  the results of this method if your collection does not support
     *  the null element.
     */
    public static Object[] getFullNonNullElements() {
        return new Object[] {
            new String(""),
            new String("One"),
            new Integer(2),
            "Three",
            new Integer(4),
            "One",
            new Double(5),
            new Float(6),
            "Seven",
            "Eight",
            new String("Nine"),
            new Integer(10),
            new Short((short)11),
            new Long(12),
            "Thirteen",
            "14",
            "15",
            new Byte((byte)16)
        };
    }


    /**
     *  Returns the default list of objects returned by
     *  {@link #getOtherElements()}.  Includes many objects
     *  of different types.
     */
    public static Object[] getOtherNonNullElements() {
        return new Object[] {
            new Integer(0),
            new Float(0),
            new Double(0),
            "Zero",
            new Short((short)0),
            new Byte((byte)0),
            new Long(0),
            new Character('\u0000'),
            "0"
        };
    }



    /**
     *  Returns a list of string elements suitable for return by
     *  {@link #getFullElements()}.  Override getFullElements to return
     *  the results of this method if your collection does not support
     *  heterogenous elements or the null element.
     */
    public static Object[] getFullNonNullStringElements() {
        return new Object[] {
            "If","the","dull","substance","of","my","flesh","were","thought",
            "Injurious","distance","could","not","stop","my","way",
        };
    }


    /**
     *  Returns a list of string elements suitable for return by
     *  {@link #getOtherElements()}.  Override getOtherElements to return
     *  the results of this method if your collection does not support
     *  heterogenous elements or the null element.
     */
    public static Object[] getOtherNonNullStringElements() {
        return new Object[] {
            "For","then","despite",/* of */"space","I","would","be","brought",
            "From","limits","far","remote","where","thou","dost","stay"
        };
    }
}
