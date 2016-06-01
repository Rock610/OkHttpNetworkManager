package com.rock.android.okhttpnetworkmanager.cache;

import com.rock.android.okhttpnetworkmanager.SerializationUtils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.internal.DiskLruCache;
import okhttp3.internal.io.FileSystem;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * Created by mrrock on 16/5/15.
 */
public class DiskLruCacheManager implements ICacheManager {

    private DiskLruCache mCache;

    public DiskLruCacheManager(File dir) {
        mCache = DiskLruCache.create(FileSystem.SYSTEM, dir, 1, 1, 5 * 1024 * 1024);
    }

    @Override
    public void put(String key, String value, int expire) {

        key = hashKeyForDisk(key);
        CacheBean cacheBean = new CacheBean();
        cacheBean.expire = System.currentTimeMillis() + (expire * 1000);
        cacheBean.object = value;
        System.out.println("put cache====>" + value);
        try {
            DiskLruCache.Editor editor = mCache.edit(key);
            Sink sink = editor.newSink(0);
            BufferedSink bufferedSink = Okio.buffer(sink);
            SerializationUtils.serialize(cacheBean, bufferedSink.outputStream());
            editor.commit();
            bufferedSink.close();
            mCache.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String get(String key) {
        String result;
        key = hashKeyForDisk(key);
        try {
            DiskLruCache.Snapshot snapshot = mCache.get(key);
            if (snapshot == null) return null;
            Source source = snapshot.getSource(0);
            BufferedSource bufferedSource = Okio.buffer(source);
            CacheBean cacheBean = SerializationUtils.deserialize(bufferedSource.readByteArray());
            long expire = cacheBean.expire;
            if (expire > System.currentTimeMillis()) {
                System.out.println("cache not expired");
                result = cacheBean.object;
            } else {
                System.out.println("cache expired");
                result = null;
            }
            bufferedSource.close();
            System.out.println("read cache====>" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public boolean isHit(String key) {
        return false;
    }

    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
