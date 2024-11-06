package com.epilogs.game_trail_tracker.database

import com.epilogs.game_trail_tracker.database.daos.MeasurementCategoryDao
import com.epilogs.game_trail_tracker.database.daos.MeasurementTypeDao
import com.epilogs.game_trail_tracker.database.entities.MeasurementCategory
import com.epilogs.game_trail_tracker.database.entities.MeasurementType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseSeeder(
    private val categoryDao: MeasurementCategoryDao,
    private val measurementTypeDao: MeasurementTypeDao
) {

    fun seed() {
        CoroutineScope(Dispatchers.IO).launch {
            val categories = listOf(
                MeasurementCategory(name = "Horn"),
                MeasurementCategory(name = "Antler"),
                MeasurementCategory(name = "Tusk"),
                MeasurementCategory(name = "Skull"),
                MeasurementCategory(name = "Crocodilian")
            )

            categories.forEach { category ->
                categoryDao.insertMeasurementCategory(category)
            }

            val measurementTypes = listOf(
                MeasurementType(
                    name = "Horn Length (Tip to Base)",
                    measurementCategoryId = 1),
                MeasurementType(
                    name = "Horn Thickness at Base",
                    measurementCategoryId = 1),
                MeasurementType(
                    name = "Horn Thickness at 1/4 Length",
                    measurementCategoryId = 1
                ),
                MeasurementType(
                    name = "Horn Thickness at 1/2 Length",
                    measurementCategoryId = 1
                ),
                MeasurementType(
                    name = "Horn Thickness at 3/4 Length",
                    measurementCategoryId = 1
                ),
                MeasurementType(
                    name = "Distance Between Horn Tips",
                    measurementCategoryId = 1),
                MeasurementType(
                    name = "Main Antler Length (Tip to Base)",
                    measurementCategoryId = 2
                ),
                MeasurementType(
                    name = "Distance Between Antlers (Inside)",
                    measurementCategoryId = 2
                ),
                MeasurementType(
                    name = "Total Number of Points (longer than 1 inch)",
                    measurementCategoryId = 2
                ),
                MeasurementType(
                    name = "Antler Thickness at Base",
                    measurementCategoryId = 2
                ),
                MeasurementType(
                    name = "Tusk Length (Tip to Base)",
                    measurementCategoryId = 3
                ),
                MeasurementType(
                    name = "Tusk Thickness at Base",
                    measurementCategoryId = 3
                ),
                MeasurementType(
                    name = "Skull Length (Nose to Back)",
                    measurementCategoryId = 4
                ),
                MeasurementType(
                    name = "Skull Width (Widest Part)",
                    measurementCategoryId = 4
                ),
                MeasurementType(
                    name = "Full Body Length (Nose to Tail)",
                    measurementCategoryId = 5
                ),
                MeasurementType(
                    name = "Head Length (Nose to Back of Head)",
                    measurementCategoryId = 5
                ),
            )

            measurementTypes.forEach { type ->
                measurementTypeDao.insertMeasurementType(type)
            }
        }
    }
}