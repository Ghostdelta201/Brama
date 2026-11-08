package com.example.brama

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var openNum: EditText
    private lateinit var closeNum: EditText
    private var pending: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("brama", Context.MODE_PRIVATE)

        openNum = EditText(this).apply {
            hint = "Numer do OTWARCIA"
            inputType = InputType.TYPE_CLASS_PHONE
            setText(prefs.getString("open", ""))
        }
        closeNum = EditText(this).apply {
            hint = "Numer do ZAMKNIĘCIA"
            inputType = InputType.TYPE_CLASS_PHONE
            setText(prefs.getString("close", ""))
        }
        val btnOpen = Button(this).apply {
            text = "OTWÓRZ BRAMĘ"
            textSize = 22f
            setOnClickListener { dial(openNum.text.toString()) }
        }
        val btnClose = Button(this).apply {
            text = "ZAMKNIJ BRAMĘ"
            textSize = 22f
            setOnClickListener { dial(closeNum.text.toString()) }
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
            addView(openNum)
            addView(closeNum)
            addView(btnOpen, LinearLayout.LayoutParams(-1, 300).apply { topMargin = 48 })
            addView(btnClose, LinearLayout.LayoutParams(-1, 300).apply { topMargin = 48 })
        }
        setContentView(layout)
    }

    private fun dial(number: String) {
        if (number.isBlank()) {
            Toast.makeText(this, "Wpisz numer", Toast.LENGTH_SHORT).show()
            return
        }
        getSharedPreferences("brama", Context.MODE_PRIVATE).edit()
            .putString("open", openNum.text.toString())
            .putString("close", closeNum.text.toString())
            .apply()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            == PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(Intent.ACTION_CALL, Uri.fromParts("tel", number.trim(), null)))
        } else {
            pending = number
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
        }
    }

    override fun onRequestPermissionsResult(code: Int, perms: Array<out String>, results: IntArray) {
        super.onRequestPermissionsResult(code, perms, results)
        if (code == 1 && results.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            pending?.let { dial(it) }
        }
        pending = null
    }
}
