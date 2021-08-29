#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include "HaarCascadeDetector.h"


extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_kartykamer_facedetectors_HaarCascadeNativeBridge_nativeCreateDetector(JNIEnv *env,
                                                                                       jclass clazz,
                                                                                       jstring cascade_path,
                                                                                       jint min_face_size) {
    std::string cascadeFilePath(env->GetStringUTFChars(cascade_path, NULL));
    jlong nativeDetectorAddress = 0;

    return nativeDetectorAddress;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_kartykamer_facedetectors_HaarCascadeNativeBridge_nativeReleaseDetector(JNIEnv *env,
                                                                                        jclass clazz,
                                                                                        jlong native_address) {
    // TODO: implement nativeReleaseDetector()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_kartykamer_facedetectors_HaarCascadeNativeBridge_nativeDetectFace(JNIEnv *env,
                                                                                   jclass clazz,
                                                                                   jlong native_address,
                                                                                   jlong gray_frame,
                                                                                   jlong result_faces) {
    // TODO: implement nativeDetectFace()
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_kartykamer_facedetectors_HaarCascadeNativeBridge_test(JNIEnv *env, jclass clazz,
                                                                       jlong gray_frame_address,
                                                                       jlong classifierAddress,
                                                                       jlong result_faces_address) {
    cv::Mat &frame = *(cv::Mat *) gray_frame_address;
    cv::CascadeClassifier &classifier = *(cv::CascadeClassifier *) classifierAddress;
    std::vector<cv::Rect> faces;

    classifier.detectMultiScale(frame, faces);
    // *((cv::Mat*) result_faces_address) = cv::Mat(faces, true);
}