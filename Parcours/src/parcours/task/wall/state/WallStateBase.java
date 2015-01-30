package parcours.task.wall.state;

public abstract class WallStateBase {

	private static final int BACKWARD_LEFT_BUTTON_WALL = -20;
	private static final int BACKWARD_LEFT_BUTTON = -10;
	private static final int BACKWARD_RIGHT_BUTTON = -10;
	private static final int NO_WALL_DISTANCE = 25;
	private static final int BACKWARD_BOTH_BUTTONS = -25;

	public final void handleBothButtonsPressed(WallStateContext context,
			int distance) {
		context.getPilot().travel(BACKWARD_BOTH_BUTTONS, true);
		context.setState(WallState.START_90_DEGREE_LEFT_TURN);
	}

	public final void handleLeftButtonPressed(WallStateContext context,
			int distance) {
		// TODO Auto-generated method stub
		if (distance < NO_WALL_DISTANCE) {
			context.getPilot().travel(BACKWARD_LEFT_BUTTON_WALL, true);
			context.setState(WallState.START_90_DEGREE_LEFT_TURN);
			/*
			 * awaitRotation(); pilot.rotate(90,true); awaitRotation();
			 */
		} else {
			context.getPilot().travel(BACKWARD_LEFT_BUTTON, true);
			context.setState(WallState.START_GOING_LEFT);
			/*
			 * a1waitRotation(); pi1lot.rotate(-15,true); awaitRotation();
			 */
		}
	}

	public final void handleRightButtonPressed(WallStateContext context,
			int distance) {
		context.getPilot().travel(BACKWARD_RIGHT_BUTTON,true);
		context.setState(WallState.START_GOING_RIGHT);
		/*awaitRotation();
		pilot.rotate(20,true);
		awaitRotation();*/
	}

	public abstract void handleNoButtonIsPressed(WallStateContext context,
			int distance);

}
