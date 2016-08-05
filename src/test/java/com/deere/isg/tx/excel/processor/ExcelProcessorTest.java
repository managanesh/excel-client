package com.deere.isg.tx.excel.processor;

import com.deere.isg.tx.excel.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bv80586 on 12/31/2015.
 */

public class ExcelProcessorTest extends BaseTest {

    @Autowired
    ExcelProcessor<TestClass> excelProcessor;

    @Test
    public void addRow() {
        List<TestClass> list = new ArrayList<>();
        TestClass obj1 = new TestClass();
        obj1.setTerminalId(1);
        obj1.setTerminalNum("Terminal");
        list.add(obj1);

        TestClass obj2 = new TestClass();
        obj2.setTerminalId(2);
        obj2.setTerminalNum("Terminal2");
        list.add(obj2);

        excelProcessor.createSheetData(list);

    }

    @Test
    public void removeRow() {
        List<TestClass> list = new ArrayList<>();
        TestClass obj1 = new TestClass();
        obj1.setTerminalId(2);
        obj1.setTerminalNum("Terminal2");
        list.add(obj1);
        excelProcessor.removeSheetData(list);

    }

    @Test
    public void updateRow() {
        List<TestClass> list = new ArrayList<>();
        TestClass obj1 = new TestClass();
        obj1.setTerminalId(1);
        obj1.setTerminalNum("Terminal1_Updated");
        list.add(obj1);
        excelProcessor.updateSheetData(list);

    }

}
