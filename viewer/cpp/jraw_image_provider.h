#ifndef JRAW_IMAGE_PROVIDER_H
#define JRAW_IMAGE_PROVIDER_H

#include "cache_image_provider.h"

struct RawMetadata {
  int width = 0;
  int height = 0;
  int bitwidth = 10;
  int bayerPattern = 0;
  std::array<float, 4> blackLevel = {0};
  std::array<float, 3> rgb_gain = {1.0f, 1.0f, 1.0f};
  std::array<float, 9> ccm = {1, 0, 0, 0, 1, 0, 0, 0, 1};
};

class JRawImageProvider : public CacheImageProvider {
 public:
  JRawImageProvider() : CacheImageProvider() {}

  QString generateCacheKey(const QString& id) override;

  QImage generateImage(const QString& id) override;

 private:
  QImage createErrorImage(const QString& message);

  bool readMetadata(const QString& path, RawMetadata& meta);

  QImage processRawPipeline(const QString& path, const RawMetadata& meta);
};

#endif  // JG_RAW_IMAGE_PROVIDER_H
