package fr.coudert.network.commands;

import fr.coudert.game.ServerMain;

public class HelpCmd extends Command{

	protected HelpCmd() {
		super("help", "Give the list of all commands");
	}

	public void process(String... params) {
		ServerMain.print("List of all commands:");
		for(Command command : COMMANDS)
			ServerMain.print("... " + command.name + " : " + command.description);
	}

}