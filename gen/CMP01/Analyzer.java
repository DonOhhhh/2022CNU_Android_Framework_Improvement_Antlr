package CMP01;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.util.HashMap;

public class Analyzer {

    public static int numOfMethods = 0;
    public static int numOfFields = 0;
    public static HashMap<String, Integer> cntParams = new HashMap<>();
    public static HashMap<String, Integer> cntNestedStmt = new HashMap<>();

    public void printResult() {
//        for (String key : cntParams.keySet()) {
//            System.out.println(key + " : " + cntParams.get(key));
//        }
//        System.out.println("Methods count : " + numOfMethods);
//        System.out.println("Fields count : " + numOfFields);
        for (String key : cntNestedStmt.keySet()) {
            System.out.println(key);
            System.out.println(cntNestedStmt.get(key));
        }
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

    public void checkNested(JavaParser.StatementContext ctx, String stmt) {
        int cnt = 0;
        int childNum = switch (stmt) {
            case "if", "while" -> 2;
            case "for" -> 4;
            case "do" -> 1;
            default -> 0;
        };
        ParserRuleContext tmp = ctx;
        String curStmt = tmp.getChild(0).getText();
        while (curStmt.equals(stmt)) {
//            for(String s : cntNestedStmt.keySet())
//            {
//                if(s.contains(ctx.getText()))
//                {
//                    return;
//                }
//            }
            tmp = (ParserRuleContext) tmp.getChild(childNum).getChild(0);
            if (tmp.getChild(1).getText().equals(".")) {
                break;
            } else {
                tmp = (ParserRuleContext) tmp.getChild(1).getChild(0);
                curStmt = tmp.getChild(0).getText();
            }
//            System.out.println(curStmt + " / " + stmt);
            cnt++;
        }
        if (cnt <= 1) return;
        cntNestedStmt.put(ctx.getText(), cnt);
    }
}
