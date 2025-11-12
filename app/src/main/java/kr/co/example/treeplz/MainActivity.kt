package kr.co.example.treeplz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.co.example.treeplz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Set up UI with data from ViewModel
    }
}