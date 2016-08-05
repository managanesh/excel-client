package com.deere.isg.tx.excel.processor.helpers;

import com.deere.isg.tx.excel.processor.ExcelProcessor;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by bv80586 on 1/21/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CellValueHelperTest {

    @Mock
    ExcelProcessor<Object> excelProcessor;

    @InjectMocks
    CellValueHelper<Object> cellValueHelper = new CellValueHelper<>(excelProcessor);

    @Mock
    Cell cell;


    @Test
    public void test_get_cell_value() {

        Object obj = cellValueHelper.getCellValue(null);
        assertThat(obj, is(nullValue()));


        when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_BLANK);
        obj = cellValueHelper.getCellValue(cell);
        assertThat(obj, is(""));

        when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_BOOLEAN);
        when(cell.getBooleanCellValue()).thenReturn(true);
        obj = cellValueHelper.getCellValue(cell);
        assertThat(obj, is("TRUE"));
        when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_NUMERIC);
        when(cell.getNumericCellValue()).thenReturn(1.0);
        obj = cellValueHelper.getCellValue(cell);
        assertThat(obj, is(BigDecimal.valueOf(1.0)));
        when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_STRING);
        when(cell.getStringCellValue()).thenReturn("value");
        obj = cellValueHelper.getCellValue(cell);
        assertThat(obj, is("value"));
    }

}
