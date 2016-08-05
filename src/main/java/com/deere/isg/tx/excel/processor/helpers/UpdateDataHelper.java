package com.deere.isg.tx.excel.processor.helpers;

import com.deere.isg.tx.excel.processor.ExcelProcessor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;
import java.util.function.Consumer;

public class UpdateDataHelper<T> {
    private final ExcelProcessor<T> excelProcessor;

    public UpdateDataHelper(ExcelProcessor<T> excelProcessor) {
        this.excelProcessor = excelProcessor;
    }

    public Consumer<Row> updateEachRow(Map<Object, T> map) {
        return (row) -> {

            Object val = excelProcessor.getKeyVal(row);

            T data = map.get(val);

            if (data != null) {
                excelProcessor.getLog().debug("Updating row {} with {}", row.getRowNum(), data);
                excelProcessor.getFields().entrySet().forEach(getEntryConsumer(row, data));

            }

        };
    }

    public Consumer<Map.Entry<Integer, String>> getEntryConsumer(Row row, T data) {
        return (entry) -> {

            Cell cCell = row.getCell(entry.getKey());
            //since we are already validating if key field  value is present or not ,
            // so if cell is not present we can create a Cell here
            //#TODO: use row.createCell(entry.getKey(),<type>) for Specific DataTypes

            if (cCell == null) cCell = row.createCell(entry.getKey());

            Object cVal = excelProcessor.getValue(data, entry.getValue());
            if (cCell == null || cVal == null || excelProcessor.getCellValue(cCell) == null) return;

            if (!excelProcessor.getCellValue(cCell).equals(cVal)) {

                excelProcessor.getLog().debug("Updating row {} and column {} with {}", row.getRowNum(), cCell.getColumnIndex(), cVal);
                excelProcessor.setCellValue(cCell, cVal);

            }

        };
    }
}