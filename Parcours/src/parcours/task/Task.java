package parcours.task;

public abstract class Task {

	public void run() {
		init();

		do {
			control();
		} while (!abort());

	}

	protected abstract void init();

	protected abstract void control();

	protected abstract boolean abort();

}
