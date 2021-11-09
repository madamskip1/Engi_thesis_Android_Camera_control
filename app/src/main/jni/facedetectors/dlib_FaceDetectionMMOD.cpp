#include <string>

#include <jni.h>
#include <dlib/opencv.h>
#include <opencv2/core.hpp>
#include <dlib/dnn.h>
#include <dlib/image_processing.h>

#include "../dlib_natives/dlib_utility.h"


template<long num_filters, typename SUBNET>
using con5d = dlib::con<num_filters, 5, 5, 2, 2, SUBNET>;

template<long num_filters, typename SUBNET>
using con5 = dlib::con<num_filters, 5, 5, 1, 1, SUBNET>;

template<typename SUBNET>
using downsampler = dlib::relu<dlib::affine<con5d<32, dlib::relu<dlib::affine<con5d<32, dlib::relu<dlib::affine<con5d<16, SUBNET>>>>>>>>>;

template<typename SUBNET>
using rcon5 = dlib::relu<dlib::affine<con5<45, SUBNET>>>;

using net_type = dlib::loss_mmod<dlib::con<1, 9, 9, 1, 1, rcon5<rcon5<rcon5<downsampler<dlib::input_rgb_image_pyramid<dlib::pyramid_down<6>>>>>>>>;


class MMODDetector {
public:
    MMODDetector() = delete;

    explicit MMODDetector(const std::string &modelPath) {
        path = modelPath;
        dlib::deserialize(path) >> net;
    }

    std::vector<dlib::mmod_rect> detect(const cv::Mat &frame) {
        dlib::cv_image<dlib::rgb_pixel> dlibMat{frame};
        dlib::matrix<dlib::rgb_pixel> dlibMatrix;
        dlib::assign_image(dlibMatrix, dlibMat);

        return net(dlibMatrix);
    }

private:
    std::string path;
    net_type net;
};


extern "C"
JNIEXPORT jlong JNICALL
Java_org_pw_engithesis_androidcameracontrol_facedetectionalgorithms_FaceDetectionDlibMMOD_init(
        JNIEnv *env, jclass, jstring model_path) {
    auto cstrPath = (env)->GetStringUTFChars(model_path, nullptr);
    std::string strPath = std::string(cstrPath);
    auto addr = reinterpret_cast<jlong>(new MMODDetector(strPath));
    env->ReleaseStringUTFChars(model_path, cstrPath);

    return addr;
}
extern "C"
JNIEXPORT void JNICALL
Java_org_pw_engithesis_androidcameracontrol_facedetectionalgorithms_FaceDetectionDlibMMOD_nativeDetect(
        JNIEnv *, jclass, jlong detector_addr, jlong frame_addr, jlong result_faces) {
    cv::Mat &frame = *(cv::Mat *) frame_addr;
    auto detector = reinterpret_cast<MMODDetector *>(detector_addr);
    auto dlibRects = detector->detect(frame);
    std::vector<cv::Rect> cvRects;

    cvRects.reserve(dlibRects.size());
    for (const auto &rect : dlibRects) {
        cvRects.emplace_back(dlibRectToCvRect(rect));
    }

    *((cv::Mat *) result_faces) = cv::Mat(cvRects, true);
}