#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include "dlib_android/dlib_utils.hpp"

#define TAG "DLIB_FACE_CPP"

dlib::shape_predictor sp;
dlib::frontal_face_detector detector = dlib::get_frontal_face_detector();

unsigned char* as_unsigned_char_array(jbyteArray array, JNIEnv* env) {
    int len = env->GetArrayLength(array);
    unsigned char* buf = new unsigned char[len];
    env->GetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte*>(buf));
    return buf;
}

struct membuf: std::streambuf {
    membuf(char* begin, char * end){
        this->setg(begin, begin, end);
    }
};

extern "C"
JNIEXPORT void JNICALL
Java_com_example_dlibandroidfacelandmark_DLibResult_setupDlib(JNIEnv *env, jobject thiz,
    jobject asset_manager,
    jstring file_name
) {
    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s", "START LOADING SHAPE PRED.");

    const char* fileName = (env)->GetStringUTFChars(file_name, NULL);

    AAssetManager* native_asset = AAssetManager_fromJava(env, asset_manager);
    AAsset* assetFile = AAssetManager_open(native_asset, fileName, AASSET_MODE_BUFFER);

    size_t file_length = static_cast<size_t>(AAsset_getLength(assetFile));
    char* model_buffer = (char *) malloc(file_length);

    AAsset_read(assetFile, model_buffer, file_length);
    AAsset_close(assetFile);

    membuf mem_buf(model_buffer, model_buffer + file_length);
    std::istream in(&mem_buf);
    dlib::deserialize(sp, in);

    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s", "LOADED SUCCESSFULLY!");
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_dlibandroidfacelandmark_DLibResult_processFrame(JNIEnv *env, jobject thiz,
                                                                 jbyteArray yuv, jint w, jint h) {
    unsigned char* YUV = as_unsigned_char_array(yuv, env);
    std::vector<dlib::full_object_detection> shapes;
    shapes = myu::predictLandmakars(YUV, w, h, detector, sp);
}