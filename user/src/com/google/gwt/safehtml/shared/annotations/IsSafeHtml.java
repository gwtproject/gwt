package com.google.gwt.safehtml.shared.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Target;

/**
 * A type annotation that represents values that are safe to use in a HTML context.
 *
 * The annotated type {@code @Html String} and the type {@code SafeHtml} are semantically 
 * equivalent.
 */
@Target({PARAMETER, METHOD, FIELD, LOCAL_VARIABLE})
public @interface IsSafeHtml {
}