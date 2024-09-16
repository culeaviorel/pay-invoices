package org.fasttrackit.util;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

public class XlsxUtility {
    private final Workbook workbook1;
    private final Workbook workbook2;

    @SneakyThrows
    public XlsxUtility(File file1) {
        this.workbook1 = WorkbookFactory.create(file1);
        this.workbook2 = null;
    }

    @SneakyThrows
    public XlsxUtility(File file1, File file2) {
        this.workbook1 = WorkbookFactory.create(file1);
        this.workbook2 = WorkbookFactory.create(file2);
    }

    @SneakyThrows
    public boolean compare() {
        if (workbook1 == null || workbook2 == null) {
            return false;
        }
        if (workbook1.getNumberOfSheets() != workbook2.getNumberOfSheets()) {
            return false;
        }
        for (int i = 0; i < workbook1.getNumberOfSheets(); i++) {
            if (!compareSheet(workbook1.getSheetAt(i), workbook2.getSheetAt(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean compareSheet(Sheet sheet1, Sheet sheet2) {
        if (sheet1.getPhysicalNumberOfRows() != sheet2.getPhysicalNumberOfRows()) {
            return false;
        }

        for (int i = 0; i < sheet1.getPhysicalNumberOfRows(); i++) {
            if (!compareRow(sheet1.getRow(i), sheet2.getRow(i))) {
                return false;
            }
        }

        return true;
    }

    private boolean compareRow(Row row1, Row row2) {
        if (row1.getPhysicalNumberOfCells() != row2.getPhysicalNumberOfCells()) {
            return false;
        }

        for (int i = 0; i < row1.getPhysicalNumberOfCells(); i++) {
            if (!compareCell(row1.getCell(i), row2.getCell(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean compareCell(Cell cell1, Cell cell2) {
        if (cell1.getCellType() != cell2.getCellType()) {
            return false;
        }

        return switch (cell1.getCellType()) {
            case BLANK -> true;
            case BOOLEAN -> cell1.getBooleanCellValue() == cell2.getBooleanCellValue();
            case NUMERIC -> cell1.getNumericCellValue() == cell2.getNumericCellValue();
            case STRING -> cell1.getStringCellValue().equals(cell2.getStringCellValue());
            case FORMULA -> {
                FormulaEvaluator evaluator = workbook1.getCreationHelper().createFormulaEvaluator();
                yield evaluator.evaluateFormulaCell(cell1) == evaluator.evaluateFormulaCell(cell2);
            }
            case ERROR -> cell1.getErrorCellValue() == cell2.getErrorCellValue();
            default -> false;
        };
    }

    private String getCellValue(Cell cell1) {
        if (cell1 == null) {
            return null;
        }
        return switch (cell1.getCellType()) {
            case BLANK -> "";
            case BOOLEAN -> cell1.getBooleanCellValue() + "";
            case NUMERIC -> {
                double value = cell1.getNumericCellValue();
                DecimalFormat format = new DecimalFormat("#.#");
                yield format.format(value);
            }
            case STRING -> cell1.getStringCellValue();
            case FORMULA -> {
                String s;
                try {
                    s = cell1.getStringCellValue();
                } catch (IllegalStateException e) {
                    try {
                        s = cell1.getNumericCellValue() + "";
                    } catch (IllegalStateException e2) {
                        s = cell1.getErrorCellValue() + "";
                    }
//                    FormulaEvaluator evaluator = workbook1.getCreationHelper().createFormulaEvaluator();
//                    s = evaluator.evaluateFormulaCell(cell1) + "";
                }
                yield s;
            }
            case ERROR -> cell1.getErrorCellValue() + "";
            default -> null;
        };
    }

    @SneakyThrows
    public void convertToCSV(File csvFile) {
        FileOutputStream fos = new FileOutputStream(csvFile);
        StringBuilder data = new StringBuilder();
        workbook1.getSheetAt(0).forEach(row -> {
            row.forEach(cell -> {
                data.append(getCellValue(cell)).append(",");
            });
            data.deleteCharAt(data.length() - 1);
            System.out.println(data);
        });
        fos.write(data.toString().getBytes());
    }
}
