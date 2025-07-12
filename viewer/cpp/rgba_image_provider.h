#ifndef RGBA_IMAGE_PROVIDER_H
#define RGBA_IMAGE_PROVIDER_H

#include "cache_image_provider.h"

class RGBAImageProvider : public CacheImageProvider {
 public:
  RGBAImageProvider() : CacheImageProvider() {}

  QString generateCacheKey(const QString& id) override;

  QImage generateImage(const QString &id) override;

 private:
  QImage createErrorImage(const QString &message);
};

#endif  // RGBA_IMAGE_PROVIDER_H
