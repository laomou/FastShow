#ifndef CACHE_IMAGE_PROVIDER_H
#define CACHE_IMAGE_PROVIDER_H

#include <QQuickImageProvider>
#include <QMutex>

class CacheImageProvider : public QQuickImageProvider {
    Q_OBJECT
public:
    explicit CacheImageProvider() : QQuickImageProvider(QQuickImageProvider::Image) {}
    ~CacheImageProvider() override {}

    virtual QString generateCacheKey(const QString& id) = 0;

    virtual QImage generateImage(const QString& id) = 0;

    QImage requestImage(const QString& id, QSize* size, const QSize& requestedSize) override;


private:
    QMap<QString, QImage> memoryCache;
    mutable QMutex cacheMutex;

};

#endif // CACHE_IMAGE_PROVIDER_H
