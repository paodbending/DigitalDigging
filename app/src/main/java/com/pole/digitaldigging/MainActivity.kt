package com.pole.digitaldigging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pole.data.dataModule
import com.pole.digitaldigging.databinding.ActivityMainBinding
import com.pole.domain.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(domainModule, dataModule, appModule)
        }

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}