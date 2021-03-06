package parcours.task;

import lejos.nxt.Motor;
import lejos.nxt.TouchSensor;
import lejos.robotics.navigation.DifferentialPilot;
import parcours.detector.LineDetector;
import parcours.task.base.ControllerTask;
import parcours.task.state.LineSideState;
import parcours.utils.RobotDesign;

public class FindLineTask extends ControllerTask {
	private static final int BASE_SPEED = 20;	
	private static final int BACKOFF_DISTANCE = -3;
	private static final int IMPACT_CORRECTION_ANGLE = 15;
	private static final int ARC_LEFT_RADIUS = 50;
	private static final int ARC_RIGHT_RADIUS = -50;
	
	private DifferentialPilot pilot;
	private LineDetector lineDetector;
	private TouchSensor touchR, touchL;
	private boolean driveStraight;
	private LineSideState state;
	private boolean abortOnImpact = false;
	private boolean abortDetected = false;
	
	public FindLineTask() {
		this.driveStraight = true;
	}
	
	public FindLineTask(boolean abortOnImpact) {
		this.abortOnImpact = abortOnImpact;
		this.driveStraight = true;
	}
	
	public FindLineTask(boolean driveStraight, LineSideState state) {
		this.driveStraight = driveStraight;
		this.state = state;
	}

	@Override
	protected void init() {
		touchR = RobotDesign.touchSensorRight;
		touchL = RobotDesign.touchSensorLeft;
		
		pilot = RobotDesign.differentialPilot;
		lineDetector = new LineDetector();
		pilot.setTravelSpeed(BASE_SPEED);
		continueDriving( false );
	}

	@Override
	protected void control() {
		if ( touchR.isPressed() ) {
			abortDetected = abortOnImpact;
			pilot.stop();
			pilot.travel(BACKOFF_DISTANCE);
			pilot.rotate(IMPACT_CORRECTION_ANGLE);
			continueDriving( true );
		}
		
		if (touchL.isPressed() ) {
			abortDetected = abortOnImpact;
			pilot.stop();
			pilot.travel(BACKOFF_DISTANCE);
			pilot.rotate(-1 * IMPACT_CORRECTION_ANGLE);
			continueDriving( false );
		}
	}
	
	private void continueDriving(boolean impactRight) {
		if ( driveStraight ) {
			pilot.forward();
		} else {
			pilot.arcForward(impactRight ? ARC_LEFT_RADIUS : ARC_RIGHT_RADIUS);
			state.rightSideState = impactRight;
		}
	}

	@Override
	protected boolean abort() {
		return abortDetected || lineDetector.hasDetected();
	}

	@Override
	protected void tearDown() {
		pilot.stop();
	}

}
