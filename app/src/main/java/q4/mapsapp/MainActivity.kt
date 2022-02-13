package q4.mapsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import q4.mapsapp.ui.mainMaps.MainMapsFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState.let {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    MainMapsFragment.newInstance()
                )
                .commit()
        }
    }
}