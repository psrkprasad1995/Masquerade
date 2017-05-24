package test;

import java.lang.annotation.*;

/**
 * Created by shrey.garg on 24/04/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(ValidationAnnotations.class)
public @interface ValidationAnnotation {
    Platform name();
    int since();
    int till() default 0;
}
