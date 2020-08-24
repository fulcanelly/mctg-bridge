package me.fulcanelly.tgbridge.tapi;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.fulcanelly.tgbridge.tapi.events.CommandEvent;

public class CommandManager {

	class Command {

		final CommandAction action;
		final Pattern privatePattern;
		final Pattern publicPattern;

		Command(String command, CommandAction action) {
			commands.add(this);
			this.action = action;

			String private_form = "/" + command;
			String public_form = generateGroupPattern(private_form);
			String template = "(\\s(?<arguments>.*)){0,}";

			privatePattern = Pattern.compile(private_form + template);
			publicPattern = Pattern.compile(public_form + template);
		}
		
		String generateGroupPattern(String command) {
			return String.format("%s@%s", command, username);
		}

		boolean tryRunEventWith(CommandEvent event, Pattern pattern) {
			Matcher matcher = pattern.matcher(
				event.getText() );

			if (matcher.find()) {
				String arguments = matcher.group("arguments");
				if (arguments != null) { 
					event.args = arguments.split("\\s");
				}
				action.run(event);
				return true;
			}
			return false;
		}

		boolean matchForPrivate(CommandEvent event) {
			return tryRunEventWith(event, privatePattern);
		}

		boolean matchForGroup(CommandEvent event) {
			return tryRunEventWith(event, publicPattern);
		}
	}

	public List<Command> commands = new ArrayList<>();

	String username;

	public CommandManager(String uname) {
		username = uname;
	}

	public CommandManager addCommand(String command, CommandAction action) {
		new Command(command, action);
		return this;
	}	

	public CommandManager addCommand(String command, String answer) {
		new Command(command, msg -> msg.reply(answer));
		return this;
	}	

	public CommandManager addCommand(String command, StringReturner sreturner) {
		CommandAction action =  msg -> msg.reply(
			sreturner.get() );
		new Command(command, action);
		return this;
	}
	
	public interface StringReturner {
		String get();
	}

	interface CommandMatcher {
		boolean match(Command cmd);
	};
	
	public void tryMatch(CommandEvent event) {
		
		boolean is_private = event
			.getChat()
			.isPrivate();

		CommandMatcher matcher = is_private ? 
			command -> command.matchForPrivate(event): 
			command -> command.matchForGroup(event);

		for (Command command: commands) {
			if (matcher.match(command)) {
				return;
			}
		}
    }
}