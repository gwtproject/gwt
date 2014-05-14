package com.google.gwt.dev;

import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Sets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

/**
 * A map from GWT.create() arguments to the types that will be constructed.
 * (Both keys and values are Java source type names.)
 *
 * <p>Each map typically contains the answers for one (soft) permutation.
 */
public class AnswerMap implements Serializable {
  private final TreeMap<String, String> answers;

  public AnswerMap() {
    answers = Maps.newTreeMap();
  }

  private AnswerMap(TreeMap<String, String> answers) {
    this.answers = answers;
  }

  /**
   * Returns the Java class that a GWT.create() call will construct.
   *
   * @param key the GWT.create() argument (source type name)
   * @return a source type name
   */
  public String get(String key) {
    return answers.get(key);
  }

  /**
   * Sets the Java class that a GWT.create() call will construct.
   *
   * @param key a source type name
   * @param value a source type name
   */
  void put(String key, String value) {
    answers.put(key, value);
  }

  /**
   * Returns true if the map contains a answer for the given GWT.create() argument.
   */
  public boolean containsKey(String key) {
    return answers.containsKey(key);
  }

  /**
   * Asserts that two maps contain the same answers for the given GWT.create() arguments.
   */
  void assertSameAnswers(AnswerMap other, Iterable<String> keysToCheck) {
    for (String key : keysToCheck) {
      assert answers.get(key).equals(other.answers.get(key));
    }
  }

  /**
   * Returns the answers that are the same in every permutation.
   */
  public static AnswerMap getCommonAnswers(Iterable<AnswerMap> maps) {
    Iterator<AnswerMap> it = maps.iterator();

    // Start with an arbitrary copy
    TreeMap<String, String> out = Maps.newTreeMap(it.next().answers);

    while (it.hasNext()) {
      AnswerMap next = it.next();
      // Only keep key/value pairs present in both maps.
      out.entrySet().retainAll(next.answers.entrySet());
    }

    return new AnswerMap(out);
  }

  /**
   * Returns the Java classes that GWT.create() might return in at least one permutation.
   *
   * @param keysWanted the GWT.create() arguments to include (source type names).
   */
  public static SortedMap<String, SortedSet<String>> getPossibleAnswers(Iterable<AnswerMap> maps,
      Set<String> keysWanted) {

    SortedMap<String, SortedSet<String>> out = Maps.newTreeMap();

    for (AnswerMap map : maps) {
      for (Map.Entry<String, String> entry : map.answers.entrySet()) {
        if (!keysWanted.contains(entry.getKey())) {
          continue;
        }

        SortedSet<String> answers = out.get(entry.getKey());
        if (answers == null) {
          answers = Sets.newTreeSet();
          out.put(entry.getKey(), answers);
        }

        answers.add(entry.getValue());
      }
    }

    return out;
  }


  /**
   * Given an argument to GWT.create(), returns a map containing each possible return type and the
   * permutations that would create it.
   *
   * @return a map from a Java class (source type name) to a list of indexes into the maps list.
   */
  public static Map<String, List<Integer>> getAnswerPermutations(List<AnswerMap> maps, String key) {

    Map<String, List<Integer>> out = Maps.newLinkedHashMap();

    int permutationCount = maps.size();
    for (int i = 0; i < permutationCount; i++) {
      String answerType = maps.get(i).get(key);

      List<Integer> list = out.get(answerType);
      if (list == null) {
        list = new ArrayList<Integer>();
        out.put(answerType, list);
      }

      list.add(i);
    }
    return out;
  }
}
