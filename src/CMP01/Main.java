package CMP01;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    private static final HashMap<String, Integer> cntArgs = new HashMap<>();
    private static String path = null;
    private static String fileName;

    private static WriteToXls wtx;

    public static WriteToXls getWtx() {
        return wtx;
    }

    public static HashMap<String, Integer> getCntArgs() {
        return cntArgs;
    }

    public static String getFileName() {
        return fileName;
    }

    public static String getPath() {
        return path;
    }

    public static void showFilesInDIr(String dirPath) throws IOException {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) throw new AssertionError();
        for (File file : files) {
            if (file.isDirectory()) {
                showFilesInDIr(file.getPath());
            } else {
//                System.out.println("file: " + file);
                fileName = file.getName();
                startAnalyze(file.getAbsolutePath());
            }
        }
        int curRowOrZeroColumn = wtx.getCurRowOfFnCell();
        int lastRowOfSheet = wtx.getLastRow();
        int newLastRowOfSheet = curRowOrZeroColumn + (lastRowOfSheet - curRowOrZeroColumn) + 1;
        try {
            for (int i = curRowOrZeroColumn; i < newLastRowOfSheet; i++) {
                wtx.writeCell(dirPath.substring(dirPath.lastIndexOf("server")), i, 0);
            }
//            wtx.writeMergedCell(dirPath, curRowOrZeroColumn, newLastRowOfSheet, 0, 0);
            wtx.setCurRowOfFnCell(newLastRowOfSheet);
        } catch (NullPointerException ignored) {
        }
    }

    public static void startAnalyze(String file) throws IOException {
        CharStream codeCharStream = CharStreams.fromFileName(file);
        JavaLexer lexer = new JavaLexer(codeCharStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new JavaParserBaseListener(), tree);
    }

    public static void main(String[] args) throws IOException {
        List<String> checkList = List.of("--nestedIf", "--elseIf", "--nestedLoop", "--cases");
        if (args.length == 0) {
            System.out.println("Please use the options.");
            return;
        }
        int i = 0;
        while (i < args.length) {
            if (checkList.contains(args[i])) {
                cntArgs.put(args[i].substring(2), Integer.valueOf(args[i + 1]));
            } else if (args[i].equals("--path")) {
                path = args[i + 1];
            } else {
                System.out.println("--nestedIf [cnt]: Detect nested if equal and greater than cnt times");
                System.out.println("--nestedLoop [cnt]: Detect nested Loop equal and greater than cnt times");
                System.out.println("--elseIf [cnt]: Detect elseif statement equal and greater than cnt times");
                System.out.println("--cases [cnt]: Detect cases statement equal and greater than cnt times");
                System.out.println("--path [path]: Path containing java sources to detect code smells");
                return;
            }
            i += 2;
        }
        if (path == null) {
            System.out.println("Please enter the path. not a file or empty");
        }
        wtx = new WriteToXls();
        showFilesInDIr(path);
//        startAnalyze("/home/cccc/project/frameworks/base/services/core/java/com/android/server/VibratorService.java");
        wtx.writeToFile(path);
    }
}
