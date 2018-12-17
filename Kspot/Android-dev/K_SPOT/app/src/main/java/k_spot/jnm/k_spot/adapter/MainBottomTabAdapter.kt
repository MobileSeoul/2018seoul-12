package k_spot.jnm.k_spot.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import k_spot.jnm.k_spot.fragment.*

class MainBottomTabAdapter(private val fragmentCount : Int, fm : FragmentManager) : FragmentStatePagerAdapter(fm){
    val mainPage = MainPageFragment()
    val categoryPage = CategoryPageFragment()
    val mapPage = MapPageFragment()
    val myPage = MyPageFragment()

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> mainPage
            1 -> categoryPage
            2 -> mapPage
            3 -> myPage
            else -> null
        }
    }

    override fun getCount(): Int = fragmentCount
}