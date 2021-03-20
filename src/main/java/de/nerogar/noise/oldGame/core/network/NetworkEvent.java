package de.nerogar.noise.oldGame.core.network;

import de.nerogar.noiseInterface.event.IEvent;
import de.nerogar.noise.oldGame.NoiseGame;
import de.nerogar.noise.network.Packet;

public abstract class NetworkEvent extends Packet implements IEvent {

	@Override
	public final int getChannel() {
		return NoiseGame.EVENT_PACKET_CHANNEL;
	}

}
