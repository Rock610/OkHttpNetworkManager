package com.rock.android.okhttpnetworkmanager.builder;

import com.rock.android.okhttpnetworkmanager.request.OkHttpRequest;
import com.rock.android.okhttpnetworkmanager.request.PostFileRequest;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.MediaType;

/**
 * Created by zhy on 15/12/14.
 */
public class PostFileBuilder extends OkHttpRequestBuilder
{
    private File file;
    private MediaType mediaType;


    public OkHttpRequestBuilder file(File file)
    {
        this.file = file;
        return this;
    }

    public OkHttpRequestBuilder mediaType(MediaType mediaType)
    {
        this.mediaType = mediaType;
        return this;
    }

    @Override
    public OkHttpRequest buildRequest()
    {
        return new PostFileRequest(url, tag, params, headers, file, mediaType);
    }

    @Override
    public PostFileBuilder url(String url)
    {
        this.url = url;
        return this;
    }

    @Override
    public PostFileBuilder tag(Object tag)
    {
        this.tag = tag;
        return this;
    }

    @Override
    public PostFileBuilder headers(Map<String, String> headers)
    {
        this.headers = headers;
        return this;
    }

    @Override
    public PostFileBuilder addHeader(String key, String val)
    {
        if (this.headers == null)
        {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return this;
    }
}
