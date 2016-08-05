package com.deere.isg.tx.excel.exception;

/**
 * Created by bv80586 on 12/31/2015.
 */
public class ExcelProcessorException extends RuntimeException {

    private int row;

    private int column;

    private String message;

    public ExcelProcessorException(int row, int column, String message) {

        super(message);
        this.row = row;
        this.column = column;
        this.message = message;

    }

    public ExcelProcessorException(int row, int column, String message, Throwable throwable) {
        super(message, throwable);
        this.row = row;
        this.column = column;
        this.message = message;
    }

    public ExcelProcessorException(String message, int row, Throwable throwable) {
        super(message, throwable);
        this.message = message;
        this.row = row;
    }

    public ExcelProcessorException(int column, String message, Throwable throwable) {
        super(message, throwable);
        this.column = column;
        this.message = message;
    }

    public ExcelProcessorException(String message, Throwable throwable) {
        super(message, throwable);
        this.message = message;
    }

    public ExcelProcessorException(String message) {
        super(message);
        this.message = message;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
