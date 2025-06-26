package com.laomou.utils

import net.coobird.thumbnailator.Thumbnails
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.Executors
import javax.swing.ImageIcon

object ThumbnailLoader {
    private val cpuCount = Runtime.getRuntime().availableProcessors()
    private val executor = Executors.newFixedThreadPool(cpuCount.coerceAtMost(4))
    private var isShutdown = false

    fun getThumbnail(
        imageFile: File,
        width: Int,
        height: Int,
        onComplete: (ImageIcon?) -> Unit
    ) {
        if (isShutdown) {
            onComplete(null)
            return
        }

        if (!imageFile.exists() || !imageFile.canRead()) {
            onComplete(null)
            return
        }

        executor.execute {
            val icon = try {
                val buffered = Thumbnails.of(imageFile)
                    .size(width, height)
                    .keepAspectRatio(true)
                    .imageType(BufferedImage.TYPE_INT_ARGB)
                    .asBufferedImage()
                ImageIcon(buffered)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            try {
                onComplete(icon)
            } catch (callbackEx: Exception) {
                callbackEx.printStackTrace()
            }
        }
    }

    fun release() {
        if (!isShutdown) {
            isShutdown = true
            executor.shutdownNow()
        }
    }
}