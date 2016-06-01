package com.rock.android.okhttpnetworkmanager.response;

import okhttp3.ResponseBody;

/**
 * Created by rock on 16/5/31.
 */
public class LocalResponse {

    public String responseStr;
    public ResponseBody responseBody;

    public LocalResponse(String responseStr, ResponseBody responseBody) {
        this.responseStr = responseStr;
        this.responseBody = responseBody;
    }
}
