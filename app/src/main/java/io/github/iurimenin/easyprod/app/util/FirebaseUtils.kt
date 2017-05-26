package io.github.iurimenin.easyprod.app.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * Created by Iuri Menin on 22/05/17.
 */

class FirebaseUtils {

    private val FARM_REFERENCE = "/farms/"
    private val FIELD_REFERENCE = "/fields/"

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
        sb.append(getEmailWithouDots())
        sb.append(FARM_REFERENCE)
        val reference = mFirebaseDatabase.getReference(sb.toString())
        reference.keepSynced(true)
        return reference
    }

    fun getFieldReference(farmKey: String) : DatabaseReference {
        val sb = StringBuilder()
        sb.append(getEmailWithouDots())
        sb.append(FIELD_REFERENCE)
        sb.append("farmKey(")
        sb.append(farmKey)
        sb.append(")")
        val reference = mFirebaseDatabase.getReference(sb.toString())
        reference.keepSynced(true)
        return reference
    }

    private fun  getEmailWithouDots(): String {
        return mUser.email!!.replace(".", "|")
    }

}