package com.licheedev.serialtool.comn.core;

import androidx.annotation.Nullable;

interface WaitRoom<S, R> {

    @Nullable
    R getResponse(long timeout);

    void putResponse(byte[] recv);

    boolean isMyResponse(S send, R recv);
}
