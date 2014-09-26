/*
 * Copyright 2014 Google Inc.
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
package org.hibernate.validator.internal.engine;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.MatchResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.validation.Path;

import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

/**
 * Adaptation of the original JBoss source to make it compatible with GWT client classpath.
 * Basically replacing java.util.regex Pattern and Matcher with gwt RegExp and MatchResult.
 *
 * @see <a
 *      href="https://github.com/hibernate/hibernate-validator/blob/4.3/engine/src/main/java/org/hibernate/validator/internal/engine/PathImpl.java">
 *      Original source code</a>
 */
public final class PathImpl implements Path, Serializable {
  private static final long serialVersionUID = 7564511574909882392L;
  private static final Log log = LoggerFactory.make();

  public static final String PROPERTY_PATH_SEPARATOR = ".";

  /**
   * Regular expression used to split a string path into its elements.
   *
   * @see <a href="http://www.regexplanet.com/simple/index.jsp">Regular expression tester</a>
   */
  private static final String LEADING_PROPERTY_GROUP = "[^\\[\\.]+"; // everything up to a [ or .
  private static final String OPTIONAL_INDEX_GROUP = "\\[(\\w*)\\]";
  private static final String REMAINING_PROPERTY_STRING = "\\.(.*)"; // processed recursively

  private static final RegExp PATH_PATTERN = RegExp.compile("(" + LEADING_PROPERTY_GROUP + ")("
      + OPTIONAL_INDEX_GROUP + ")?(" + REMAINING_PROPERTY_STRING + ")*");
  private static final int PROPERTY_NAME_GROUP = 1;
  private static final int INDEXED_GROUP = 2;
  private static final int INDEX_GROUP = 3;
  private static final int REMAINING_STRING_GROUP = 5;

  private final List<Node> nodeList;
  private NodeImpl currentLeafNode;
  private int hashCode;

  /**
   * Returns a {@code Path} instance representing the path described by the given string. To create
   * a root node the empty string should be passed.
   *
   * @param propertyPath the path as string representation.
   * @return a {@code Path} instance representing the path described by the given string.
   * @throws IllegalArgumentException in case {@code property == null}
   *         or {@code property} cannot be parsed.
   */
  public static PathImpl createPathFromString(String propertyPath) {
    Contracts.assertNotNull(propertyPath, "null is not allowed as property path.");

    if (propertyPath.length() == 0) {
      return createNewPath(null);
    }

    return parseProperty(propertyPath);
  }

  // ==========================================================================
  // java.lang.reflect.Method NOT SUPPORTED BY GWT
  // ==========================================================================
  // /**
  // * Creates a path representing the specified method parameter.
  // *
  // * @param method The method hosting the parameter to represent.
  // * @param parameterName The parameter's name, e.g. "arg0" or "param1".
  // *
  // * @return A path representing the specified method parameter.
  // */
  // public static PathImpl createPathForMethodParameter(Method method, String parameterName) {
  // Contracts.assertNotNull( method, "A method is required to create a method parameter path." );
  // Contracts.assertNotNull( parameterName,
  // "A parameter name is required to create a method parameter path." );
  //
  // PathImpl path = createRootPath();
  // path.addMethodParameterNode( method, parameterName );
  //
  // return path;
  // }
  //
  // public static PathImpl createPathForMethodReturnValue(Method method) {
  // Contracts.assertNotNull( method, "A method is required to create a method return value path."
  // );
  //
  // PathImpl path = createRootPath();
  // path.addMethodReturnValueNode( method );
  //
  // return path;
  // }

  public static PathImpl createRootPath() {
    return createNewPath(null);
  }

  public static PathImpl createCopy(PathImpl path) {
    return new PathImpl(path);
  }

  public final boolean isRootPath() {
    return nodeList.size() == 1 && nodeList.get(0).getName() == null;
  }

  public final PathImpl getPathWithoutLeafNode() {
    return new PathImpl(nodeList.subList(0, nodeList.size() - 1));
  }

  public final NodeImpl addNode(String nodeName) {
    NodeImpl parent = nodeList.isEmpty() ? null : (NodeImpl) nodeList.get(nodeList.size() - 1);
    currentLeafNode = new NodeImpl(nodeName, parent, false, null, null);
    nodeList.add(currentLeafNode);
    hashCode = -1;
    return currentLeafNode;
  }

  // ==========================================================================
  // java.lang.reflect.Method NOT SUPPORTED BY GWT
  // ==========================================================================
  // private NodeImpl addMethodParameterNode(Method method, String parameterName) {
  // NodeImpl parent = nodeList.isEmpty() ? null : (NodeImpl) nodeList.get( nodeList.size() - 1 );
  // currentLeafNode = new MethodParameterNodeImpl( method, parameterName, parent );
  // nodeList.add( currentLeafNode );
  // hashCode = -1;
  // return currentLeafNode;
  // }
  //
  // private NodeImpl addMethodReturnValueNode(Method method) {
  // NodeImpl parent = nodeList.isEmpty() ? null : (NodeImpl) nodeList.get( nodeList.size() - 1 );
  // currentLeafNode = new MethodReturnValueNodeImpl( method, parent );
  // nodeList.add( currentLeafNode );
  // hashCode = -1;
  // return currentLeafNode;
  // }

  public final NodeImpl makeLeafNodeIterable() {
    currentLeafNode =
        new NodeImpl(currentLeafNode.getName(), currentLeafNode.getParent(), true, null, null);
    nodeList.remove(nodeList.size() - 1);
    nodeList.add(currentLeafNode);
    hashCode = -1;
    return currentLeafNode;
  }

  public final NodeImpl setLeafNodeIndex(Integer index) {
    currentLeafNode =
        new NodeImpl(currentLeafNode.getName(), currentLeafNode.getParent(), true, index, null);
    nodeList.remove(nodeList.size() - 1);
    nodeList.add(currentLeafNode);
    hashCode = -1;
    return currentLeafNode;
  }

  public final NodeImpl setLeafNodeMapKey(Object key) {
    currentLeafNode =
        new NodeImpl(currentLeafNode.getName(), currentLeafNode.getParent(), true, null, key);
    nodeList.remove(nodeList.size() - 1);
    nodeList.add(currentLeafNode);
    hashCode = -1;
    return currentLeafNode;
  }

  public final NodeImpl getLeafNode() {
    return currentLeafNode;
  }

  public final Iterator<Path.Node> iterator() {
    if (nodeList.size() == 0) {
      return Collections.<Path.Node>emptyList().iterator();
    }
    if (nodeList.size() == 1) {
      return nodeList.iterator();
    }
    return nodeList.subList(1, nodeList.size()).iterator();
  }

  public final String asString() {
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (int i = 1; i < nodeList.size(); i++) {
      NodeImpl nodeImpl = (NodeImpl) nodeList.get(i);
      if (nodeImpl.getName() != null) {
        if (!first) {
          builder.append(PROPERTY_PATH_SEPARATOR);
        }
        builder.append(nodeImpl.asString());
      }

      first = false;
    }
    return builder.toString();
  }

  @Override
  public String toString() {
    return asString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PathImpl other = (PathImpl) obj;
    if (nodeList == null) {
      if (other.nodeList != null) {
        return false;
      }
    } else if (!nodeList.equals(other.nodeList)) {
      return false;
    }
    return true;
  }

  @Override
  // deferred hash code building
  public int hashCode() {
    if (hashCode == -1) {
      hashCode = buildHashCode();
    }

    return hashCode;
  }

  private int buildHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((nodeList == null) ? 0 : nodeList.hashCode());
    return result;
  }

  private static PathImpl createNewPath(String name) {
    PathImpl path = new PathImpl();
    path.addNode(name);
    return path;
  }

  /**
   * Copy constructor.
   *
   * @param path the path to make a copy of.
   */
  private PathImpl(PathImpl path) {
    this(path.nodeList);
    currentLeafNode = (NodeImpl) nodeList.get(nodeList.size() - 1);
  }

  private PathImpl() {
    nodeList = new ArrayList<Node>();
  }

  private PathImpl(List<Node> nodeList) {
    this.nodeList = new ArrayList<Node>(nodeList);
  }

  private static PathImpl parseProperty(String property) {
    PathImpl path = createNewPath(null);
    String tmp = property;
    do {
      MatchResult matcher = PATH_PATTERN.exec(tmp);
      if (matcher != null) {

        String value = matcher.getGroup(PROPERTY_NAME_GROUP);
        if (!isValidJavaIdentifier(value)) {
          throw log.getInvalidJavaIdentifierException(value);
        }

        // create the node
        path.addNode(value);

        // is the node indexable
        if (matcher.getGroup(INDEXED_GROUP) != null) {
          path.makeLeafNodeIterable();
        }

        // take care of the index/key if one exists
        String indexOrKey = matcher.getGroup(INDEX_GROUP);
        if (indexOrKey != null && indexOrKey.length() > 0) {
          try {
            Integer i = Integer.parseInt(indexOrKey);
            path.setLeafNodeIndex(i);
          } catch (NumberFormatException e) {
            path.setLeafNodeMapKey(indexOrKey);
          }
        }

        // match the remaining string
        tmp = matcher.getGroup(REMAINING_STRING_GROUP);
      } else {
        throw log.getUnableToParsePropertyPathException(property);
      }
    } while (tmp != null);

    if (path.getLeafNode().isIterable()) {
      path.addNode(null);
    }

    return path;
  }

  /**
   * Validate that the given identifier is a valid Java identifier.
   * (according to the Java Language Specification,
   * <a href="http://java.sun.com/docs/books/jls/third_edition/html/lexical.html#3.8">
   * chapter 3.8</a>)
   *
   * @param identifier string identifier to validate
   * @return true if the given identifier is a valid Java Identifier
   * @throws IllegalArgumentException if the given identifier is {@code null}
   */
  private static boolean isValidJavaIdentifier(String identifier) {
    Contracts.assertNotNull(identifier, "identifier param cannot be null");

    if (identifier.length() == 0 || !Character.isJavaIdentifierStart((int) identifier.charAt(0))) {
      return false;
    }

    for (int i = 1; i < identifier.length(); i++) {
      if (!Character.isJavaIdentifierPart((int) identifier.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Simulation of the Java Character class (not supported in GWT client) containing the necessary
   * methods for {@link PathImpl} class.
   */
  private static class Character {
    static boolean isJavaIdentifierStart(int c) {
      // Check $
      if (c == 36)
        return true;

      // Check _
      if (c == 95)
        return true;

      // Check Letter
      if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c == 170) || (c == 181) || (c == 186)
          || (c >= 192 && c <= 214) || (c >= 216 && c <= 246) || (c >= 248 && c <= 705)
          || (c >= 710 && c <= 721) || (c >= 736 && c <= 740) || (c == 748) || (c == 750)
          || (c >= 880 && c <= 884) || (c >= 886 && c <= 887) || (c >= 890 && c <= 893)
          || (c == 902) || (c >= 904 && c <= 906) || (c == 908) || (c >= 910 && c <= 929)
          || (c >= 931 && c <= 1013) || (c >= 1015 && c <= 1153) || (c >= 1162 && c <= 1319)
          || (c >= 1329 && c <= 1366) || (c == 1369) || (c >= 1377 && c <= 1415)
          || (c >= 1488 && c <= 1514) || (c >= 1520 && c <= 1522) || (c >= 1568 && c <= 1610)
          || (c >= 1646 && c <= 1647) || (c >= 1649 && c <= 1747) || (c == 1749)
          || (c >= 1765 && c <= 1766) || (c >= 1774 && c <= 1775) || (c >= 1786 && c <= 1788)
          || (c == 1791) || (c == 1808) || (c >= 1810 && c <= 1839) || (c >= 1869 && c <= 1957)
          || (c == 1969) || (c >= 1994 && c <= 2026) || (c >= 2036 && c <= 2037) || (c == 2042)
          || (c >= 2048 && c <= 2069) || (c == 2074) || (c == 2084) || (c == 2088)
          || (c >= 2112 && c <= 2136) || (c >= 2308 && c <= 2361) || (c == 2365) || (c == 2384)
          || (c >= 2392 && c <= 2401) || (c >= 2417 && c <= 2423) || (c >= 2425 && c <= 2431)
          || (c >= 2437 && c <= 2444) || (c >= 2447 && c <= 2448) || (c >= 2451 && c <= 2472)
          || (c >= 2474 && c <= 2480) || (c == 2482) || (c >= 2486 && c <= 2489) || (c == 2493)
          || (c == 2510) || (c >= 2524 && c <= 2525) || (c >= 2527 && c <= 2529)
          || (c >= 2544 && c <= 2545) || (c >= 2565 && c <= 2570) || (c >= 2575 && c <= 2576)
          || (c >= 2579 && c <= 2600) || (c >= 2602 && c <= 2608) || (c >= 2610 && c <= 2611)
          || (c >= 2613 && c <= 2614) || (c >= 2616 && c <= 2617) || (c >= 2649 && c <= 2652)
          || (c == 2654) || (c >= 2674 && c <= 2676) || (c >= 2693 && c <= 2701)
          || (c >= 2703 && c <= 2705) || (c >= 2707 && c <= 2728) || (c >= 2730 && c <= 2736)
          || (c >= 2738 && c <= 2739) || (c >= 2741 && c <= 2745) || (c == 2749) || (c == 2768)
          || (c >= 2784 && c <= 2785) || (c >= 2821 && c <= 2828) || (c >= 2831 && c <= 2832)
          || (c >= 2835 && c <= 2856) || (c >= 2858 && c <= 2864) || (c >= 2866 && c <= 2867)
          || (c >= 2869 && c <= 2873) || (c == 2877) || (c >= 2908 && c <= 2909)
          || (c >= 2911 && c <= 2913) || (c == 2929) || (c == 2947) || (c >= 2949 && c <= 2954)
          || (c >= 2958 && c <= 2960) || (c >= 2962 && c <= 2965) || (c >= 2969 && c <= 2970)
          || (c == 2972) || (c >= 2974 && c <= 2975) || (c >= 2979 && c <= 2980)
          || (c >= 2984 && c <= 2986) || (c >= 2990 && c <= 3001) || (c == 3024)
          || (c >= 3077 && c <= 3084) || (c >= 3086 && c <= 3088) || (c >= 3090 && c <= 3112)
          || (c >= 3114 && c <= 3123) || (c >= 3125 && c <= 3129) || (c == 3133)
          || (c >= 3160 && c <= 3161) || (c >= 3168 && c <= 3169) || (c >= 3205 && c <= 3212)
          || (c >= 3214 && c <= 3216) || (c >= 3218 && c <= 3240) || (c >= 3242 && c <= 3251)
          || (c >= 3253 && c <= 3257) || (c == 3261) || (c == 3294) || (c >= 3296 && c <= 3297)
          || (c >= 3313 && c <= 3314) || (c >= 3333 && c <= 3340) || (c >= 3342 && c <= 3344)
          || (c >= 3346 && c <= 3386) || (c == 3389) || (c == 3406) || (c >= 3424 && c <= 3425)
          || (c >= 3450 && c <= 3455) || (c >= 3461 && c <= 3478) || (c >= 3482 && c <= 3505)
          || (c >= 3507 && c <= 3515) || (c == 3517) || (c >= 3520 && c <= 3526)
          || (c >= 3585 && c <= 3632) || (c >= 3634 && c <= 3635) || (c >= 3648 && c <= 3654)
          || (c >= 3713 && c <= 3714) || (c == 3716) || (c >= 3719 && c <= 3720) || (c == 3722)
          || (c == 3725) || (c >= 3732 && c <= 3735) || (c >= 3737 && c <= 3743)
          || (c >= 3745 && c <= 3747) || (c == 3749) || (c == 3751) || (c >= 3754 && c <= 3755)
          || (c >= 3757 && c <= 3760) || (c >= 3762 && c <= 3763) || (c == 3773)
          || (c >= 3776 && c <= 3780) || (c == 3782) || (c >= 3804 && c <= 3805) || (c == 3840)
          || (c >= 3904 && c <= 3911) || (c >= 3913 && c <= 3948) || (c >= 3976 && c <= 3980)
          || (c >= 4096 && c <= 4138) || (c == 4159) || (c >= 4176 && c <= 4181)
          || (c >= 4186 && c <= 4189) || (c == 4193) || (c >= 4197 && c <= 4198)
          || (c >= 4206 && c <= 4208) || (c >= 4213 && c <= 4225) || (c == 4238)
          || (c >= 4256 && c <= 4293) || (c >= 4304 && c <= 4346) || (c == 4348)
          || (c >= 4352 && c <= 4680) || (c >= 4682 && c <= 4685) || (c >= 4688 && c <= 4694)
          || (c == 4696) || (c >= 4698 && c <= 4701) || (c >= 4704 && c <= 4744)
          || (c >= 4746 && c <= 4749) || (c >= 4752 && c <= 4784) || (c >= 4786 && c <= 4789)
          || (c >= 4792 && c <= 4798) || (c == 4800) || (c >= 4802 && c <= 4805)
          || (c >= 4808 && c <= 4822) || (c >= 4824 && c <= 4880) || (c >= 4882 && c <= 4885)
          || (c >= 4888 && c <= 4954) || (c >= 4992 && c <= 5007) || (c >= 5024 && c <= 5108)
          || (c >= 5121 && c <= 5740) || (c >= 5743 && c <= 5759) || (c >= 5761 && c <= 5786)
          || (c >= 5792 && c <= 5866) || (c >= 5888 && c <= 5900) || (c >= 5902 && c <= 5905)
          || (c >= 5920 && c <= 5937) || (c >= 5952 && c <= 5969) || (c >= 5984 && c <= 5996)
          || (c >= 5998 && c <= 6000) || (c >= 6016 && c <= 6067) || (c == 6103) || (c == 6108)
          || (c >= 6176 && c <= 6263) || (c >= 6272 && c <= 6312) || (c == 6314)
          || (c >= 6320 && c <= 6389) || (c >= 6400 && c <= 6428) || (c >= 6480 && c <= 6509)
          || (c >= 6512 && c <= 6516) || (c >= 6528 && c <= 6571) || (c >= 6593 && c <= 6599)
          || (c >= 6656 && c <= 6678) || (c >= 6688 && c <= 6740) || (c == 6823)
          || (c >= 6917 && c <= 6963) || (c >= 6981 && c <= 6987) || (c >= 7043 && c <= 7072)
          || (c >= 7086 && c <= 7087) || (c >= 7104 && c <= 7141) || (c >= 7168 && c <= 7203)
          || (c >= 7245 && c <= 7247) || (c >= 7258 && c <= 7293) || (c >= 7401 && c <= 7404)
          || (c >= 7406 && c <= 7409) || (c >= 7424 && c <= 7615) || (c >= 7680 && c <= 7957)
          || (c >= 7960 && c <= 7965) || (c >= 7968 && c <= 8005) || (c >= 8008 && c <= 8013)
          || (c >= 8016 && c <= 8023) || (c == 8025) || (c == 8027) || (c == 8029)
          || (c >= 8031 && c <= 8061) || (c >= 8064 && c <= 8116) || (c >= 8118 && c <= 8124)
          || (c == 8126) || (c >= 8130 && c <= 8132) || (c >= 8134 && c <= 8140)
          || (c >= 8144 && c <= 8147) || (c >= 8150 && c <= 8155) || (c >= 8160 && c <= 8172)
          || (c >= 8178 && c <= 8180) || (c >= 8182 && c <= 8188) || (c == 8305) || (c == 8319)
          || (c >= 8336 && c <= 8348) || (c == 8450) || (c == 8455) || (c >= 8458 && c <= 8467)
          || (c == 8469) || (c >= 8473 && c <= 8477) || (c == 8484) || (c == 8486) || (c == 8488)
          || (c >= 8490 && c <= 8493) || (c >= 8495 && c <= 8505) || (c >= 8508 && c <= 8511)
          || (c >= 8517 && c <= 8521) || (c == 8526) || (c >= 8579 && c <= 8580)
          || (c >= 11264 && c <= 11310) || (c >= 11312 && c <= 11358) || (c >= 11360 && c <= 11492)
          || (c >= 11499 && c <= 11502) || (c >= 11520 && c <= 11557) || (c >= 11568 && c <= 11621)
          || (c == 11631) || (c >= 11648 && c <= 11670) || (c >= 11680 && c <= 11686)
          || (c >= 11688 && c <= 11694) || (c >= 11696 && c <= 11702) || (c >= 11704 && c <= 11710)
          || (c >= 11712 && c <= 11718) || (c >= 11720 && c <= 11726) || (c >= 11728 && c <= 11734)
          || (c >= 11736 && c <= 11742) || (c == 11823) || (c >= 12293 && c <= 12294)
          || (c >= 12337 && c <= 12341) || (c >= 12347 && c <= 12348) || (c >= 12353 && c <= 12438)
          || (c >= 12445 && c <= 12447) || (c >= 12449 && c <= 12538) || (c >= 12540 && c <= 12543)
          || (c >= 12549 && c <= 12589) || (c >= 12593 && c <= 12686) || (c >= 12704 && c <= 12730)
          || (c >= 12784 && c <= 12799) || (c >= 13312 && c <= 19893) || (c >= 19968 && c <= 40907)
          || (c >= 40960 && c <= 42124) || (c >= 42192 && c <= 42237) || (c >= 42240 && c <= 42508)
          || (c >= 42512 && c <= 42527) || (c >= 42538 && c <= 42539) || (c >= 42560 && c <= 42606)
          || (c >= 42623 && c <= 42647) || (c >= 42656 && c <= 42725) || (c >= 42775 && c <= 42783)
          || (c >= 42786 && c <= 42888) || (c >= 42891 && c <= 42894) || (c >= 42896 && c <= 42897)
          || (c >= 42912 && c <= 42921) || (c >= 43002 && c <= 43009) || (c >= 43011 && c <= 43013)
          || (c >= 43015 && c <= 43018) || (c >= 43020 && c <= 43042) || (c >= 43072 && c <= 43123)
          || (c >= 43138 && c <= 43187) || (c >= 43250 && c <= 43255) || (c == 43259)
          || (c >= 43274 && c <= 43301) || (c >= 43312 && c <= 43334) || (c >= 43360 && c <= 43388)
          || (c >= 43396 && c <= 43442) || (c == 43471) || (c >= 43520 && c <= 43560)
          || (c >= 43584 && c <= 43586) || (c >= 43588 && c <= 43595) || (c >= 43616 && c <= 43638)
          || (c == 43642) || (c >= 43648 && c <= 43695) || (c == 43697)
          || (c >= 43701 && c <= 43702) || (c >= 43705 && c <= 43709) || (c == 43712)
          || (c == 43714) || (c >= 43739 && c <= 43741) || (c >= 43777 && c <= 43782)
          || (c >= 43785 && c <= 43790) || (c >= 43793 && c <= 43798) || (c >= 43808 && c <= 43814)
          || (c >= 43816 && c <= 43822) || (c >= 43968 && c <= 44002) || (c >= 44032 && c <= 55203)
          || (c >= 55216 && c <= 55238) || (c >= 55243 && c <= 55291) || (c >= 63744 && c <= 64045)
          || (c >= 64048 && c <= 64109) || (c >= 64112 && c <= 64217) || (c >= 64256 && c <= 64262)
          || (c >= 64275 && c <= 64279) || (c == 64285) || (c >= 64287 && c <= 64296)
          || (c >= 64298 && c <= 64310) || (c >= 64312 && c <= 64316) || (c == 64318)
          || (c >= 64320 && c <= 64321) || (c >= 64323 && c <= 64324) || (c >= 64326 && c <= 64433)
          || (c >= 64467 && c <= 64829) || (c >= 64848 && c <= 64911) || (c >= 64914 && c <= 64967)
          || (c >= 65008 && c <= 65019) || (c >= 65136 && c <= 65140) || (c >= 65142 && c <= 65276)
          || (c >= 65313 && c <= 65338) || (c >= 65345 && c <= 65370) || (c >= 65382 && c <= 65470)
          || (c >= 65474 && c <= 65479) || (c >= 65482 && c <= 65487) || (c >= 65490 && c <= 65495)
          || (c >= 65498 && c <= 65500) || (c >= 65536 && c <= 65547) || (c >= 65549 && c <= 65574)
          || (c >= 65576 && c <= 65594) || (c >= 65596 && c <= 65597) || (c >= 65599 && c <= 65613)
          || (c >= 65616 && c <= 65629) || (c >= 65664 && c <= 65786) || (c >= 66176 && c <= 66204)
          || (c >= 66208 && c <= 66256) || (c >= 66304 && c <= 66334) || (c >= 66352 && c <= 66368)
          || (c >= 66370 && c <= 66377) || (c >= 66432 && c <= 66461) || (c >= 66464 && c <= 66499)
          || (c >= 66504 && c <= 66511) || (c >= 66560 && c <= 66717) || (c >= 67584 && c <= 67589)
          || (c == 67592) || (c >= 67594 && c <= 67637) || (c >= 67639 && c <= 67640)
          || (c == 67644) || (c >= 67647 && c <= 67669) || (c >= 67840 && c <= 67861)
          || (c >= 67872 && c <= 67897) || (c == 68096) || (c >= 68112 && c <= 68115)
          || (c >= 68117 && c <= 68119) || (c >= 68121 && c <= 68147) || (c >= 68192 && c <= 68220)
          || (c >= 68352 && c <= 68405) || (c >= 68416 && c <= 68437) || (c >= 68448 && c <= 68466)
          || (c >= 68608 && c <= 68680) || (c >= 69635 && c <= 69687) || (c >= 69763 && c <= 69807)
          || (c >= 73728 && c <= 74606) || (c >= 77824 && c <= 78894) || (c >= 92160 && c <= 92728)
          || (c >= 110592 && c <= 110593) || (c >= 119808 && c <= 119892)
          || (c >= 119894 && c <= 119964) || (c >= 119966 && c <= 119967) || (c == 119970)
          || (c >= 119973 && c <= 119974) || (c >= 119977 && c <= 119980)
          || (c >= 119982 && c <= 119993) || (c == 119995) || (c >= 119997 && c <= 120003)
          || (c >= 120005 && c <= 120069) || (c >= 120071 && c <= 120074)
          || (c >= 120077 && c <= 120084) || (c >= 120086 && c <= 120092)
          || (c >= 120094 && c <= 120121) || (c >= 120123 && c <= 120126)
          || (c >= 120128 && c <= 120132) || (c == 120134) || (c >= 120138 && c <= 120144)
          || (c >= 120146 && c <= 120485) || (c >= 120488 && c <= 120512)
          || (c >= 120514 && c <= 120538) || (c >= 120540 && c <= 120570)
          || (c >= 120572 && c <= 120596) || (c >= 120598 && c <= 120628)
          || (c >= 120630 && c <= 120654) || (c >= 120656 && c <= 120686)
          || (c >= 120688 && c <= 120712) || (c >= 120714 && c <= 120744)
          || (c >= 120746 && c <= 120770) || (c >= 120772 && c <= 120779)
          || (c >= 131072 && c <= 173782) || (c >= 173824 && c <= 177972)
          || (c >= 177984 && c <= 178205) || (c == 194560))
        return true;

      // Check Non Letters valid Java identifier starts (but $ and _)
      if ((c >= 162 && c <= 165) || (c == 1547) || (c >= 2546 && c <= 2547) || (c == 2555)
          || (c == 2801) || (c == 3065) || (c == 3647) || (c >= 5870 && c <= 5872) || (c == 6107)
          || (c >= 8255 && c <= 8256) || (c == 8276) || (c >= 8352 && c <= 8377)
          || (c >= 8544 && c <= 8578) || (c >= 8581 && c <= 8584) || (c == 12295)
          || (c >= 12321 && c <= 12329) || (c >= 12344 && c <= 12346) || (c >= 42726 && c <= 42735)
          || (c == 43064) || (c == 65020) || (c >= 65075 && c <= 65076)
          || (c >= 65101 && c <= 65103) || (c == 65129) || (c == 65284) || (c == 65343)
          || (c >= 65504 && c <= 65505) || (c >= 65509 && c <= 65510) || (c >= 65856 && c <= 65908)
          || (c == 66369) || (c == 66378) || (c >= 66513 && c <= 66517) || (c == 74752))
        return true;

      return false;
    }

    static boolean isJavaIdentifierPart(int c) {
      // Check $
      if (c == 36)
        return true;

      // Check _
      if (c == 95)
        return true;

      // Check Digit
      if ((c >= 48 && c <= 57) || (c >= 1632 && c <= 1641) || (c >= 1776 && c <= 1785)
          || (c >= 1984 && c <= 1993) || (c >= 2406 && c <= 2415) || (c >= 2534 && c <= 2543)
          || (c >= 2662 && c <= 2671) || (c >= 2790 && c <= 2799) || (c >= 2918 && c <= 2927)
          || (c >= 3046 && c <= 3055) || (c >= 3174 && c <= 3183) || (c >= 3302 && c <= 3311)
          || (c >= 3430 && c <= 3439) || (c >= 3664 && c <= 3673) || (c >= 3792 && c <= 3801)
          || (c >= 3872 && c <= 3881) || (c >= 4160 && c <= 4169) || (c >= 4240 && c <= 4249)
          || (c >= 6112 && c <= 6121) || (c >= 6160 && c <= 6169) || (c >= 6470 && c <= 6479)
          || (c >= 6608 && c <= 6617) || (c >= 6784 && c <= 6793) || (c >= 6800 && c <= 6809)
          || (c >= 6992 && c <= 7001) || (c >= 7088 && c <= 7097) || (c >= 7232 && c <= 7241)
          || (c >= 7248 && c <= 7257) || (c >= 42528 && c <= 42537) || (c >= 43216 && c <= 43225)
          || (c >= 43264 && c <= 43273) || (c >= 43472 && c <= 43481) || (c >= 43600 && c <= 43609)
          || (c >= 44016 && c <= 44025) || (c >= 65296 && c <= 65305) || (c >= 66720 && c <= 66729)
          || (c >= 69734 && c <= 69743) || (c == 120782))
        return true;

      // Check Letter
      if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c == 170) || (c == 181) || (c == 186)
          || (c >= 192 && c <= 214) || (c >= 216 && c <= 246) || (c >= 248 && c <= 705)
          || (c >= 710 && c <= 721) || (c >= 736 && c <= 740) || (c == 748) || (c == 750)
          || (c >= 880 && c <= 884) || (c >= 886 && c <= 887) || (c >= 890 && c <= 893)
          || (c == 902) || (c >= 904 && c <= 906) || (c == 908) || (c >= 910 && c <= 929)
          || (c >= 931 && c <= 1013) || (c >= 1015 && c <= 1153) || (c >= 1162 && c <= 1319)
          || (c >= 1329 && c <= 1366) || (c == 1369) || (c >= 1377 && c <= 1415)
          || (c >= 1488 && c <= 1514) || (c >= 1520 && c <= 1522) || (c >= 1568 && c <= 1610)
          || (c >= 1646 && c <= 1647) || (c >= 1649 && c <= 1747) || (c == 1749)
          || (c >= 1765 && c <= 1766) || (c >= 1774 && c <= 1775) || (c >= 1786 && c <= 1788)
          || (c == 1791) || (c == 1808) || (c >= 1810 && c <= 1839) || (c >= 1869 && c <= 1957)
          || (c == 1969) || (c >= 1994 && c <= 2026) || (c >= 2036 && c <= 2037) || (c == 2042)
          || (c >= 2048 && c <= 2069) || (c == 2074) || (c == 2084) || (c == 2088)
          || (c >= 2112 && c <= 2136) || (c >= 2308 && c <= 2361) || (c == 2365) || (c == 2384)
          || (c >= 2392 && c <= 2401) || (c >= 2417 && c <= 2423) || (c >= 2425 && c <= 2431)
          || (c >= 2437 && c <= 2444) || (c >= 2447 && c <= 2448) || (c >= 2451 && c <= 2472)
          || (c >= 2474 && c <= 2480) || (c == 2482) || (c >= 2486 && c <= 2489) || (c == 2493)
          || (c == 2510) || (c >= 2524 && c <= 2525) || (c >= 2527 && c <= 2529)
          || (c >= 2544 && c <= 2545) || (c >= 2565 && c <= 2570) || (c >= 2575 && c <= 2576)
          || (c >= 2579 && c <= 2600) || (c >= 2602 && c <= 2608) || (c >= 2610 && c <= 2611)
          || (c >= 2613 && c <= 2614) || (c >= 2616 && c <= 2617) || (c >= 2649 && c <= 2652)
          || (c == 2654) || (c >= 2674 && c <= 2676) || (c >= 2693 && c <= 2701)
          || (c >= 2703 && c <= 2705) || (c >= 2707 && c <= 2728) || (c >= 2730 && c <= 2736)
          || (c >= 2738 && c <= 2739) || (c >= 2741 && c <= 2745) || (c == 2749) || (c == 2768)
          || (c >= 2784 && c <= 2785) || (c >= 2821 && c <= 2828) || (c >= 2831 && c <= 2832)
          || (c >= 2835 && c <= 2856) || (c >= 2858 && c <= 2864) || (c >= 2866 && c <= 2867)
          || (c >= 2869 && c <= 2873) || (c == 2877) || (c >= 2908 && c <= 2909)
          || (c >= 2911 && c <= 2913) || (c == 2929) || (c == 2947) || (c >= 2949 && c <= 2954)
          || (c >= 2958 && c <= 2960) || (c >= 2962 && c <= 2965) || (c >= 2969 && c <= 2970)
          || (c == 2972) || (c >= 2974 && c <= 2975) || (c >= 2979 && c <= 2980)
          || (c >= 2984 && c <= 2986) || (c >= 2990 && c <= 3001) || (c == 3024)
          || (c >= 3077 && c <= 3084) || (c >= 3086 && c <= 3088) || (c >= 3090 && c <= 3112)
          || (c >= 3114 && c <= 3123) || (c >= 3125 && c <= 3129) || (c == 3133)
          || (c >= 3160 && c <= 3161) || (c >= 3168 && c <= 3169) || (c >= 3205 && c <= 3212)
          || (c >= 3214 && c <= 3216) || (c >= 3218 && c <= 3240) || (c >= 3242 && c <= 3251)
          || (c >= 3253 && c <= 3257) || (c == 3261) || (c == 3294) || (c >= 3296 && c <= 3297)
          || (c >= 3313 && c <= 3314) || (c >= 3333 && c <= 3340) || (c >= 3342 && c <= 3344)
          || (c >= 3346 && c <= 3386) || (c == 3389) || (c == 3406) || (c >= 3424 && c <= 3425)
          || (c >= 3450 && c <= 3455) || (c >= 3461 && c <= 3478) || (c >= 3482 && c <= 3505)
          || (c >= 3507 && c <= 3515) || (c == 3517) || (c >= 3520 && c <= 3526)
          || (c >= 3585 && c <= 3632) || (c >= 3634 && c <= 3635) || (c >= 3648 && c <= 3654)
          || (c >= 3713 && c <= 3714) || (c == 3716) || (c >= 3719 && c <= 3720) || (c == 3722)
          || (c == 3725) || (c >= 3732 && c <= 3735) || (c >= 3737 && c <= 3743)
          || (c >= 3745 && c <= 3747) || (c == 3749) || (c == 3751) || (c >= 3754 && c <= 3755)
          || (c >= 3757 && c <= 3760) || (c >= 3762 && c <= 3763) || (c == 3773)
          || (c >= 3776 && c <= 3780) || (c == 3782) || (c >= 3804 && c <= 3805) || (c == 3840)
          || (c >= 3904 && c <= 3911) || (c >= 3913 && c <= 3948) || (c >= 3976 && c <= 3980)
          || (c >= 4096 && c <= 4138) || (c == 4159) || (c >= 4176 && c <= 4181)
          || (c >= 4186 && c <= 4189) || (c == 4193) || (c >= 4197 && c <= 4198)
          || (c >= 4206 && c <= 4208) || (c >= 4213 && c <= 4225) || (c == 4238)
          || (c >= 4256 && c <= 4293) || (c >= 4304 && c <= 4346) || (c == 4348)
          || (c >= 4352 && c <= 4680) || (c >= 4682 && c <= 4685) || (c >= 4688 && c <= 4694)
          || (c == 4696) || (c >= 4698 && c <= 4701) || (c >= 4704 && c <= 4744)
          || (c >= 4746 && c <= 4749) || (c >= 4752 && c <= 4784) || (c >= 4786 && c <= 4789)
          || (c >= 4792 && c <= 4798) || (c == 4800) || (c >= 4802 && c <= 4805)
          || (c >= 4808 && c <= 4822) || (c >= 4824 && c <= 4880) || (c >= 4882 && c <= 4885)
          || (c >= 4888 && c <= 4954) || (c >= 4992 && c <= 5007) || (c >= 5024 && c <= 5108)
          || (c >= 5121 && c <= 5740) || (c >= 5743 && c <= 5759) || (c >= 5761 && c <= 5786)
          || (c >= 5792 && c <= 5866) || (c >= 5888 && c <= 5900) || (c >= 5902 && c <= 5905)
          || (c >= 5920 && c <= 5937) || (c >= 5952 && c <= 5969) || (c >= 5984 && c <= 5996)
          || (c >= 5998 && c <= 6000) || (c >= 6016 && c <= 6067) || (c == 6103) || (c == 6108)
          || (c >= 6176 && c <= 6263) || (c >= 6272 && c <= 6312) || (c == 6314)
          || (c >= 6320 && c <= 6389) || (c >= 6400 && c <= 6428) || (c >= 6480 && c <= 6509)
          || (c >= 6512 && c <= 6516) || (c >= 6528 && c <= 6571) || (c >= 6593 && c <= 6599)
          || (c >= 6656 && c <= 6678) || (c >= 6688 && c <= 6740) || (c == 6823)
          || (c >= 6917 && c <= 6963) || (c >= 6981 && c <= 6987) || (c >= 7043 && c <= 7072)
          || (c >= 7086 && c <= 7087) || (c >= 7104 && c <= 7141) || (c >= 7168 && c <= 7203)
          || (c >= 7245 && c <= 7247) || (c >= 7258 && c <= 7293) || (c >= 7401 && c <= 7404)
          || (c >= 7406 && c <= 7409) || (c >= 7424 && c <= 7615) || (c >= 7680 && c <= 7957)
          || (c >= 7960 && c <= 7965) || (c >= 7968 && c <= 8005) || (c >= 8008 && c <= 8013)
          || (c >= 8016 && c <= 8023) || (c == 8025) || (c == 8027) || (c == 8029)
          || (c >= 8031 && c <= 8061) || (c >= 8064 && c <= 8116) || (c >= 8118 && c <= 8124)
          || (c == 8126) || (c >= 8130 && c <= 8132) || (c >= 8134 && c <= 8140)
          || (c >= 8144 && c <= 8147) || (c >= 8150 && c <= 8155) || (c >= 8160 && c <= 8172)
          || (c >= 8178 && c <= 8180) || (c >= 8182 && c <= 8188) || (c == 8305) || (c == 8319)
          || (c >= 8336 && c <= 8348) || (c == 8450) || (c == 8455) || (c >= 8458 && c <= 8467)
          || (c == 8469) || (c >= 8473 && c <= 8477) || (c == 8484) || (c == 8486) || (c == 8488)
          || (c >= 8490 && c <= 8493) || (c >= 8495 && c <= 8505) || (c >= 8508 && c <= 8511)
          || (c >= 8517 && c <= 8521) || (c == 8526) || (c >= 8579 && c <= 8580)
          || (c >= 11264 && c <= 11310) || (c >= 11312 && c <= 11358) || (c >= 11360 && c <= 11492)
          || (c >= 11499 && c <= 11502) || (c >= 11520 && c <= 11557) || (c >= 11568 && c <= 11621)
          || (c == 11631) || (c >= 11648 && c <= 11670) || (c >= 11680 && c <= 11686)
          || (c >= 11688 && c <= 11694) || (c >= 11696 && c <= 11702) || (c >= 11704 && c <= 11710)
          || (c >= 11712 && c <= 11718) || (c >= 11720 && c <= 11726) || (c >= 11728 && c <= 11734)
          || (c >= 11736 && c <= 11742) || (c == 11823) || (c >= 12293 && c <= 12294)
          || (c >= 12337 && c <= 12341) || (c >= 12347 && c <= 12348) || (c >= 12353 && c <= 12438)
          || (c >= 12445 && c <= 12447) || (c >= 12449 && c <= 12538) || (c >= 12540 && c <= 12543)
          || (c >= 12549 && c <= 12589) || (c >= 12593 && c <= 12686) || (c >= 12704 && c <= 12730)
          || (c >= 12784 && c <= 12799) || (c >= 13312 && c <= 19893) || (c >= 19968 && c <= 40907)
          || (c >= 40960 && c <= 42124) || (c >= 42192 && c <= 42237) || (c >= 42240 && c <= 42508)
          || (c >= 42512 && c <= 42527) || (c >= 42538 && c <= 42539) || (c >= 42560 && c <= 42606)
          || (c >= 42623 && c <= 42647) || (c >= 42656 && c <= 42725) || (c >= 42775 && c <= 42783)
          || (c >= 42786 && c <= 42888) || (c >= 42891 && c <= 42894) || (c >= 42896 && c <= 42897)
          || (c >= 42912 && c <= 42921) || (c >= 43002 && c <= 43009) || (c >= 43011 && c <= 43013)
          || (c >= 43015 && c <= 43018) || (c >= 43020 && c <= 43042) || (c >= 43072 && c <= 43123)
          || (c >= 43138 && c <= 43187) || (c >= 43250 && c <= 43255) || (c == 43259)
          || (c >= 43274 && c <= 43301) || (c >= 43312 && c <= 43334) || (c >= 43360 && c <= 43388)
          || (c >= 43396 && c <= 43442) || (c == 43471) || (c >= 43520 && c <= 43560)
          || (c >= 43584 && c <= 43586) || (c >= 43588 && c <= 43595) || (c >= 43616 && c <= 43638)
          || (c == 43642) || (c >= 43648 && c <= 43695) || (c == 43697)
          || (c >= 43701 && c <= 43702) || (c >= 43705 && c <= 43709) || (c == 43712)
          || (c == 43714) || (c >= 43739 && c <= 43741) || (c >= 43777 && c <= 43782)
          || (c >= 43785 && c <= 43790) || (c >= 43793 && c <= 43798) || (c >= 43808 && c <= 43814)
          || (c >= 43816 && c <= 43822) || (c >= 43968 && c <= 44002) || (c >= 44032 && c <= 55203)
          || (c >= 55216 && c <= 55238) || (c >= 55243 && c <= 55291) || (c >= 63744 && c <= 64045)
          || (c >= 64048 && c <= 64109) || (c >= 64112 && c <= 64217) || (c >= 64256 && c <= 64262)
          || (c >= 64275 && c <= 64279) || (c == 64285) || (c >= 64287 && c <= 64296)
          || (c >= 64298 && c <= 64310) || (c >= 64312 && c <= 64316) || (c == 64318)
          || (c >= 64320 && c <= 64321) || (c >= 64323 && c <= 64324) || (c >= 64326 && c <= 64433)
          || (c >= 64467 && c <= 64829) || (c >= 64848 && c <= 64911) || (c >= 64914 && c <= 64967)
          || (c >= 65008 && c <= 65019) || (c >= 65136 && c <= 65140) || (c >= 65142 && c <= 65276)
          || (c >= 65313 && c <= 65338) || (c >= 65345 && c <= 65370) || (c >= 65382 && c <= 65470)
          || (c >= 65474 && c <= 65479) || (c >= 65482 && c <= 65487) || (c >= 65490 && c <= 65495)
          || (c >= 65498 && c <= 65500) || (c >= 65536 && c <= 65547) || (c >= 65549 && c <= 65574)
          || (c >= 65576 && c <= 65594) || (c >= 65596 && c <= 65597) || (c >= 65599 && c <= 65613)
          || (c >= 65616 && c <= 65629) || (c >= 65664 && c <= 65786) || (c >= 66176 && c <= 66204)
          || (c >= 66208 && c <= 66256) || (c >= 66304 && c <= 66334) || (c >= 66352 && c <= 66368)
          || (c >= 66370 && c <= 66377) || (c >= 66432 && c <= 66461) || (c >= 66464 && c <= 66499)
          || (c >= 66504 && c <= 66511) || (c >= 66560 && c <= 66717) || (c >= 67584 && c <= 67589)
          || (c == 67592) || (c >= 67594 && c <= 67637) || (c >= 67639 && c <= 67640)
          || (c == 67644) || (c >= 67647 && c <= 67669) || (c >= 67840 && c <= 67861)
          || (c >= 67872 && c <= 67897) || (c == 68096) || (c >= 68112 && c <= 68115)
          || (c >= 68117 && c <= 68119) || (c >= 68121 && c <= 68147) || (c >= 68192 && c <= 68220)
          || (c >= 68352 && c <= 68405) || (c >= 68416 && c <= 68437) || (c >= 68448 && c <= 68466)
          || (c >= 68608 && c <= 68680) || (c >= 69635 && c <= 69687) || (c >= 69763 && c <= 69807)
          || (c >= 73728 && c <= 74606) || (c >= 77824 && c <= 78894) || (c >= 92160 && c <= 92728)
          || (c >= 110592 && c <= 110593) || (c >= 119808 && c <= 119892)
          || (c >= 119894 && c <= 119964) || (c >= 119966 && c <= 119967) || (c == 119970)
          || (c >= 119973 && c <= 119974) || (c >= 119977 && c <= 119980)
          || (c >= 119982 && c <= 119993) || (c == 119995) || (c >= 119997 && c <= 120003)
          || (c >= 120005 && c <= 120069) || (c >= 120071 && c <= 120074)
          || (c >= 120077 && c <= 120084) || (c >= 120086 && c <= 120092)
          || (c >= 120094 && c <= 120121) || (c >= 120123 && c <= 120126)
          || (c >= 120128 && c <= 120132) || (c == 120134) || (c >= 120138 && c <= 120144)
          || (c >= 120146 && c <= 120485) || (c >= 120488 && c <= 120512)
          || (c >= 120514 && c <= 120538) || (c >= 120540 && c <= 120570)
          || (c >= 120572 && c <= 120596) || (c >= 120598 && c <= 120628)
          || (c >= 120630 && c <= 120654) || (c >= 120656 && c <= 120686)
          || (c >= 120688 && c <= 120712) || (c >= 120714 && c <= 120744)
          || (c >= 120746 && c <= 120770) || (c >= 120772 && c <= 120779)
          || (c >= 131072 && c <= 173782) || (c >= 173824 && c <= 177972)
          || (c >= 177984 && c <= 178205) || (c == 194560))
        return true;

      // Check Non Letters and Digits valid Java identifier parts (but $ and _)
      if ((c >= 0 && c <= 8) || (c >= 14 && c <= 27) || (c == 36) || (c == 95)
          || (c >= 127 && c <= 159) || (c >= 162 && c <= 165) || (c == 173)
          || (c >= 768 && c <= 879) || (c >= 1155 && c <= 1159) || (c >= 1425 && c <= 1469)
          || (c == 1471) || (c >= 1473 && c <= 1474) || (c >= 1476 && c <= 1477) || (c == 1479)
          || (c >= 1536 && c <= 1539) || (c == 1547) || (c >= 1552 && c <= 1562)
          || (c >= 1611 && c <= 1631) || (c == 1648) || (c >= 1750 && c <= 1757)
          || (c >= 1759 && c <= 1764) || (c >= 1767 && c <= 1768) || (c >= 1770 && c <= 1773)
          || (c == 1807) || (c == 1809) || (c >= 1840 && c <= 1866) || (c >= 1958 && c <= 1968)
          || (c >= 2027 && c <= 2035) || (c >= 2070 && c <= 2073) || (c >= 2075 && c <= 2083)
          || (c >= 2085 && c <= 2087) || (c >= 2089 && c <= 2093) || (c >= 2137 && c <= 2139)
          || (c >= 2304 && c <= 2307) || (c >= 2362 && c <= 2364) || (c >= 2366 && c <= 2383)
          || (c >= 2385 && c <= 2391) || (c >= 2402 && c <= 2403) || (c >= 2433 && c <= 2435)
          || (c == 2492) || (c >= 2494 && c <= 2500) || (c >= 2503 && c <= 2504)
          || (c >= 2507 && c <= 2509) || (c == 2519) || (c >= 2530 && c <= 2531)
          || (c >= 2546 && c <= 2547) || (c == 2555) || (c >= 2561 && c <= 2563) || (c == 2620)
          || (c >= 2622 && c <= 2626) || (c >= 2631 && c <= 2632) || (c >= 2635 && c <= 2637)
          || (c == 2641) || (c >= 2672 && c <= 2673) || (c == 2677) || (c >= 2689 && c <= 2691)
          || (c == 2748) || (c >= 2750 && c <= 2757) || (c >= 2759 && c <= 2761)
          || (c >= 2763 && c <= 2765) || (c >= 2786 && c <= 2787) || (c == 2801)
          || (c >= 2817 && c <= 2819) || (c == 2876) || (c >= 2878 && c <= 2884)
          || (c >= 2887 && c <= 2888) || (c >= 2891 && c <= 2893) || (c >= 2902 && c <= 2903)
          || (c >= 2914 && c <= 2915) || (c == 2946) || (c >= 3006 && c <= 3010)
          || (c >= 3014 && c <= 3016) || (c >= 3018 && c <= 3021) || (c == 3031) || (c == 3065)
          || (c >= 3073 && c <= 3075) || (c >= 3134 && c <= 3140) || (c >= 3142 && c <= 3144)
          || (c >= 3146 && c <= 3149) || (c >= 3157 && c <= 3158) || (c >= 3170 && c <= 3171)
          || (c >= 3202 && c <= 3203) || (c == 3260) || (c >= 3262 && c <= 3268)
          || (c >= 3270 && c <= 3272) || (c >= 3274 && c <= 3277) || (c >= 3285 && c <= 3286)
          || (c >= 3298 && c <= 3299) || (c >= 3330 && c <= 3331) || (c >= 3390 && c <= 3396)
          || (c >= 3398 && c <= 3400) || (c >= 3402 && c <= 3405) || (c == 3415)
          || (c >= 3426 && c <= 3427) || (c >= 3458 && c <= 3459) || (c == 3530)
          || (c >= 3535 && c <= 3540) || (c == 3542) || (c >= 3544 && c <= 3551)
          || (c >= 3570 && c <= 3571) || (c == 3633) || (c >= 3636 && c <= 3642) || (c == 3647)
          || (c >= 3655 && c <= 3662) || (c == 3761) || (c >= 3764 && c <= 3769)
          || (c >= 3771 && c <= 3772) || (c >= 3784 && c <= 3789) || (c >= 3864 && c <= 3865)
          || (c == 3893) || (c == 3895) || (c == 3897) || (c >= 3902 && c <= 3903)
          || (c >= 3953 && c <= 3972) || (c >= 3974 && c <= 3975) || (c >= 3981 && c <= 3991)
          || (c >= 3993 && c <= 4028) || (c == 4038) || (c >= 4139 && c <= 4158)
          || (c >= 4182 && c <= 4185) || (c >= 4190 && c <= 4192) || (c >= 4194 && c <= 4196)
          || (c >= 4199 && c <= 4205) || (c >= 4209 && c <= 4212) || (c >= 4226 && c <= 4237)
          || (c == 4239) || (c >= 4250 && c <= 4253) || (c >= 4957 && c <= 4959)
          || (c >= 5870 && c <= 5872) || (c >= 5906 && c <= 5908) || (c >= 5938 && c <= 5940)
          || (c >= 5970 && c <= 5971) || (c >= 6002 && c <= 6003) || (c >= 6068 && c <= 6099)
          || (c == 6107) || (c == 6109) || (c >= 6155 && c <= 6157) || (c == 6313)
          || (c >= 6432 && c <= 6443) || (c >= 6448 && c <= 6459) || (c >= 6576 && c <= 6592)
          || (c >= 6600 && c <= 6601) || (c >= 6679 && c <= 6683) || (c >= 6741 && c <= 6750)
          || (c >= 6752 && c <= 6780) || (c == 6783) || (c >= 6912 && c <= 6916)
          || (c >= 6964 && c <= 6980) || (c >= 7019 && c <= 7027) || (c >= 7040 && c <= 7042)
          || (c >= 7073 && c <= 7082) || (c >= 7142 && c <= 7155) || (c >= 7204 && c <= 7223)
          || (c >= 7376 && c <= 7378) || (c >= 7380 && c <= 7400) || (c == 7405) || (c == 7410)
          || (c >= 7616 && c <= 7654) || (c >= 7676 && c <= 7679) || (c >= 8203 && c <= 8207)
          || (c >= 8234 && c <= 8238) || (c >= 8255 && c <= 8256) || (c == 8276)
          || (c >= 8288 && c <= 8292) || (c >= 8298 && c <= 8303) || (c >= 8352 && c <= 8377)
          || (c >= 8400 && c <= 8412) || (c == 8417) || (c >= 8421 && c <= 8432)
          || (c >= 8544 && c <= 8578) || (c >= 8581 && c <= 8584) || (c >= 11503 && c <= 11505)
          || (c == 11647) || (c >= 11744 && c <= 11775) || (c == 12295)
          || (c >= 12321 && c <= 12335) || (c >= 12344 && c <= 12346) || (c >= 12441 && c <= 12442)
          || (c == 42607) || (c >= 42620 && c <= 42621) || (c >= 42726 && c <= 42737)
          || (c == 43010) || (c == 43014) || (c == 43019) || (c >= 43043 && c <= 43047)
          || (c == 43064) || (c >= 43136 && c <= 43137) || (c >= 43188 && c <= 43204)
          || (c >= 43232 && c <= 43249) || (c >= 43302 && c <= 43309) || (c >= 43335 && c <= 43347)
          || (c >= 43392 && c <= 43395) || (c >= 43443 && c <= 43456) || (c >= 43561 && c <= 43574)
          || (c == 43587) || (c >= 43596 && c <= 43597) || (c == 43643) || (c == 43696)
          || (c >= 43698 && c <= 43700) || (c >= 43703 && c <= 43704) || (c >= 43710 && c <= 43711)
          || (c == 43713) || (c >= 44003 && c <= 44010) || (c >= 44012 && c <= 44013)
          || (c == 64286) || (c == 65020) || (c >= 65024 && c <= 65039)
          || (c >= 65056 && c <= 65062) || (c >= 65075 && c <= 65076) || (c >= 65101 && c <= 65103)
          || (c == 65129) || (c == 65279) || (c == 65284) || (c == 65343)
          || (c >= 65504 && c <= 65505) || (c >= 65509 && c <= 65510) || (c >= 65529 && c <= 65531)
          || (c >= 65856 && c <= 65908) || (c == 66045) || (c == 66369) || (c == 66378)
          || (c >= 66513 && c <= 66517) || (c >= 68097 && c <= 68099) || (c >= 68101 && c <= 68102)
          || (c >= 68108 && c <= 68111) || (c >= 68152 && c <= 68154) || (c == 68159)
          || (c >= 69632 && c <= 69634) || (c >= 69688 && c <= 69702) || (c >= 69760 && c <= 69762)
          || (c >= 69808 && c <= 69818) || (c == 69821) || (c >= 74752 && c <= 74850)
          || (c >= 119141 && c <= 119145) || (c >= 119149 && c <= 119170)
          || (c >= 119173 && c <= 119179) || (c >= 119210 && c <= 119213)
          || (c >= 119362 && c <= 119364) || (c == 917505) || (c >= 917536 && c <= 917631)
          || (c == 917760))
        return true;

      return false;
    }
  }

}
