package nsu.framework;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Serialize {
    boolean allFields() default false;

    boolean requiresParent() default false;
}
