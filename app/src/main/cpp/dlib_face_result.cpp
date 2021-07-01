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

void bitmap2Array2dRGB(JNIEnv* env, jobject bitmap, dlib::array2d<dlib::bgr_pixel>& out) {
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

void bitmap2Array2dGrayScale(JNIEnv* env, jobject bitmap, dlib::array2d<unsigned char>& out) {
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
            auto r = (unsigned char)(0xFF & (*color));
            auto g = (unsigned char)(0xFF & ((*color) >> 8));
            auto b = (unsigned char)(0xFF & ((*color) >> 16));
            out[h][w] = 0.2126 * r + 0.7152 * g + 0.0722 * b;
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
    addNewPosition(env, thiz, point.x() / 2, point.y() / 2);
}

void addFaceLandmarks(JNIEnv* env, jobject thiz,
                      dlib::array2d<unsigned char>& img,
                      dlib::rectangle& det ) {
    dlib::full_object_detection shape = sp(img, det);
    for (unsigned long i = 0; i < shape.num_parts(); i++)
        addNewPosition(env, thiz, shape.part(i));

}

std::vector<dlib::rectangle> bb2rect(JNIEnv* env, jintArray bb) {
    std::vector<dlib::rectangle> rects;
    jsize len = env->GetArrayLength(bb);
    jint* arr = env->GetIntArrayElements(bb, NULL);
    long x, y, w, h, i, j;
    for (i = 0; i < len / 4; i++) {
        j = i * 4;
        x = arr[j++];
        y = arr[j++];
        w = arr[j++];
        h = arr[j++];
        dlib::rectangle rect(x, y, x + w, y + h);
        rects.push_back(rect);
    }
    env->ReleaseIntArrayElements(bb, arr, NULL);
    return rects;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_dlibandroidfacelandmark_DLibResult_processLandMarks(
        JNIEnv *env,
        jobject thiz,
        jobject bitmap,
        jintArray bb
) {
//    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s", "START FRAME PROCESSING");

    dlib::array2d<unsigned char> img;
    bitmap2Array2dGrayScale(env, bitmap, img);
    std::vector<dlib::rectangle> rects = bb2rect(env, bb);

    for (dlib::rectangle det: rects)
        addFaceLandmarks(env, thiz, img, det);

//    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "%s", "FRAME PROCESSING DONE!");
}