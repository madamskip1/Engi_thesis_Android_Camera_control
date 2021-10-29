#include "dlib_utility.h"

cv::Rect dlibRectToCvRect(const dlib::rectangle &dlibRect) {
    return cv::Rect{cv::Point2i(dlibRect.left(), dlibRect.top()),
                    cv::Point2i(dlibRect.right() + 1, dlibRect.bottom() + 1)};
}