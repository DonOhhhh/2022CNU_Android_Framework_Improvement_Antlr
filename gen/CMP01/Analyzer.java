package CMP01;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Analyzer {

    private final HashMap<String, List<StatementCnt>> cntResult;

    private StatementCnt info;

    private List<Integer> childNums;

    private List<String> stmts;

    private final String filename;
    private final WriteToXls wtx = Main.getWtx();

    public Analyzer(String filename) {
        this.filename = filename;
        cntResult = new HashMap<>();
        for (String arg : List.of("nestedIf", "nestedLoop", "elseIf", "cases")) {
            cntResult.put(arg, new ArrayList<StatementCnt>());
        }
    }

    public void putInfoToList(String arg) {
        cntResult.get(arg).add(info);
    }

    public int getInfoCnt() {
        return info.getCnt();
    }

    public void initClass(String stmt, String text) {
        info = new StatementCnt(stmt, text, 0);
        if (stmt.equals("if")) {
            childNums = List.of(2);
            stmts = List.of("if");
        } else {
            childNums = Arrays.asList(1, 2, 4);
            stmts = Arrays.asList("for", "while", "do");
        }
    }

    public void writeResult() {
        int STARTCOL = wtx.getSTARTCOL();
        int curRow = wtx.getLastRow();
        int resultSize = 0;
        List<String> args = new ArrayList<>(cntResult.keySet());
        HashMap<String, Integer> findCellCol = new HashMap<>();
        for (int i = 0; i < args.size(); i++) {
            findCellCol.put(args.get(i), i + STARTCOL);
        }
        // write data
        for (String arg : args) {
            List<StatementCnt> cslist = cntResult.get(arg);
            for (StatementCnt statementCnt : cslist) {
                wtx.writeCell(statementCnt.getText(), curRow + resultSize, STARTCOL - 1);
                wtx.writeCell(String.valueOf(statementCnt.getCnt()), curRow + resultSize, findCellCol.get(statementCnt.getStmt()));
                resultSize++;
            }
        }
        // write filename
        if (resultSize != 0) {
            wtx.writeMergedCell(this.filename, curRow, curRow + resultSize, 1, 1);
            wtx.setLastRow(curRow + resultSize);
        }
    }

    public void printResult() {
        System.out.println(this.filename);
        for (String arg : new ArrayList<>(cntResult.keySet())) {
            for (StatementCnt sCnt : cntResult.get(arg)) {
                System.out.println(sCnt.getText());
                System.out.println(sCnt.getStmt() + " : " + sCnt.getCnt());
            }
        }
    }

    public static boolean isBlockStatementContext(ParserRuleContext ctx, int childNum) {
        try {
            return (ctx.getChild(childNum).getChild(0) instanceof JavaParser.BlockContext);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public void countNestedInTry(ParserRuleContext ctx, int depth) {
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            ParserRuleContext tmp = (ParserRuleContext) ctx.getChild(i).getChild(0);
            if (tmp instanceof JavaParser.StatementContext && stmts.contains(tmp.getChild(0).getText())) {
                checkNested(tmp, depth + 1);
            } else if (tmp.getChild(0).getText().equals("try")) {
                countNestedInTry((ParserRuleContext) tmp.getChild(1), depth);
            }
        }
    }

    public void checkNested(ParserRuleContext ctx, int depth) {
        for (Integer childNum : childNums) {
            if (!isBlockStatementContext(ctx, childNum)) continue;
            int blockSize = ctx.getChild(childNum).getChild(0).getChildCount();
            for (int i = 1; i < blockSize - 1; i++) {
                ParserRuleContext tmp = (ParserRuleContext) ctx.getChild(childNum).getChild(0).getChild(i).getChild(0); // getBlockStatementContext Node
                if (tmp instanceof JavaParser.StatementContext && stmts.contains(tmp.getChild(0).getText())) {
                    checkNested(tmp, depth + 1);
                } else if (tmp.getChild(0).getText().equals("try")) {
                    countNestedInTry((ParserRuleContext) tmp.getChild(1), depth);
                }
            }
            if (depth > info.getCnt()) info.setCnt(depth);
        }
    }


    public void countElseIf(ParserRuleContext ctx) {
        int cnt = 1;
        ParseTree tmp = ctx;
        while (true) {
            if (tmp.getChildCount() < 4) break;
            if (!(tmp.getChild(4).getChild(0) instanceof TerminalNode)) break;
            tmp = tmp.getChild(4);
            cnt++;
        }
        info.setCnt(cnt);
    }

    public void countCases(ParserRuleContext ctx) {
        int cnt = 0;
        for (int i = 3; i < ctx.getChildCount() - 1; i++) {
            ParserRuleContext tmp = (ParserRuleContext) ctx.getChild(i);
            for (int j = 0; j < tmp.getChildCount(); j++) {
                if (tmp.getChild(j) instanceof JavaParser.SwitchLabelContext) {
                    cnt++;
                }
            }
        }
        info.setCnt(cnt);
    }
}
