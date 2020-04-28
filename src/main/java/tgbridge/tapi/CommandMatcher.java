package tgbridge.tapi;

import java.util.regex.*;

interface Reactor {
	boolean run(Message msg);
}

public class CommandMatcher {
    Reactor reactor;
    String command;

	static String username;
	public static TGBot bot;
	public static void setUsername(String u) {
		username = u;
	}

	public static void setBot(TGBot b) {
	   bot = b;
   }

	public CommandMatcher() {
	}

    public CommandMatcher(String c, Reactor r) {
    	reactor = r;
    	command = c;
    }

    boolean tryMatch(Message msg) {
		
		String regexPattern = String.format("/%s@%s", msg.getText(), username); 

    	if(msg.getText() == command || Pattern.matches(regexPattern, command)) {
			reactor.run(msg);
			return true;
    	}
    	return false;
    }
}