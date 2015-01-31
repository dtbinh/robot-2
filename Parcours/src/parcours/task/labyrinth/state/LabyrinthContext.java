package parcours.task.labyrinth.state;

import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.RotateMoveController;

public class LabyrinthContext implements RotateMoveController {

	private LabyrinthStateBase state;
	private DifferentialPilot pilot;

	public LabyrinthContext(DifferentialPilot pilot) {
		state = LabyrinthState.NORMAL.getState();
		this.pilot = pilot;
	}

	public void handleBothButtonsPressed(int distance) {
		state.handleBothButtonsPressed(this, distance);
	}

	public void handleLeftButtonPressed(int distance) {
		state.handleLeftButtonPressed(this, distance);
	}

	public void handleRightButtonPressed(int distance) {
		state.handleRightButtonPressed(this, distance);
	}

	public void handleNoButtonIsPressed(int distance) {
		state.handleNoButtonIsPressed(this, distance);
	}

	public void setState(LabyrinthState stateEnumEntry) {
		this.state = stateEnumEntry.getState();
	}

	public DifferentialPilot getPilot() {
		return pilot;
	}

	@Override
	public void setTravelSpeed(double travelSpeed) {
		pilot.setTravelSpeed(travelSpeed);
	}

	@Override
	public double getTravelSpeed() {
		return pilot.getTravelSpeed();
	}

	@Override
	public double getMaxTravelSpeed() {
		return pilot.getMaxTravelSpeed();
	}

	@Override
	public void setRotateSpeed(double rotateSpeed) {
		pilot.setRotateSpeed(rotateSpeed);
	}

	@Override
	public double getRotateSpeed() {
		return pilot.getRotateSpeed();
	}

	@Override
	public double getRotateMaxSpeed() {
		return pilot.getRotateMaxSpeed();
	}

	@Override
	public void forward() {
		pilot.forward();
	}

	@Override
	public void backward() {
		pilot.backward();
	}

	/**
	 * Note: This method will immediateReturn 
	 */
	@Override
	public void rotate(double angle) {
		pilot.rotate(angle, true);
	}

	@Override
	public void rotate(double angle, boolean immediateReturn) {
		pilot.rotate(angle, immediateReturn);
	}

	@Override
	public void stop() {
		pilot.stop();
	}

	/**
	 * Note: This method will immediateReturn 
	 */
	@Override
	public void travel(double distance) {
		pilot.travel(distance, true);
	}

	@Override
	public void travel(double distance, boolean immediateReturn) {
		pilot.travel(distance, immediateReturn);
	}

	@Override
	public boolean isMoving() {
		return pilot.isMoving();
	}

	@Override
	public Move getMovement() {
		return pilot.getMovement();
	}

	@Override
	public void addMoveListener(MoveListener listener) {
		pilot.addMoveListener(listener);
	}

}
