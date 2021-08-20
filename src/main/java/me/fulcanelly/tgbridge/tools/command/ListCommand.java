package me.fulcanelly.tgbridge.tools.command;

import java.util.List;

import com.google.inject.Inject;

import me.fulcanelly.tgbridge.tapi.events.CommandEvent;
import me.fulcanelly.tgbridge.tools.command.base.FullCommandBuilder;
import me.fulcanelly.tgbridge.utils.analyst.CommonMetrix;

public class ListCommand extends FullCommandBuilder {

    final CommonMetrix metrix;
    String emptyServerMessage;

    @Inject
    public ListCommand(CommonMetrix metrix, String emptyServerMessage) {
        super("list");
        this.metrix = metrix;
        this.emptyServerMessage = emptyServerMessage;
        this.setAction(this::getListCmdHandler);
    }


    public ListCommand(CommonMetrix metrix) {
        this(metrix, "Server unfortunately is empty :c");
    }
 
    void getListCmdHandler(CommandEvent msg) {
        List<String> nick_names = metrix.getOnlineList();
        String result = nick_names.size() > 0 ? 
            "Currently there are " + nick_names.size() + " players online: \n\n" + String.join("\n", nick_names):
            emptyServerMessage;

        msg.reply(result);        
    }


}
