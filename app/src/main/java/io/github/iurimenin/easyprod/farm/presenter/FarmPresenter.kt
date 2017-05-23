package io.github.iurimenin.easyprod.farm.presenter

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import io.github.iurimenin.easyprod.farm.model.FarmModel
import io.github.iurimenin.easyprod.farm.util.FarmUtils
import io.github.iurimenin.easyprod.farm.view.FarmActivity

/**
 * Created by Iuri Menin on 23/05/17.
 */
class FarmPresenter () {

    private var farmActivity: FarmActivity? = null

    fun  bindView(farmActivity: FarmActivity) {
        this.farmActivity = farmActivity
    }

    fun unBindView() {
        this.farmActivity = null
    }

    fun loadFarms() {

        val farmRef = FarmUtils().getFarmReference()
        farmRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val vo = dataSnapshot.getValue(FarmModel::class.java)
                farmActivity?.addItem(vo)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                val updated = dataSnapshot.getValue(FarmModel::class.java)
                farmActivity?.updateItem(updated)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val removed = dataSnapshot.getValue(FarmModel::class.java)
                farmActivity?.removeItem(removed)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

}