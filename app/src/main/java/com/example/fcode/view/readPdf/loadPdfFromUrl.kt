package com.example.fcode.view.readPdf

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import com.github.barteksc.pdfviewer.PDFView
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class loadPdfFromUrl(context: Context, private val inPutUrl: String, val pdfView: PDFView)
    : AsyncTaskLoader<InputStream>(context) {

    override fun loadInBackground(): InputStream? {
        // khơi tạo inputstream đọc dũ liệu từ nguồn url
        var inputStream : InputStream? = null
        try{
            // tạo đối tượng Url từ chuỗi inPutUrl
            val url = URL(inPutUrl)

            // thiết lập kết nối với Url vừa tạo trên
            val urlConnection : HttpURLConnection = url.openConnection() as HttpURLConnection

            // kiểm tra kết nối thành công
            if (urlConnection.responseCode == 200){
                // lấy inputstream ra
                inputStream = BufferedInputStream(urlConnection.inputStream)
            }
        }catch (e: Exception){
            e.printStackTrace()
            return null
        }
        return inputStream
    }

    override fun onStartLoading() {
        forceLoad()
    }

    override fun onStopLoading() {
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()
        onStopLoading()
    }

    override fun deliverResult(data: InputStream?) {
        super.deliverResult(data)
        pdfView.fromStream(data).load()
    }

}