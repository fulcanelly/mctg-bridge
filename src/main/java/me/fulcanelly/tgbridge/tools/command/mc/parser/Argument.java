package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.Optional;
import java.util.function.Function;

import com.google.common.base.Supplier;

import lombok.ToString;

/**
 * init: cmd args
 * init: cmds subcmd args
 * 
 * cmd: WORD
 * 
 * subcmd: cmd
 *       | cmd | subcmd
 * 
 * arg: WORD:WORD
 *    | WORD:NUMBER
 *  
 * args:
 *     | WORD 
 *     | arg
 *     | arg args
 *  
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

@ToString
public class Argument {

    String name; 
    boolean required = true;
    Supplier<Object> defaultSupplier;

    Optional<String> permission = Optional.empty();
    Function<String, Object> parser = a -> a;

    boolean isOptional() {
        return !required;
    }
}