package com.google.gwt.safehtml.shared.annotations;

/**
 * Annotates methods that could not be automatically refactored to use {@link IsSafeHtml} correctly.
 * <p>
 * This annotation marks methods in which an expression without a
 * {@link com.google.gwt.safehtml.shared.annotations.IsSafeHtml} annotation is used in a context
 *  where such an annotation is required (e.g., the return statement of a method that
 * returns {@code @IsSafeHtml String}).
 * <p>
 * As such, use of this annotation marks code that is potentially prone to HTML-injection
 * vulnerabilities, and which hence needs to be carefully security reviewed or replaced with a safe
 * construct.
 */
public @interface SuppressIsSafeHtmlCastCheckLegacy {

}

