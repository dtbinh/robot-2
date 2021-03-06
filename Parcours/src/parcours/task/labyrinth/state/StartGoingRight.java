package parcours.task.labyrinth.state;

import parcours.task.labyrinth.LabyrinthContext;

public class StartGoingRight extends LabyrinthStateBase {
	
	public String name() {
		return "SGoR";
	}

	@Override
	public void handleNoButtonIsPressed(LabyrinthContext context, int distance) {
		if(context.isMoving()) {
			return;
		}
		
		context.rotate(context.getGoingRightAngle());
		context.setState(LabyrinthState.FINISH_MOVEMENT);
	}

}
