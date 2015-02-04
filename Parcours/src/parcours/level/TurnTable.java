package parcours.level;

import java.util.ArrayList;
import java.util.List;

import parcours.level.base.Level;
import parcours.task.Task;


public class TurnTable extends Level {

	@Override
	public String getLabel() {
		return "Turn Table";
	}

	@Override
	public List<Task> createTaskList() {
		final ArrayList<Task> taskList = new ArrayList<Task>();
		return taskList;
	}

}
