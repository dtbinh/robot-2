package parcours.task.wall.state;

public class Start90DegreeRightTurn extends WallStateBase {



	@Override
	public void handleNoButtonIsPressed(WallStateContext context, int distance) {
		if(context.getPilot().isMoving()) {
			return;
		}
		
		context.getPilot().rotate(-90, true);
		context.setState(WallState.END_90_DEGREE_RIGHT_TURN);
	}

}
