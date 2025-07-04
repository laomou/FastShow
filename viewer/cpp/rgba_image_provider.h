#ifndef RGBA_IMAGE_PROVIDER_H
#define RGBA_IMAGE_PROVIDER_H

#include <QQuickImageProvider>

class RGBAImageProvider : public QQuickImageProvider
{
public:
    RGBAImageProvider() : QQuickImageProvider(QQuickImageProvider::Image) {}

    QImage requestImage(const QString &id, QSize *size, const QSize &requestedSize) override;

private:
    QImage createErrorImage(const QString &message);
};

#endif // RGBA_IMAGE_PROVIDER_H
