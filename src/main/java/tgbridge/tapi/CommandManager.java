package tgbridge.tapi;

import java.util.ArrayList;
import java.util.List;


public class CommandManager {

	static class Command {

		final String command;
		final String forGroups;
		final Action action;

		Command(String command, Action action) {
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

	public CommandManager addCommand(String text, Action action) {
		commands.add(new Command(text, action));
		return this;
	}	

	interface Matcher {
		boolean match(Command cmd);
	};
	
	public void tryMatch(Message msg) {
		
		boolean is_private = msg
			.getChat()
			.isPrivate();

		String text = msg.getText();

		Matcher matcher = is_private ? 
			command -> command.matchForPrivate(text): 
			command -> command.matchForGroup(text);

		for(Command command: commands) {
			if(matcher.match(command)) {
				command.action.run(msg);
			}
		}
    }
}