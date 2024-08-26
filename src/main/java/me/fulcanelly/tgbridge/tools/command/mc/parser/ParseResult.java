package me.fulcanelly.tgbridge.tools.command.mc.parser;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.With;


@Data @AllArgsConstructor @With @ToString
public class ParseResult  {
    
    final List<String> expected, redundant, comment;
    
    public boolean isEmpty() {
        return this.equals(empty);        
    }

    public boolean isNotEmpty() {
        return !this.isEmpty();
    }

    static public ParseResult getEmpty() {
        return empty;
    }
    
    static public ParseResult expected(List<String> expects, String... warning) {
        return expected(expects).withComment(List.of(warning));
    }

    static public ParseResult expected(List<String> expects) {
        return empty.withExpected(expects);
    }

    static public ParseResult empty = new ParseResult(List.of(), List.of(), List.of()) {
        public boolean isEmpty() {
            return true;        
        }
    };

}
