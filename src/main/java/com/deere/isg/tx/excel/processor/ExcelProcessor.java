package com.deere.isg.tx.excel.processor;

import com.deere.isg.tx.excel.exception.ExcelProcessorException;
import com.deere.isg.tx.excel.processor.helpers.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by bv80586 on 12/29/2015.
 */
public class ExcelProcessor<T> implements ResourceLoaderAware {

    private static Logger log = LoggerFactory.getLogger(ExcelProcessor.class);
    private final UpdateDataHelper<T> updateDataHelper = new UpdateDataHelper<T>(this);
    private final RemoveDataHelper<T> removeDataHelper = new RemoveDataHelper<T>(this);
    private final ReadDataHelper<T> readDataHelper = new ReadDataHelper<T>(this);
    private final ReflectionHelper<T> reflectionHelper = new ReflectionHelper<T>(this);
    private final CellValueHelper<T> cellValueHelper = new CellValueHelper<T>(this);

    private XSSFWorkbook workBook;
    private XSSFSheet workSheet;
    private ResourceLoader resourceLoader;
    private String excelPath;
    private String worksheetName;
    private String defaultDateFormat;
    private Integer keyFieldIndex;
    private boolean skipHeader = true;
    private Map<Integer, String> fields;
    private Map<Integer, String> headers;
    private Class<T> beanClass;

    public ExcelProcessor(Class<T> beanClass) {
        this.beanClass = beanClass;
    }

    public void loadExcel() {
        loadExcel(false);
    }

    public void loadExcel(boolean isNew) {
        try {
            PathResource excelResource = new PathResource(excelPath);

            if (isNew) {
                workBook = new XSSFWorkbook();
            } else {
                workBook = new XSSFWorkbook(excelResource.getInputStream());

            }

            workSheet = workBook.getSheet(worksheetName);

            if (null == workSheet) {
                log.info("WorkSheet Not Found:{}", worksheetName);
            }

            log.debug("Loading of Excelfile: {} completed", excelPath);

        } catch (Exception e) {

            throw new ExcelProcessorException("Unable to load excelfile: ", e);
        }
    }


    public List<T> getSheetData() {

        return getSheetData(0);
    }

    //#NEW
    public List<T> getSheetData(int skipRows){

        List<T> dataList = new ArrayList<>();
        loadExcel();

        getWorkSheet().rowIterator().forEachRemaining(readDataHelper.getRowConsumer(dataList,skipRows));
        log.debug("Extracting data from Excel :{} is Completed", excelPath);

        return dataList;
    }

    public void createSheetData(List<T> dataList) {
        createSheetData(dataList, false, false);
    }

    public void createSheetData(List<T> dataList, boolean addBelowFlag, boolean isNewWorkBook) {

        loadExcel(isNewWorkBook);
        createWorkSheet();

        final AtomicInteger rowNumAI = new AtomicInteger(getWorkSheet().getPhysicalNumberOfRows());

        dataList.stream().forEach((data) -> {
            int rowNum = rowNumAI.getAndIncrement();


            if (!addBelowFlag) {

                if (getWorkSheet().getLastRowNum() > 0) {
                    rowNum = 1;
                    getWorkSheet().shiftRows(1, getWorkSheet().getPhysicalNumberOfRows() - 1, 1);
                }
            }


            Row row = getWorkSheet().createRow(rowNum);
            fields.entrySet().forEach((entry) -> {
                Cell cell = row.createCell(entry.getKey());

                Object val = this.getValue(data, entry.getValue());
                if (null != val)
                    this.setCellValue(cell, val);
            });
            log.debug("Row Created .{}", row.getRowNum());
        });

        //Set CellWidth
        setAutoSizeColumns();
        log.debug("Collecting  data for Excel:{} is Completed", excelPath);

        updateSheet();
    }


    public void updateSheetData(List<T> dataList) {
        loadExcel();

        Map<Object, T> map = dataList.stream()
                .collect(Collectors.toMap(data -> reflectionHelper.getValue(data, getKeyField()), data -> data, (obj1, obj2) -> obj1));

        getWorkSheet().rowIterator().forEachRemaining(updateDataHelper.updateEachRow(map));
        log.debug("Collecting  data for Excel:{} is Completed", excelPath);

        updateSheet();

    }

    public void removeSheetData(List<T> dataList) {
        loadExcel();
        Map<Object, T> map = dataList.stream()
                .collect(Collectors.toMap(data -> reflectionHelper.getValue(data, getKeyField()), data -> data, (data1, data2) -> data1));

        dataList.stream().forEach(removeDataHelper.getRemoveListConsumer(map));
        log.debug("Removing data for Excel: {} is Completed", excelPath);

        updateSheet();

    }
    //#Modified
    public void updateSheet() {

        try {

            OutputStream outputStream = null;

            if (ResourceUtils.isUrl(excelPath)) {
                URL url = ResourceUtils.getURL(excelPath);
                URLConnection connection = url.openConnection();
                connection.setDoOutput(true);
                outputStream = connection.getOutputStream();

            } else {
                PathResource resource = new PathResource(excelPath);
                outputStream = resource.getOutputStream();
            }

            workBook.write(outputStream);

        } catch (Throwable t) {
            throw new ExcelProcessorException("Unable to update Excel: ", t);
        }
    }

    private Consumer<Row> updateEachRow(Map<Object, T> map) {
        return updateDataHelper.updateEachRow(map);
    }


    private Consumer<Map.Entry<Integer, String>> getEntryConsumer(Row row, T data) {
        return updateDataHelper.getEntryConsumer(row, data);
    }

    public Object getKeyVal(Row row) {
        Integer keyIndex = getKeyFieldIndex();
        String keyFieldName = getKeyField();
        Method method = BeanUtils.findMethodWithMinimalParameters(beanClass, "set" + keyFieldName);
        Cell cell = row.getCell(keyIndex);
        Object val = cellValueHelper.getCellValue(cell);
        val = reflectionHelper.convertObject(val, method);

        return val;
    }

    public String getKeyField() {
        return fields.get(getKeyFieldIndex());
    }

    public Integer getKeyFieldIndex() {
        return keyFieldIndex;
    }

    public void setKeyFieldIndex(Integer keyFieldIndex) {
        this.keyFieldIndex = keyFieldIndex;
    }

    private Consumer<Row> getRowConsumer(List<T> dataList, int skipRows) {
        return readDataHelper.getRowConsumer(dataList,skipRows);
    }

    private Consumer<Cell> getCellConsumer(T dataRow) {

        return readDataHelper.getCellConsumer(dataRow);
    }

    public void setValue(T dataRow, Object val, String fieldName) {

        reflectionHelper.setValue(dataRow, val, fieldName);
    }

    private Object convertObject(Object val, Method method) {
        return reflectionHelper.convertObject(val, method);
    }

    public Object getValue(T dataRow, String fieldName) {

        return reflectionHelper.getValue(dataRow, fieldName);
    }

    public Object getCellValue(Cell cell) {

        return cellValueHelper.getCellValue(cell);
    }

    public void setCellValue(Cell cell, Object val) {

        cellValueHelper.setCellValue(cell, val);
    }

    public String getExcelPath() {
        return excelPath;
    }

    public void setExcelPath(String excelPath) {
        this.excelPath = excelPath;
    }

    public String getWorksheetName() {
        return worksheetName;
    }

    public void setWorksheetName(String worksheetName) {
        this.worksheetName = worksheetName;
    }

    public Map<Integer, String> getFields() {
        return fields;
    }

    public void setFields(Map<Integer, String> fields) {
        this.fields = fields;
    }

    public boolean isSkipHeader() {
        return skipHeader;
    }

    public void setSkipHeader(boolean skipHeader) {
        this.skipHeader = skipHeader;
    }

    public XSSFSheet getWorkSheet() {
        if (null == workSheet) {
            throw new ExcelProcessorException("Workseet:" + worksheetName + " not found in Excel:" + excelPath);
        }
        return workSheet;
    }

    public void setWorkSheet(XSSFSheet workSheet) {
        this.workSheet = workSheet;
    }

    public void createWorkSheet() {
        if (workSheet == null) {
            workSheet = workBook.createSheet(worksheetName);

            if (!CollectionUtils.isEmpty(headers)) {

                final XSSFRow row = workSheet.createRow(0);

                headers.keySet().stream().forEach((key) -> {

                    XSSFCell cell = row.createCell(key);

                    cell.setCellValue(headers.get(key));

                });

                XSSFCellStyle cellStyle = workBook.createCellStyle();
                XSSFFont font = workBook.createFont();
                font.setBold(true);
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);

                row.setRowStyle(cellStyle);
                workSheet.createFreezePane(0, 1);
            }

        }
    }

    public Logger getLog() {
        return log;
    }

    public Class<T> getBeanClass() {
        return beanClass;
    }

    public XSSFWorkbook getWorkBook() {
        return workBook;
    }

    public void setWorkBook(XSSFWorkbook workBook) {
        this.workBook = workBook;
    }

    public Map<Integer, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<Integer, String> headers) {
        this.headers = headers;
    }

    /**
     * Set the ResourceLoader that this object runs in.
     * <p>This might be a ResourcePatternResolver, which can be checked
     * through {@code instanceof ResourcePatternResolver}. See also the
     * {@code ResourcePatternUtils.getResourcePatternResolver} method.
     * <p>Invoked after population of normal bean properties but before an init callback
     * like InitializingBean's {@code afterPropertiesSet} or a custom init-method.
     * Invoked before ApplicationContextAware's {@code setApplicationContext}.
     *
     * @param resourceLoader ResourceLoader object to be used by this object
     * @see ResourcePatternResolver
     * @see ResourcePatternUtils#getResourcePatternResolver
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    public void setDefaultDateFormat(String defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
    }


    private void setAutoSizeColumns() {
        fields.keySet().stream().forEach((key) -> {
            getWorkSheet().autoSizeColumn(key);
        });
    }
}
