package k_spot.jnm.k_spot.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.SearchActivity
import k_spot.jnm.k_spot.activity.MainActivity
import k_spot.jnm.k_spot.adapter.CategoryPageTabPagerAdapter
import k_spot.jnm.k_spot.db.SharedPreferenceController
import kotlinx.android.synthetic.main.fragment_category_list.*
import kotlinx.android.synthetic.main.fragment_category_list.view.*
import kotlinx.android.synthetic.main.tablayout_category_page_fragment.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class CategoryPageFragment : Fragment() {

    val categoryPageTabPagerAdapter : CategoryPageTabPagerAdapter by lazy {
        CategoryPageTabPagerAdapter(2, childFragmentManager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_category_list, container, false)
        setOnClickListener(view)
        return view


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        configureRankTabMenu()
        translateCategoryPage()
        category_list_fragment_translation_btn.setOnClickListener {
            (activity as MainActivity).changeMainActivityLanguage()
        }

    }

    private fun setOnClickListener(view: View) {
        view.category_list_fragment_search_btn.setOnClickListener {
            startActivity<SearchActivity>()
        }
    }


    private fun whereIsTab(position: Int) {
        if (position == 0) {
            celebrity_title_tv.setTextColor(Color.parseColor("#6B6B6B"))
            broadcast_title_tv.setTextColor(Color.parseColor("#D8D8D8"))
        } else {
            celebrity_title_tv.setTextColor(Color.parseColor("#D8D8D8"))
            broadcast_title_tv.setTextColor(Color.parseColor("#6B6B6B"))
        }
    }

    fun configureRankTabMenu() {
        category_list_fragment_viewpager.adapter = categoryPageTabPagerAdapter
        category_list_fragment_tablayout.setupWithViewPager(category_list_fragment_viewpager)

        val headerView: View = (activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.tablayout_category_page_fragment, null, false)
        val celebrity = headerView.findViewById(R.id.celebrity_tab_btn) as RelativeLayout
        val broadcast = headerView.findViewById(R.id.broadcast_tab_btn) as RelativeLayout

        category_list_fragment_tablayout.getTabAt(0)!!.customView = celebrity
        category_list_fragment_tablayout.getTabAt(1)!!.customView = broadcast

        category_list_fragment_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                whereIsTab(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                whereIsTab(tab!!.position)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                whereIsTab(tab!!.position)
            }
        })
    }



    private fun translateCategoryPage() {
        if (SharedPreferenceController.getFlag(context!!) == "0") {
            celebrity_title_tv.text = "연예인"
            broadcast_title_tv.text = "방송"
        } else {
            celebrity_title_tv.text = "Celebrity"
            broadcast_title_tv.text = "Broadcast"
        }
    }

    fun translateCategoryLanguage(){
        translateCategoryPage()
        translateCategoryDataReconnection()
    }
    private fun translateCategoryDataReconnection(){
        (categoryPageTabPagerAdapter.celebrityTab as CategoryPageFragCelebrityTab).requestCategoryList()
        (categoryPageTabPagerAdapter.broadcastTab as CategoryPageFragBraodcastTab).requestCategoryList()
    }
}