package parcours.task;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.DifferentialPilot;

public class FollowRightWallTask extends Task {

	private static final int WHITE_LINE_THRESHOLD = 50;
	private UltrasonicSensor ultra;
	private DifferentialPilot pilot;
	private LightSensor light;
	private boolean isAboarted = false;

	@Override
	protected void init() {
		ultra = new UltrasonicSensor(SensorPort.S3);
		pilot = new DifferentialPilot(8.16, 13, Motor.B, Motor.A);
		light = new LightSensor(SensorPort.S1);
	}

	@Override
	protected void control() {
		int a = ultra.getDistance();
		pilot.setTravelSpeed(20);
		pilot.forward();

		if (a < 10) {
			pilot.rotate(10, true);
			awaitRotation();
			pilot.travel(10, true);
			awaitRotation();
			pilot.rotate(-10, true);
			awaitRotation();
		} else if (a > 20 && a < 50) {
			pilot.rotate(-10, true);
			awaitRotation();
			pilot.travel(15, true);
			awaitRotation();
			pilot.rotate(10, true);
			awaitRotation();
		} else if (a > 55) {
			pilot.travel(5, true);
			awaitRotation();
			pilot.rotate(-90, true);
			awaitRotation();
			pilot.travel(25, true);
			awaitRotation();
		}
	}

	private void awaitRotation() {
		while (pilot.isMoving()) {
			if(abort()) {
				isAboarted  = true;
			}
			Thread.yield();
		}
	}

	@Override
	protected boolean abort() {
		return isAboarted || light.getLightValue() >= WHITE_LINE_THRESHOLD;
	}

	@Override
	protected void tearDown() {
		Motor.B.suspendRegulation();
		Motor.A.suspendRegulation();
	}

}