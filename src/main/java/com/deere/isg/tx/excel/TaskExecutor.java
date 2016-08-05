package com.deere.isg.tx.excel;

import com.deere.isg.tx.excel.beans.TouchReportBean;
import com.deere.isg.tx.excel.processor.ExcelProcessor;
import com.deere.isg.tx.excel.services.TouchReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by ganesh.vallabhaneni on 5/21/2016.
 */

@Component
public class TaskExecutor {
    @Autowired
    @Qualifier(value = "ReaderProcessor")
    ExcelProcessor<TouchReportBean> processor;

    @Autowired
    TouchReportService touchReportService;

    @PostConstruct
    public void execute() {


        touchReportService.createTouchReport(processor.getSheetData(8));

    }
}
