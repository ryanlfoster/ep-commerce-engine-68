package com.elasticpath.commons.util.net.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.elasticpath.commons.util.net.HttpClientFactory;

/**
 * Default implementation of HttpClientFactory that creates concrete instances of HttpClient related classes. 
 */
public class HttpClientFactoryImpl implements HttpClientFactory {

    @Override
    public HttpClient createHttpClient() {
        return new HttpClient();
    }
    
    @Override
    public GetMethod createGetMethod(final String url) {
        return new GetMethod(url);
    }

    @Override
    public PostMethod createPostMethod(final String url) {
        return new PostMethod(url);
    }

}
