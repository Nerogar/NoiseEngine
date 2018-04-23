package de.nerogar.noise.game.core.network.packets;

import de.nerogar.noise.game.NoiseGame;
import de.nerogar.noise.network.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientMapLoadProgress extends Packet {

	private float   progress;
	private boolean done;

	public ClientMapLoadProgress() {
	}

	public ClientMapLoadProgress(float progress, boolean done) {
		this.progress = progress;
		this.done = done;
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		progress = in.readFloat();
		done = in.readBoolean();
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeFloat(progress);
		out.writeBoolean(done);
	}

	public float getProgress() { return progress; }

	public boolean isDone()    { return done; }

	@Override
	public int getChannel() {
		return NoiseGame.CONTROL_PACKET_CHANNEL;
	}

}
