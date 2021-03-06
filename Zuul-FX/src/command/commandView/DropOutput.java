package command.commandView;

import command.ICommandOutput;
import command.commandController.DropController;
import view.IOHandler;

public class DropOutput extends DropController implements ICommandOutput {
	public void init(String[] inputArray) {
		String error = super.validateUserInput(inputArray);
		if (error != null) {
			IOHandler.output.printError(error);
			return;
		}
		if (super.execute(inputArray)) {
			IOHandler.output.println("You dropped " + toDrop);
		}
	}

}
