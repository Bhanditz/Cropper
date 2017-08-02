package com.wencharm.cropper

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val SELECT_IMAGE = 0x001
    lateinit var cropView: CropView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "chooser"), SELECT_IMAGE)
        }
        cropView = findViewById(R.id.image)
        cropView.setCropListener(object : BitmapLoadListener {
            override fun onStart() {
            }

            override fun onError(e: Throwable) {
                Toast.makeText(cropView.context, "error when cropping", Toast.LENGTH_SHORT).show()
            }

            override fun onComplete(uri: Uri, bitmap: Bitmap?) {
                cropView.setImageUri(uri)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                if (cropView.uri != null) cropView.crop()
                return true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                val result = data?.data
                Log.d("selected image: ", result?.path)
                if (result != null) cropView.setImageUri(result)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
