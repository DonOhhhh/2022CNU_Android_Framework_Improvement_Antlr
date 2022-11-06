package CMP01;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WriteToXls {

    private final Sheet sheet;
    private final Workbook workBook;
    private final CellStyle defaultStyle;
    private final List<String> args = new ArrayList<>(Main.getCntArgs().keySet());
    private final Integer STARTCOL = 3;
    private Integer curRowOfFnCell = 1;

    public Integer getCurRowOfFnCell() {
        return curRowOfFnCell;
    }

    public void setCurRowOfFnCell(Integer curRowOfFnCell) {
        this.curRowOfFnCell = curRowOfFnCell;
    }

    private final HashMap<String, Integer> cellColNum;

    public HashMap<String, Integer> getCellColNum() {
        return cellColNum;
    }

    public int getSTARTCOL() {
        return this.STARTCOL;
    }

    public Sheet getSheet() {
        return this.sheet;
    }

    HashMap<String, Integer> colName = new HashMap<>();

    WriteToXls() {
        workBook = new HSSFWorkbook();
        defaultStyle = workBook.createCellStyle();
        defaultStyle.setAlignment(HorizontalAlignment.CENTER);
        defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        sheet = workBook.createSheet();
        // 시트 생성 및 셀 높이 설정
        sheet.setDefaultRowHeightInPoints(30);
        // codesmell List
        writeHeader();
        List<String> args = new ArrayList<>(Main.getCntArgs().keySet());
        cellColNum = new HashMap<>();
        for (int i = 0; i < args.size(); i++) {
            cellColNum.put(args.get(i), i + STARTCOL);
        }
    }

    public int getLastRow() {
        return this.sheet.getLastRowNum();
    }

    public void writeHeader() {
        Row row = sheet.createRow(0);
        for (int i = STARTCOL; i < args.size() + STARTCOL; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(defaultStyle);
            cell.setCellValue(args.get(i - STARTCOL));
            sheet.setColumnWidth(i, 3000);
            colName.put(args.get(i - STARTCOL), i);
        }
        writeCell("Path", 0, 0);
        writeCell("Filename", 0, 1);
        writeCell("Smelly Code", 0, 2);
    }

    public void writeToFile(String path) {
        try {
            File xlsFile = new File(Main.getOutputPath() + "/" + path.substring(path.lastIndexOf('/') + 1) + ".xls");
            FileOutputStream fileOut = new FileOutputStream(xlsFile);
            workBook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                workBook.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void writeMergedCell(String str, int fr, int lr, int fc, int lc) {
        for (int rowNum = fr; rowNum < lr; rowNum++) {
            writeCell("", rowNum, fc);
        }
        mergeCell(fr, lr, fc, lc, str);
    }

    public void mergeCell(int fr, int lr, int fc, int lc, Object obj) {
        try {
            sheet.addMergedRegion(new CellRangeAddress(fr, lr - 1, fc, lc));
        } catch (IllegalArgumentException ignored) {
        }
        Cell cell = sheet.getRow(fr).getCell(fc);
        cell.setCellValue((String) obj);
        defaultStyle.setWrapText(true);
        cell.setCellStyle(defaultStyle);
    }

    public void writeFormula(String formula, int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        Cell cell = row.createCell(colNum);
        cell.setCellStyle(defaultStyle);
        cell.setCellFormula(formula);
    }

    public void writeCell(Object obj, int rowNum, int colNum) {
        Row row;
        Cell cell;
        if ((row = sheet.getRow(rowNum)) == null) {
            row = sheet.createRow(rowNum);
        }
        cell = row.createCell(colNum);
        cell.setCellStyle(defaultStyle);
        if (obj instanceof String)
            cell.setCellValue((String) obj);
        else if (obj instanceof Integer)
            cell.setCellValue((Integer) obj);
    }
}
