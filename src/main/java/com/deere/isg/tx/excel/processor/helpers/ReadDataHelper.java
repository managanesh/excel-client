package com.deere.isg.tx.excel.processor.helpers;

import com.deere.isg.tx.excel.exception.ExcelProcessorException;
import com.deere.isg.tx.excel.processor.ExcelProcessor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Consumer;

public class ReadDataHelper<T> {
    private final ExcelProcessor<T> excelProcessor;

    public ReadDataHelper(ExcelProcessor<T> excelProcessor) {
        this.excelProcessor = excelProcessor;
    }

    //#Modified
    public Consumer<Row> getRowConsumer(List<T> dataList, int skipRows) {
        return (row) -> {
            try {

                if (excelProcessor.isSkipHeader() && row.getRowNum() <= skipRows) return;

                T dataRow = excelProcessor.getBeanClass().newInstance();
                row.cellIterator().forEachRemaining(getCellConsumer(dataRow));
                dataList.add(dataRow);

            } catch (InstantiationException | IllegalAccessException e) {
                new ExcelProcessorException(row.getRowNum(), -1, "Unable to Process row", e);
            }


        };
    }

    public Consumer<Cell> getCellConsumer(T dataRow) {

        return (Cell cell) -> {
            try {
                Object val = excelProcessor.getCellValue(cell);
                int cellNum = cell.getColumnIndex();
                String fieldName = excelProcessor.getFields().get(cellNum);
                if (StringUtils.hasLength(fieldName)) excelProcessor.setValue(dataRow, val, fieldName);
            } catch (Exception e) {
                new ExcelProcessorException(cell.getColumnIndex(), -1, "Unable to Process row", e);
            }
        };
    }
}