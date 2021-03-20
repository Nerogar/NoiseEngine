package de.nerogar.noiseInterface.game;

import de.nerogar.noiseInterface.event.IEvent;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConsumesEvent {
	Class<? extends IEvent>[] value();
}
