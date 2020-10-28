package com.kamba.qrreader

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.Barcode.FORMAT_AZTEC
import com.google.mlkit.vision.barcode.Barcode.FORMAT_QR_CODE
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.frame.Frame
import com.otaliastudios.cameraview.frame.FrameProcessor
import java.io.IOException

private const val CORNER_WIDTH_DEFAULT_VALUE = 50f
private const val CORNER_HEIGHT_DEFAULT_VALUE = 6f

class QrReaderView : FrameLayout {

    private val mTag = this.javaClass.simpleName

    private var cameraView: CameraView? = null
    private var barcodeView: View? = null
    private var centerRectangleView: View? = null
    private var cornersParent: RelativeLayout? = null
    private var waterMarkImg: ImageView? = null

    private var imageDetected = false

    private lateinit var scanner: BarcodeScanner
    private lateinit var frameProcessor: FrameProcessor
    private var qrDetectorListener: QrDetectorListener? = null

    private var scanAnimation: Animation? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
        applyViewValues(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
        applyViewValues(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        init(context)
        applyViewValues(context, attrs)
    }

    private fun init(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.qr_reader_view, this, true)

        cameraView = findViewById(R.id.cameraView)
        centerRectangleView = findViewById(R.id.center_rectangle_view)
        barcodeView = findViewById(R.id.center_rectangle_bar)
        cornersParent = findViewById(R.id.cornersParent)
        waterMarkImg = findViewById(R.id.waterMarkImg)

        scanAnimation = AnimationUtils.loadAnimation(context, R.anim.bar_code_animation)
    }

    private fun applyViewValues(context: Context, attrs: AttributeSet) {
        // get the attributes specified in attrs.xml using the name we included
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.QrReaderViewStyleable, 0, 0)

        try {
            // get the text and colors specified using the names in attrs.xml
            val lineWidth = a.getDimension(R.styleable.QrReaderViewStyleable_lineWidth, 1f)
            val barHeight = a.getDimension(R.styleable.QrReaderViewStyleable_barHeight, 1f)
            val cornerWidth = a.getDimension(R.styleable.QrReaderViewStyleable_cornerWidth, CORNER_WIDTH_DEFAULT_VALUE)
            val cornerHeight = a.getDimension(R.styleable.QrReaderViewStyleable_cornerHeight, CORNER_HEIGHT_DEFAULT_VALUE)
            val lineColorValue = a.getColor(R.styleable.QrReaderViewStyleable_lineColor,
                ContextCompat.getColor(context, R.color.colorAccent))
            val waterMarkDrawable = a.getDrawable(R.styleable.QrReaderViewStyleable_waterMarkImage)

            // get the qr_rectangle_drawable.xml
            val layerDrawable = ContextCompat.getDrawable(context, R.drawable.qr_rectangle_drawable) as LayerDrawable
            val gradientDrawable = layerDrawable.findDrawableByLayerId(R.id.shape_item) as GradientDrawable
            val lineWidthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                lineWidth, resources.displayMetrics).toInt()
            gradientDrawable.setStroke(lineWidthPx, lineColorValue)

            // Apply values
            centerRectangleView!!.background = layerDrawable
            barcodeView!!.setBackgroundColor(lineColorValue)

            val cornerWPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                cornerWidth, resources.displayMetrics).toInt()
            val cornerHPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                cornerHeight, resources.displayMetrics).toInt()

            (0 until cornersParent!!.childCount).forEach {
                val view = cornersParent!!.getChildAt(it)
                when (view.id) {
                    R.id.corner0, R.id.corner1, R.id.corner4, R.id.corner5 -> {
                        view.layoutParams.width = cornerWPx
                        view.layoutParams.height = cornerHPx
                    }
                    R.id.corner2, R.id.corner3, R.id.corner6, R.id.corner7 -> {
                        view.layoutParams.width = cornerHPx
                        view.layoutParams.height = cornerWPx
                    }
                }
            }

            barcodeView!!.layoutParams.height =
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            barHeight, resources.displayMetrics).toInt()

            if (waterMarkDrawable != null) {
                waterMarkImg!!.setImageDrawable(waterMarkDrawable)
                waterMarkImg!!.visibility = View.VISIBLE
            }

        } finally {
            a.recycle()
        }

        startBarCodeAnimation()
    }

    private fun getVisionImageFromFrame(frame: Frame): InputImage {
        val data = frame.data
        return InputImage.fromByteArray(
            data, frame.size.width, frame.size.height, 90, InputImage.IMAGE_FORMAT_NV21
        )
    }

    private fun startBarCodeAnimation() {
        scanAnimation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                barcodeView!!.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {
                barcodeView!!.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {
                // no-op
            }
        })
        barcodeView!!.startAnimation(scanAnimation)
    }

    private fun processFrame(frame: Frame) {
        scanner.process(getVisionImageFromFrame(frame))
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        val barcodeList = it.result ?: return@addOnCompleteListener
                        if (barcodeList.isEmpty()) return@addOnCompleteListener

                        if (!imageDetected) {
                            imageDetected = true
                            val code = barcodeList[0]
                            cameraView?.close()
                            barcodeView!!.clearAnimation()
                            qrDetectorListener?.onDetectQrFromCamera(code)
                        }
                    }
                }
                .addOnFailureListener {
                    qrDetectorListener?.onQrDetectError("onQrDetectError: $it")
                }
    }

    /**
     * Add Qr detector listener to handle Qr detection states.
     * @param qrDetectorListener QrDetectorListener to handle detection states
     */
    fun addDetectorListener(qrDetectorListener: QrDetectorListener) {
        this.qrDetectorListener = qrDetectorListener
    }

    /**
     * Setup camera view, qr-scanner and frame processor.
     * @param lifecycleOwner The lifecycle owner for the camera view
     */
    fun setupCameraView(lifecycleOwner: LifecycleOwner) {

        val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                        FORMAT_QR_CODE,
                        FORMAT_AZTEC)
                .build()

        scanner = BarcodeScanning.getClient(options)

        frameProcessor = (FrameProcessor { frame ->
            processFrame(frame)
        })

        cameraView?.setLifecycleOwner(lifecycleOwner)
        cameraView?.addFrameProcessor(frameProcessor)
    }

    /**
     * Detect Qr-Code via image from gallery.
     * @param uri The [Uri] of the image to be scanned.
     */
    fun detectQrFromImage(uri: Uri) {
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, uri)
            scanner.process(image)
                .addOnSuccessListener { barcodeList ->
                    if (barcodeList.isEmpty()) {
                        qrDetectorListener?.onQrDetectError("onQrDetectError: Barcode is empty.")
                    } else {
                        if (!imageDetected) {
                            for (code in barcodeList) {
                                qrDetectorListener?.onDetectQrFromGallery(code)
                                imageDetected = true
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    qrDetectorListener?.onQrDetectError("onQrDetectError: $it")
                }
        } catch (e: IOException) {
            qrDetectorListener?.onQrDetectError("onQrDetectError: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Pause camera preview
     */
    fun pauseCameraPreview() {
        cameraView?.close()
        barcodeView!!.clearAnimation()
    }

    /**
     * Resume camera preview
     */
    fun resumeCameraPreview() {
        cameraView?.open()
        barcodeView!!.startAnimation(scanAnimation)
        imageDetected = false
    }
}
