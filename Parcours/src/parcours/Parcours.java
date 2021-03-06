package parcours;

import lejos.nxt.Motor;
import lejos.util.Delay;
import parcours.level.base.Level;
import parcours.level.base.LevelCombination;
import parcours.level.combination.BluetoothLevelCombination;
import parcours.level.combination.FullParcours;
import parcours.level.combination.QualificationVariantA;
import parcours.level.combination.QualificationVariantB;
import parcours.level.combination.TestLevelCombination;
import parcours.menu.LevelMenu;
import parcours.menu.MainMenu;

public class Parcours {

	private static final int COMBINATION_COUNT = 5;
	private static final LevelCombination[] ITEMS = new LevelCombination[COMBINATION_COUNT];
	private MainMenu mainMenu;

	static {
		int i = 0;
		ITEMS[i++] = new FullParcours();
		ITEMS[i++] = new QualificationVariantA();
		ITEMS[i++] = new QualificationVariantB();
		ITEMS[i++] = new TestLevelCombination();
		ITEMS[i++] = new BluetoothLevelCombination();
	}

	public static void main(String[] args) {
		try {
			//init();
			new Parcours();
		} catch (Exception e) {
			System.out.println("Exception: ");
			System.out.println(e.getMessage());
			Delay.msDelay(20 * 1000);
			throw e;
		}
	}

	private static void init() {
		Motor.C.setSpeed(100);
		Motor.C.backward();
		Delay.msDelay(800);
		Motor.C.stop();
		Motor.C.suspendRegulation();
		Motor.C.stop();
		Motor.C.resetTachoCount();
		Motor.C.setSpeed(900);
	}

	public Parcours() {
		mainMenu = new MainMenu(ITEMS);
		while (true) {
			LevelCombination selectedLevelCombination = mainMenu
					.selectLevelCombination();
			if (selectedLevelCombination == null) {
				break;
			}
			LevelMenu menu = new LevelMenu(selectedLevelCombination);
			while (true) {
				Level selectedLevel = menu.selectLevel();
				if (selectedLevel == null) {
					break;
				}
				selectedLevel.run();
				if (selectedLevelCombination.isTestLevelCombination()) {
					continue; // Return to menu instead of running next level
				}
				while (true) {
					Level nextLevel = menu.getNextLevel();
					if (nextLevel == null) {
						break;
					}
					nextLevel.run();
				}
			}
		}
	}

}
