package ru.bingosoft.teploObhod.ui.routeList

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_cardview_routelist.view.*
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.RouteList.RouteList
import java.text.SimpleDateFormat
import java.util.*


class RouteListAdapter(
    val routeList: List<RouteList>,
    private val itemListener: RouteListRVClickListeners,
    private val ctx: Context
) : RecyclerView.Adapter<RouteListAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cardview_routelist, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    fun getOrder(position: Int): RouteList {
        return routeList[position]
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.route.text = "Маршрут ${routeList[position].route}"
        holder.dateRoute.text = SimpleDateFormat(
            "dd.MM.yyyy HH:mm:ss",
            Locale("ru", "RU")
        ).format(routeList[position].dateRoute)

        /*holder.phone.setOnTouchListener { _, _ ->
            Timber.d("fabButton.setOnClickListener ${routeList[position].phone}")
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${routeList[position].phone}"))
            if (intent.resolveActivity(ctx.packageManager) != null) {
                startActivity(ctx,intent,null)
            }
            true
        }*/

        /*if (orders[position].state.equals("1")) {
            holder.targetImage.setImageResource(R.drawable.ic_flash_on_black_24dp)
        } else {
            holder.targetImage.setImageResource(R.drawable.ic_flash_on_black_done24dp)
        }*/


        holder.listener = itemListener

        /*if (orders[position].checked) {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    ctx,
                    R.color.colorCardSelect
                ))
        } else {
            holder.cardView.setCardBackgroundColor(
                ContextCompat.getColor(
                    ctx,
                    R.color.colorCardItem
                ))
        }*/
    }

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        override fun onClick(v: View?) {
            listener.recyclerViewListClicked(v, this.layoutPosition)
        }

        var route: TextView = itemView.route
        var dateRoute: TextView = itemView.dateRoute
        var targetImage: ImageView = itemView.targetImage as ImageView
        //var fabButton: FloatingActionButton = itemView.fab2 as FloatingActionButton
        lateinit var listener: RouteListRVClickListeners

        var cardView: CardView = itemView.cv

        init {


            view.setOnClickListener(this)
        }


    }

}