package de.nerogar.noise.game;

import de.nerogar.noise.util.Logger;

public class NoiseGame {

	public static final Logger logger = new Logger("NoiseGame");

	public static final String DATA_DIR = "data/";

	// packet channels

	/**
	 * The base packet channel.
	 * All channels from {@link NoiseGame#BASE_PACKET_CHANNEL} to {@link NoiseGame#MAX_PACKET_CHANNEL} are reserved.
	 */
	public static final int BASE_PACKET_CHANNEL = 0x1_00_00;

	/**
	 * The last reserved packet channel.
	 * All channels from {@link NoiseGame#BASE_PACKET_CHANNEL} to {@link NoiseGame#MAX_PACKET_CHANNEL} are reserved.
	 */
	public static final int MAX_PACKET_CHANNEL = 0x1_FF_FF;

	public static final int CONTROL_PACKET_CHANNEL = BASE_PACKET_CHANNEL + 0;

	public static final int MAP_PACKET_CHANNEL     = BASE_PACKET_CHANNEL + 10;
	public static final int SYSTEMS_PACKET_CHANNEL = BASE_PACKET_CHANNEL + 12;

	public static final int EVENT_PACKET_CHANNEL   = BASE_PACKET_CHANNEL + 20;
	public static final int REQUEST_PACKET_CHANNEL = BASE_PACKET_CHANNEL + 21;

	public static final int DEBUG_SCREEN_CAHNNEL = BASE_PACKET_CHANNEL + 101;

	// network adapters
	public static final int NETWORK_ADAPTER_DEFAULT    = 1;
	public static final int NETWORK_ADAPTER_START_MAPS = 10;

}
