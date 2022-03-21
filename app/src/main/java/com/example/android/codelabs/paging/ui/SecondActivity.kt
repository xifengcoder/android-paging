package com.example.android.codelabs.paging.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.android.codelabs.paging.R

class SecondActivity : AppCompatActivity() {

    companion object {
        const val TAG  = "yxf"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG,"SecondActivity onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

    override fun onStart() {
        Log.d(TAG,"SecondActivity onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG,"SecondActivity onResume")
        super.onResume()
    }

    override fun onStop() {
        Log.d(TAG,"SecondActivity onStop")
        super.onStop()
    }
    override fun onDestroy() {
        Log.d(TAG,"SecondActivity onDestroy")
        super.onDestroy()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG,"SecondActivity onSaveInstanceState")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG,"SecondActivity onSaveInstanceState")
    }
}