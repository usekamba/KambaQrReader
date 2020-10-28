package com.juhchamp.kambaqrreaderlibraryproj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.juhchamp.kambaqrreaderlibrary.listeners.QrDetectorListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), QrDetectorListener {

    companion object {
        private const val mTag = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the Qr Camera View and processor
        qrReaderView.setupCameraView(this)
        // Add the detector listeners for handle detection states
        // See region QrDetectorListener at line 39
        qrReaderView.addDetectorListener(this)
    }

    override fun onPause() {
        super.onPause()
        // Pause the camera preview
        qrReaderView.pauseCameraPreview()
    }

    override fun onResume() {
        super.onResume()
        // Resume the camera preview
        qrReaderView.resumeCameraPreview()
    }

    //region QrDetectorListener

    override fun onDetectQrFromCamera(item: com.google.mlkit.vision.barcode.Barcode?) {
        val contentIntent = Intent(this, ViewQrContentActivity::class.java)
        contentIntent.putExtra("qr-content", "${item?.rawValue}")
        startActivity(contentIntent)
        Log.w(mTag, "onDetectQrFromCamera: ${item?.rawValue}")
    }

    override fun onDetectQrFromGallery(item: com.google.mlkit.vision.barcode.Barcode?) {
        Log.w(mTag, "onDetectQrFromGallery: ${item?.rawValue}")
    }

    override fun onQrDetectError(message: String) {
        Log.w(mTag, "onQrDetectError: $message")
    }

    //endregion
}