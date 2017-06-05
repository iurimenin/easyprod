package io.github.iurimenin.easyprod.app.production.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import io.github.iurimenin.easyprod.app.util.FirebaseUtils

/**
 * Created by Iuri Menin on 24/05/17.
 */
class ProductionModel (var key: String, var bags : Double) : Parcelable {

    constructor() : this("", 0.0) {
        //Firebase needs the constructor without parameters
    }

    constructor(source: Parcel) : this(
            source.readString(),
            source.readDouble()
    )

    companion object {

        val TAG = "ProductionModel"

        @JvmField val CREATOR: Parcelable.Creator<ProductionModel> = object : Parcelable.Creator<ProductionModel> {
            override fun createFromParcel(source: Parcel): ProductionModel = ProductionModel(source)
            override fun newArray(size: Int): Array<ProductionModel?> = arrayOfNulls(size)
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(this.key)
        dest.writeDouble(this.bags)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ProductionModel

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    @Exclude
    fun save(cultivationKey: String) {
        val myRef = FirebaseUtils().getProductionReference(cultivationKey)

        if (key.isNullOrEmpty())
            key = myRef.push().key

        myRef.child(key).setValue(this)
    }

}