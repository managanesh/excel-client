package com.deere.isg.tx.excel;

import com.deere.isg.tx.excel.beans.TouchReportBean;
import com.deere.isg.tx.excel.processor.ExcelProcessor;
import com.deere.isg.tx.excel.services.TouchReportService;
import com.deere.isg.tx.mail.IMAPSMailComponent;
import com.deere.isg.tx.sp10.SharePointUploadDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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

    @Value("${excel.source.dir}")
    String sourcePath;

    @Value("${excel.dest.dir}")
    String destPath;

    @Autowired
    IMAPSMailComponent mailComponent;

    @Autowired
    SharePointUploadDocument sharePointUploadDocument;

    @PostConstruct
    public void execute() {

        mailComponent.saveAttachments("Ticket Activity By Resolve Date");

        boolean updateFolder = true;
        try (final Stream<Path> pathStream = Files.walk(Paths.get(getSourcePath()), FileVisitOption.FOLLOW_LINKS)) {

            pathStream
                    .filter(path -> path.toFile().isFile())
                    .forEach((path) -> {
                        processor.setExcelPath(path.toString());
                        touchReportService.createTouchReport(processor.getSheetData(8), path);
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            new RuntimeException(e);
                        }
                    });


        } catch (final Exception e) {
            updateFolder = false;
            throw new RuntimeException(e);
        }

      //  mailComponent.closeFolder(updateFolder);

        try (final Stream<Path> destPathStream = Files.walk(Paths.get(destPath), FileVisitOption.FOLLOW_LINKS)) {

            destPathStream
                    .filter(path -> path.toFile().isFile())
                    .forEach((path) -> {
                        sharePointUploadDocument.uploadTouchReport(path.toFile().getPath());
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            new RuntimeException(e);
                        }
                    });


        } catch (final Exception e) {

            throw new RuntimeException(e);
        }


    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
}
