package ru.bingosoft.teploObhod.ui.routeList

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_cardview_routelist.view.*
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.RouteList.RouteList
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class RouteListAdapter(
    val routeList: List<RouteList>,
    private val itemListener: RouteListRVClickListeners,
    private val ctx: Context
) : RecyclerView.Adapter<RouteListAdapter.OrderViewHolder>() {
    lateinit var parentView: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        parentView = parent
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
        holder.nameRouteTemplate.text = routeList[position].nameRouteTemplate
        if (routeList[position].dateRoute != null) {
            val date = routeList[position].dateRoute
            holder.dateRoute.text = SimpleDateFormat(
                "dd MMM HH:mm",
                Locale("ru", "RU")
            ).format(date!!)

            // Считаем сколько осталось до наступления маршрута
            val currentTime = Calendar.getInstance().time
            Timber.d("currentTime=$currentTime")

            val df: DateFormat = SimpleDateFormat("hh:mm")
            val strDate1 = df.format(currentTime)
            val strDate2 = df.format(date)
            Timber.d("strDate1=$strDate1")

            val date1: Date = df.parse(strDate1)
            val date2: Date = df.parse(strDate2)
            val diff = (date2.time - date1.time) / 1000

            val hours = diff / 3600
            val minutes = (diff - (3600 * hours)) / 60
            holder.untilTime.text = "$hours ч $minutes мин"

            if (diff < 0) {
                holder.untilTime.setTextColor(ContextCompat.getColor(ctx, R.color.minusTime))
            }
        }

        holder.tvViewSchema.setOnClickListener { _ ->
            Timber.d("viewSchema ${routeList[position].dateRoute}")
            alertSchema()
            /*val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${orders[position].phone}"))
            if (intent.resolveActivity(ctx.packageManager) != null) {
                ContextCompat.startActivity(ctx, intent, null)
            }*/
        }



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

        var nameRouteTemplate: TextView = itemView.route
        var dateRoute: TextView = itemView.dateRoute
        var targetImage: ImageView = itemView.targetImage as ImageView
        var untilTime: TextView = itemView.findViewById(R.id.untilTime)
        var tvViewSchema: TextView = itemView.findViewById(R.id.viewSchema)
        //var fabButton: FloatingActionButton = itemView.fab2 as FloatingActionButton
        lateinit var listener: RouteListRVClickListeners

        var cardView: CardView = itemView.cv

        init {
            view.setOnClickListener(this)
        }


    }

    private fun alertSchema() {
        Timber.d("alertSchema")
        lateinit var alertSchema: AlertDialog
        val layoutInflater = LayoutInflater.from(ctx)
        val dialogView: View =
            layoutInflater.inflate(R.layout.alert_schema, parentView, false)

        val image = dialogView.findViewById<ImageView>(R.id.ivSchema)
        // При загрузки схем из папки использовать Glide пример см. с загрузкой фото
        Timber.d("грузим схему")
        image.setImageResource(R.drawable.schema)

        val builder = AlertDialog.Builder(ctx)

        /*dialogView.buttonInetOK.setOnClickListener {
            Timber.d("dialogView.buttonInetOK")
            //TODO возможно тут потребуется вывести окно авторизации
            //showMessageLogin(R.string.auth_ok)
            alertSchema.dismiss()

        }*/

        /*dialogView.buttonInetNo.setOnClickListener {
            alertSchema.dismiss()
        }*/

        builder.setView(dialogView)
        builder.setCancelable(true)
        alertSchema = builder.create()
        alertSchema.show()
        //alertSchema.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,500)


    }

}