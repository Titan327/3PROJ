package sample_test_app.com.models

import kotlinx.serialization.Serializable

@Serializable
class Category {
    var id: String? = ""
    var label: String? = ""
    var icon: String? = ""
    var color: String? = ""
}

object CategoryStore {
    var categories: List<Category> = listOf()
}