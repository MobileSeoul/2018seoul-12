package k_spot.jnm.k_spot.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import k_spot.jnm.k_spot.fragment.CategoryPageFragBraodcastTab
import k_spot.jnm.k_spot.fragment.CategoryPageFragCelebrityTab

class CategoryPageTabPagerAdapter(val tabCount : Int, fm : FragmentManager) : FragmentStatePagerAdapter(fm) {

    val celebrityTab : Fragment = CategoryPageFragCelebrityTab()
    val broadcastTab : Fragment = CategoryPageFragBraodcastTab()

    override fun getItem(position: Int): Fragment? {
        return when (position){
            0 -> celebrityTab
            1 -> broadcastTab
            else -> null
        }
    }

    override fun getCount(): Int = tabCount


}