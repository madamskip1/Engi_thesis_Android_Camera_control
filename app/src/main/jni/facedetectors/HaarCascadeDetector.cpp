#include "HaarCascadeDetector.h"

HaarCascadeDetector::HaarCascadeDetector(cv::Ptr<cv::CascadeClassifier> _classifier) : classifier(
        _classifier) {

}
