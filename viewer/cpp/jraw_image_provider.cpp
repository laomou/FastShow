#include "jraw_image_provider.h"

#include <QColorSpace>
#include <QFileInfo>
#include <QPainter>

#include "cv/cv_core.h"

QImage JRawImageProvider::createErrorImage(const QString& message) {
  QImage img(256, 256, QImage::Format_RGB888);
  img.fill(Qt::red);
  QPainter p(&img);
  p.drawText(img.rect(), Qt::AlignCenter, message);
  return img;
}

bool JRawImageProvider::readMetadata(const QString& rawPath,
                                     RawMetadata& meta) {
  QFileInfo fileInfo(rawPath);
  QFile metaFile(fileInfo.path() + "/jiigan_siq_sdk_dump_metadata.txt");
  if (!metaFile.open(QIODevice::ReadOnly)) return false;

  QTextStream in(&metaFile);
  while (!in.atEnd()) {
    QString line = in.readLine().trimmed();
    if (line.startsWith("WbGain:")) {
      QStringList gains = line.mid(7).split(',');
      if (gains.size() == 3) {
        meta.rgb_gain[0] = gains[0].toFloat();  // R
        meta.rgb_gain[1] = gains[1].toFloat();  // G
        meta.rgb_gain[2] = gains[2].toFloat();  // B
      }
    } else if (line.startsWith("CCM:")) {
      QStringList values = line.mid(4).split(',');
      if (values.size() == 9) {
        for (int i = 0; i < 9; ++i) {
          meta.ccm[i] = values[i].toFloat();
        }
      }
    } else if (line.startsWith("BlackLevel:")) {
      QStringList levels = line.mid(11).split(',');
      if (levels.size() >= 4) {
        for (int i = 0; i < 4; ++i) {
          meta.blackLevel[i] = levels[i].toFloat();
        }
      }
    } else if (line.startsWith("BayerInfo")) {
      int patternStart = line.indexOf("pattern:") + 8;
      int patternEnd = line.indexOf(',', patternStart);
      meta.bayerPattern =
          line.mid(patternStart, patternEnd - patternStart).toInt();

      int bitStart = line.indexOf("bitwidth:") + 9;
      meta.bitwidth = line.mid(bitStart).toInt();
    } else if (line.startsWith("ImageSize")) {
      int wStart = line.indexOf("width:") + 6;
      int wEnd = line.indexOf(',', wStart);
      meta.width = line.mid(wStart, wEnd - wStart).toInt();

      int hStart = line.indexOf("height:") + 7;
      int hEnd = line.indexOf(',', hStart);
      meta.height = line.mid(hStart, hEnd - hStart).toInt();
    }
  }

  return meta.width > 0 && meta.height > 0;
}

QImage JRawImageProvider::processRawPipeline(const QString& rawPath,
                                             const RawMetadata& meta) {
  QFile file(rawPath);
  if (!file.open(QIODevice::ReadOnly)) {
    return createErrorImage("Cannot open RAW file");
  }

  RawArray bayer_u16(meta.height, meta.width, 1, DT_U16,
                     static_cast<BayerPattern>(meta.bayerPattern));
  if (!file.read(bayer_u16.data(),
                 (meta.height * meta.width * sizeof(uint16_t)))) {
    return createErrorImage("Invalid RAW file size");
  }

  if (!bayer_to_bayer_rggb(bayer_u16.c_ptr())) {
    return createErrorImage("Failed to convert to bayer RGGB");
  }

  RawArray rgb_u16(meta.height, meta.width, 3, DT_U16, BayerPattern::BP_RGB);
  if (!bayer_rggb_to_rgb(bayer_u16.c_ptr(), rgb_u16.c_ptr())) {
    return createErrorImage("Failed to convert to RGB");
  }

  RawArray rgb_f32(meta.height, meta.width, 3, DT_F32, BayerPattern::BP_RGB);
  if (!normalize(rgb_u16.c_ptr(), rgb_f32.c_ptr(), 64, 1023)) {
    return createErrorImage("Failed to normalize");
  }

  if (!apply_ccm(rgb_f32.c_ptr(), &meta.ccm[0])) {
    return createErrorImage("Failed to apply ccm");
  }

  if (!apply_rgb_gain(rgb_f32.c_ptr(), &meta.rgb_gain[0])) {
    return createErrorImage("Failed to apply rgb gain");
  }

  QImage rgbImage(meta.width, meta.height, QImage::Format::Format_RGB888);

  RawArray rgb_u8(rgbImage.bits(), meta.height, meta.width, 3, DT_U8,
                  BayerPattern::BP_RGB);
  if (!normalize(rgb_f32.c_ptr(), rgb_u8.c_ptr(), 0.f, 1.f)) {
    return createErrorImage("Failed to normalize to u8");
  }

  return rgbImage;
}

QImage JRawImageProvider::requestImage(const QString& id, QSize* size,
                                       const QSize& requestedSize) {
  QString rawPath = id;
  if (!QFileInfo::exists(rawPath)) {
    return createErrorImage("File not found");
  }

  RawMetadata meta;
  if (!readMetadata(rawPath, meta)) {
    return createErrorImage("Failed to read metadata");
  }

  QImage image = processRawPipeline(rawPath, meta);
  if (image.isNull()) {
    return createErrorImage("Failed to process RAW");
  }

  if (requestedSize.isValid()) {
    return image.scaled(requestedSize, Qt::KeepAspectRatio,
                        Qt::SmoothTransformation);
  }

  if (size) {
    *size = image.size();
  }

  return image;
}
