package com.deere.isg.tx.sp10;

import com.microsoft.sharepoint.webservices.CopySoap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Component
public class SharePointUploadDocument extends SharePointBase {

    private static Properties properties = new Properties();
    private static final Log logger = LogFactory.getLog(SharePointUploadDocument.class);

    public Properties getProperties() {
        return properties;
    }


    @PostConstruct
    protected void initialize() throws Exception {
        logger.info("initialize()...");
        properties.load(getClass().getResourceAsStream("/SharePointUploadDocumentExample.properties"));
        super.initialize();
    }


    public void uploadTouchReport(String path ) {

        try {
            CopySoap p = getCopySoap();
            uploadDocument(p, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
