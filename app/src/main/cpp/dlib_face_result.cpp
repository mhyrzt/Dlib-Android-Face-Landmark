#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include "dlib_android/dlib_utils.hpp"
#define TAG "DLIB_FACE_CPP"

dlib::shape_predictor sp;
dlib::frontal_face_detector detector = dlib::get_frontal_face_detector();
//unsigned char* as_unsigned_char_array(jbyteArray array, JNIEnv* env) {
//    int len = env->GetArrayLength(array);
//    unsigned char* buf = new unsigned char[len];
//    env->GetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte*>(buf));
//    return buf;
//}

struct membuf: std::streambuf {
    membuf(char* begin, char * end){
        this->setg(begin, begin, end);
    }
};

void bitmap2Array2d(JNIEnv* env, jobject bitmap, dlib::array2d<dlib::bgr_pixel>& out) {
    AndroidBitmapInfo bitmapInfo;
    void *pixels;
    int state;

    if (0 > (state = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo))){
        // ERROR
        return;
    } else if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        // !RGB_565
    }

    if (0 > (state =AndroidBitmap_lockPixels(env, bitmap, &pixels))) {
        // ERROR LOCK FAILED
        return;
    }

    out.set_size((long) bitmapInfo.height, (long) bitmapInfo.width);

    char* line = (char *) pixels;
    for (int h = 0; h < bitmapInfo.height; ++h) {
        for (int w = 0; w < bitmapInfo.width; ++w) {
            uint32_t* color = (uint32_t *) (line + 4 * w);
            out[h][w].red   = (unsigned char)(0xFF & (*color));
            out[h][w].green = (unsigned char)(0xFF & ((*color) >> 8));
            out[h][w].blue  = (unsigned char)(0xFF & ((*color) >> 16));
        }
        line += bitmapInfo.stride;
    }

    AndroidBitmap_unlockPixels(env, bitmap);
}

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
                                                                 jobject bitmap) {
    dlib::array2d<dlib::bgr_pixel> img;
    bitmap2Array2d(env, bitmap, img);
    //dlib::pyramid_up(img);
    std::vector<dlib::full_object_detection> shapes;
    std::vector<dlib::rectangle> dets;
    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s", "START");
    dets = detector(img);
    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s", "DONE");

}