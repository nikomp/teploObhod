package ru.bingosoft.teploObhod.ui.qrlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import dagger.android.support.AndroidSupportInjection
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.db.QRList.QRList
import ru.bingosoft.teploObhod.db.RouteList.RouteList
import ru.bingosoft.teploObhod.ui.mainactivity.MainActivity
import timber.log.Timber
import javax.inject.Inject


class QRListFragment : Fragment(), QRListContractView, QRListRVClickListeners,
    View.OnClickListener {

    @Inject
    lateinit var QRListPresenter: QRListPresenter

    private lateinit var root: View
    private lateinit var currentCheckup: Checkup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        Timber.d("QRListFragment.onCreateView")

        root = inflater.inflate(R.layout.fragment_qrlist, container, false)

        (this.requireActivity() as AppCompatActivity).supportActionBar?.setTitle(R.string.menu_checkups)

        val btnScan = root.findViewById(R.id.mbScanQr) as MaterialButton
        btnScan.setOnClickListener(this)

        val strDate = arguments?.getString("strDate")
        val tvQrListTitle: TextView = root.findViewById(R.id.qrListTitle)
        if (strDate != null && strDate != "") {
            tvQrListTitle.text = strDate
        }

        val tag = arguments?.getBoolean("checkUpForOrder")
        Timber.d("tag=$tag")
        QRListPresenter.attachView(this)
        if (tag == null || tag == false) {
            QRListPresenter.loadQRList() // Грузим все объекты
        }


        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        QRListPresenter.onDestroy()
    }

    override fun showCheckups(qrs: List<QRList>) {
        Timber.d("Список меток")
        val checkuplist_recycler_view =
            root.findViewById(R.id.checkuplist_recycler_view) as RecyclerView
        checkuplist_recycler_view.layoutManager = LinearLayoutManager(this.activity)
        val adapter = QRListAdapter(qrs, this, this)
        Timber.d("В адаптере строк=${adapter.itemCount}")
        checkuplist_recycler_view.adapter = adapter
    }


    override fun recyclerViewListClicked(v: View?, position: Int) {
        Timber.d("Открываем выбранное обследование")

        Timber.d("Сканируем QR")
        // Запустим сканер QR
        /*try {
            val intent = Intent("com.google.zxing.client.android.SCAN")
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE")
            parentFragment.startActivityForResult(intent, Const.RequestCodes.QR_SCAN)
        } catch (e: Exception) {

            val marketUri = Uri.parse("market://details?id=com.google.zxing.client.android")
            val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
            parentFragment.startActivity(marketIntent)
        }*/

        //Заглушка без сканера
        // Если чеклист открывается из спсика блокируем сохранение изменений
        val idQr = 1L
        val sfm = (activity as MainActivity).supportFragmentManager
        (activity as MainActivity).mainPresenter.openCheckup(sfm, idQr, enabled = false)

        /*val fragmentCheckup = CheckupFragment()
        val fragmentManager = this.requireActivity().supportFragmentManager

        // Добавим id текущего фрагмента в аргументы, чтоб можно было восстановить при onBackPressed
        val bundle = Bundle()
        val idCheckup = currentCheckup.id
        bundle.putLong("checkupId", idCheckup)
        fragmentCheckup.arguments = bundle

        fragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragmentCheckup, "checkup_fragment_tag")
            .addToBackStack(null)
            .commit()

        fragmentManager.executePendingTransactions()

        (this.requireActivity() as FragmentsContractActivity).setCheckup(currentCheckup)*/


    }

    fun showQRListRoute(order: RouteList) {
        QRListPresenter.loadQRListByRoute(order.id)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.mbScanQr -> {
                    //Заглушка без сканера
                    val idQr = 1L
                    val sfm = (activity as MainActivity).supportFragmentManager
                    (activity as MainActivity).mainPresenter.openCheckup(sfm, idQr)
                }
            }
        }
    }


}