#include "dlib_utility.h"

cv::Rect dlibRectToCvRect(const dlib::rectangle &dlibRect) {
    return cv::Rect{cv::Point2i(dlibRect.left(), dlibRect.top()),
                    cv::Point2i(dlibRect.right() + 1, dlibRect.bottom() + 1)};
}


dlib::rectangle cvRectToDlibRect(const cv::Rect &cvRect) {
    return dlib::rectangle((long) cvRect.tl().x, (long) cvRect.tl().y, (long) cvRect.br().x - 1,
                           (long) cvRect.br().y - 1);
}
