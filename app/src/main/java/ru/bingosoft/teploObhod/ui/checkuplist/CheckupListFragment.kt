package ru.bingosoft.teploObhod.ui.checkuplist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_checkuplist.*
import ru.bingosoft.teploObhod.R
import ru.bingosoft.teploObhod.db.Checkup.Checkup
import ru.bingosoft.teploObhod.db.RouteList.RouteList
import ru.bingosoft.teploObhod.ui.checkup.CheckupFragment
import ru.bingosoft.teploObhod.ui.mainactivity.FragmentsContractActivity
import timber.log.Timber
import javax.inject.Inject


class CheckupListFragment : Fragment(), CheckupListContractView, CheckupListRVClickListeners {

    @Inject
    lateinit var checkupListPresenter: CheckupListPresenter

    private lateinit var root: View
    private lateinit var currentCheckup: Checkup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AndroidSupportInjection.inject(this)
        Timber.d("CheckupListFragment.onCreateView")

        root = inflater.inflate(R.layout.fragment_checkuplist, container, false)

        (this.requireActivity() as AppCompatActivity).supportActionBar?.setTitle(R.string.menu_checkups)

        val tag = arguments?.getBoolean("checkUpForOrder")
        Timber.d("tag=$tag")
        checkupListPresenter.attachView(this)
        if (tag == null || tag == false) {
            checkupListPresenter.loadCheckupList() // Грузим все объекты
        }


        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        checkupListPresenter.onDestroy()
    }

    override fun showCheckups(checkups: List<Checkup>) {
        Timber.d("Список обследований")
        val checkuplist_recycler_view =
            root.findViewById(R.id.checkuplist_recycler_view) as RecyclerView
        checkuplist_recycler_view.layoutManager = LinearLayoutManager(this.activity)
        val adapter = CheckupsListAdapter(checkups, this, this.requireContext())
        Timber.d("В адаптере строк=${adapter.itemCount}")
        checkuplist_recycler_view.adapter = adapter
    }


    override fun recyclerViewListClicked(v: View?, position: Int) {
        Timber.d("Открываем выбранное обследование")

        currentCheckup =
            (checkuplist_recycler_view.adapter as CheckupsListAdapter).getCheckup(position)
        Timber.d(currentCheckup.toString())

        val fragmentCheckup = CheckupFragment()
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

        (this.requireActivity() as FragmentsContractActivity).setCheckup(currentCheckup)

    }

    fun showCheckupListOrder(order: RouteList) {
        checkupListPresenter.loadCheckupListByOrder(order.id)
    }


}