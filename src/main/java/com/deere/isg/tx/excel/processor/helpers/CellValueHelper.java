package com.deere.isg.tx.excel.processor.helpers;

import com.deere.isg.tx.excel.processor.ExcelProcessor;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public class CellValueHelper<T> {

    ExcelProcessor<T> excelProcessor;

    public CellValueHelper(ExcelProcessor<T> excelProcessor) {
        this.excelProcessor = excelProcessor;
    }

    public Object getCellValue(Cell cell) {

        if (Objects.isNull(cell)) return null;

        switch (cell.getCellType()) {

            case XSSFCell.CELL_TYPE_BLANK:
                return "";

            case XSSFCell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue() ? "TRUE" : "FALSE";

            case XSSFCell.CELL_TYPE_ERROR:
                return ErrorEval.getText(cell.getErrorCellValue());

            case XSSFCell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();

            case XSSFCell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return BigDecimal.valueOf(cell.getNumericCellValue());

            case XSSFCell.CELL_TYPE_STRING:
                return cell.getStringCellValue();

            default: {

                return "Not Supported Data Type";
            }

        }
    }

    public void setCellValue(Cell cell, Object val) {
        if (Objects.isNull(cell) || Objects.isNull(val)) return;

        switch (cell.getCellType()) {
            case XSSFCell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell) && val instanceof Date) {
                    // Date date = Date.from(((LocalDate) val).atStartOfDay(ZoneId.systemDefault()).toInstant());
                    cell.setCellValue((Date) val);
                } else {
                    cell.setCellValue(Double.parseDouble(val.toString()));
                }
                break;
            case XSSFCell.CELL_TYPE_STRING:
                cell.setCellValue(val.toString());
                break;
            default: {

                if (DateUtil.isCellDateFormatted(cell) || val instanceof Date) {
                    XSSFCreationHelper createHelper = excelProcessor.getWorkBook().getCreationHelper();
                    XSSFCellStyle cellStyle = excelProcessor.getWorkBook().createCellStyle();
                    cellStyle.setDataFormat(
                            createHelper.createDataFormat().getFormat(excelProcessor.getDefaultDateFormat()));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((Date) val);
                } else {
                    cell.setCellValue(val.toString());
                }


            }

        }
    }
}