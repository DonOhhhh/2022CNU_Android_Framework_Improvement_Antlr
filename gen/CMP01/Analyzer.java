package CMP01;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Analyzer {

    private int numOfMethods = 0;
    private int numOfFields = 0;
    private int nestedCnt = 0;
    private static final HashMap<String, Integer> cntParams = new HashMap<>();
    private ArrayList<NestedStatement> infos = new ArrayList<>();
    private NestedStatement info;

    public void putInfoToList() {
        infos.add(info);
    }

    public int getNestedCnt() {
        return info.getCnt();
    }

    public void initClass(String stmt, String text, int cnt) {
        info = new NestedStatement(stmt,text,cnt);
    }

    public void printResult() {
//        for (String key : cntParams.keySet()) {
//            System.out.println(key + " : " + cntParams.get(key));
//        }
//        System.out.println("Methods count : " + numOfMethods);
//        System.out.println("Fields count : " + numOfFields);
//        for (NestedStatement tmp : infos) {
//            System.out.println(tmp.getText());
//            System.out.println(tmp.getStmt() + " : " + tmp.getCnt());
//        }
    }

    public void countParameters(JavaParser.MethodDeclarationContext ctx) {
        cntParams.put(ctx.getChild(1).getText(), ctx.getChild(2).getChildCount());
    }

    public void countMethods() {
        numOfMethods++;
    }

    public void countFields() {
        numOfFields++;
    }

    public static boolean isBlockStatementContext(ParserRuleContext ctx, int childNum) {
        return (ctx.getChild(childNum).getChild(0) instanceof JavaParser.BlockContext);
    }

    public void checkNested(ParserRuleContext ctx, String stmt, int depth) {
        int childNum = switch (stmt) {
            case "if", "while" -> 2;
            case "for" -> 4;
            case "do" -> 1;
            default -> 0;
        };
        if (!isBlockStatementContext(ctx, childNum)) return;
        int blockSize = ctx.getChild(childNum).getChild(0).getChildCount();
        for (int i = 1; i < blockSize - 1; i++) {
            ParserRuleContext tmp = (ParserRuleContext) ctx.getChild(childNum).getChild(0).getChild(i).getChild(0); // getBlockStatementContext Node
            if (tmp instanceof JavaParser.StatementContext && tmp.getChild(0).getText().equals(stmt)) {
                checkNested(tmp, stmt, depth + 1);
            }
        }
        if(depth > info.getCnt()) info.setCnt(depth);
    }

    public void countElseIf(ParserRuleContext ctx) {
        int cnt = 1;
        ParseTree tmp = ctx;
        while(true)
        {
            if(tmp.getChildCount() < 4) break;
            if(!(tmp.getChild(4).getChild(0) instanceof TerminalNode)) break;
            tmp = tmp.getChild(4);
            cnt++;
        }
        if(cnt > 2)
        {
            System.out.println(ctx.getText());
            System.out.println(cnt);
        }
    }

    public void countCases(ParserRuleContext ctx) {
        int cnt = 0;
        System.out.println(ctx.getText());
        System.out.println(ctx.getChildCount()-4);
    }
}
