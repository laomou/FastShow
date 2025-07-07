#ifndef JRAW_IMAGE_PROVIDER_H
#define JRAW_IMAGE_PROVIDER_H

#include <QQuickImageProvider>

class JRawImageProvider : public QQuickImageProvider
{
public:
    JRawImageProvider() : QQuickImageProvider(QQuickImageProvider::Image) {}

    QImage requestImage(const QString &id, QSize *size, const QSize &requestedSize) override;

private:
    QImage createErrorImage(const QString &message);
};

#endif // JG_RAW_IMAGE_PROVIDER_H
