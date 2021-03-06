#include <string>
#include <jni.h>
#include <dlib/opencv.h>
#include <opencv2/core.hpp>
#include <dlib/image_processing.h>

#include "../dlib_natives/dlib_utility.h"

constexpr int FACEMARKS_NUM = 68;

dlib::rectangle getDlibRectFromJniArray(JNIEnv *, const jintArray &);

jintArray dlibFacemarksToIntArray(JNIEnv *, const dlib::full_object_detection &);

class FacemarksKazemi {
public:
    explicit FacemarksKazemi(const std::string &filePath) {
        dlib::deserialize(filePath) >> shapePredictor;
    }

    dlib::full_object_detection detectFacemarks(const cv::Mat &frame, const dlib::rectangle &face) {
        cv::Mat faceMat{frame, dlibRectToCvRect(face)};
        dlib::cv_image<dlib::rgb_pixel> dlibMat{frame};
        dlib::array2d<dlib::rgb_pixel> dlibFrame;
        dlib::assign_image(dlibFrame, dlibMat);

        return shapePredictor(dlibFrame, face);
    }

private:
    dlib::shape_predictor shapePredictor;
};

extern "C"
JNIEXPORT jlong JNICALL
Java_org_pw_engithesis_androidcameracontrol_detectors_facemarksdetectionalgorithms_FacemarksKazemi_init(
        JNIEnv *env,
        jclass,
        jstring model_path) {
    auto cstrPath = (env)->GetStringUTFChars(model_path, nullptr);
    std::string strPath = std::string(cstrPath);
    auto addr = reinterpret_cast<jlong>(new FacemarksKazemi(strPath));
    env->ReleaseStringUTFChars(model_path, cstrPath);

    return addr;
}
extern "C"
JNIEXPORT jintArray JNICALL
Java_org_pw_engithesis_androidcameracontrol_detectors_facemarksdetectionalgorithms_FacemarksKazemi_getFacemarks(
        JNIEnv *env, jclass, jlong detector_addr, jlong frame_addr, jintArray face_rect) {
    cv::Mat &frame = *(cv::Mat *) frame_addr;
    auto dlibFaceRect = getDlibRectFromJniArray(env, face_rect);
    auto detector = reinterpret_cast<FacemarksKazemi *>(detector_addr);

    auto facemarks = detector->detectFacemarks(frame, dlibFaceRect);

    return dlibFacemarksToIntArray(env, facemarks);
}

dlib::rectangle getDlibRectFromJniArray(JNIEnv *env, const jintArray &jintRect) {
    jint *jintArrayAddr = env->GetIntArrayElements(jintRect, nullptr);

    auto x1 = static_cast<int>(jintArrayAddr[0]);
    auto y1 = static_cast<int>(jintArrayAddr[1]);
    auto x2 = x1 + static_cast<int>(jintArrayAddr[2]) - 1;
    auto y2 = y1 + static_cast<int>(jintArrayAddr[3]) - 1;

    env->ReleaseIntArrayElements(jintRect, jintArrayAddr, 0);
    return dlib::rectangle{x1, y1, x2, y2};
}

jintArray dlibFacemarksToIntArray(JNIEnv *env, const dlib::full_object_detection &dlibFacemarks) {
    auto facemarksIntArray = env->NewIntArray(FACEMARKS_NUM * 2);
    auto facemarksIntArrayAddr = env->GetIntArrayElements(facemarksIntArray, nullptr);

    for (int i = 0; i < FACEMARKS_NUM; i++) {
        int arrayIndex = i * 2;
        dlib::point point = dlibFacemarks.part(i);
        facemarksIntArrayAddr[arrayIndex] = point.x();
        facemarksIntArrayAddr[arrayIndex + 1] = point.y();
    }

    env->ReleaseIntArrayElements(facemarksIntArray, facemarksIntArrayAddr, 0);

    return facemarksIntArray;
}
