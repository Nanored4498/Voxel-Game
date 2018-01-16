package fr.coudert.network.commands;

import fr.coudert.network.Server;

public class StopCmd extends Command {

	protected StopCmd() {
		super("stop", "Stop the server");
	}

	protected void process(String... params) {
		Server.stop();
	}

}