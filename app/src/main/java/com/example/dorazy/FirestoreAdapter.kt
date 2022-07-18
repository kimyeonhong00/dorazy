package com.example.dorazy

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentChange.Type
import com.google.firebase.firestore.EventListener
import java.util.*

abstract class FirestoreAdapter<VH : RecyclerView.ViewHolder?>(private var mQuery: Query?) :
    RecyclerView.Adapter<VH>(),
    EventListener<QuerySnapshot?> {
    private var mRegistration: ListenerRegistration? = null
    private val mSnapshots = ArrayList<DocumentSnapshot>()

    override fun onEvent(
        documentSnapshots: QuerySnapshot?,
        e: FirebaseFirestoreException?
    ) {
        if (e != null) {
            Log.w(TAG, "onEvent:error", e)
            onError(e)
            return
        }
        // Dispatch the event
        Log.d(TAG,"onEvent:numChanges:" + documentSnapshots!!.documentChanges.size)
        for (change in documentSnapshots!!.documentChanges) {
            when (change.type) {
                Type.ADDED -> onDocumentAdded(change)
                Type.MODIFIED -> onDocumentModified(
                    change
                )
                Type.REMOVED -> onDocumentRemoved(
                    change
                )
            }
        }
        onDataChanged()
    }

    fun startListening() {
        if (mQuery != null && mRegistration == null) {
            mRegistration = mQuery!!.addSnapshotListener(this)
        }
    }

    fun stopListening() {
        if (mRegistration != null) {
            mRegistration!!.remove()
            mRegistration = null
        }
        mSnapshots.clear()
        notifyDataSetChanged()
    }

    fun setQuery(query: Query?) {
        // Stop listening
        stopListening()
        // Clear existing data
        mSnapshots.clear()
        notifyDataSetChanged()

        // Listen to new query

        mQuery = query
        startListening()
    }

    override fun getItemCount() =  mSnapshots.size


    protected fun getSnapshot(index: Int): DocumentSnapshot {
        return mSnapshots[index]
    }

    protected fun onDocumentAdded(change: DocumentChange) {
        mSnapshots.add(change.newIndex, change.document)
        notifyItemInserted(change.newIndex)
    }

    protected fun onDocumentModified(change: DocumentChange) {
        if (change.oldIndex == change.newIndex) {
            // Item changed but remained in same position

            mSnapshots[change.oldIndex] = change.document
            notifyItemChanged(change.oldIndex)
        } else {
            // Item changed and changed position

            mSnapshots.removeAt(change.oldIndex)
            mSnapshots.add(change.newIndex, change.document)
            notifyItemMoved(change.oldIndex, change.newIndex)
        }
    }

    protected fun onDocumentRemoved(change: DocumentChange) {
        mSnapshots.removeAt(change.oldIndex)
        notifyItemRemoved(change.oldIndex)
    }

    protected fun onError(e: FirebaseFirestoreException) {
        Log.w(TAG, "onError", e)
    }

    protected fun onDataChanged() {}

    companion object {
        private const val TAG = "FirestoreAdapter"
    }

}