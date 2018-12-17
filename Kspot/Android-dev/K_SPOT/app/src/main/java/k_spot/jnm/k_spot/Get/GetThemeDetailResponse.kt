package k_spot.jnm.k_spot.Get

data class GetThemeDetailResponse (
        val message: String,
        val data: ThemeDetailData
)

data class ThemeDetailData (
        val theme: ThemeDetailThemeData,
        val theme_contents: ArrayList<ThemeDetailThemeContentData>
)

data class ThemeDetailThemeData (
        val title: ArrayList<String>,
        val subtitle: String,
        val img: String
)

data class ThemeDetailThemeContentData (
        val spot_id: Long,
        val title: String,
        val description: ArrayList<String>,
        val img: String
)
