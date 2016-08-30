
package com.microsoft.sharepoint.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "CopySoap", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface CopySoap {


    /**
     * 
     * @param results
     * @param copyIntoItemsLocalResult
     * @param sourceUrl
     * @param destinationUrls
     */
    @WebMethod(operationName = "CopyIntoItemsLocal", action = "http://schemas.microsoft.com/sharepoint/soap/CopyIntoItemsLocal")
    @RequestWrapper(localName = "CopyIntoItemsLocal", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", className = "com.microsoft.sharepoint.webservices.CopyIntoItemsLocal")
    @ResponseWrapper(localName = "CopyIntoItemsLocalResponse", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", className = "com.microsoft.sharepoint.webservices.CopyIntoItemsLocalResponse")
    public void copyIntoItemsLocal(
            @WebParam(name = "SourceUrl", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/")
                    String sourceUrl,
            @WebParam(name = "DestinationUrls", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/")
                    DestinationUrlCollection destinationUrls,
            @WebParam(name = "CopyIntoItemsLocalResult", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", mode = WebParam.Mode.OUT)
                    Holder<Long> copyIntoItemsLocalResult,
            @WebParam(name = "Results", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", mode = WebParam.Mode.OUT)
                    Holder<CopyResultCollection> results);

    /**
     * 
     * @param stream
     * @param results
     * @param copyIntoItemsResult
     * @param sourceUrl
     * @param destinationUrls
     * @param fields
     */
    @WebMethod(operationName = "CopyIntoItems", action = "http://schemas.microsoft.com/sharepoint/soap/CopyIntoItems")
    @RequestWrapper(localName = "CopyIntoItems", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", className = "com.microsoft.sharepoint.webservices.CopyIntoItems")
    @ResponseWrapper(localName = "CopyIntoItemsResponse", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", className = "com.microsoft.sharepoint.webservices.CopyIntoItemsResponse")
    public void copyIntoItems(
            @WebParam(name = "SourceUrl", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/")
                    String sourceUrl,
            @WebParam(name = "DestinationUrls", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/")
                    DestinationUrlCollection destinationUrls,
            @WebParam(name = "Fields", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/")
                    FieldInformationCollection fields,
            @WebParam(name = "Stream", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/")
                    byte[] stream,
            @WebParam(name = "CopyIntoItemsResult", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", mode = WebParam.Mode.OUT)
                    Holder<Long> copyIntoItemsResult,
            @WebParam(name = "Results", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", mode = WebParam.Mode.OUT)
                    Holder<CopyResultCollection> results);

    /**
     * 
     * @param getItemResult
     * @param stream
     * @param url
     * @param fields
     */
    @WebMethod(operationName = "GetItem", action = "http://schemas.microsoft.com/sharepoint/soap/GetItem")
    @RequestWrapper(localName = "GetItem", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", className = "com.microsoft.sharepoint.webservices.GetItem")
    @ResponseWrapper(localName = "GetItemResponse", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", className = "com.microsoft.sharepoint.webservices.GetItemResponse")
    public void getItem(
            @WebParam(name = "Url", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/")
                    String url,
            @WebParam(name = "GetItemResult", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", mode = WebParam.Mode.OUT)
                    Holder<Long> getItemResult,
            @WebParam(name = "Fields", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", mode = WebParam.Mode.OUT)
                    Holder<FieldInformationCollection> fields,
            @WebParam(name = "Stream", targetNamespace = "http://schemas.microsoft.com/sharepoint/soap/", mode = WebParam.Mode.OUT)
                    Holder<byte[]> stream);

}
