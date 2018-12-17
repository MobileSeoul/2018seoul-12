package k_spot.jnm.k_spot.Get

data class GetMainFragResponse (
        val message: String,
        val data: MainFragData
)

data class MainFragData (
        val theme: ArrayList<Theme>,
        val main_recommand_spot: ArrayList<Main>,
        val main_best_place: ArrayList<Main>,
        val main_best_event: ArrayList<Main>
)

data class Main (
        val spot_id: Long,
        val name: String,
        var description: String,
        val img: String
)

data class Theme (
        val theme_id: Long,
        val title: String,
        val subtitle: String,
        val main_img: String
)
