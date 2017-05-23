package io.github.iurimenin.easyprod.farm.model

import com.google.firebase.database.Exclude
import io.github.iurimenin.easyprod.farm.util.FarmUtils

/**
 * Created by Iuri Menin on 22/05/17.
 */

class FarmModel () {

    var key: String = ""
    var name: String = ""

    constructor( key: String, name: String = "") : this() {

        this.key = key
        this.name = name
    }


    @Exclude
    fun  save() {
        val farmUtils = FarmUtils()
        val myRef = farmUtils.getFarmReference()

        if (key.isNullOrEmpty())
            key = myRef.push().getKey()

        myRef.child(key).setValue(this);
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as FarmModel

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

}