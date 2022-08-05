package CMP01;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        CharStream codeCharStream = CharStreams.fromFileName("/home/cccc/project/frameworks/base/services/core/java/com/android/server/wm/ActivityStarter.java");
//        CharStream codeCharStream = CharStreams.fromFileName("/home/cccc/Desktop/CMP/untitled/Test.java");
        JavaLexer lexer = new JavaLexer(codeCharStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new JavaParserBaseListener(), tree);
    }
}
