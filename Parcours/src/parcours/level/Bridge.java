package parcours.level;

import parcours.level.base.Level;
import parcours.task.FindBridgeEdgeTask;
import parcours.task.FollowBridgeTask;
import parcours.task.ControllerTask;
import parcours.task.TravelHandleCollisionTask;


public class Bridge extends Level {
	
	private static final int TASK_COUNT = 3;
	private static final ControllerTask[] tasks = new ControllerTask[TASK_COUNT];
	private static final int INITIAL_TRAVEL_DISTANCE = 30; // used to clear the 3 lines at the beginning
	
	static {
		int i = 0;
		tasks[i++] = new TravelHandleCollisionTask(INITIAL_TRAVEL_DISTANCE, 40);
		tasks[i++] = new FindBridgeEdgeTask();
		tasks[i++] = new FollowBridgeTask();
	}

	@Override
	public String getLabel() {
		return "Bridge";
	}

	@Override
	public ControllerTask[] getTasks() {
		return tasks;
	}

}
