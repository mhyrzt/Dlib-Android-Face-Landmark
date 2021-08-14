#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include <vector>
#include "dlib_android/dlib_utils.hpp"
#define TAG "DLIB_FACE_CPP"

dlib::shape_predictor sp;
dlib::frontal_face_detector detector = dlib::get_frontal_face_detector();

struct membuf: std::streambuf {
    membuf(char* begin, char * end){
        this->setg(begin, begin, end);
    }
};

void bitmap2Array2dRGB(
        JNIEnv* env,
        jobject bitmap,
        dlib::array2d<dlib::bgr_pixel>& out
        ) {
    AndroidBitmapInfo bitmapInfo;
    void *pixels; int state;

    if (0 > (state = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo))){return/* ERROR */; }
    else if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {/* !RGB_565 */}
    if (0 > (state = AndroidBitmap_lockPixels(env, bitmap, &pixels))) {return /* ERROR LOCK FAILED */ ;}

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

void bitmap2Array2dGrayScale(
        JNIEnv* env,
        jobject bitmap,
        dlib::array2d<unsigned char>& out
        ) {
    AndroidBitmapInfo bitmapInfo;
    void *pixels; int state;

    if (0 > (state = AndroidBitmap_getInfo(env, bitmap, &bitmapInfo))){return/* ERROR */; }
    else if (bitmapInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {/* !RGB_565 */}
    if (0 > (state = AndroidBitmap_lockPixels(env, bitmap, &pixels))) {return /* ERROR LOCK FAILED */ ;}

    out.set_size((long) bitmapInfo.height, (long) bitmapInfo.width);
    char* line = (char *) pixels;

    for (int h = 0; h < bitmapInfo.height; ++h) {
        for (int w = 0; w < bitmapInfo.width; ++w) {
            uint32_t* color = (uint32_t *) (line + 4 * w);
            unsigned char r = (unsigned char)(0xFF & (*color));
            unsigned char g = (unsigned char)(0xFF & ((*color) >> 8));
            unsigned char b = (unsigned char)(0xFF & ((*color) >> 16));
            out[h][w] = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        }
        line += bitmapInfo.stride;
    }
    AndroidBitmap_unlockPixels(env, bitmap);
}

void addNewPosition(JNIEnv* env, jobject thiz, long x, long y) {
    jclass thizz = env->GetObjectClass(thiz);
    if (NULL == thizz)
        return;
    jmethodID addPos = env->GetMethodID(thizz, "addPosition", "(II)V");
    if (NULL == addPos)
        return;
    env->CallVoidMethod(thiz, addPos, x, y);
}

void addNewPosition(JNIEnv* env, jobject thiz, dlib::point point) {
    addNewPosition(env, thiz, point.x(), point.y());
}

void addFaceLandmarks(
        JNIEnv* env,
        jobject thiz,
        dlib::array2d<unsigned char>& img,
        dlib::rectangle& det
        ) {
    dlib::full_object_detection shape = sp(img, det);
    for (unsigned long i = 0; i < shape.num_parts(); i++)
        addNewPosition(env, thiz, shape.part(i));

}

dlib::rectangle bb2rect(JNIEnv* env, jintArray bb) {
    jint* arr = env->GetIntArrayElements(bb, nullptr);
    long x, y, w, h, i = 0;
    x = arr[i++]; y = arr[i++];
    w = arr[i++]; h = arr[i++];
    dlib::rectangle rect(x, y, x + w, y + h);
    env->ReleaseIntArrayElements(bb, arr, NULL);
    return rect;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_dlibandroidfacelandmark_DLibResult_setupDlib(
        JNIEnv *env,
        jobject thiz,
        jobject asset_manager,
        jstring file_name
) {
    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s", "START LOADING SHAPE PRED.");

    const char* fileName = (env)->GetStringUTFChars(file_name, nullptr);

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
Java_com_example_dlibandroidfacelandmark_DLibResult_processLandMarks(
        JNIEnv *env,
        jobject thiz,
        jobject bitmap,
        jintArray bb
) {

    dlib::array2d<unsigned char> img; // unsigned char -> byte
    bitmap2Array2dGrayScale(env, bitmap, img);
    dlib::rectangle rect = bb2rect(env, bb);
    addFaceLandmarks(env, thiz, img, rect);
}