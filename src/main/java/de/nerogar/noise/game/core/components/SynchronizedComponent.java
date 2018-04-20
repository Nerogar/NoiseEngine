package de.nerogar.noise.game.core.components;

import de.nerogar.noise.game.Component;
import de.nerogar.noise.game.Side;
import de.nerogar.noise.game.core.events.ComponentUpdateEvent;
import de.nerogar.noise.network.Streamable;

public abstract class SynchronizedComponent extends Component implements Streamable {

	protected void synchronize() {
		if (getEntity().getMap().getSide() == Side.SERVER) {
			getEntity().getMap().getEventManager().trigger(new ComponentUpdateEvent(this));
		}
	}

}
