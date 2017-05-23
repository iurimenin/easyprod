package io.github.iurimenin.easyprod.farm.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Iuri Menin on 22/05/17.
 */

class FarmUtils {

    private val FARM_REFERENCE = "farms/"

    companion object {

        @JvmStatic var mFirebaseDatabase: FirebaseDatabase = getStaticFirebase()

        private fun getStaticFirebase() :FirebaseDatabase {

            var firebasedatabase = FirebaseDatabase.getInstance()
            firebasedatabase.setPersistenceEnabled(true)
            return firebasedatabase
        }

    }

    private var  mUser: FirebaseUser

    init {
        mUser = FirebaseAuth.getInstance().currentUser!!
    }

    fun getFarmReference(): DatabaseReference {

        var sb = StringBuilder()
        sb.append(FARM_REFERENCE)
        sb.append(getEmailWithouDots())
        val reference = mFirebaseDatabase.getReference(sb.toString())
        reference.keepSynced(true)
        return reference
    }

    private fun  getEmailWithouDots(): String {
        return mUser.email!!.replace(".", "|")
    }

}