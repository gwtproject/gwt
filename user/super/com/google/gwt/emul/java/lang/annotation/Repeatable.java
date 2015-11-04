package java.lang.annotation;

/**
 * See <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Repeatable.html">
 * the official Java API doc</a> for details.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Repeatable {
  Class<? extends Annotation> value();
}
