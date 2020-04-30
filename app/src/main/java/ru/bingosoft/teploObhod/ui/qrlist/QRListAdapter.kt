package ru.bingosoft.teploObhod.ui.qrlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_cardview_qrlist.view.*
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.QRList.QRList
import timber.log.Timber

class QRListAdapter(
    val qrs: List<QRList>,
    val itemListener: QRListRVClickListeners,
    val parentFragment: Fragment
) : RecyclerView.Adapter<QRListAdapter.CheckupsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckupsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cardview_qrlist, parent, false)
        return CheckupsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return qrs.size
    }

    fun getQR(position: Int): QRList {
        return qrs[position]
    }

    override fun onBindViewHolder(holder: CheckupsViewHolder, position: Int) {
        Timber.d("checkups[position].nameObject=${qrs[position].name}")
        holder.qrName.text = qrs[position].name
        holder.qrLocation.text = qrs[position].location
        holder.listener = itemListener
        holder.qustionCount.text = "${qrs[position].answeredCount}/${qrs[position].questionCount}"

        if (qrs[position].errorCount > 0) {
            val params = holder.errorCount.layoutParams
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            holder.errorCount.layoutParams = params
            holder.errorCount.text =
                parentFragment.context?.getString(R.string.errorMsg, qrs[position].errorCount)
            holder.errorCount.setTextColor(
                ContextCompat.getColor(
                    parentFragment.context!!,
                    R.color.errorAnswere
                )
            )
        } else {
            if (qrs[position].answeredCount == qrs[position].questionCount && qrs[position].answeredCount != 0) {
                val params = holder.errorCount.layoutParams
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                holder.errorCount.layoutParams = params
                holder.errorCount.text = parentFragment.context?.getString(R.string.all_right)
                holder.errorCount.setTextColor(
                    ContextCompat.getColor(
                        parentFragment.context!!,
                        R.color.errorNo
                    )
                )
            }
        }

    }

    class CheckupsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        override fun onClick(v: View?) {
            listener.recyclerViewListClicked(v, this.layoutPosition)
        }


        var qrName: TextView = itemView.nameQR
        var qrLocation: TextView = itemView.locationQR
        var qustionCount: TextView = itemView.questionCount
        var errorCount: TextView = itemView.errorCount
        lateinit var listener: QRListRVClickListeners
        //lateinit var btnScan: MaterialButton

        init {
            //btnScan=itemView.mbScanQR
            view.setOnClickListener(this)
        }


    }


}