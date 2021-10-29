#include <dlib/opencv.h>
#include <opencv2/core.hpp>

#ifndef ANDROID_CAMERA_CONTROL_DLIB_UTILITY_H
#define ANDROID_CAMERA_CONTROL_DLIB_UTILITY_H

cv::Rect dlibRectToCvRect(const dlib::rectangle &);

#endif //ANDROID_CAMERA_CONTROL_DLIB_UTILITY_H
