package com.example.travelmantics

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_image_slider.*

class ImageSlider : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_slider)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.tool_title)
        toolbar.setTitleTextColor(Color.WHITE)

        imageSliderImplementation()
    }

    private fun imageSliderImplementation() {
        val adapter = ImageSliderAdapter(this)
        viewpager.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu,menu)
        return true
    }


}
