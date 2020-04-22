package ru.bingosoft.teploObhod.ui.checkuplist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_cardview_checkuplist.view.*
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import timber.log.Timber

class CheckupsListAdapter(
    val checkups: List<Checkup>,
    val itemListener: CheckupListRVClickListeners,
    val ctx: Context
) : RecyclerView.Adapter<CheckupsListAdapter.CheckupsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckupsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cardview_checkuplist, parent, false)
        return CheckupsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return checkups.size
    }

    fun getCheckup(position: Int): Checkup {
        return checkups[position]
    }

    override fun onBindViewHolder(holder: CheckupsViewHolder, position: Int) {
        Timber.d("checkups[position].nameObject=${checkups[position].nameObject}")
        holder.checkupsKind.text = checkups[position].kindObject
        holder.checkupsName.text = checkups[position].nameObject
        holder.listener = itemListener

    }

    class CheckupsViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        override fun onClick(v: View?) {
            listener.recyclerViewListClicked(v, this.layoutPosition)
        }


        var checkupsKind: TextView
        var checkupsName: TextView
        lateinit var listener: CheckupListRVClickListeners

        var cardView: CardView = itemView.cvCheckupList

        init {
            checkupsKind = itemView.kindObject
            checkupsName = itemView.nameObject
            view.setOnClickListener(this)
        }


    }


}