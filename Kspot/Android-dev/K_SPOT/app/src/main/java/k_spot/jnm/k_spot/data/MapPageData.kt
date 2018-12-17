package k_spot.jnm.k_spot.data

data class FilterOptionData(
        var order_option : Int,
        var distance : Double,
        var latitude : Double,
        var longitude : Double,
        var is_food : Int,
        var is_cafe : Int,
        var is_sights : Int,
        var is_event : Int,
        var is_etc :Int
)


data class SpotLanguageName(
        val ko : String,
        val en : String
)

