package CMP01;

public class NestedStatement {
    private String stmt;
    private int cnt;
    private String text;

    NestedStatement(String stmt, String text, int cnt) {
        this.stmt = stmt;
        this.text = text;
        this.cnt = cnt;
    }

    public String getStmt() {
        return stmt;
    }

    public int getCnt() {
        return cnt;
    }

    public String getText() {
        return text;
    }

    public void setStmt(String stmt) {
        this.stmt = stmt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public void setText(String text) {
        this.text = text;
    }
}
