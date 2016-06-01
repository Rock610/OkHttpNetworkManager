package com.rock.android.okhttpnetworkmanager.cache;

/**
 * Created by rock on 16/5/6.
 */
public interface ICacheManager {

    void put(String key, String value, int expire);
    String get(String key);

    boolean contains(String key);

    boolean isHit(String key);
}
