package io.github.iurimenin.easyprod.area.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.iurimenin.easyprod.R
import io.github.iurimenin.easyprod.farm.model.FarmModel

class AreaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area)

        var farm : FarmModel = intent.extras[FarmModel.TAG] as FarmModel
    }
}
