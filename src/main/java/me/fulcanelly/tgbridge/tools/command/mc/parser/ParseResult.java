package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data @AllArgsConstructor @NoArgsConstructor @With
public class ParseResult {
    
    List<String> expected, redundant;

    
    static public ParseResult empty = new ParseResult(List.of(), List.of());
}
