package de.nerogar.noise.oldGame.core.network;

import de.nerogar.noise.event.Event;
import de.nerogar.noise.oldGame.NoiseGame;
import de.nerogar.noise.network.Packet;

public abstract class NetworkEvent extends Packet implements Event{

	@Override
	public final int getChannel() {
		return NoiseGame.EVENT_PACKET_CHANNEL;
	}

}
