package de.nerogar.noiseInterface.game;

import de.nerogar.noiseInterface.event.IEvent;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ConsumesEventContainer.class)
public @interface ConsumesEvent {

	Class<? extends IEvent> event();

	String method();
}
