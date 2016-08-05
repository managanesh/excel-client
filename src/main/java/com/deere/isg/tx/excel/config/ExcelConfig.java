package com.deere.isg.tx.excel.config;

import com.deere.isg.tx.excel.beans.TouchReportBean;
import com.deere.isg.tx.excel.processor.ExcelProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by bv80586 on 12/29/2015.
 */
@Configuration
@ComponentScan(basePackages = {"com.deere.isg.tx.excel"})
@PropertySource("classpath:excel-client-${spring.profiles.active}.properties")
@EnableConfigurationProperties
public class ExcelConfig<T> {

    @Bean(name = "ReaderProcessor")
    @ConfigurationProperties(prefix = "excel.reader")
    public ExcelProcessor<TouchReportBean> getProcessor() {
        ExcelProcessor<TouchReportBean> processor = new ExcelProcessor<>(TouchReportBean.class);
        return processor;
    }


    @Bean(name = "TouchReportProcessor")
    @ConfigurationProperties(prefix = "excel.create.touchreport")
    public ExcelProcessor<TouchReportBean> getTouchReportProcessor() {
        ExcelProcessor<TouchReportBean> processor = new ExcelProcessor<>(TouchReportBean.class);
        return processor;
    }
}
