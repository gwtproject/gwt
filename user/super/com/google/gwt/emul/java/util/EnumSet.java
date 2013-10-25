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
package java.util;

/**
 * A {@link java.util.Set} of {@link Enum}s. <a
 * href="http://java.sun.com/j2se/1.5.0/docs/api/java/util/EnumSet.html">[Sun
 * docs]</a>
 * 
 * @param <E> enumeration type
 */
public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E> {

  /**
   * Implemented via bit vectors. Iteration takes linear time with respect to
   * the number of elements in the set.
   * 
   * Note: Implemented as a subclass instead of a concrete final EnumSet class.
   * This is because declaring an EnumSet.add(E) causes hosted mode to bind to
   * the tighter method rather than the bridge method; but the tighter method
   * isn't available in the real JRE.
   */
  static final class EnumSetImpl<E extends Enum<E>> extends EnumSet<E> {
    private class IteratorImpl implements Iterator<E> {

      /**
       * The index of the last element returned. This helps us know which
       * element to remove.
       */
      private int lastReturnedIndex = -1;

      /**
       * The index of the section we're looking at.
       */
      private int currentSectionIndex = 0;

      /**
       * The elements in the current section that have not yet been returned.
       */
      private int currentSectionUnreturnedElements = sections[currentSectionIndex];

      public boolean hasNext() {
        while (currentSectionUnreturnedElements == 0 && currentSectionIndex < sections.length - 1) {
          currentSectionIndex++;
          currentSectionUnreturnedElements = sections[currentSectionIndex];
        }
        return currentSectionUnreturnedElements != 0;
      }

      public E next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        int nextElementBitVector = currentSectionUnreturnedElements
            & -currentSectionUnreturnedElements;
        int nextElementIndexInSection = Integer.numberOfTrailingZeros(nextElementBitVector);
        lastReturnedIndex = currentSectionIndex * Integer.SIZE + nextElementIndexInSection;
        E nextElement = all[lastReturnedIndex];
        // Since we're about to return the next element, remove it from
        // currentSectionUnreturnedElements.
        currentSectionUnreturnedElements &= ~nextElementBitVector;
        return nextElement;
      }

      public void remove() {
        if (lastReturnedIndex < 0) {
          throw new IllegalStateException("Call next() before calling remove().");
        }
        E elementToRemove = all[lastReturnedIndex];
        if (!contains(elementToRemove)) {
          /*
           * We've already removed the element. According to the javadoc, we are supposed to
           * throw an exception.
           */
          throw new IllegalStateException("remove() can only be called once for an element.");
        }
        EnumSetImpl.this.remove(elementToRemove);
      }
    }

    /**
     * All enums; reference to the class's copy; must not be modified.
     */
    private final E[] all;

    /**
     * Live enums in the set.
     */
    private final int[] sections;

    /**
     * Constructs an empty set.
     */
    public EnumSetImpl(E[] all) {
      this.all = all;
      this.sections = new int[getNumberOfSections(all.length)];
    }

    /**
     * Constructs a set and takes ownership of the given sections.
     */
    public EnumSetImpl(E[] all, int[] sections) {
      this.all = all;
      this.sections = sections;
    }

    private boolean isSameType(Enum e) {
      return e.ordinal() < all.length && all[e.ordinal()] == e;
    }

    @Override
    public boolean add(E e) {
      if (e == null) {
        throw new NullPointerException();
      }
      int sectionIndex = e.ordinal() / Integer.SIZE;
      int previousValue = sections[sectionIndex];
      sections[sectionIndex] |= 1 << e.ordinal();
      return sections[sectionIndex] != previousValue;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
      if (!(c instanceof EnumSetImpl)) {
        return super.addAll(c);
      }
      boolean setChanged = false;
      EnumSetImpl<? extends E> set = (EnumSetImpl<? extends E>) c;
      for (int i = 0; i < sections.length; i++) {
        int previousSectionValue = sections[i];
        sections[i] |= set.sections[i];
        setChanged |= previousSectionValue != sections[i];
      }
      return setChanged;
    }

    public EnumSet<E> clone() {
      int sectionsCopy[] = new int[sections.length];
      System.arraycopy(sections, 0, sectionsCopy, 0, sections.length);
      return new EnumSetImpl<E>(all, sectionsCopy);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
      if (o instanceof Enum) {
        Enum e = (Enum) o;
        if (!isSameType(e)) {
          return false;
        }
        return (sections[e.ordinal() / Integer.SIZE] & (1 << e.ordinal())) != 0;
      }
      return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      if (!(c instanceof EnumSetImpl)) {
        return super.containsAll(c);
      }
      EnumSetImpl<?> set = (EnumSetImpl<?>) c;
      if (set.isEmpty()) {
        return true;
      } else if (!isSameType((Enum) set.iterator().next())) {
        return false;
      }
      for (int i = 0; i < sections.length; i++) {
        if ((sections[i] & set.sections[i]) != set.sections[i]) {
          return false;
        }
      }
      return true;
    }

    @Override
    public Iterator<E> iterator() {
      return new IteratorImpl();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
      if (o instanceof Enum) {
        Enum e = (Enum) o;
        if (!isSameType(e)) {
          return false;
        }
        int sectionIndex = e.ordinal() / Integer.SIZE;
        int previousValue = sections[sectionIndex];
        sections[sectionIndex] &= ~(1 << e.ordinal());
        return previousValue != sections[sectionIndex];
      }
      return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      if (!(c instanceof EnumSetImpl)) {
        return super.removeAll(c);
      }
      EnumSetImpl<?> set = (EnumSetImpl<?>) c;
      if (c.isEmpty() || !isSameType((Enum) c.iterator().next())) {
        return false;
      }
      boolean isSetChanged = false;
      for (int i = 0; i < sections.length; i++) {
        int previousSectionValue = sections[i];
        sections[i] &= ~set.sections[i];
        isSetChanged |= previousSectionValue != sections[i];
      }
      return isSetChanged;
    }

    @Override
    public int size() {
      int size = 0;
      for (int section : sections) {
        size += Integer.bitCount(section);
      }
      return size;
    }

    @Override
    int capacity() {
      return all.length;
    }
  }

  private static int getNumberOfSections(int numberOfElements) {
    return (numberOfElements + Integer.SIZE - 1) / Integer.SIZE;
  }

  public static <E extends Enum<E>> EnumSet<E> allOf(Class<E> elementType) {
    E[] all = elementType.getEnumConstants();
    int[] sections = new int[getNumberOfSections(all.length)];
    for (int i = 0; i < sections.length; i++) {
      sections[i] = -1;
    }
    sections[sections.length - 1] >>>= (sections.length * Integer.SIZE - all.length);
    return new EnumSetImpl<E>(all, sections);
  }

  public static <E extends Enum<E>> EnumSet<E> complementOf(EnumSet<E> other) {
    EnumSetImpl<E> s = (EnumSetImpl<E>) other;
    E[] all = s.all;
    int[] oldSections = s.sections;
    int[] newSections = new int[s.sections.length];
    for (int i = 0; i < newSections.length; i++) {
      newSections[i] = ~oldSections[i];
    }
    newSections[newSections.length - 1] &= (-1 >>> newSections.length * Integer.SIZE - all.length);
    return new EnumSetImpl<E>(all, newSections);
  }

  public static <E extends Enum<E>> EnumSet<E> copyOf(Collection<E> c) {
    if (c instanceof EnumSet) {
      return EnumSet.copyOf((EnumSet<E>) c);
    }

    Iterator<E> it = c.iterator();
    E first = it.next();
    Class<E> clazz = first.getDeclaringClass();
    EnumSet<E> set = EnumSet.noneOf(clazz);
    set.addAll(c);
    return set;
  }

  public static <E extends Enum<E>> EnumSet<E> copyOf(EnumSet<E> s) {
    return s.clone();
  }

  public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
    E[] all = elementType.getEnumConstants();
    return new EnumSetImpl<E>(all);
  }

  public static <E extends Enum<E>> EnumSet<E> of(E first) {
    E[] all = first.getDeclaringClass().getEnumConstants();
    EnumSet<E> set = EnumSet.noneOf(first.getDeclaringClass());
    set.add(first);
    return set;
  }

  public static <E extends Enum<E>> EnumSet<E> of(E first, E... rest) {
    E[] all = first.getDeclaringClass().getEnumConstants();
    EnumSet<E> set = EnumSet.noneOf(first.getDeclaringClass());
    set.add(first);
    for (E e : rest) {
      set.add(e);
    }
    return set;
  }

  public static <E extends Enum<E>> EnumSet<E> range(E from, E to) {
    if (from.compareTo(to) > 0) {
      throw new IllegalArgumentException(from + " > " + to);
    }
    E[] all = from.getDeclaringClass().getEnumConstants();
    EnumSet<E> set = EnumSet.noneOf(from.getDeclaringClass());

    // Inclusive
    int start = from.ordinal();
    int end = to.ordinal() + 1;
    for (int i = start; i < end; ++i) {
      set.add(all[i]);
    }
    return set;
  }

  /**
   * Single implementation only.
   */
  EnumSet() {
  }

  public abstract EnumSet<E> clone();

  abstract int capacity();
}
