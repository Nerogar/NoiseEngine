package de.nerogar.noise.oldGame.core.components;

import de.nerogar.noise.oldGame.Component;
import de.nerogar.noise.oldGame.Side;
import de.nerogar.noise.oldGame.core.events.ComponentUpdateEvent;
import de.nerogar.noise.network.Streamable;

public abstract class SynchronizedComponent<T extends SynchronizedComponent<T>> extends Component<T> implements Streamable {

	protected void synchronize() {
		if (getEntity().getMap().getSide() == Side.SERVER) {
			getEntity().getMap().getEventManager().trigger(new ComponentUpdateEvent(this));
		}
	}

}
