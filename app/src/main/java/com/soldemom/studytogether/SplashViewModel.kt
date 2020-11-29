package com.soldemom.studytogether

import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel() {
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    companion object {
        private var instance : SplashViewModel? = null
        fun getInstance() =
            instance ?: synchronized(SplashViewModel::class.java){
                instance ?: SplashViewModel().also { instance = it }
            }
    }

}