package me.fulcanelly.tgbridge.tapi;

import java.util.ArrayList;
import java.util.List;

import me.fulcanelly.tgbridge.tapi.events.CommandEvent;

public class CommandManager {

	static class Command {

		final String command;
		final String forGroups;
		final CommandAction action;

		//todo
		String[] parseArgs(CommandEvent msg) {
			return null;
		}

		Command(String command, CommandAction action) {
			this.command = command;
			this.action = action;
			forGroups = generatePattern();
		}
		
		String generatePattern() {
			return String.format("/%s@%s", command, username);
		}

		boolean matchForPrivate(String input) {
			return ("/" + command).equals(input);
		}

		boolean matchForGroup(String input) {
			return forGroups.equals(input);
		}
	}

	List<Command> commands = new ArrayList<>();

	static String username;
	public static TGBot bot;

	public static void setUsername(String u) {
		username = u;
	}

	public static void setBot(TGBot b) {
		bot = b;
	}

	public CommandManager() {
	}

	public CommandManager addCommand(String text, CommandAction action) {
		commands.add(new Command(text, action));
		return this;
	}	

	interface Matcher {
		boolean match(Command cmd);
	};
	
	public void tryMatch(CommandEvent msg) {
		
		boolean is_private = msg
			.getChat()
			.isPrivate();

		String text = msg.getText();

		Matcher matcher = is_private ? 
			command -> command.matchForPrivate(text): 
			command -> command.matchForGroup(text);

		for (Command command: commands) {
			if (matcher.match(command)) {
			//	msg.args = command.parseArgs(msg);
				command.action.run(msg);
			}
		}
    }
}