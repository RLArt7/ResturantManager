package RestBL;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WhileWaiting {
	//		String value();
	//		int    estimatedEffortTime() default 20;
}
