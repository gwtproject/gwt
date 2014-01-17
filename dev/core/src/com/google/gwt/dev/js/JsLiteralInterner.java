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
package com.google.gwt.dev.js;

import com.google.gwt.dev.jjs.InternalCompilerException;
import com.google.gwt.dev.jjs.SourceInfo;
import com.google.gwt.dev.jjs.ast.JProgram;
import com.google.gwt.dev.js.ast.JsArrayLiteral;
import com.google.gwt.dev.js.ast.JsBinaryOperation;
import com.google.gwt.dev.js.ast.JsBlock;
import com.google.gwt.dev.js.ast.JsContext;
import com.google.gwt.dev.js.ast.JsLiteral;
import com.google.gwt.dev.js.ast.JsModVisitor;
import com.google.gwt.dev.js.ast.JsName;
import com.google.gwt.dev.js.ast.JsNode;
import com.google.gwt.dev.js.ast.JsNumberLiteral;
import com.google.gwt.dev.js.ast.JsObjectLiteral;
import com.google.gwt.dev.js.ast.JsPostfixOperation;
import com.google.gwt.dev.js.ast.JsPrefixOperation;
import com.google.gwt.dev.js.ast.JsProgram;
import com.google.gwt.dev.js.ast.JsProgramFragment;
import com.google.gwt.dev.js.ast.JsPropertyInitializer;
import com.google.gwt.dev.js.ast.JsRegExp;
import com.google.gwt.dev.js.ast.JsScope;
import com.google.gwt.dev.js.ast.JsStringLiteral;
import com.google.gwt.dev.js.ast.JsVars;
import com.google.gwt.dev.js.ast.JsVars.JsVar;
import com.google.gwt.dev.js.ast.JsVisitor;
import com.google.gwt.dev.js.ast.NodeKind;
import com.google.gwt.thirdparty.guava.common.base.Preconditions;
import com.google.gwt.thirdparty.guava.common.collect.HashMultiset;
import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.thirdparty.guava.common.collect.Multiset;
import com.google.gwt.thirdparty.guava.common.collect.TreeMultiset;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;

/**
 * Interns conditionally either all literals in a JsProgram, or literals
 * which exceed a certain usage count. Each unique String will be assigned to a
 * variable in an appropriate program fragment and the JsStringLiteral will be
 * replaced with a JsNameRef. This optimization is complete in a single pass,
 * although it may be performed multiple times without duplicating the intern
 * pool.
 */
public class JsLiteralInterner {

  /**
   * Counts occurrences of each potentially internable String literal.
   */
  private static class OccurenceCounter extends JsVisitor {

    private Multiset<JsLiteral> countByLiteral = TreeMultiset.create(LITERAL_COMPARATOR);

    public Multiset<JsLiteral> computeLiteralCounts() {
      return countByLiteral;
    }

    private Multiset<JsLiteral> lhsLiterals = HashMultiset.create();

    private boolean hasLhsLiteral(JsBinaryOperation x) {
      return x.getOperator().isAssignment()
          && (x.getArg1() instanceof JsLiteral);
    }

    @Override
    public void endVisit(JsBinaryOperation x, JsContext ctx) {
      if (hasLhsLiteral(x)) {
        lhsLiterals.remove(x.getArg1());
      }
    }

    @Override
    public boolean visit(JsBinaryOperation x, JsContext ctx) {
      if (hasLhsLiteral(x)) {
        lhsLiterals.add((JsLiteral) x.getArg1());
      }
      return true;
    }

    /**
     * Prevents 'fixing' an otherwise illegal operation.
     */
    @Override
    public boolean visit(JsPostfixOperation x, JsContext ctx) {
      return !(x.getArg() instanceof JsLiteral);
    }

    /**
     * Prevents 'fixing' an otherwise illegal operation.
     */
    @Override
    public boolean visit(JsPrefixOperation x, JsContext ctx) {
      return !(x.getArg() instanceof JsLiteral);
    }

    /**
     * We ignore property initializer labels in object literals, but do process
     * the expression. This is because the LHS is always treated as a string,
     * and never evaluated as an expression.
     */
    @Override
    public boolean visit(JsPropertyInitializer x, JsContext ctx) {
      accept(x.getValueExpr());
      return false;
    }

    /**
     * Count occurences of String literal.
     */
    @Override
    public boolean visit(JsStringLiteral x, JsContext ctx) {
      countByLiteral.add(x);
      return false;
    }

    /**
     * Count occurences of Object literal.
     */
    @Override
    public boolean visit(JsObjectLiteral x, JsContext ctx) {
      if (x.isInternable()) {
        countByLiteral.add(x);
      }
      // The literal was not internable but might have some internable constants inside.
      // TODO(rluble): as the interner does not recursively intern complex object literals,
      // some literals might be overcounted.
      return true;
    }

    @Override
    public boolean visit(JsRegExp x, JsContext ctx) {
      if (x.isInternable()) {
        countByLiteral.add(x);
      }
      return true;
    }

    @Override
    public boolean visit(JsNumberLiteral x, JsContext ctx) {
      if (x.isInternable()) {
        countByLiteral.add(x);
      }
      return true;
    }

    /**
     * Count occurences of Array literal.
     */
    @Override
    public boolean visit(JsArrayLiteral x, JsContext ctx) {
      if (x.isInternable()) {
        countByLiteral.add(x);
      }
      return true;
    }
    /**
     * This prevents duplicating the intern pool by not traversing JsVar
     * declarations that look like they were created by the interner.
     */
    @Override
    public boolean visit(JsVar x, JsContext ctx) {
      return !(x.getName().getIdent().startsWith(PREFIX));
    }
  }

  /**
   * Replaces internable JsLiterals with JsNameRefs, creating new JsName allocations
   * on the fly.
   */
  private static class LiteralInterningVisitor extends JsModVisitor {
    /*
     * Minimum number of times a string must occur to be interned.
     */
    private static final Integer INTERN_THRESHOLD = Integer.parseInt(
        // TODO(rluble): change the property name to reflect that not only strings are interned.
        System.getProperty("gwt.jjs.stringInternerThreshold", "2"));

    /**
     * The current fragment being visited.
     */
    private int currentFragment = 0;

    /**
     * This map records which program fragment the variable for this JsName
     * should be created in.
     */
    private final SortedMap<JsLiteral, Integer> fragmentAssignment =
        Maps.newTreeMap(LITERAL_COMPARATOR);

    /**
     * A counter used for assigning ids to Strings. Even though it's unlikely
     * that someone would actually have two billion strings in their
     * application, it doesn't hurt to think ahead.
     */
    private long lastId = 0;

    /**
     * Count of # of occurences of each String literal, or null if
     * count-sensitive interning is off.
     */
    private Multiset<JsLiteral> occurrenceMap;

    /**
     * Only used to get fragment load order so strings used in multiple
     * fragments need only be downloaded once.
     */
    private final JProgram program;

    /**
     * Records the scope in which the interned identifiers are declared.
     */
    private final JsScope scope;

    /**
     * This is a TreeMap to ensure consistent iteration order, based on the
     * lexicographical ordering of the string constant.
     */
    private final Map<JsLiteral, JsName> toCreate = Maps.newTreeMap(LITERAL_COMPARATOR);

    /**
     * This is a set of flags indicating what types of literals are to be interned.
     */
    private final byte whatToIntern;

    /**
     * Constructor.
     *
     * @param scope specifies the scope in which the interned strings should be
     * @param occurrenceMap
     */
    public LiteralInterningVisitor(JProgram program, JsScope scope,
        Multiset<JsLiteral> occurrenceMap, byte whatToIntern) {
      this.program = program;
      this.scope = scope;
      this.occurrenceMap = occurrenceMap;
      this.whatToIntern = whatToIntern;
    }

    @Override
    public void endVisit(JsProgramFragment x, JsContext ctx) {
      currentFragment++;
    }

    /**
     * Prevents 'fixing' an otherwise illegal operation.
     */
    @Override
    public boolean visit(JsBinaryOperation x, JsContext ctx) {
      return !x.getOperator().isAssignment()
          || !(x.getArg1() instanceof JsStringLiteral);
    }

    /**
     * Prevents 'fixing' an otherwise illegal operation.
     */
    @Override
    public boolean visit(JsPostfixOperation x, JsContext ctx) {
      return !(x.getArg() instanceof JsStringLiteral);
    }

    /**
     * Prevents 'fixing' an otherwise illegal operation.
     */
    @Override
    public boolean visit(JsPrefixOperation x, JsContext ctx) {
      return !(x.getArg() instanceof JsStringLiteral);
    }

    /**
     * We ignore property initializer labels in object literals, but do process
     * the expression. This is because the LHS is always treated as a string,
     * and never evaluated as an expression.
     */
    @Override
    public boolean visit(JsPropertyInitializer x, JsContext ctx) {
      x.setValueExpr(accept(x.getValueExpr()));
      return false;
    }

    /**
     * Replace JsStringLiteral instances with JsNameRefs.
     */
    @Override
    public boolean visit(JsStringLiteral x, JsContext ctx) {
      if ((whatToIntern & INTERN_STRINGS) != 0) {
        maybeInternLiteral(x, ctx);
      }
      return false;
    }

    /**
     * Replace JsStringLiteral instances with JsNameRefs.
     */
    @Override
    public boolean visit(JsObjectLiteral x, JsContext ctx) {
      boolean interned = false;
      if ((whatToIntern & INTERN_OBJECTLITERALS) != 0) {
        interned = maybeInternLiteral(x, ctx);
      }

      // If the object literal interned do not try to intern any of its contents.
      return interned;
    }

    /**
     * Replace JsStringLiteral instances with JsNameRefs.
     */
    @Override
    public boolean visit(JsRegExp x, JsContext ctx) {
      if ((whatToIntern & INTERN_REGEXES) != 0) {
        maybeInternLiteral(x, ctx);
      }
      return false;
    }

    /**
     * Replace JsStringLiteral instances with JsNameRefs.
     */
    @Override
    public boolean visit(JsNumberLiteral x, JsContext ctx) {
      if ((whatToIntern & INTERN_NUMBERS) != 0) {
        maybeInternLiteral(x, ctx);
      }
      return false;
    }

    private boolean maybeInternLiteral(JsLiteral x, JsContext ctx) {
      if (!x.isInternable()) {
        return false;
      }

      if (occurrenceMap != null) {
        int occurrences = occurrenceMap.count(x);
        if (occurrences < INTERN_THRESHOLD) {
          return false;
        }
      }

      JsName name = toCreate.get(x);
      if (name == null) {
        String ident = PREFIX + lastId++;
        name = scope.declareName(ident);
        toCreate.put(x, name);
      }

      Integer currentAssignment = fragmentAssignment.get(x);
      if (currentAssignment == null) {
        // Assign the JsName to the current program fragment
        fragmentAssignment.put(x, currentFragment);

      } else if (currentAssignment != currentFragment) {
        // See if we need to move the assignment to a common ancestor
        Preconditions.checkState(program != null, "JsLiteralInterner cannot be used with "
            + "fragmented JsProgram without an accompanying JProgram");

        int newAssignment = program.getCommonAncestorFragmentId(currentAssignment, currentFragment);
        if (newAssignment != currentAssignment) {
          // Assign the JsName to the common ancestor.
          fragmentAssignment.put(x, newAssignment);
        }
      }

      ctx.replaceMe(name.makeRef(x.getSourceInfo().makeChild()));
      return true;
    }

    /**
     * This prevents duplicating the intern pool by not traversing JsVar
     * declarations that look like they were created by the interner.
     */
    @Override
    public boolean visit(JsVar x, JsContext ctx) {
      return !(x.getName().getIdent().startsWith(PREFIX));
    }
  }

  /**
   * Flags to control what type of literals to intern.
   */
  public static final byte INTERN_STRINGS = 0x01;
  public static final byte INTERN_REGEXES = 0x02;
  public static final byte INTERN_OBJECTLITERALS = 0x04;
  public static final byte INTERN_NUMBERS = 0x08;
  public static final byte INTERN_ALL = INTERN_STRINGS | INTERN_REGEXES | INTERN_OBJECTLITERALS |
      INTERN_NUMBERS;

  private static final String PREFIX = "$intern_";

  /**
   * Used to count occurrences of different internable literals.
   *
   */
  private static final Comparator<JsLiteral> LITERAL_COMPARATOR =
      new Comparator<JsLiteral>() {
        public int compare(JsLiteral o1, JsLiteral o2) {
          NodeKind o1Kind = o1.getKind();
          NodeKind o2Kind = o2.getKind();
          if (o1Kind != o2Kind) {
            return o1Kind.ordinal() - o2Kind.ordinal();
          }

          // Both are the same type of literal.
          if (o1 instanceof JsStringLiteral) {
            return ((JsStringLiteral) o1).getValue().compareTo(((JsStringLiteral) o2).getValue());
          }

          if (o1 instanceof JsObjectLiteral || o1 instanceof JsRegExp ||
              o1 instanceof JsNumberLiteral) {
            return o1.toSource().compareTo(o2.toSource());
          }

          throw new InternalCompilerException("Tried to intern a non internable literal type " +
              o1.getClass().getName());
        }
      };

  /**
   * Apply interning of String literals to a JsProgram. The symbol names for the
   * interned strings will be defined within the program's top scope and the
   * symbol declarations will be added as the first statement in the program's
   * global block.
   *
   * @param jprogram the JProgram that has fragment dependency data for
   *          <code>program</code>
   * @param program the JsProgram
   * @param whatToIntern a byte mask indicating what types of literals are interned.
   * @return a map describing the interning that occurred
   */
  public static Map<JsName, JsLiteral> exec(JProgram jprogram, JsProgram program,
      byte whatToIntern ) {
    LiteralInterningVisitor v = new LiteralInterningVisitor(jprogram, program.getScope(),
        getOccurenceMap(program), whatToIntern);
    v.accept(program);

    Map<Integer, Set<JsLiteral>> bins = Maps.newHashMap();
    for (int i = 0, j = program.getFragmentCount(); i < j; i++) {
      bins.put(i, new TreeSet<JsLiteral>(LITERAL_COMPARATOR) {
      });
    }
    for (Map.Entry<JsLiteral, Integer> entry : v.fragmentAssignment.entrySet()) {
      Set<JsLiteral> set = bins.get(entry.getValue());
      assert set != null;
      set.add(entry.getKey());
    }

    for (Map.Entry<Integer, Set<JsLiteral>> entry : bins.entrySet()) {
      createVars(program, program.getFragmentBlock(entry.getKey()),
          entry.getValue(), v.toCreate);
    }

    return reverse(v.toCreate);
  }

  /**
   * Intern String literals that occur within a JsBlock. The symbol declarations
   * will be added as the first statement in the block.
   *
   * @param block the block to visit
   * @param scope the JsScope in which to reserve the new identifiers
   * @param alwaysIntern true for browsers like IE which must always intern literals
   * @return <code>true</code> if any changes were made to the block
   */
  public static boolean exec(JsProgram program, JsBlock block, JsScope scope,
      boolean alwaysIntern) {
    LiteralInterningVisitor v = new LiteralInterningVisitor(null, scope, alwaysIntern ? null :
        getOccurenceMap(block), INTERN_ALL);
    v.accept(block);

    createVars(program, block, v.toCreate.keySet(), v.toCreate);

    return v.didChange();
  }

  /**
   * Create variable declarations in <code>block</code> for literal strings
   * <code>toCreate</code> using the variable map <code>names</code>.
   */
  private static void createVars(JsProgram program, JsBlock block,
      Collection<JsLiteral> toCreate, Map<JsLiteral, JsName> names) {
    if (toCreate.size() > 0) {
      // Create the pool of variable names.
      SourceInfo sourceInfo = program.createSourceInfoSynthetic(JsLiteralInterner.class);
      JsVars vars = new JsVars(sourceInfo);
      for (JsLiteral literal : toCreate) {
        JsVar var = new JsVar(sourceInfo, names.get(literal));
        var.setInitExpr(literal);
        vars.add(var);
      }
      block.getStatements().add(0, vars);
    }
  }

  private static Multiset<JsLiteral> getOccurenceMap(JsNode node) {
    OccurenceCounter oc = new OccurenceCounter();
    oc.accept(node);
    return oc.computeLiteralCounts();
  }

  private static Map<JsName, JsLiteral> reverse(
      Map<JsLiteral, JsName> toCreate) {
    Map<JsName, JsLiteral> reversed = Maps.newLinkedHashMap();
    for (Entry<JsLiteral, JsName> entry : toCreate.entrySet()) {
      reversed.put(entry.getValue(), entry.getKey());
    }
    return reversed;
  }

  /**
   * Utility class.
   */
  private JsLiteralInterner() {
  }
}
