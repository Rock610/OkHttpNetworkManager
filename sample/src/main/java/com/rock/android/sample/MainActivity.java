package com.rock.android.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.rock.android.okhttpnetworkmanager.NetWorkManager;
import com.rock.android.okhttpnetworkmanager.cache.DiskLruCacheManager;
import com.rock.android.okhttpnetworkmanager.callback.CallBack;
import com.rock.android.okhttpnetworkmanager.response.LocalResponse;

import okhttp3.Call;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetWorkManager.initCacheManager(new DiskLruCacheManager(this.getApplicationContext().getCacheDir()));

//        ACacheManager manager = new ACacheManager(this.getApplicationContext());
//        NetWorkManager.initCacheManager(manager);

        NetWorkManager
                .get()
                .tag("mainactivity")
                .url("http://gank.io/api/data/Android/10/1")
                .buildRequest()
                .saveCache(true)
                .cacheMode(NetWorkManager.READ_CACHE)
                .cacheKeyAsUrl()
                .expire(20)
                .build()
                .execute(new CallBack<Android>() {
                    @Override
                    public Android parseNetworkResponse(LocalResponse response) throws Exception {
                        Android android = JSON.parseObject(response.responseStr,Android.class);
                        return android;
                    }
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(Android response) {
                        System.out.println("res=====>"+response.results.get(0).url);
                    }
                });



//        NetWorkManager
//                .get()
//                .url("http://image.tianjimedia.com/uploadImages/2012/012/2YXG0J416V69.jpg")
//                .buildRequest()
//                .build()
//                .execute(new FileCallBack(this.getApplicationContext().getCacheDir().getAbsolutePath(),"gank.jpg") {
//                    @Override
//                    public void inProgress(float progress, long total) {
//                        System.out.println("total=====>"+total);
//                        System.out.println("progress====>"+progress);
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(File response) {
//
//                        if(response == null){
//                            System.out.println("文件为空");
//                            return;
//                        }
//                        System.out.println("path=====>"+response.getAbsolutePath());
//                    }
//                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetWorkManager.cancelCallsWithTag("mainactivity");
    }

}
