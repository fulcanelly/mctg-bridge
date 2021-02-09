package me.fulcanelly.tgbridge.tools.mastery;

import me.fulcanelly.tgbridge.view.NamedTabExecutor;

/**
 * init: cmd args
 * init: cmds subcmd args
 * 
 * cmd: WORD
 * 
 * subcmd: cmd
 *       | cmd | subcmd
 * 
 * arg: --WORD=WORD
 *    | --WORD=NUMBER
 *  
 * args:
 *     | WORD 
 *     | arg
 *     | arg args
 *  
 * 
 * ExecutorCommandBuilder builder = new CommandBuilder("tg")
 *      .addSubCmd(
 *          new CommandBuilder("chat")
 *              .addSubCmd(
 *                  new CommandBuilder("show")
 *                      .setReactor(...)
 *              )
 *              .addSubCmd(
*                   new CommandBuilder("hide")
 *                      .setReactor(...)  
 *              )
 *      )
 *
* ExecutorCommandBuilder builder = new CommandBuilder("tg")
*      .addSubCmd(
*          new CommandBuilder("chat")
*              .addArg("show")
*              .addArg("hide")
*              .setReactor(...)
*      )
*/
interface Command {
    
}

interface SubCommand extends Command {

}

interface BaseCommand extends Command {

}


class CommandBuilder {
    void setName() {

    }

    void addSubCmd() {
       
    }

    void addArg() {


    }

    void setReactor() {

    }
}
public class ExecutorCommandBuilder {

    public NamedTabExecutor build() {
        return null;
    }
}
