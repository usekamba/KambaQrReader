package com.kamba.qrreader

import com.google.mlkit.vision.barcode.Barcode

interface QrDetectorListener {
    fun onDetectQrFromCamera(item: Barcode?)
    fun onDetectQrFromGallery(item: Barcode?)
    fun onQrDetectError(message: String)
}
