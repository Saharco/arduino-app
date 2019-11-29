package com.technion.columbus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.technion.columbus.map.ArduinoMap
import com.technion.columbus.map.GdxLauncher
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "Columbus"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            startActivity(Intent(this, GdxLauncher::class.java))
        }
    }
}
