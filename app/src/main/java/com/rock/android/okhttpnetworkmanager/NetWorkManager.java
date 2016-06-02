package com.rock.android.okhttpnetworkmanager;

import android.os.Handler;
import android.os.Looper;

import com.rock.android.okhttpnetworkmanager.builder.GetBuilder;
import com.rock.android.okhttpnetworkmanager.builder.OtherRequestBuilder;
import com.rock.android.okhttpnetworkmanager.builder.PostFileBuilder;
import com.rock.android.okhttpnetworkmanager.builder.PostFormBuilder;
import com.rock.android.okhttpnetworkmanager.builder.PostStringBuilder;
import com.rock.android.okhttpnetworkmanager.cache.ICacheManager;
import com.rock.android.okhttpnetworkmanager.callback.CallBack;
import com.rock.android.okhttpnetworkmanager.callback.FileCallBack;
import com.rock.android.okhttpnetworkmanager.request.RequestCall;
import com.rock.android.okhttpnetworkmanager.response.LocalResponse;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by rock on 16/5/5.
 */
public class NetWorkManager {
    public static final long DEFAULT_MILLISECONDS = 20000;
    public static final int NO_CACHE = 1;
    public static final int READ_CACHE = 2;
    public static final int READ_AND_REFRESH_CACHE = 3;


    private OkHttpClient client;
    public static ICacheManager cacheManager;

    public NetWorkManager() {
        buildClient(null);
    }

    public static void initCacheManager(ICacheManager cacheManager) {
        NetWorkManager.cacheManager = cacheManager;
    }

    protected void buildClient(OkHttpClient client) {
        if (client != null) {
            this.client = client;
        } else {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.SECONDS);
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                httpClientBuilder.sslSocketFactory(sc.getSocketFactory());
                httpClientBuilder.hostnameVerifier(hostnameVerifier);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.client = httpClientBuilder.build();
        }
    }

    HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    /* Create a new array with room for an additional trusted certificate. */
                    X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                    return myTrustedAnchors;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
    };


    public OkHttpClient getClient() {
        return client;
    }

    private static class InstanceHolder {
        private static final NetWorkManager MANAGER = new NetWorkManager();
    }

    public static NetWorkManager getInstance() {
        return InstanceHolder.MANAGER;
    }

    public static Handler HANDLER = new Handler(Looper.getMainLooper());


    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }

    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    public static OtherRequestBuilder put() {
        return new OtherRequestBuilder(METHOD.PUT);
    }


    public static OtherRequestBuilder delete() {
        return new OtherRequestBuilder(METHOD.DELETE);
    }

    private void requestNet(final RequestCall requestCall, CallBack<?> callBack) {

        if (callBack == null)
            callBack = CallBack.CALLBACK_DEFAULT;
        final CallBack finalCallback = callBack;

        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                sendFailedCallBack(call, e, finalCallback);
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                try {
                    MediaType mediaType = response.body().contentType();
                    Response newResponse;
                    Object result;
                    String resStr = "";
                    if(finalCallback instanceof FileCallBack){
                        //如果是文件那么以byte[]创建新的response
                        newResponse = response.newBuilder().body(ResponseBody.create(mediaType, response.body().bytes())).build();
                    }else{
                        resStr = response.body().string();
                        newResponse = response.newBuilder().body(ResponseBody.create(mediaType, resStr)).build();

                        if (requestCall.getOkHttpRequest().isSaveCache) {
                            if (NetWorkManager.cacheManager != null) {
                                System.out.println("saving cache");
                                NetWorkManager.cacheManager.put(requestCall.getOkHttpRequest().cacheKey, resStr, requestCall.getOkHttpRequest().expire);
                            }
                        }
                    }
                    result = finalCallback.parseNetworkResponse(new LocalResponse(resStr, newResponse.body()));
                    sendSuccessCallBack(result, finalCallback);
                } catch (Exception e) {
                    sendFailedCallBack(call, e, finalCallback);

                }

            }
        });

    }

    public void execute(final RequestCall call, final CallBack<?> callBack) {
        requestNet(call, callBack);
    }


    private void sendSuccessCallBack(final Object o, final CallBack callBack) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                callBack.onResponse(o);
            }
        });

    }

    private void sendFailedCallBack(final Call call, final Exception e, final CallBack callBack) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                callBack.onError(call, e);
            }
        });
    }

    public static final class METHOD {
        public static final String PUT = "PUT";
        public static final String DELETE = "DELETE";
    }

    public static void cancelCallsWithTag(Object tag) {

        if (tag == null) {
            return;
        }

        OkHttpClient mClient = NetWorkManager.getInstance().getClient();
        synchronized (mClient.dispatcher().getClass()) {
            for (Call call : mClient.dispatcher().queuedCalls()) {
                String tagStr = (String) call.request().tag();
                System.out.println("queued tag=======>" + tagStr);
                if (tag.equals(tagStr)) {
                    System.out.println("hit cancel");
                    call.cancel();
                }
            }

            for (Call call : mClient.dispatcher().runningCalls()) {
                String tagStr = (String) call.request().tag();
                System.out.println("running tag=======>" + tagStr);
                if (tag.equals(tagStr)) {
                    System.out.println("hit cancel");
                    call.cancel();
                }
            }
        }
    }

}
