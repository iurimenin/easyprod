package io.github.iurimenin.easyprod.app.season.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude

/**
 * Created by Iuri Menin on 31/05/17.
 */
class SeasonModel(var key: String, var startYear: Int, var endYear: Int) : Parcelable {

    constructor() : this("", 0, 0) {
        //Firebase needs the constructor without parameters
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readInt(),
            source.readInt()
    )

    companion object {

        val TAG = SeasonModel::javaClass.name

        @JvmField val CREATOR: Parcelable.Creator<SeasonModel> = object : Parcelable.Creator<SeasonModel> {
            override fun createFromParcel(source: Parcel): SeasonModel = SeasonModel(source)
            override fun newArray(size: Int): Array<SeasonModel?> = arrayOfNulls(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.key)
        dest.writeInt(this.startYear)
        dest.writeInt(this.endYear)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as SeasonModel

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    @Exclude
    override fun toString(): String {
        return startYear.toString().plus("/").plus(endYear)
    }
}