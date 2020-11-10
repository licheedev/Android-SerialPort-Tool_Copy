package com.licheedev.serialtool.comn;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * General Callback
 *
 * @param <T>
 */
public interface Callback<T> {

    void onSuccess(@Nullable T t);

    void onFailure(@NonNull Throwable tr);
}
