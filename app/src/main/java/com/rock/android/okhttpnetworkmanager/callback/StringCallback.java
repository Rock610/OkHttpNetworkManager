package com.rock.android.okhttpnetworkmanager.callback;

import com.rock.android.okhttpnetworkmanager.response.LocalResponse;

import java.io.IOException;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class StringCallback extends CallBack<String>
{
    @Override
    public String parseNetworkResponse(LocalResponse response) throws IOException
    {
        return response.responseBody.string();
    }

}
