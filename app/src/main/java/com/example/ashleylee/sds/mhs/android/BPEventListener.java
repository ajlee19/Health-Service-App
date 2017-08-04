package com.samsung.sds.mhs.android;

/**
 * Created by jw7979.lee on 2017-06-01.
 */

public interface BpEventListener {
    void onBpChanged(short sbp, short dbp, short hr);
    void onCalDone(double feat1, double feat2, double feat3, double feat4);
}
