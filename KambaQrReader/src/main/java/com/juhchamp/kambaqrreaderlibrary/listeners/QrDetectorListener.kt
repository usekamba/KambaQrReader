package com.juhchamp.kambaqrreaderlibrary.listeners

import com.google.mlkit.vision.barcode.Barcode
import java.lang.Exception

interface QrDetectorListener {
    fun onDetectQrFromCamera(item: Barcode?)
    fun onDetectQrFromGallery(item: Barcode?)
    fun onQrDetectError(message: String)
}
