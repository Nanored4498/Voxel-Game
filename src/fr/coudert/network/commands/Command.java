package fr.coudert.network.commands;

public abstract class Command {

	protected static final Command[] COMMANDS = new Command[]
			{new HelpCmd(), new StopCmd(), new KickCmd()};

	protected String name, description;

	protected Command(String name, String description) {
		this.name = name;
		this.description = description;
	}

	protected abstract void process(String... params);

	public static boolean execute(String... params) {
		for(Command command : COMMANDS)
			if(command.name.equals(params[0])) {
				command.process(params);
				return true;
			}
		return false;
	}

}