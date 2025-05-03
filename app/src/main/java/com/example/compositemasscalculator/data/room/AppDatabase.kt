package com.example.compositemasscalculator.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.compositemasscalculator.data.dao.CalculationDao
import com.example.compositemasscalculator.data.dao.MaterialDao
import com.example.compositemasscalculator.data.entities.Calculation
import com.example.compositemasscalculator.data.entities.Material
import com.example.compositemasscalculator.data.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Calculation::class, Material::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun calculationDao(): CalculationDao
    abstract fun materialDao(): MaterialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "composite_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Запускаем в фоновом потоке
                            CoroutineScope(Dispatchers.IO).launch {
                                getDatabase(context).materialDao().apply {
                                    // Добавляем начальные материалы
                                    insert(Material(code = "A", name = "Углеткань ITECMA 22502", density = 200.0))
                                    insert(Material(code = "B", name = "Лента углеродная ACM C200P", density = 200.0))
                                    insert(Material(code = "C", name = "Стеклоткань T-10-14", density = 290.0))
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}