package de.nerogar.noiseInterface.game;

public interface IGamePipeline<T> {

	void register(Object object);

	void trigger(T t);

}
