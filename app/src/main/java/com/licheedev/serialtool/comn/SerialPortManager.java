package com.licheedev.serialtool.comn;

import com.licheedev.hwutils.ByteUtil;
import com.licheedev.serialtool.comn.core.SerialWorker;
import com.licheedev.serialtool.comn.message.LogManager;
import com.licheedev.serialtool.comn.message.RecvMessage;
import com.licheedev.serialtool.comn.message.SendMessage;
import com.licheedev.serialtool.util.rx.EmptyObserver;
import com.licheedev.serialtool.util.rx.RxUtil;
import java.io.File;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2017/3/28 0028.
 */
public class SerialPortManager {

    private static final String TAG = "SerialPortManager";

    private static class InstanceHolder {

        public static SerialPortManager sManager = new SerialPortManager();
    }

    public static SerialPortManager instance() {
        return InstanceHolder.sManager;
    }

    private SerialWorker mSerialWorker;

    private SerialPortManager() {
        mSerialWorker = new SerialWorker();
    }

    /**
     * 打开串口
     *
     * @param device
     * @return open result, true:success
     */
    public boolean open(Device device) {
        try {
            mSerialWorker.open(new File(device.getPath()), Integer.parseInt(device.getBaudrate()));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void open(Device device, Callback<Void> callback) {

        RxUtil.getRxObservable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                mSerialWorker.open(new File(device.getPath()),
                    Integer.parseInt(device.getBaudrate()));
                return true;
            }
        }).compose(RxUtil.rxIoMain()).subscribe(new EmptyObserver<Object>() {

            @Override
            public void onNext(Object o) {
                callback.onSuccess(null);
            }

            @Override
            public void onError(Throwable e) {
                callback.onFailure(e);
            }
        });
    }

    /**
     * 关闭串口
     */
    public void close() {
        RxUtil.getRxObservable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                mSerialWorker.close();
                return true;
            }
        }).compose(RxUtil.rxIoMain()).subscribe(new EmptyObserver<>());
    }

    /**
     * 发送命令包
     */
    public void sendCommand(final byte[] command, Callback<byte[]> callback) {

        LogManager.instance().post(new SendMessage(ByteUtil.bytes2HexStr(command)));

        RxUtil.getRxObservable(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {

                byte[] recv = mSerialWorker.send(command);

                if (recv == null) {
                    throw new RuntimeException("receive no data");
                }
                return recv;
            }
        }).compose(RxUtil.rxIoMain()).subscribe(new EmptyObserver<byte[]>() {

            @Override
            public void onNext(byte[] recv) {

                String hexStr = ByteUtil.bytes2HexStr(recv);
                LogManager.instance().post(new RecvMessage(hexStr));

                callback.onSuccess(recv);
            }

            @Override
            public void onError(Throwable e) {
                callback.onFailure(e);
            }
        });
    }

    public void sendCommand(final String command, Callback<byte[]> callback) {
        sendCommand(ByteUtil.hexStr2bytes(command), callback);
    }
}
