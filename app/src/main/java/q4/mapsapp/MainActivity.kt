package q4.mapsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState.let {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    CarRidingFragment.newInstance()
                )
                .commit()
        }
    }
}