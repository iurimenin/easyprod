package io.github.iurimenin.easyprod.app.farm.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import io.github.iurimenin.easyprod.app.util.FirebaseUtils

/**
 * Created by Iuri Menin on 22/05/17.
 */

class FarmModel(var key: String, var name : String) : Parcelable {

    constructor() : this("", "") {
        //Firebase needs the constructor without parameters
    }

    private constructor (source : Parcel) : this (
            source.readString(),
            source.readString()
    )

    companion object {

        val TAG = "FarmModel"

        @JvmField val CREATOR: Parcelable.Creator<FarmModel> = object : Parcelable.Creator<FarmModel> {
            override fun createFromParcel(source: Parcel): FarmModel = FarmModel(source)
            override fun newArray(size: Int): Array<FarmModel?> = arrayOfNulls(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(key)
        dest.writeString(name)
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

    @Exclude
    fun save() {
        val farmUtils = FirebaseUtils()
        val myRef = farmUtils.getFarmReference()

        if (key.isNullOrEmpty())
            key = myRef.push().key

        myRef.child(key).setValue(this)
    }
}
