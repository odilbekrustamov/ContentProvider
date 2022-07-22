package com.exsample.contentprovideraccessing

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exsample.contentprovideraccessing.cp.MyContentProvider


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        return true
    }

    fun onClickAddDetails(view: View?) {
        val values = ContentValues()
        values.put(MyContentProvider.name,
            (findViewById<View>(R.id.textName) as EditText).text.toString())

        contentResolver.insert(MyContentProvider.CONTENT_URI, values)
    }

    @SuppressLint("Range")
    fun onClickShowDetails(view: View?) {
        val resultView = findViewById<View>(R.id.res) as TextView
        val cursor = contentResolver.query(Uri.parse("content://com.demo.user.provider/users"),
            null,
            null,
            null,
            null)
        if (cursor!!.moveToFirst()) {
            val strBuild = StringBuilder()
            while (!cursor.isAfterLast) {
                strBuild.append("""
    
    ${cursor.getString(cursor.getColumnIndex("id"))}-
    """.trimIndent() + cursor.getString(
                    cursor.getColumnIndex("name")))
                cursor.moveToNext()
            }
            resultView.text = strBuild
        } else {
            resultView.text = "No Records Found"
        }
    }
}