#include "rgba_image_provider.h"

#include <QColorSpace>
#include <QFileInfo>
#include <QPainter>
#include <QCryptographicHash>

QImage RGBAImageProvider::createErrorImage(const QString &message) {
  QImage img(256, 256, QImage::Format_RGB888);
  img.fill(Qt::red);
  QPainter p(&img);
  p.drawText(img.rect(), Qt::AlignCenter, message);
  return img;
}

QString RGBAImageProvider::generateCacheKey(const QString& id) {
    QFile file(id);
    if (!file.open(QIODevice::ReadOnly)) {
        return QString();
    }

    qint64 bytesToRead = 1024 * 1024;
    bytesToRead = qMin(bytesToRead, file.size());

    QCryptographicHash hash(QCryptographicHash::Md5);
    hash.addData(file.read(bytesToRead));

    return hash.result().toHex();
}

QImage RGBAImageProvider::generateImage(const QString &id) {
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

  return  image;
}
