package com.dicoding.storyapp.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import com.dicoding.storyapp.R
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Helper {

    private const val FILENAME_FORMAT = "dd-MMM-yyyy"
    private const val simpleDateFormat = "dd MMM yyyy HH.mm"
    private const val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    @SuppressLint("ConstantLocale")
    val simpleDate = SimpleDateFormat(simpleDateFormat, Locale.getDefault())

    val timeStamp: String = SimpleDateFormat(
        FILENAME_FORMAT,
        Locale.US
    ).format(System.currentTimeMillis())

    fun createTempFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    fun createFile(application: Application): File {
        val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
            File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        val outputDirectory = if (
            mediaDir != null && mediaDir.exists()
        ) mediaDir else application.filesDir

        return File(outputDirectory, "$timeStamp.jpg")
    }

    fun rotateFile(file: File, isBackCamera: Boolean = false) {
        val matrix = Matrix()
        val bitmap = BitmapFactory.decodeFile(file.path)
        val rotation = if (isBackCamera) 90f else -90f
        matrix.postRotate(rotation)
        if (!isBackCamera) {
            matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
    }

    fun reduceFileImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }


    fun uriToFile(selectedImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createTempFile(context)

        val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun getCurrentDate(): Date {
        return Date()
    }

    fun getSimpleDate(date: Date): String = simpleDate.format(date)

    private fun parseUTCDate(timestamp: String): Date {
        return try {
            val formatter = SimpleDateFormat(timestampFormat, Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(timestamp) as Date
        } catch (e: ParseException) {
            getCurrentDate()
        }
    }

    fun getUploadStoryTime(timestamp: String): String {
        val date: Date = parseUTCDate(timestamp)
        return getSimpleDate(date)
    }
    fun bitmapFromURL(context: Context, urlString: String): Bitmap {
        return try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val url = URL(urlString)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
        }
    }

}