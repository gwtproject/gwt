package com.google.gwt.safehtml.shared.annotations;

/**
 * Annotates methods that rely on potentially-unsafe type-annotation casts.
 * <p>
 * This annotation marks methods in which an expression without a
 * {@link com.google.gwt.safehtml.shared.annotations.IsSafeUrl} annotation is used in a context
 *  where such an annotation is required (e.g., the return statement of a method that
 * returns {@code @IsSafeUri String}).
 * <p>
 * As such, use of this annotation marks code that is potentially prone to XSS
 * vulnerabilities, and which hence needs to be carefully security reviewed.
 */
public @interface SuppressIsSafeUrlCastCheck {

}

