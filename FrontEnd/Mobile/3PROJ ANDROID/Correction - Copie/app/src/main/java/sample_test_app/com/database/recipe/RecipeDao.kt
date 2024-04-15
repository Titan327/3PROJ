package sample_test_app.com.database.recipe

import androidx.room.*

@Dao
interface RecipeDao {
    @Query(value = "SELECT * FROM recipes ORDER BY id ASC")
    suspend fun getAllTasks(): List<Recipe>

    @Query(value = "SELECT * FROM recipes WHERE id = :id")
    suspend fun getTask(id: Int): Recipe

    @Insert
    suspend fun insert(recipe: Recipe)

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)
}