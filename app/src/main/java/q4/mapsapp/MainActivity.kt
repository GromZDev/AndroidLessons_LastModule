package q4.mapsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import q4.mapsapp.databinding.ActivityMainBinding
import q4.mapsapp.ui.lessons.LessonsFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    MainFragment.newInstance()
                )
                .addToBackStack("")
                .commitAllowingStateLoss()
        }

        setUpMenu()

    }

    private fun setUpMenu() {
        binding.menu.setOnItemSelectedListener {
            when (it) {
                R.id.app_home -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            MainFragment.newInstance()
                        )
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
                R.id.app_lessons -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            LessonsFragment.newInstance()
                        )
                        .addToBackStack("")
                        .commitAllowingStateLoss()
                }
            }
        }
    }
}