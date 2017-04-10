# OkHttpNetworkManager

OkHttpNetworkManager

### 在[hongyangAndroid](https://github.com/hongyangAndroid)的[okhttp-utils](https://github.com/hongyangAndroid/okhttp-utils)的基础上改造而来的OkHttp封装库

## 增加了缓存任何请求的能力
```
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

```
添加缓存必须在```buildRequest()```之后

__cacheMode__
* NO_CACHE 不取缓存，强制请求网络
* READ_CACHE 强制只取缓存
* READ_AND_REFRESH_CACHE 先取缓存，同时请求网络来刷新数据

## 配置缓存网络数据
```
saveCache(true)
```
## THANKS
[hongyangAndroid](https://github.com/hongyangAndroid)

[okhttp-utils](http://blog.csdn.net/lmj623565791/article/details/47911083)
