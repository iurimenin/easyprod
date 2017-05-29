package io.github.iurimenin.easyprod.app.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import io.github.iurimenin.easyprod.app.util.FirebaseUtils

/**
 * Created by Iuri Menin on 24/05/17.
 */
class FieldModel(var key: String, var name: String, var totalArea : Double) : Parcelable {

    constructor() : this("", "", 0.00) {
        //Firebase needs the constructor without parameters
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readDouble()
    )

    companion object {

        val TAG = FieldModel::javaClass.name

        @JvmField val CREATOR: Parcelable.Creator<FieldModel> = object : Parcelable.Creator<FieldModel> {
            override fun createFromParcel(source: Parcel): FieldModel = FieldModel(source)
            override fun newArray(size: Int): Array<FieldModel?> = arrayOfNulls(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.key)
        dest.writeString(this.name)
        dest.writeDouble(this.totalArea)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as FieldModel

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