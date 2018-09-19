package de.nerogar.noise.game.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ComponentInfo {

	String name();

	ComponentSide side();

	// todo add "requires" parameter
	// maybe even test requirements during compile time by reading entity definitions?
}
