package com.rock.android.okhttpnetworkmanager.callback;

import com.rock.android.okhttpnetworkmanager.response.LocalResponse;

import okhttp3.Call;
import okhttp3.Request;

public abstract class CallBack<T> {
    /**
     * UI Thread
     *
     * @param request
     */
    public void onBefore(Request request) {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter() {
    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress) {

    }

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(LocalResponse response) throws Exception;

    public abstract void onError(Call call, Exception e);

    public abstract void onResponse(T response);


    public static CallBack CALLBACK_DEFAULT = new CallBack() {

        @Override
        public Object parseNetworkResponse(LocalResponse response) throws Exception {
            return null;
        }

        @Override
        public void onError(Call call, Exception e) {

        }

        @Override
        public void onResponse(Object response) {

        }
    };

}