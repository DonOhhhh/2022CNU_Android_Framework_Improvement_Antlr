package CMP01;

import java.util.HashMap;

public class Analyzer {

    public static int numOfMethods = 0;
    public static int numOfFields = 0;
    public static HashMap<String,Integer> params = new HashMap<>();

    public void printResult() {
        for(String key : params.keySet())
        {
            System.out.println(key + " : " + params.get(key));
        }
        System.out.println("Methods count : " + numOfMethods);
        System.out.println("Fields count : " + numOfFields);
    }

    public void countParameters(JavaParser.MethodDeclarationContext ctx) {
        params.put(ctx.getChild(1).getText(), ctx.getChild(2).getChildCount());
    }

    public void countMethods() {
        numOfMethods++;
    }

    public void countFields() {
        numOfFields++;
    }
}
