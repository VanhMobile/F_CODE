package com.example.fcode.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fcode.R
import com.example.fcode.databinding.ActivityActCourseBinding
import com.example.fcode.view.readPdf.loadPdfFromUrl

class actCourse : AppCompatActivity() {
    private lateinit var actCourseBinding: ActivityActCourseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actCourseBinding = ActivityActCourseBinding.inflate(layoutInflater)
        setContentView(actCourseBinding.root)

        val intent = intent
        val url = intent.getStringExtra("Document")
        url?.let {
            loadPdfFromUrl(this@actCourse, it,actCourseBinding.PdfView).forceLoad()
        }
    }
}