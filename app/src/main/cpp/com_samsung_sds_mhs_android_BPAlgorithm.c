//
// modified by MHL 2017.5.29
//
#include <stdlib.h>
#include <unistd.h>
#include <float.h>
#include <math.h>
#include "com_samsung_sds_mhs_android_BPAlgorithm.h"
#include <android/log.h>

extern char EstimateBP(int *data, unsigned short num_data, unsigned short *sbp, unsigned short *dbp, unsigned short *hr, double *feat1, double *feat2, double *feat3, double *feat4, char reset, char *bcounter);
static double repPulse[1024];
static int repPulseSize;

JNIEXPORT char JNICALL Java_com_samsung_sds_mhs_android_BPAlgorithm_estimateBP
  (JNIEnv *env, jobject obj, jintArray data,jshort num_data,jshortArray sbp, jshortArray dbp, jshortArray hr, jdoubleArray feat1, jdoubleArray feat2, jdoubleArray feat3, jdoubleArray feat4, jchar reset, jbyteArray count) {
        //TODO: change data type
        jint *pdata = (*env)->GetIntArrayElements(env,data,0);
        short* pHR;
        pHR = (short *)malloc(sizeof(short) * 1); // output
        short* pSBP;
        pSBP = (short *)malloc(sizeof(short) * 1); // output
        short* pDBP;
        pDBP = (short *)malloc(sizeof(short) * 1); // output
        double* pFeat1;
        pFeat1 = (double *)malloc(sizeof(double) * 1); // output
        double* pFeat2;
        pFeat2 = (double *)malloc(sizeof(double) * 1); // output
        double* pFeat3;
        pFeat3 = (double *)malloc(sizeof(double) * 1); // output
        double* pFeat4;
        pFeat4 = (double *)malloc(sizeof(double) * 1); // output
        unsigned char* pCount;
        pCount = (unsigned char *)malloc(sizeof(unsigned char) * 1);

        *pFeat1 = *((*env)->GetDoubleArrayElements(env, feat1, NULL));
        *pFeat2 = *((*env)->GetDoubleArrayElements(env, feat2, NULL));
        *pFeat3 = *((*env)->GetDoubleArrayElements(env, feat3, NULL));
        *pFeat4 = *((*env)->GetDoubleArrayElements(env, feat4, NULL));
        *pCount = *((*env)->GetByteArrayElements(env, count, NULL));

        //__android_log_print(ANDROID_LOG_ERROR, "BPALGORITHM", "feat1: %lf, reset: %d", *pFeat1, reset);
        char ret = EstimateBP(pdata,num_data,pSBP,pDBP,pHR, pFeat1, pFeat2, pFeat3, pFeat4, reset, pCount);
	    //__android_log_print(ANDROID_LOG_ERROR, "BPALGORITHM", "after: %d", *pCount);

        (*env)->ReleaseIntArrayElements(env,data,pdata, 0);
        //return value
        (*env)->SetShortArrayRegion(env,sbp, 0, 1, (const jshort*)pSBP); // output
        (*env)->SetShortArrayRegion(env,dbp, 0, 1, (const jshort*)pDBP); // output
        (*env)->SetShortArrayRegion(env,hr, 0, 1, (const jshort*)pHR); // output
        (*env)->SetDoubleArrayRegion(env,feat1, 0, 1, (const jdouble*)pFeat1); // output
        (*env)->SetDoubleArrayRegion(env,feat2, 0, 1, (const jdouble*)pFeat2); // output
        (*env)->SetDoubleArrayRegion(env,feat3, 0, 1, (const jdouble*)pFeat3); // output
        (*env)->SetDoubleArrayRegion(env,feat4, 0, 1, (const jdouble*)pFeat4); // output
        (*env)->SetByteArrayRegion(env,count, 0, 1, (const jbyte*)pCount); // output

        free(pSBP);
        free(pDBP);
        free(pHR);
        free(pFeat1);
        free(pFeat2);
        free(pFeat3);
        free(pFeat4);
        free(pCount);

        return ret;
}


