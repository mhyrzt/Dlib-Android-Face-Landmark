#include <jni.h>
#include <string>
#include "dlib_android/dlib_utils.hpp"
#include <android/log.h>

#define TAG "DLIB_FACE_CPP"

dlib::shape_predictor sp;
dlib::frontal_face_detector detector = dlib::get_frontal_face_detector();

unsigned char* as_unsigned_char_array(jbyteArray array, JNIEnv* env) {
    int len = env->GetArrayLength(array);
    unsigned char* buf = new unsigned char[len];
    env->GetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_dlibandroidfacelandmark_DLibResult_setupDlib(JNIEnv *env, jobject thiz,
                                                              jstring file_name) {
    std::string fileName = (env)->GetStringUTFChars(file_name, NULL);
    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s", "START LOADING SHAPE PRED.");
    myu::setShapePredictor(fileName);
    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s", "LOADED SUCCESSFULLY!");
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_dlibandroidfacelandmark_DLibResult_processFrame(JNIEnv *env, jobject thiz,
                                                                 jbyteArray yuv, jint w, jint h) {
    unsigned char* YUV = as_unsigned_char_array(yuv, env);
    std::vector<dlib::full_object_detection> shapes;
    shapes = myu::predictLandmakars(YUV, w, h);
}