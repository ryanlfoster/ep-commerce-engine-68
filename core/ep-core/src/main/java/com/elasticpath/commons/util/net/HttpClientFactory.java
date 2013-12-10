package com.elasticpath.commons.util.net;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Factory for creating classes associated with HttpClient.  May be mocked or stubbed for testing. 
 */
public interface HttpClientFactory {
    
    /**
     * Create an HttpClient.
     * @return a new HttpClient
     */
    HttpClient createHttpClient();
    
    /**
     * Create a GetMethod for a specified url.
     * @param url The url
     * @return a new GetMethod
     */
    GetMethod createGetMethod(String url);
    
    /**
     * Create a PostMethod for a specified url.
     * @param url The url
     * @return a new PostMethod
     */
    PostMethod createPostMethod(String url);

}
