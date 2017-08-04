package com.samsung.sds.mhs.android;

import android.app.Activity;
import android.icu.util.Output;
import android.os.Handler;
import android.util.Log;
import com.samsung.sds.mhs.android.BpEventListener;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by jw7979.lee on 2017-06-01.
 */

public class BPAlgorithm implements Runnable {
    private static final String TAG = "BPAlgorithm";
    private static final int BUFFER_SIZE = 256;
    private static final short PROCESS_SIZE = 20;

    private int[] ppgData;
    private int headIdx;
    private int tailIdx;
    private int dataNum;

    private short[] sbp;
    private short[] dbp;
    private short[] hr;
    private double[] feat1;
    private double[] feat2;
    private double[] feat3;
    private double[] feat4;
    private char reset;

    private Activity mainActivity;
    private BpEventListener bpListener;
    private Writer mWriter;
    Object dataLock = new Object();


    static {
        System.loadLibrary("bp_estimate");
    }

    public native char estimateBP(int[] data, short num_data, short[] sbp, short[] dbp, short[] hr, double[] feat1, double[] feat2, double[] feat3, double[] feat4, char reset);

    public BPAlgorithm(Activity activity) {
        ppgData = new int[BUFFER_SIZE];
        sbp = new short[1];
        dbp = new short[1];
        hr = new short[1];
        feat1 = new double[1];
        feat2 = new double[1];
        feat3 = new double[1];
        feat4 = new double[1];

        resetFeature();
        resetReq();

        mainActivity = activity;
        new Thread(this).start();
    }

    public void setFeat1(double feat) { feat1[0] = feat; }
    public void setFeat2(double feat) { feat2[0] = feat; }
    public void setFeat3(double feat) { feat3[0] = feat; }
    public void setFeat4(double feat) { feat4[0] = feat; }

    public void resetFeature() {
        feat1[0] = 0.0;
        feat2[0] = 0.0;
        feat3[0] = 0.0;
        feat4[0] = 0.0;
    }

    //added by me
    public void setFeature(double f1, double f2, double f3, double f4) {
        feat1[0] = f1;
        feat2[0] = f2;
        feat3[0] = f3;
        feat4[0] = f4;
        reset = 1;
    }

    public void resetReq() {
        reset = 1;
        headIdx = 0;
        tailIdx = 0;
        dataNum = 0;
    }

    public void pushData(int data)
    {
        Log.d("pushTest","value : "+ data);
        if (dataNum < BUFFER_SIZE) {
            ppgData[headIdx++] = data;
            if (headIdx == BUFFER_SIZE) headIdx = 0;
            dataNum++;

            synchronized (dataLock) {
                dataLock.notifyAll();
            }
        }
        else
        {
            Log.e(TAG, "BUFFER OVERFLOW");
        }
    }

    public void setWriter(Writer writer) {
        mWriter = writer;
    }


    public void setBpChangedListener(BpEventListener listener) {
        bpListener = listener;
    }

    public void run() {
        char ret;
        double orgFeat1;
        char count;
        int progress;

        while (true) {
            try {
                synchronized (dataLock) {
                    while (dataNum < PROCESS_SIZE) {
                        try {
                            dataLock.wait();
                        } catch (InterruptedException e) {
                            Log.e(TAG, "InterruptedException\nMSG: " + e.getMessage() + "\n" + e.toString());
                        }
                    }
                }

                int procData[] = new int[PROCESS_SIZE];
                for (int i = 0; i < PROCESS_SIZE; i++) {
                    procData[i] = ppgData[tailIdx++];
                    if (tailIdx == BUFFER_SIZE) tailIdx = 0;
                }
                dataNum -= PROCESS_SIZE;

                orgFeat1 = feat1[0];
                if (reset == 1) progress = 0;
                ret = estimateBP(procData, PROCESS_SIZE, sbp, dbp, hr, feat1, feat2, feat3, feat4, reset);
                reset = 0;

                if (orgFeat1 != feat1[0]) {
                    if (bpListener != null) {
                        mainActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                bpListener.onCalDone(feat1[0], feat2[0], feat3[0], feat4[0]);
                            }
                        });
                    }
                }

                if (ret == 1) {
                    Log.d(TAG, "ret: " + (int)ret + ", sbp: " + sbp[0] + ", dbp: " + dbp[0]);

                    if (bpListener != null) {
                        mainActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                bpListener.onBpChanged(sbp[0], dbp[0], hr[0]);
                            }
                        });
                    }
                }


                // Save to file
                if (mWriter != null) {
                    try {
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < PROCESS_SIZE; i++) {
                            if ((i == PROCESS_SIZE - 1) && (ret == 1)) {
                                sb.append(procData[i] + "," + sbp[0] +"," + dbp[0] + "," + hr[0] + "\n");
                            } else {
                                sb.append(procData[i] + ",0,0,0\n");
                            }
                        }

                        mWriter.write(sb.toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "THREAD EXCEPTION");
                e.printStackTrace();
            }
        }
    }
}
