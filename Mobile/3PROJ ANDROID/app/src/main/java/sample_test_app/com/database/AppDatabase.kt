package sample_test_app.com.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import sample_test_app.com.database.recipe.Recipe
import sample_test_app.com.database.recipe.RecipeDao

@Database(entities = [Recipe::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): RecipeDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(ctx: Context): AppDatabase {
            if(instance == null)
                instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                 .build()

            return instance!!
        }
    }
}