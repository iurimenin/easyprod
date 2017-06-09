package io.github.iurimenin.easyprod.app.widget.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Iuri Menin on 09/06/17.
 */
class WidgetModel(var productionKey: String, var bags: Double, var cultivationKey: String?,
                  var cultivationName: String, var seasonName: String) : Parcelable {

    constructor(source: Parcel) : this(
            source.readString(),
            source.readDouble(),
            source.readString(),
            source.readString(),
            source.readString()
    )

    companion object {

        val TAG = "SeasonModel"

        @JvmField val CREATOR: Parcelable.Creator<WidgetModel> = object : Parcelable.Creator<WidgetModel> {
            override fun createFromParcel(source: Parcel): WidgetModel = WidgetModel(source)
            override fun newArray(size: Int): Array<WidgetModel?> = arrayOfNulls(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.productionKey)
        dest.writeDouble(this.bags)
        dest.writeString(this.cultivationKey)
        dest.writeString(this.cultivationName)
        dest.writeString(this.cultivationName)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as WidgetModel

        if (productionKey != other.productionKey) return false

        return true
    }

    override fun hashCode(): Int {
        return productionKey.hashCode()
    }
}