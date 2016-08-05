package com.deere.isg.tx.excel.processor.helpers;

import com.deere.isg.tx.excel.processor.ExcelProcessor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RemoveDataHelper<T> {
    private final ExcelProcessor<T> excelProcessor;

    public RemoveDataHelper(ExcelProcessor<T> excelProcessor) {
        this.excelProcessor = excelProcessor;
    }


    public Consumer<T> getRemoveListConsumer(Map<Object, T> map) {
        return (data) -> {

            //  .forEachRemaining(getRemoveRowConsumer(map, data));
            List<Integer> removeRowList = StreamHelper.asStream(excelProcessor.getWorkSheet().rowIterator())
                    .filter(isRowMatched(map, data))
                    .map(row -> row.getRowNum())
                    .collect(Collectors.toList());

            removeRowList.stream().forEach(removeRow());

        };
    }

    private Predicate<Row> isRowMatched(Map<Object, T> map, T data) {

        return (row) -> {

            Object excelVal = excelProcessor.getKeyVal(row);
            T dataRow = map.get(excelVal);

            if (dataRow != null) {
                Object dataVal = excelProcessor.getValue(data, excelProcessor.getKeyField());

                if (excelVal.equals(dataVal)) {
                    return true;
                }
            }
            return false;
        };
    }

    private Consumer<Integer> removeRow() {

        return (rowNum) -> {
            XSSFSheet workSheet = excelProcessor.getWorkSheet();
            workSheet.removeRow(workSheet.getRow(rowNum));
            excelProcessor.getWorkSheet().shiftRows(rowNum + 1
                    , workSheet.getLastRowNum(), -1);
            excelProcessor.getLog().debug("Removed row @ {} ", rowNum);
        };
    }
}

