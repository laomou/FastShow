#include "jraw_image_provider.h"
#include <QFileInfo>
#include <QPainter>
#include <QColorSpace>

QImage JRawImageProvider::createErrorImage(const QString &message) {
    QImage img(256, 256, QImage::Format_RGB888);
    img.fill(Qt::red);
    QPainter p(&img);
    p.drawText(img.rect(), Qt::AlignCenter, message);
    return img;
}

QImage JRawImageProvider::requestImage(const QString &id, QSize *size, const QSize &requestedSize) {
    QString filePath = id;
    if (!QFileInfo::exists(filePath)) {
        return createErrorImage("File not found");
    }

    QImage image(filePath);
    if (image.isNull()) {
        return createErrorImage("Invalid image");
    }

    QImage::Format targetFormat =
#if defined(Q_OS_MACOS)
        QImage::Format::Format_RGBA8888;
#else
        QImage::Format::Format_RGBA8888_Premultiplied;
#endif

    QColorSpace colorSpace = image.colorSpace();
    if (colorSpace.isValid() && colorSpace != QColorSpace::SRgb) {
        image = image.convertedToColorSpace(QColorSpace::SRgb, targetFormat);
    }

    if (requestedSize.isValid()) {
        return image.scaled(requestedSize, Qt::KeepAspectRatio, Qt::SmoothTransformation);
    }

    return image;
}
