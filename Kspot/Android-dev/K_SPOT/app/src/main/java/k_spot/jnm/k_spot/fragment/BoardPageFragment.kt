package k_spot.jnm.k_spot.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import k_spot.jnm.k_spot.R
import k_spot.jnm.k_spot.activity.CategoryDetailActivity
import k_spot.jnm.k_spot.activity.MapDetailActivity
import k_spot.jnm.k_spot.activity.UserInfoEditActivity
import kotlinx.android.synthetic.main.fragment_board_page.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast

class BoardPageFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_board_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        textbtn_category_detail.setOnClickListener {
            startActivity<CategoryDetailActivity>()
        }

        textbtn_map.setOnClickListener {
            startActivity<MapDetailActivity>("X" to 100, "Y" to 100)
        }

    }
}