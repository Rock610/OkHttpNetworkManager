package com.rock.android.okhttpnetworkmanager.request;


import com.rock.android.okhttpnetworkmanager.NetWorkManager;
import com.rock.android.okhttpnetworkmanager.callback.CallBack;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by zhy on 15/11/6.
 */
public abstract class OkHttpRequest {
    protected String url;
    protected Object tag;
    protected Map<String, String> params;
    protected Map<String, String> headers;

    public int cacheMode = NetWorkManager.NO_CACHE;
    public boolean isSaveCache;
    public String cacheKey;
    public int expire;

    protected Request.Builder builder = new Request.Builder();

    protected OkHttpRequest(String url, Object tag,
                            Map<String, String> params, Map<String, String> headers) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;

        if (url == null) {
//            Exceptions.illegalArgument("url can not be null.");
        }

        initBuilder();
    }

    public OkHttpRequest expire(int expire) {
        this.expire = expire;
        return this;
    }

    public OkHttpRequest cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return this;
    }

    public OkHttpRequest cacheKeyAsUrl() {
        this.cacheKey = url;
        return this;
    }

    public OkHttpRequest cacheMode(int cacheMode) {
        this.cacheMode = cacheMode;
        return this;
    }

    public OkHttpRequest saveCache(boolean saveCache) {
        isSaveCache = saveCache;
        return this;
    }


    /**
     * 初始化一些基本参数 url , tag , headers
     */
    private void initBuilder() {
        builder.url(url).tag(tag);
        appendHeaders();
    }

    protected abstract RequestBody buildRequestBody();

    protected RequestBody wrapRequestBody(RequestBody requestBody, final CallBack callback) {
        return requestBody;
    }

    protected abstract Request buildRequest(RequestBody requestBody);

    public RequestCall build() {
        return new RequestCall(this);
    }


    public Request generateRequest(CallBack callback) {
        RequestBody requestBody = buildRequestBody();
        RequestBody wrappedRequestBody = wrapRequestBody(requestBody, callback);
        Request request = buildRequest(wrappedRequestBody);
        return request;
    }


    protected void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

}
