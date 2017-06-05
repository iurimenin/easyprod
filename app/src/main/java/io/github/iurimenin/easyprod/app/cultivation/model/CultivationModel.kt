package io.github.iurimenin.easyprod.app.cultivation.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import io.github.iurimenin.easyprod.app.util.FirebaseUtils

/**
 * Created by Iuri Menin on 01/06/17.
 */
class CultivationModel(var key: String, var name: String,
                       var seasonKey: String, var seasonName: String) : Parcelable {

    constructor() : this("", "", "", "") {
        //Firebase needs the constructor without parameters
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    companion object {

        val TAG = "CultivationModel"

        @JvmField val CREATOR: Parcelable.Creator<CultivationModel> = object : Parcelable.Creator<CultivationModel> {
            override fun createFromParcel(source: Parcel): CultivationModel = CultivationModel(source)
            override fun newArray(size: Int): Array<CultivationModel?> = arrayOfNulls(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.key)
        dest.writeString(this.name)
        dest.writeString(this.seasonKey)
        dest.writeString(this.seasonName)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as CultivationModel

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    @Exclude
    fun save(farmKey: String) {
        val myRef = FirebaseUtils().getFieldReference(farmKey)

        if (key.isNullOrEmpty())
            key = myRef.push().key

        myRef.child(key).setValue(this)
    }
}