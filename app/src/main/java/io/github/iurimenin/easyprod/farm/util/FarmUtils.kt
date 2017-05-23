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

    private var  mUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    companion object {

        @JvmStatic var mFirebaseDatabase: FirebaseDatabase = getStaticFirebase()

        private fun getStaticFirebase() :FirebaseDatabase {

            val firebasedatabase = FirebaseDatabase.getInstance()
            firebasedatabase.setPersistenceEnabled(true)
            return firebasedatabase
        }

    }

    fun getFarmReference(): DatabaseReference {

        val sb = StringBuilder()
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