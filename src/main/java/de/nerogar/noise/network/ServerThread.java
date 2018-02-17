package de.nerogar.noise.network;

import de.nerogar.noise.Noise;
import de.nerogar.noise.network.packets.PacketConnectionInfo;
import de.nerogar.noise.util.Logger;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerThread extends Thread{

	private ServerSocket socket;

	private List<Connection> connections        = new ArrayList<>();
	private List<Connection> newConnections     = new ArrayList<>();
	private List<Connection> pendingConnections = new ArrayList<>();

	private INetworkAdapter[] networkAdapters = new INetworkAdapter[256];
	private INetworkAdapter wildcardNetworkAdapter;

	public ServerThread(int port) throws BindException {
		try {
			socket = new ServerSocket(port);
		} catch (BindException e) {
			throw e;
		} catch (IOException e) {
			Noise.getLogger().log(Logger.ERROR, "The server crashed brutally");
			e.printStackTrace();
		}
		this.wildcardNetworkAdapter = new ServerWildcardNetworkAdapter(this);

		this.setDaemon(true);
		this.start();
	}

	/**
	 * returns the {@link INetworkAdapter} with the specified id
	 *
	 * @param id the adapter id
	 * @return the network adapter
	 * @throws IllegalArgumentException when the id is over 64
	 */
	public INetworkAdapter getNetworkAdapter(int id) {
		if (id > 255) throw new IllegalArgumentException("id has to fit in 8 bits (0 <= id <= 255)");

		if (networkAdapters[id] == null) {
			networkAdapters[id] = new ServerNetworkAdapter(this, id);
		}

		return networkAdapters[id];
	}

	/**
	 * returns an {@link INetworkAdapter} that aggregates all network adapters
	 *
	 * @return the network adapter
	 */
	public INetworkAdapter getWildcardNetworkAdapter() {
		return wildcardNetworkAdapter;
	}

	@Override
	public void run() {

		try {
			while (!isInterrupted()) {
				addPendingConnection(new Connection(socket.accept()));
			}
		} catch (SocketException e) {
			// System.err.println("SocketException in Server");
			// e.printStackTrace();
		} catch (IOException e) {
			Noise.getLogger().log(Logger.ERROR, "The server crashed brutally");
			e.printStackTrace();
		}

		stopThread();
		Noise.getLogger().log(Logger.INFO, "SHUTDOWN: Server - " + socket);

	}

	private void checkPendingConnections() {
		for (Iterator<Connection> iter = pendingConnections.iterator(); iter.hasNext(); ) {
			Connection conn = iter.next();
			if (conn.isClosed()) {
				iter.remove();
				continue;
			}

			conn.pollPackets(true);
			ArrayList<Packet> connectionPackets = conn.getNetworkAdapter(0).getPackets(PacketInfo.SYSTEM_PACKET_CHANNEL);

			// The only CONNECTION_INFO packet can be ConnectionInfo. If that's not the case, deal with the ClassCastException and fix it.
			// Also ignore any additional packets. Just the first ConnectionInfo packet gets processed
			if (connectionPackets.size() > 0) {
				PacketConnectionInfo packet = (PacketConnectionInfo) connectionPackets.get(0);
				if (packet.version == PacketInfo.NETWORKING_VERSION) {
					addConnection(conn);
				} else {
					// Wrong Networking version
					conn.close();
				}
				iter.remove();
			}
		}
	}

	private void cleanupClosedConnections() {
		connections.removeIf(Connection::isClosed);
		newConnections.removeIf(Connection::isClosed);
	}

	private void addConnection(Connection conn) {
		connections.add(conn);
		newConnections.add(conn);
	}

	private void addPendingConnection(Connection conn) {
		//conn.send(new PacketConnectionInfo(PacketInfo.NETWORKING_VERSION));
		//conn.flushPackets();
		pendingConnections.add(conn);
	}

	synchronized void broadcast(int adapterId, Packet packet) {
		for (Connection connection : connections) {
			connection.send(adapterId, packet);
		}
	}

	ArrayList<Packet> getPackets(int adapterId, int channelID) {
		ArrayList<Packet> packets = new ArrayList<>();

		for (Connection connection : connections) {
			packets.addAll(connection.getPackets(adapterId, channelID));
		}

		return packets;
	}

	public void stopThread() {
		for (Connection connection : connections) {
			connection.close();
		}
		interrupt();
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				Noise.getLogger().log(Logger.WARNING, "Could not close Server-Socket");
				e.printStackTrace();
			}
		}
	}

	public List<Connection> getNewConnections() {
		cleanupClosedConnections();
		checkPendingConnections();
		List<Connection> connections = newConnections;
		newConnections = new ArrayList<>();
		return connections;
	}

	public int getPort() {
		return socket.getLocalPort();
	}

}
