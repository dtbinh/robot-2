package parcours.level.test;

import parcours.level.base.SingleTaskLevel;
import parcours.task.DebugMeasureTask;


public class DebugMeasureLevel extends SingleTaskLevel {

	public DebugMeasureLevel() {
		super(new DebugMeasureTask());
	}

	@Override
	public String getLabel() {
		return "Measure";
	}

}
