#ifndef RGB_IMAGE_PROVIDER_H
#define RGB_IMAGE_PROVIDER_H

#include <QQuickImageProvider>
#include <QImage>
#include <QMutex>

class RGBImageProvider : public QQuickImageProvider
{
public:
    RGBImageProvider();

    QImage requestImage(const QString &id, QSize *size, const QSize &requestedSize) override;

private:
    QMutex m_mutex;

    QImage createErrorImage(const QString &message);
};

#endif // RGB_IMAGE_PROVIDER_H
