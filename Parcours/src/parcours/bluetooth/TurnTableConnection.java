package parcours.bluetooth;

import java.io.IOException;

public class TurnTableConnection extends BluetoothConnection{
	
	private static final String BRICK_NAME = "TurnTable";

	public TurnTableConnection() {
		super(BRICK_NAME);
	}

	public enum TurnTableCommand {
		HELLO, TURN, DONE, CYA, UNKNOWN;

		public static TurnTableCommand getByOrdinal(int commandOrdinal) {
			if (commandOrdinal >= values().length) {
				return UNKNOWN;
			}
			return values()[commandOrdinal];
		}
	}

	public void receiveHello() throws IOException {
		TurnTableCommand command = receiveCommand();
		assertCommand(command, TurnTableCommand.HELLO);
	}

	public void sendTurnCommand() throws IOException {
		sendCommand(TurnTableCommand.TURN);
	}

	public void receiveOkay() throws IOException {
		TurnTableCommand command = receiveCommand();
		assertCommand(command, TurnTableCommand.DONE);
	}

	public void sendCyaCommand() throws IOException {
		sendCommand(TurnTableCommand.CYA);
	}

	private void assertCommand(TurnTableCommand command,
			TurnTableCommand assertetedCommand) throws IOException {
		if (command != assertetedCommand) {
			System.out.println("Invalid command:");
			System.out.println("Expected:" + assertetedCommand);
			throw new IOException("Invalid Command");
		}
	}

	private TurnTableCommand receiveCommand() throws IOException {
		int commandOrdinal = receiveIntegerCommand();
		TurnTableCommand command = TurnTableCommand
				.getByOrdinal(commandOrdinal);
		System.out.println("Receive:" + command.name());
		return command;
	}

	private void sendCommand(TurnTableCommand command) throws IOException {
		sendIntegerCommand(command.ordinal());
		System.out.println("Send: " + command.name());
	}

}
