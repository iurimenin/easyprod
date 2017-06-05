package io.github.iurimenin.easyprod.app.view

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import io.github.iurimenin.easyprod.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val arrowBack = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp)
        arrowBack.setColorFilter(ContextCompat.getColor(this, android.R.color.white),
                PorterDuff.Mode.SRC_ATOP)

        supportActionBar?.apply {
            title = getString(R.string.about)
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(arrowBack)
        }

        textViewDevelopedBy.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_iuri)))
            startActivity(browserIntent)
        }

        val pInfo: PackageInfo
        try {
            pInfo = this.packageManager.getPackageInfo(this.packageName, 0)
            textViewVersion.text = getString(R.string.version, pInfo.versionName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
