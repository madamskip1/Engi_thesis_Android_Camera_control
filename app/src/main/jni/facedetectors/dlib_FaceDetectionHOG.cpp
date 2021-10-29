#include <jni.h>
#include <dlib/opencv.h>
#include <opencv2/core.hpp>
#include <dlib/image_processing/frontal_face_detector.h>

class FaceDetectorHOG {
public:
    FaceDetectorHOG() : detector{dlib::get_frontal_face_detector()} {};

    std::vector<dlib::rectangle> detect(const cv::Mat &frame) {
        dlib::cv_image<dlib::rgb_pixel> dlibMat{frame};
        return detector(dlibMat, 0);
    }

private:
    dlib::frontal_face_detector detector;
};


cv::Rect dlibRectToCvRect(const dlib::rectangle &dlibRect) {
    return cv::Rect{cv::Point2i(dlibRect.left(), dlibRect.top()),
                    cv::Point2i(dlibRect.right() + 1, dlibRect.bottom() + 1)};
}


extern "C"
JNIEXPORT jlong JNICALL
Java_org_pw_engithesis_androidcameracontrol_facedetectionalgorithms_FaceDetectionDlibHOG_initNativeDetector(
        JNIEnv *, jclass) {
    return reinterpret_cast<jlong>(new FaceDetectorHOG());
}
extern "C"
JNIEXPORT void JNICALL
Java_org_pw_engithesis_androidcameracontrol_facedetectionalgorithms_FaceDetectionDlibHOG_nativeDetect(
        JNIEnv *, jclass, jlong detector_addr, jlong frame_addr, jlong result_faces) {
    cv::Mat &frame = *(cv::Mat *) frame_addr;
    auto detector = reinterpret_cast<FaceDetectorHOG *>(detector_addr);
    std::vector<dlib::rectangle> dlibRects = detector->detect(frame);
    std::vector<cv::Rect> cvRects;

    for (const auto &rect : dlibRects) {
        cvRects.emplace_back(dlibRectToCvRect(rect));
    }

    *((cv::Mat *) result_faces) = cv::Mat(cvRects, true);
}