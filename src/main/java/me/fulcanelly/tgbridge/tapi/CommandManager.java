package me.fulcanelly.tgbridge.tapi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.fulcanelly.tgbridge.tapi.events.CommandEvent;

public class CommandManager {

	class Command {

		final Consumer<CommandEvent> action;
		final Pattern privatePattern;
		final Pattern publicPattern;

		Command(String command, Consumer<CommandEvent> action) {
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
				event.getMessage().getText() );

			if (matcher.find()) {
				String arguments = matcher.group("arguments");
				if (arguments != null) { 
					event.args = arguments.split("\\s");
				}
				action.accept(event);
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

		CommandManager getCommandManager() {
			return CommandManager.this;
		}
	}

	public List<Command> commands = new ArrayList<>();

	String username;

	public CommandManager(String uname) {
		username = uname;
	}

	public CommandManager addCommand(String command, Consumer<CommandEvent> action) {
		return new Command(command, action).getCommandManager();
	}	

	public CommandManager addCommand(String command, String answer) {
		return new Command(command, msg -> msg.getMessage().reply(answer)).getCommandManager();
	}	

	public CommandManager addCommand(String command, Supplier<String> strProducer) {
		return new Command(command, makeConsumerFromSupplier(strProducer)).getCommandManager();
	}

	Consumer<CommandEvent> makeConsumerFromSupplier(Supplier<String> strProducer) {
		return event -> event.getMessage().reply(strProducer.get());
	}

	Consumer<CommandEvent> makeConsumerFromFunction(Function<CommandEvent, String> function) {
		return event -> event.getMessage().reply(function.apply(event));
	}

	public CommandManager addCommand(String command, Function<CommandEvent, String> function) {
		return new Command(command, makeConsumerFromFunction(function)).getCommandManager();
	}
	
	public interface StringReturner {
		String get();
	}

	interface CommandMatcher {
		boolean match(Command cmd);
	};
	
	public void tryMatch(CommandEvent event) {
		
		boolean is_private = event
		.getMessage()
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