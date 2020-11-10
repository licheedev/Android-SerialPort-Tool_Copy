package com.licheedev.serialtool.comn.core;

import androidx.annotation.Nullable;

class SampleWaitRoom implements WaitRoom<byte[], byte[]> {

    private final byte[] mSend;
    private byte[] mResponse;

    public SampleWaitRoom(byte[] send) {
        mSend = send;
    }

    @Nullable
    @Override
    public synchronized byte[] getResponse(long timeout) {
        try {
            wait(timeout);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // reset isInterrupted flag
        }
        return mResponse;
    }

    @Override
    public synchronized void putResponse(byte[] recv) {
        if (isMyResponse(mSend, recv) && mResponse == null) {
            mResponse = recv;
            notifyAll();
        }
    }

    @Override
    public boolean isMyResponse(byte[] send, byte[] recv) {
        // TODO: please custom this rule
        return true; // all true, every `recv` data is the response of `send` 
    }
}
