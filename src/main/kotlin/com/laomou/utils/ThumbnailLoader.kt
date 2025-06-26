package com.laomou.utils

import com.github.laomou.thumbnailator.Thumbnails
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
                val buffered =
                    Thumbnails.generateThumbnail(imageFile.absoluteFile.toString(), width, height).toBufferedImage()
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