package io.github.iurimenin.easyprod.app.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Iuri Menin on 24/05/17.
 */
class ProductionModel (var key: String, var name : String) : Parcelable {

    constructor() : this("", "") {
        //Firebase needs the constructor without parameters
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readString()
    )

    companion object {

        val TAG = FieldModel::javaClass.name

        @JvmField val CREATOR: Parcelable.Creator<ProductionModel> = object : Parcelable.Creator<ProductionModel> {
            override fun createFromParcel(source: Parcel): ProductionModel = ProductionModel(source)
            override fun newArray(size: Int): Array<ProductionModel?> = arrayOfNulls(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.key)
        dest.writeString(this.name)
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

}