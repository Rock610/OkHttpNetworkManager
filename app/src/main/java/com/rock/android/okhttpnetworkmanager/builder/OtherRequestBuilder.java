package com.rock.android.okhttpnetworkmanager.builder;


import com.rock.android.okhttpnetworkmanager.request.OkHttpRequest;
import com.rock.android.okhttpnetworkmanager.request.OtherRequest;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * DELETE、PUT、PATCH等其他方法
 */
public class OtherRequestBuilder extends OkHttpRequestBuilder
{
    private RequestBody requestBody;
    private String method;
    private String content;

    public OtherRequestBuilder(String method)
    {
        this.method = method;
    }

    @Override
    public OkHttpRequest buildRequest()
    {
        return new OtherRequest(requestBody, content, method, url, tag, params, headers);
    }

    public OtherRequestBuilder requestBody(RequestBody requestBody)
    {
        this.requestBody = requestBody;
        return this;
    }

    public OtherRequestBuilder requestBody(String content)
    {
        this.content = content;
        return this;
    }

    @Override
    public OtherRequestBuilder url(String url)
    {
        this.url = url;
        return this;
    }

    @Override
    public OtherRequestBuilder tag(Object tag)
    {
        this.tag = tag;
        return this;
    }


    @Override
    public OtherRequestBuilder headers(Map<String, String> headers)
    {
        this.headers = headers;
        return this;
    }

    @Override
    public OtherRequestBuilder addHeader(String key, String val)
    {
        if (this.headers == null)
        {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return this;
    }
}
