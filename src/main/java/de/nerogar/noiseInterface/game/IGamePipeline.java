package de.nerogar.noiseInterface.game;

public interface IGamePipeline<T> {

	void register(IGameSystem system);

	void trigger(T t);

}
