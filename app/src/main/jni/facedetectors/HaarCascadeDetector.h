//
// Created by Maciej on 28.08.2021.
//

#ifndef KARTYKAMER_HAARCASCADEDETECTOR_H
#define KARTYKAMER_HAARCASCADEDETECTOR_H

#include <opencv2/core.hpp>
#include <opencv2/objdetect.hpp>

class HaarCascadeDetector : public cv::DetectionBasedTracker::IDetector {
public:
    HaarCascadeDetector() = delete;

    HaarCascadeDetector(cv::Ptr<cv::CascadeClassifier> _classifier);

    void detect(const cv::Mat &frame, std::vector<cv::Rect> &resultFaces) {
        classifier->detectMultiScale(frame, resultFaces);
    }

private:
    cv::Ptr<cv::CascadeClassifier> classifier;
};


#endif //KARTYKAMER_HAARCASCADEDETECTOR_H
