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

    private static int fileCnt;

    private static String baseDirName;

    private static WriteToXls wtx;

    private static String outputPath;

    public static String getOutputPath() {
        return outputPath;
    }

    public static boolean isIsForAnalysis() {
        return isForAnalysis;
    }

    public static void setIsForAnalysis(boolean isForAnalysis) {
        Main.isForAnalysis = isForAnalysis;
    }

    private static boolean isForAnalysis = false;

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
                String curPath = file.getPath();
                System.out.println("Examining in " + curPath.substring(curPath.lastIndexOf(baseDirName)));
                showFilesInDIr(file.getPath());
            }
        }
        for (File file : files) {
            if (file.isFile()) {
//                System.out.println("file: " + file);
                fileName = file.getName();
                fileCnt++;
                startAnalyze(file.getAbsolutePath());
            }
        }
        int curRowOrZeroColumn = wtx.getCurRowOfFnCell();
        int lastRowOfSheet = wtx.getLastRow();
        int newLastRowOfSheet = curRowOrZeroColumn + (lastRowOfSheet - curRowOrZeroColumn) + 1;
        try {

            if (isIsForAnalysis()) {
                // write directory path at every cell
                for (int i = curRowOrZeroColumn; i < newLastRowOfSheet; i++) {
                    wtx.writeCell(dirPath.substring(dirPath.lastIndexOf(baseDirName)), i, 0);
                }
            } else {
                // merge and write directory path
                wtx.writeMergedCell(dirPath.substring(dirPath.lastIndexOf(baseDirName)), curRowOrZeroColumn, newLastRowOfSheet, 0, 0);
            }
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

    public static void showUsage() {
        System.out.println("-h: Show how to use options");
        System.out.println("--nestedIf [cnt]: Detect nested if equal and greater than cnt times");
        System.out.println("--nestedLoop [cnt]: Detect nested Loop equal and greater than cnt times");
        System.out.println("--elseIf [cnt]: Detect elseif statement equal and greater than cnt times");
        System.out.println("--cases [cnt]: Detect cases statement equal and greater than cnt times");
        System.out.println("--path [AbsPath]: Absolute path containing java sources to detect code smells");
        System.out.println("--forAnalysis: Return excel file for data analysis. if this parameter isn't set than return neat excel file");
        System.out.println("--output [AbsPath]: Absolute path where the result excel will be placed. If this arguments isn't used, result will be placed in working directory.");
    }

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        List<String> checkList = List.of("--nestedIf", "--elseIf", "--nestedLoop", "--cases");
        if (args.length == 0) {
            System.out.println("Please use the options.");
            return;
        } else if (args[0].equals("-h")) {
            showUsage();
            return;
        }

        int i = 0;
        while (i < args.length) {
            if (checkList.contains(args[i])) {
                cntArgs.put(args[i].substring(2), Integer.valueOf(args[i + 1]));
            } else if (args[i].equals("--path")) {
                path = args[i + 1];
                baseDirName = path.substring(path.lastIndexOf('/') + 1);
            } else if (args[i].equals("--forAnalysis")) {
                isForAnalysis = true;
            } else if (args[i].equals("--output")) {
                outputPath = args[i + 1];
            } else {
                showUsage();
                return;
            }
            i += 2;
        }
        if (path == null) {
            System.out.println("Please enter the path. not a file or empty");
        }
        if (outputPath == null) {
            outputPath = System.getProperty("user.dir");
        }
        System.out.println("Output path : " + outputPath);
        wtx = new WriteToXls();
        showFilesInDIr(path);
//        startAnalyze("/home/cccc/project/frameworks/base/services/core/java/com/android/server/VibratorService.java");
        long end = System.currentTimeMillis();
        System.out.println("Examined " + fileCnt + " files");
        System.out.println("Elapsed time : " + (((double) end - start) / 1000) + "(s)");
        wtx.writeToFile(path);
    }
}
