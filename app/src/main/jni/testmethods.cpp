
#include <jni.h>
#include <string>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui.hpp>

extern "C"
{
JNIEXPORT jstring JNICALL Java_com_example_kartykamer_NativeTest_testString(JNIEnv*, jobject thiz);

JNIEXPORT jstring JNICALL Java_com_example_kartykamer_NativeTest_testString(JNIEnv* env,
                                                                            jobject thiz) {
    jstring str = env->NewStringUTF("Test");
    return str;
}
}



extern "C"
{
JNIEXPORT void JNICALL Java_com_example_kartykamer_NativeTest_testMat(JNIEnv*, jobject, jlong);

JNIEXPORT void JNICALL Java_com_example_kartykamer_NativeTest_testMat(JNIEnv* env, jobject, jlong matTest) {
    cv::Mat &mat = *(cv::Mat *) matTest;

    cv::Canny(mat, mat, 0, 0, 3);
}
}