#include "cache_image_provider.h"


QImage CacheImageProvider::requestImage(const QString& id, QSize* size, const QSize& requestedSize) {

    const QString cacheKey = generateCacheKey(id);
    QImage result;
    {
        QMutexLocker locker(&cacheMutex);
        if (memoryCache.contains(cacheKey)) {
            result = memoryCache[cacheKey];
        }
    }

    if (result.isNull()) {
        result = generateImage(id);
        {
            QMutexLocker locker(&cacheMutex);
            memoryCache.insert(cacheKey, result);
        }
    }

    if (requestedSize.isValid()) {
        result = result.scaled(requestedSize, Qt::KeepAspectRatio, Qt::SmoothTransformation);
    }
    if (size) {
        *size = result.size();
    }

    return result;
}
