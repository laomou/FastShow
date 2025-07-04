#include "rgb_image_provider.h"
#include <QFileInfo>
#include <QPainter>

RGBImageProvider::RGBImageProvider() : QQuickImageProvider(QQuickImageProvider::Image) {}

QImage RGBImageProvider::createErrorImage(const QString &message) {
    QImage img(256, 256, QImage::Format_RGB888);
    img.fill(Qt::red);
    QPainter p(&img);
    p.drawText(img.rect(), Qt::AlignCenter, message);
    return img;
}


QImage RGBImageProvider::requestImage(const QString &id, QSize *size, const QSize &requestedSize) {
    QMutexLocker locker(&m_mutex);

    QString filePath = id;
    if (!QFileInfo::exists(filePath)) {
        qWarning() << "File not found:" << filePath;
        return createErrorImage("File not found");
    }

    QImage image(filePath);
    if (image.isNull()) {
        return createErrorImage("Invalid image");
    }

    QImage rgbImage = image.convertToFormat(QImage::Format_RGB888);
    if (size) *size = rgbImage.size();
    if (requestedSize.isValid()) {
        return rgbImage.scaled(requestedSize, Qt::KeepAspectRatio, Qt::SmoothTransformation);
    }

    return rgbImage;
}
