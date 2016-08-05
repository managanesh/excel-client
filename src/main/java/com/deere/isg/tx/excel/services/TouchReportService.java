package com.deere.isg.tx.excel.services;

import com.deere.isg.tx.excel.beans.TouchReportBean;
import com.deere.isg.tx.excel.processor.ExcelProcessor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by ganesh.vallabhaneni on 5/21/2016.
 */
@Service
public class TouchReportService {

    @Autowired
    @Qualifier(value = "TouchReportProcessor")
    ExcelProcessor<TouchReportBean> processor;


    public List<Integer> urgentCols = Arrays.asList(0, 1, 2);
    public List<Integer> highCols = Arrays.asList(4, 5, 6);
    public List<Integer> mediCols = Arrays.asList(8, 9, 10);
    public List<Integer> lowCols = Arrays.asList(12, 13, 14);
    public List<Integer> projCols = Arrays.asList(16, 17, 18);

    public List<String> casdHeaderList = Arrays.asList("Ticket", "Groups", "Days");

    public void createTouchReport(List<TouchReportBean> touchReportBeanList) {
        processor.loadExcel(true);
        final XSSFWorkbook workbook = processor.getWorkBook();
        XSSFSheet sheet = workbook.createSheet(processor.getWorksheetName());

        setHeader(workbook);

        Map<String, List<TouchReportBean>> groupByPriority = touchReportBeanList.stream().filter(b -> b.getCasdId() != null)
                .collect(Collectors.groupingBy(TouchReportBean::getPriority));

        groupByPriority.forEach((k, v) -> {

            Map<Integer, List<TouchReportBean>> groupByCASD = v.stream()
                    .collect(Collectors.groupingBy(TouchReportBean::getCasdId));
            AtomicInteger rowCount = new AtomicInteger(2);

            groupByCASD.forEach((casd, beanList) -> {

                if (k.toUpperCase().contains("URGENT")) {
                    setCASDHeaderBYPriority(workbook, urgentCols, IndexedColors.RED.getIndex(), rowCount);
                    setCASDRows(workbook, urgentCols, IndexedColors.RED.getIndex(), rowCount, beanList);
                    sheet.autoSizeColumn(urgentCols.get(1));
                } else if (k.toUpperCase().contains("HIGH")) {
                    setCASDHeaderBYPriority(workbook, highCols, IndexedColors.ORANGE.getIndex(), rowCount);
                    setCASDRows(workbook, highCols, IndexedColors.ORANGE.getIndex(), rowCount, beanList);
                    sheet.autoSizeColumn(highCols.get(1));
                } else if (k.toUpperCase().contains("MEDIUM")) {
                    setCASDHeaderBYPriority(workbook, mediCols, IndexedColors.CORNFLOWER_BLUE.getIndex(), rowCount);
                    setCASDRows(workbook, mediCols, IndexedColors.CORNFLOWER_BLUE.getIndex(), rowCount, beanList);
                    sheet.autoSizeColumn(mediCols.get(1));
                } else if (k.toUpperCase().contains("LOW")) {
                    setCASDHeaderBYPriority(workbook, lowCols, IndexedColors.SEA_GREEN.getIndex(), rowCount);
                    setCASDRows(workbook, lowCols, IndexedColors.SEA_GREEN.getIndex(), rowCount, beanList);
                    sheet.autoSizeColumn(lowCols.get(1));
                } else if (k.toUpperCase().contains("PROJECT")) {
                    setCASDHeaderBYPriority(workbook, projCols, IndexedColors.GREEN.getIndex(), rowCount);
                    setCASDRows(workbook, projCols, IndexedColors.GREEN.getIndex(), rowCount, beanList);
                    sheet.autoSizeColumn(projCols.get(1));
                }

                rowCount.getAndIncrement();
            });

        });

        processor.updateSheet();
    }

    public void setCASDRows(XSSFWorkbook workbook, List<Integer> list, short colorIndex, AtomicInteger rowCount, List<TouchReportBean> beanList) {
        XSSFSheet sheet = workbook.getSheet(processor.getWorksheetName());

        Optional<Long> total = beanList.stream().map(b -> b.getDuration()).reduce((b1, b2) -> b1 + b2);
        TouchReportBean totalBean = new TouchReportBean();
        totalBean.setDuration(total.orElse(0l));
        totalBean.setSupportGroup("Total");
        beanList.add(totalBean);

        Map<String, List<TouchReportBean>> beansbySupport = beanList.stream().collect(Collectors.groupingBy(TouchReportBean::getSupportGroup));

        Font font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);

        Integer startRow = rowCount.get();
        TreeMap<String, List<TouchReportBean>> sortedbySupport = new TreeMap<String, List<TouchReportBean>>(((o1, o2) -> {
            if (o1 == null) return -1;
            if (o2 == null) return 1;
            return o1.compareTo(o2);
        }));
        sortedbySupport.putAll(beansbySupport);
        sortedbySupport.forEach((k, v) -> {

            final Row casdV = (sheet.getRow(rowCount.get()) == null)
                    ? sheet.createRow(rowCount.getAndIncrement()) : sheet.getRow(rowCount.getAndIncrement());
            final Iterator<Integer> iterator = list.iterator();
            setTicket(workbook, casdV, iterator.next(), colorIndex, v.get(0).getCasdId(), null);
            setTicket(workbook, casdV, iterator.next(), colorIndex, v.get(0).getSupportGroup(), null);

            Optional<Double> totalDuration = v.stream().map(b -> b.getDurationInDays()).reduce((b1, b2) -> b1 + b2);
            if (v.get(0).getCasdId() != null) {
                setTicket(workbook, casdV, iterator.next(), colorIndex, totalDuration.orElse(0.0), null);
            } else {
                setTicket(workbook, casdV, iterator.next(), colorIndex, totalDuration.orElse(0.0), font);
            }

        });
        Integer endRow = rowCount.get() - 1;

        if (beanList.size() > 1) {
            sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, list.get(0), list.get(0)));

        }


    }

    public void setTicket(XSSFWorkbook workbook, Row casdV, Integer index, Short colorIndex, Object val, Font font) {

        XSSFSheet sheet = workbook.getSheet(processor.getWorksheetName());

        Cell cell = casdV.createCell(index);
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(colorIndex);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);

        if (val != null) {
            if (val instanceof Number) {
                cell.setCellValue(Double.parseDouble(val.toString()));

            } else {
                cell.setCellValue(val.toString());
            }
        }
        if (font != null) {
            style.setFont(font);
        }
        cell.setCellStyle(style);

        CellUtil.setAlignment(cell, workbook, CellStyle.ALIGN_CENTER);

        RegionUtil.setBorderTop(CellStyle.BORDER_THIN,
                new CellRangeAddress(casdV.getRowNum(), casdV.getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet, workbook);
        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                new CellRangeAddress(casdV.getRowNum(), casdV.getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet, workbook);
        RegionUtil.setBorderLeft(CellStyle.BORDER_THIN,
                new CellRangeAddress(casdV.getRowNum(), casdV.getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet, workbook);
        RegionUtil.setBorderRight(CellStyle.BORDER_THIN,
                new CellRangeAddress(casdV.getRowNum(), casdV.getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet, workbook);

    }

    public void setCASDHeaderBYPriority(XSSFWorkbook workbook, List<Integer> list, short colorIndex, AtomicInteger rowCount) {
        XSSFSheet sheet = workbook.getSheet(processor.getWorksheetName());

        final Row casdH = (sheet.getRow(rowCount.get()) == null)
                ? sheet.createRow(rowCount.getAndIncrement()) : sheet.getRow(rowCount.getAndIncrement());


        AtomicInteger colCount = new AtomicInteger(0);
        list.stream().forEach((index) -> {

            Cell cell = casdH.createCell(index);


            CellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(colorIndex);
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);

            Font font = workbook.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style.setFont(font);

            cell.setCellValue(casdHeaderList.get(colCount.getAndIncrement()));
            cell.setCellStyle(style);

            CellUtil.setAlignment(cell, workbook, CellStyle.ALIGN_CENTER);

            RegionUtil.setBorderTop(CellStyle.BORDER_THIN,
                    new CellRangeAddress(casdH.getRowNum(), casdH.getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet, workbook);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                    new CellRangeAddress(casdH.getRowNum(), casdH.getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN,
                    new CellRangeAddress(casdH.getRowNum(), casdH.getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN,
                    new CellRangeAddress(casdH.getRowNum(), casdH.getRowNum(), cell.getColumnIndex(), cell.getColumnIndex()), sheet, workbook);

        });
    }

    public void setHeader(XSSFWorkbook workbook) {

        //set hearder for urgent
        XSSFSheet sheet = workbook.getSheet(processor.getWorksheetName());
        Row headerRow = sheet.createRow(0);

        setByPriority(workbook, urgentCols, IndexedColors.RED.getIndex(), "URGENT", headerRow);
        setByPriority(workbook, highCols, IndexedColors.ORANGE.getIndex(), "HIGH", headerRow);
        setByPriority(workbook, mediCols, IndexedColors.CORNFLOWER_BLUE.getIndex(), "MEDIUM", headerRow);
        setByPriority(workbook, lowCols, IndexedColors.SEA_GREEN.getIndex(), "LOW", headerRow);
        setByPriority(workbook, projCols, IndexedColors.GREEN.getIndex(), "PROJECT", headerRow);

    }

    public void setByPriority(XSSFWorkbook workbook, List<Integer> list, short colorIndex, String title, Row headerRow) {

        list.stream().forEach(colNum -> {
            if (headerRow.getCell(colNum) == null) headerRow.createCell(colNum);
        });

        XSSFSheet sheet = workbook.getSheet(processor.getWorksheetName());
        sheet.addMergedRegion(new CellRangeAddress(0, 0, list.get(0), list.get(list.size() - 1)));
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(colorIndex);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        Cell cell = headerRow.getCell(list.get(0));
        cell.setCellValue(title);
        Font font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style.setFont(font);
        cell.setCellStyle(style);
        CellUtil.setAlignment(cell, workbook, CellStyle.ALIGN_CENTER);
    }

}
