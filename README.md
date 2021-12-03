# DLib Android Face Landmark Detection
it's good to mention that this project is not a greatly optimized Implementation of face landmark detection. It’s quite a naive and straightforward one. :)
```
$ git clone --recursive https://github.com/mhyrzt/Dlib-Android-Face-Landmark.git
```
# Summary
It utilizes the following libraries for making a face landmark detection system:

- [dlib](http://dlib.net/): This library was used natively with c++ for extracting face landmarks.

- [CamerView](https://github.com/natario1/CameraView): for capturing images from both mobile cameras (front & back) and processing frames in real-time.

- [OpenCv](https://opencv.org/android/): OpenCv was used for due to the slow speed of ```dlib::frontal_face_detector()``` .


