package com.rock.android.okhttpnetworkmanager.request;


import android.text.TextUtils;

import com.rock.android.okhttpnetworkmanager.NetWorkManager;
import com.rock.android.okhttpnetworkmanager.callback.CallBack;
import com.rock.android.okhttpnetworkmanager.response.LocalResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zhy on 15/12/15.
 * 对OkHttpRequest的封装，对外提供更多的接口：cancel(),readTimeOut()...
 */
public class RequestCall {
    private OkHttpRequest okHttpRequest;
    private Request request;
    private Call call;

    private long readTimeOut;
    private long writeTimeOut;
    private long connTimeOut;
    private OkHttpClient clone;


    public RequestCall(OkHttpRequest request) {
        this.okHttpRequest = request;
    }

    public RequestCall readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return this;
    }

    public RequestCall writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return this;
    }

    public RequestCall connTimeOut(long connTimeOut) {
        this.connTimeOut = connTimeOut;
        return this;
    }

    public Call buildCall(CallBack callback) {
        request = generateRequest(callback);

        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {
            readTimeOut = readTimeOut > 0 ? readTimeOut : NetWorkManager.DEFAULT_MILLISECONDS;
            writeTimeOut = writeTimeOut > 0 ? writeTimeOut : NetWorkManager.DEFAULT_MILLISECONDS;
            connTimeOut = connTimeOut > 0 ? connTimeOut : NetWorkManager.DEFAULT_MILLISECONDS;

            clone = NetWorkManager.getInstance().getClient().newBuilder()
                    .readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                    .connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                    .build();

            call = clone.newCall(request);
        } else {
            call = NetWorkManager.getInstance().getClient().newCall(request);
        }
        return call;
    }

    private Request generateRequest(CallBack callback) {
        return okHttpRequest.generateRequest(callback);
    }

    public RequestCall execute(CallBack callback) {

        readCache(callback);
        return this;

    }

    private void executeNet(CallBack callback) {
        buildCall(callback);

        if (callback != null) {
            callback.onBefore(request);
        }

        NetWorkManager.getInstance().execute(this, callback);
    }

    private void readCache(final CallBack callback) {
        if (NetWorkManager.cacheManager != null && !TextUtils.isEmpty(okHttpRequest.cacheKey)) {

            switch (okHttpRequest.cacheMode) {
                case NetWorkManager.READ_CACHE:
                case NetWorkManager.READ_AND_REFRESH_CACHE:
                    Observable.create(new Observable.OnSubscribe<Object>() {
                        @Override
                        public void call(Subscriber<? super Object> subscriber) {
                            //okio线程不安全,所以通过在子线程读取以及回调的方式获得缓存数据
                            String s = NetWorkManager.cacheManager.get(okHttpRequest.cacheKey);

                            Object o = null;
                            if (!TextUtils.isEmpty(s)) {
                                LocalResponse localResponse = new LocalResponse(s,null);
                                try {
                                    o = callback.parseNetworkResponse(localResponse);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else {
                                System.out.println("no-cache-hit");
                            }
                            subscriber.onNext(o);
                        }
                    })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Object>() {
                                @Override
                                public void call(Object o) {
                                    if (o != null) {
                                        callback.onResponse(o);
                                        System.out.println("cache-hit");
                                        if (okHttpRequest.cacheMode == NetWorkManager.READ_CACHE) {
                                            System.out.println("cache-mode --- only read cache");
                                            return;
                                        } else {
                                            System.out.println("cache-mode --- refresh cache");
                                        }
                                    }
                                    executeNet(callback);

                                }
                            });
                    break;
                default:
                    executeNet(callback);
                    break;
            }

        } else {
            executeNet(callback);
        }

    }

    public Call getCall() {
        return call;
    }

    public Request getRequest() {
        return request;
    }

    public OkHttpRequest getOkHttpRequest() {
        return okHttpRequest;
    }

    public Response execute() throws IOException {
        buildCall(null);
        return call.execute();
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }


}
