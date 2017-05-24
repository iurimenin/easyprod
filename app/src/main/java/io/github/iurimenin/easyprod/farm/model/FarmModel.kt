package io.github.iurimenin.easyprod.farm.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import io.github.iurimenin.easyprod.farm.util.FarmUtils

/**
 * Created by Iuri Menin on 22/05/17.
 */

class FarmModel() : Parcelable {

    var key: String = ""
    var name: String = ""

    companion object {
        @JvmStatic val TAG = this::javaClass.name

        @JvmField val CREATOR: Parcelable.Creator<FarmModel> = object : Parcelable.Creator<FarmModel> {
            override fun createFromParcel(source: Parcel): FarmModel = FarmModel(source)
            override fun newArray(size: Int): Array<FarmModel?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.key)
        dest.writeString(this.name)
    }

    constructor(key: String, name: String = "") : this() {
        this.key = key
        this.name = name
    }

    @Exclude
    fun save() {
        val farmUtils = FarmUtils()
        val myRef = farmUtils.getFarmReference()

        if (key.isNullOrEmpty())
            key = myRef.push().key

        myRef.child(key).setValue(this)
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