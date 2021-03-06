package parcours.task;

import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;
import parcours.task.base.ControllerTask;
import parcours.task.boss.BossContext;
import parcours.utils.RobotDesign;

public class GoIntoElevatorTask extends ControllerTask {
	protected DifferentialPilot pilot;
	protected TouchSensor touchSensorRight, touchSensorLeft;
	private BossContext context;
	
	@Override
	protected void init() {
		pilot = RobotDesign.differentialPilot;
		touchSensorRight = RobotDesign.touchSensorRight;
		touchSensorLeft = RobotDesign.touchSensorLeft;
		context = new BossContext(pilot);
		pilot.setTravelSpeed(10);
		pilot.forward();
	}

	@Override
	protected void control() {
		
		boolean leftIsPressed = touchSensorLeft.isPressed();
		boolean rightIsPressed = touchSensorRight.isPressed();

		/*if (leftIsPressed && rightIsPressed) {
		} else if (leftIsPressed) {
			context.handleLeftButtonPressed();
		} else if (rightIsPressed) {
			context.handleRightButtonPressed();
		} else {
			context.handleNoButtonIsPressed();
		}*/

		pilot.travel(15);
		pilot.forward();
		
	}

	@Override
	protected boolean abort() {
		return (touchSensorRight.isPressed() || touchSensorLeft.isPressed());
	}

	@Override
	protected void tearDown() {
		pilot.stop();
	}

}
