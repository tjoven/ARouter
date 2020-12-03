package com.example.base;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 干掉LiveData的黏性事假
 * @param <T>
 */
public class BusLivaData<T> extends MutableLiveData<T> {

    private boolean mIsStivk;
    public void observe(@NonNull LifecycleOwner owner, boolean isStivk ,@NonNull Observer<? super T> observer) {
        this.mIsStivk = isStivk;
        observe(owner,observer);
    }

    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner,observer);
        if(!mIsStivk){
            //不需要粘性
            //必须要先观察，后发送消息
            try {
                hook(observer);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改 ObserverWrapper.mLastVersion = mVersion
     * @param observer
     */
    private void hook(Observer<? super T> observer) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //find mVersion
        Class liveDataClass = LiveData.class;
        Field versionField = liveDataClass.getDeclaredField("mVersion");
        versionField.setAccessible(true);
        Object version = versionField.get(this);

        //find mObservers
        Field  fieldObservers = liveDataClass.getDeclaredField("mObservers");
        fieldObservers.setAccessible(true);
        Object observers = fieldObservers.get(this);

        //find LifecycleBoundObserver
        Class classObserve = observers.getClass();
        Method methodObserve = classObserve.getDeclaredMethod("get",Object.class);
        methodObserve.setAccessible(true);
        Object observeEntry = methodObserve.invoke(observers,observer);
        if(observeEntry instanceof Map.Entry){
            Object lifecycleBoundObserver = ((Map.Entry)observeEntry).getValue();

            //find mLastVersion
            Class classObserverWrapper = lifecycleBoundObserver.getClass().getSuperclass();
            Field fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion");
            fieldLastVersion.setAccessible(true);


            Object ObserLastVersion = fieldLastVersion.get(lifecycleBoundObserver);
            Log.d("tzw","LastVersion: "+ObserLastVersion.toString());
            Log.d("tzw","version: "+version.toString());
            fieldLastVersion.set(lifecycleBoundObserver,version);
            Log.d("tzw","LastVersion: "+ fieldLastVersion.get(lifecycleBoundObserver).toString());
        }
    }
}
