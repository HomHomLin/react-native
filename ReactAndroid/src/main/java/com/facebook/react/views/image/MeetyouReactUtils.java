package com.facebook.react.views.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Linhh on 17/3/7.
 */

public class MeetyouReactUtils {
    private final static HashMap<String, Drawable> mDrawableMap = new HashMap<>();

    public static Map<String, String> convertMap(ReadableMap writableMap){
        if(writableMap == null) {
            return null;
        } else {
            HashMap<String, Object> hashMap = ((ReadableNativeMap)writableMap).toHashMap();
            Map<String,String> map = new HashMap<>();
            Iterator<Map.Entry<String, Object>> it = hashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                map.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
            return map;
        }
    }

    public static String assembleFilePath(Uri uri) {
        if (uri != null && uri.getPath() != null) {
            return uri.getPath().replaceFirst("/", "");
        }
        return "";
    }

    /**
     * 获取路劲,去掉其他无用信息
     * @param source
     * @return
     */
    public static String spliteSource(String source){
        String[] s = source.split("\\?");
        return s[0];
    }

    public static String getCacheRootPath(Context context){
        File cacheDir = context.getExternalCacheDir() != null ? context.getExternalCacheDir() : context.getCacheDir();
        return cacheDir.getAbsolutePath();
    }

    public static String getCachePath(Context context, String source) throws Exception{
        File cache_file = context.getCacheDir();
        File js_dir = new File(cache_file.getAbsoluteFile() + "/meetyou_react");
        if(!js_dir.exists()){
            js_dir.mkdir();
        }
        return js_dir.getAbsolutePath() + "/" + base64(source);
    }

    public static String makeSHA1HashBase64(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(bytes, 0, bytes.length);
            byte[] sha1hash = md.digest();
            return Base64.encodeToString(sha1hash, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * create secureHashKey
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String base64(final String key) throws UnsupportedEncodingException {
        return makeSHA1HashBase64(key.getBytes("UTF-8"));
    }

    public static String safeBase64(final String key){
        try {
            return base64(key);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return key;
    }

    public static Drawable getDrawable(Context context, String path){
        try {
            String key = safeBase64(path);
            Drawable drawable = mDrawableMap.get(key);
            if (drawable != null) {
                return drawable;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            if (null != bitmap) {
                drawable = new BitmapDrawable(context.getResources(), bitmap);
                mDrawableMap.put(key, drawable);
            }
            return drawable;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
