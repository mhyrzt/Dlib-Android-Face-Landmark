#include "dlib_utils.hpp"

#define CORRECT_CH(V) ((unsigned char)(V < 0 ? 0 : V > 255000 ? 255 : V / 1000))

void myu::setShapePredictor(std::string spn) {
    dlib::deserialize(spn) >> myu::sp;
}

void myu::convertBytes(dlib::array2d<dlib::rgb_pixel>& out, unsigned char* yuv, int w, int h) {
    int frameSize = w * h;
    int y, u, v, i;
    int r, g, b;

    out.set_size((long) h, (long) w);

    for (int R = 0; R < h; R++) {
        for (int C = 0; C < w; C++) {
            i = frameSize + (R >> 1) * w + (C & ~1);
            y = 0xff & yuv[R * w + C] - 16;
            v = 0xff & yuv[i] - 128;
            u = 0xff & yuv[i + 1] - 128;
            y = y < 0 ? 0 : 1164 * y;

            r = y + 1596 * v;
            g = y - 183  * v - 391 * u;
            b = y + 2018 * u;

            out[R][C].red   = CORRECT_CH(r);
            out[R][C].green = CORRECT_CH(g);
            out[R][C].blue  = CORRECT_CH(b);
        }
    }
}

std::vector<dlib::full_object_detection> myu::predictLandmakars(unsigned char* yuv, int w, int h){
    /*
        Convert Bytes to RGB image
        Scale it up
    */
    dlib::array2d<dlib::rgb_pixel> img;
    convertBytes(img, yuv, w, h);
    dlib::pyramid_up(img);
    
    std::vector<dlib::rectangle> dets = myu::detector(img);
    std::vector<dlib::full_object_detection> shapes;
    
    for (unsigned long j = 0; j < dets.size(); ++j) {
        dlib::full_object_detection shape = sp(img, dets[j]);
        shapes.push_back(shape);
    }

    return shapes;
}