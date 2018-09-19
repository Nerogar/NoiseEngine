package de.nerogar.noise.input;

public class Xbox360Controller extends Joystick {

	public static final int BUTTON_A           = 0;
	public static final int BUTTON_B           = 1;
	public static final int BUTTON_X           = 2;
	public static final int BUTTON_Y           = 3;
	public static final int BUTTON_LB          = 4;
	public static final int BUTTON_RB          = 5;
	public static final int BUTTON_STICK_LEFT  = 8;
	public static final int BUTTON_STICK_RIGHT = 9;
	public static final int BUTTON_D_LEFT      = 13;
	public static final int BUTTON_D_RIGHT     = 11;
	public static final int BUTTON_D_UP        = 10;
	public static final int BUTTON_D_DOWN      = 12;
	public static final int BUTTON_BACK        = 6;
	public static final int BUTTON_START       = 7;

	public static final int AXIS_LEFT_X           = 0;
	public static final int AXIS_LEFT_X_POSITIVE  = AXIS_LEFT_X;
	public static final int AXIS_LEFT_X_NEGATIVE  = AXIS_LEFT_X | NEGATIVE_AXIS_BIT;
	public static final int AXIS_LEFT_Y           = 1;
	public static final int AXIS_LEFT_Y_POSITIVE  = AXIS_LEFT_Y;
	public static final int AXIS_LEFT_Y_NEGATIVE  = AXIS_LEFT_Y | NEGATIVE_AXIS_BIT;
	public static final int AXIS_RIGHT_X          = 2;
	public static final int AXIS_RIGHT_X_POSITIVE = AXIS_RIGHT_X;
	public static final int AXIS_RIGHT_X_NEGATIVE = AXIS_RIGHT_X | NEGATIVE_AXIS_BIT;
	public static final int AXIS_RIGHT_Y          = 3;
	public static final int AXIS_RIGHT_Y_POSITIVE = AXIS_RIGHT_Y;
	public static final int AXIS_RIGHT_Y_NEGATIVE = AXIS_RIGHT_Y | NEGATIVE_AXIS_BIT;
	public static final int AXIS_LT               = 4;
	public static final int AXIS_RT               = 5;

	protected Xbox360Controller(int id, String name) {
		super(id, name, 14, 6);
	}

	public static boolean accept(String name) {
		return name.toLowerCase().contains("xbox");
	}

	@Override
	public String getButtonName(int button) {

		switch (button) {
		case BUTTON_A: return "A";
		case BUTTON_B: return "B";
		case BUTTON_X: return "X";
		case BUTTON_Y: return "Y";
		case BUTTON_LB: return "LB";
		case BUTTON_RB: return "RB";
		case BUTTON_STICK_LEFT: return "stick left";
		case BUTTON_STICK_RIGHT: return "stick right";
		case BUTTON_D_LEFT: return "D-pad left";
		case BUTTON_D_RIGHT: return "D-pad right";
		case BUTTON_D_UP: return "D-pad up";
		case BUTTON_D_DOWN: return "D-pad down";
		case BUTTON_START: return "start";
		case BUTTON_BACK: return "back";
		}

		return super.getButtonName(button);

	}

	@Override
	public String getAxisName(int axis) {

		switch (axis) {
		case AXIS_LEFT_X_POSITIVE: return "left X+";
		case AXIS_LEFT_X_NEGATIVE: return "left X-";
		case AXIS_LEFT_Y_POSITIVE: return "left Y+";
		case AXIS_LEFT_Y_NEGATIVE: return "left Y-";
		case AXIS_RIGHT_X_POSITIVE: return "right X+";
		case AXIS_RIGHT_X_NEGATIVE: return "right X-";
		case AXIS_RIGHT_Y_POSITIVE: return "right Y+";
		case AXIS_RIGHT_Y_NEGATIVE: return "right Y-";
		case AXIS_LT: return "LT";
		case AXIS_RT: return "RT";
		}

		return super.getAxisName(axis);

	}

	@Override
	public float getAxisStatus(int axis) {
		float value = super.getAxisStatus(axis);

		if (axis == AXIS_LT || axis == AXIS_RT) {
			value = (value + 1f) * 0.5f;
		}

		// todo add dead zones to joystick class
		if (Math.abs(value) < 0.1f) return 0;

		return value;
	}
}
