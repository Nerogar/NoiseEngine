package de.nerogar.noise.oldGame.core.network.packets;

import de.nerogar.noise.oldGame.Components;
import de.nerogar.noise.oldGame.core.components.SynchronizedComponent;
import de.nerogar.noise.oldGame.core.network.NetworkEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ComponentPacket extends NetworkEvent {

	private int   id;
	private short componentID;

	private SynchronizedComponent component;
	private DataInputStream       input;

	private byte[] data;

	public ComponentPacket() {
	}

	public ComponentPacket(SynchronizedComponent component) {
		this.component = component;

		id = component.getEntity().getID();
		componentID = Components.getIDFromComponent(component.getClass());

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			component.toStream(new DataOutputStream(byteArrayOutputStream));
			data = byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fromStream(DataInputStream in) throws IOException {
		id = in.readInt();
		componentID = in.readShort();

		this.input = in;
	}

	@Override
	public void toStream(DataOutputStream out) throws IOException {
		out.writeInt(id);
		out.writeShort(componentID);

		out.write(data);
	}

	public int getId()                { return id; }

	public short getComponentID()     { return componentID; }

	public DataInputStream getInput() { return input; }

}
