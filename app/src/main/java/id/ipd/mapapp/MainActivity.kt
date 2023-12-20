package id.ipd.mapapp

import android.os.Bundle
import id.ipd.mapipd.ui.CurrentLocationMapActivity

class MainActivity: CurrentLocationMapActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init(
            null,
            null
        )
    }

}