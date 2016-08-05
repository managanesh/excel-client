package com.deere.isg.tx.excel;

import com.deere.isg.tx.excel.config.ExcelConfig;
import com.deere.isg.tx.excel.processor.ExcelProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * Created by bv80586 on 12/29/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ExcelConfig.class}, initializers = {ConfigFileApplicationContextInitializer.class})
@ActiveProfiles("junit")
@Configuration
public class BaseTest {

    @Bean
    @ConfigurationProperties("excel.reader")
    public ExcelProcessor<TestClass> excelProcessor() {
        return new ExcelProcessor<TestClass>(TestClass.class);
    }


    @Test
    public void contextLoads() {

    }


    public static class TestClass {

        private Integer terminalId;

        private Long terminalLongId;

        private Date terminalDate;

        private String terminalNum;

        @Override
        public String toString() {
            return "TestClass{" +
                    "terminalId=" + terminalId +
                    ", terminalLongId=" + terminalLongId +
                    ", terminalDate=" + terminalDate +
                    ", terminalNum='" + terminalNum + '\'' +
                    '}';
        }

        public Integer getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(Integer terminalId) {
            this.terminalId = terminalId;
        }

        public String getTerminalNum() {
            return terminalNum;
        }

        public void setTerminalNum(String terminalNum) {
            this.terminalNum = terminalNum;
        }

        public Long getTerminalLongId() {
            return terminalLongId;
        }

        public void setTerminalLongId(Long terminalLongId) {
            this.terminalLongId = terminalLongId;
        }

        public Date getTerminalDate() {
            return terminalDate;
        }

        public void setTerminalDate(Date terminalDate) {
            this.terminalDate = terminalDate;
        }
    }


}
