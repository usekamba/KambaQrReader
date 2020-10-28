package com.juhchamp.kambaqrreaderlibraryproj

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_view_qr_content.*

class ViewQrContentActivity : AppCompatActivity() {

    private var qrContentString: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_qr_content)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        getExtras()
        contentTv.text = "QR Content String: $qrContentString"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getExtras() {
        val extras = intent.extras
        qrContentString = extras?.getString("qr-content")
    }
}