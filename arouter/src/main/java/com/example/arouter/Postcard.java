package com.example.arouter;

import android.os.Bundle;
import android.os.Parcelable;

import com.example.annotation.RouteMeta;

import java.util.ArrayList;

/**
 * Author: 夏胜明
 * Date: 2018/4/24 0024
 * Email: xiasem@163.com
 * Description:
 */

public class Postcard extends RouteMeta {
    private Bundle mBundle;
    private int flags = -1;
    //新版风格
    private Bundle optionsCompat;
    //老版
    private int enterAnim;
    private int exitAnim;


    public Postcard(String path, String group) {
        this(path, group, null);
    }

    public Postcard(String path, String group, Bundle bundle) {
        setPath(path);
        setGroup(group);
        this.mBundle = (null == bundle ? new Bundle() : bundle);
    }

    public Bundle getExtras() {return mBundle;}



    /**
     * Intent.FLAG_ACTIVITY**
     * @param flag
     * @return
     */
    public Postcard withFlags(int flag) {
        this.flags = flag;
        return this;
    }

    public int getFlags() {
        return flags;
    }

    public Postcard withString( String key,  String value) {
        mBundle.putString(key, value);
        return this;
    }


    public Postcard withBoolean( String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }


    public Postcard withShort( String key, short value) {
        mBundle.putShort(key, value);
        return this;
    }


    public Postcard withInt( String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }


    public Postcard withLong( String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }


    public Postcard withDouble( String key, double value) {
        mBundle.putDouble(key, value);
        return this;
    }


    public Postcard withByte( String key, byte value) {
        mBundle.putByte(key, value);
        return this;
    }


    public Postcard withChar( String key, char value) {
        mBundle.putChar(key, value);
        return this;
    }


    public Postcard withFloat( String key, float value) {
        mBundle.putFloat(key, value);
        return this;
    }


    public Postcard withParcelable( String key,  Parcelable value) {
        mBundle.putParcelable(key, value);
        return this;
    }


    public Postcard withStringArray( String key,  String[] value) {
        mBundle.putStringArray(key, value);
        return this;
    }


    public Postcard withBooleanArray( String key, boolean[] value) {
        mBundle.putBooleanArray(key, value);
        return this;
    }


    public Postcard withShortArray( String key, short[] value) {
        mBundle.putShortArray(key, value);
        return this;
    }


    public Postcard withIntArray( String key, int[] value) {
        mBundle.putIntArray(key, value);
        return this;
    }


    public Postcard withLongArray( String key, long[] value) {
        mBundle.putLongArray(key, value);
        return this;
    }


    public Postcard withDoubleArray( String key, double[] value) {
        mBundle.putDoubleArray(key, value);
        return this;
    }


    public Postcard withByteArray( String key, byte[] value) {
        mBundle.putByteArray(key, value);
        return this;
    }


    public Postcard withCharArray( String key, char[] value) {
        mBundle.putCharArray(key, value);
        return this;
    }


    public Postcard withFloatArray( String key, float[] value) {
        mBundle.putFloatArray(key, value);
        return this;
    }


    public Postcard withParcelableArray( String key,  Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
        return this;
    }

    public Postcard withParcelableArrayList( String key,  ArrayList<? extends
            Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
        return this;
    }

    public Postcard withIntegerArrayList( String key,  ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
        return this;
    }

    public Postcard withStringArrayList( String key,  ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
        return this;
    }

    public Bundle getOptionsBundle() {
        return optionsCompat;
    }


    public Object navigation() {
        return Router.getsInstance().navigation(null, this, -1, null);
    }


}
