# DLib Android Face Landmark Detection
at first it's good to mention that it's not a great and optimized Implementation of face landmark detection it's kinda naive and simple one. :)

# summary
this project utilize following libraries for making a face landmark detection system:

- [dlib](http://dlib.net/): used this library natively with c++ for extracting face landmarks.

- [CamerView](https://github.com/natario1/CameraView): for capturing image from mobile cameras (frontal & back) and processing frames in real time.

- [OpenCv](https://opencv.org/android/): due to the slow speed of ```dlib::frontal_face_detector()``` decided to use OpenCv for face Bounding Box Detection.