package com.example.fieldops.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        WorkOrderEntity::class,
        SyncOperationEntity::class,
        LocationEvidenceEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class FieldOpsDatabase : RoomDatabase() {

    abstract fun workOrderDao(): WorkOrderDao

    abstract fun syncOperationDao(): SyncOperationDao

    abstract fun locationEvidenceDao(): LocationEvidenceDao

    companion object {

        @Volatile
        private var INSTANCE: FieldOpsDatabase? = null

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS sync_operations (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            workOrderId TEXT NOT NULL,
                            operation TEXT NOT NULL,
                            createdAt INTEGER NOT NULL,
                            status TEXT NOT NULL
                        )
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_2_3 =
            object : Migration(2, 3) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS location_evidence (
                            workOrderId TEXT NOT NULL,
                            latitude REAL NOT NULL,
                            longitude REAL NOT NULL,
                            capturedAt INTEGER NOT NULL,
                            PRIMARY KEY(workOrderId)
                        )
                        """.trimIndent()
                    )
                }
            }

        fun getInstance(
            context: Context
        ): FieldOpsDatabase {

            return INSTANCE
                ?: synchronized(this) {

                    INSTANCE
                        ?: Room.databaseBuilder(
                            context.applicationContext,
                            FieldOpsDatabase::class.java,
                            "fieldops_database"
                        )
                            .addMigrations(
                                MIGRATION_1_2,
                                MIGRATION_2_3
                            )
                            .build()
                            .also { database ->
                                INSTANCE = database
                            }
                }
        }
    }
}