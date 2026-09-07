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
        LocationEvidenceEntity::class,
        ActivityHistoryEntity::class,
        WorkOrderNoteEntity::class,
        WorkOrderPhotoEntity::class,
        NotificationEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class FieldOpsDatabase : RoomDatabase() {

    abstract fun workOrderDao(): WorkOrderDao

    abstract fun syncOperationDao(): SyncOperationDao

    abstract fun locationEvidenceDao(): LocationEvidenceDao

    abstract fun activityHistoryDao(): ActivityHistoryDao

    abstract fun workOrderNoteDao(): WorkOrderNoteDao

    abstract fun workOrderPhotoDao(): WorkOrderPhotoDao

    abstract fun notificationDao(): NotificationDao

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

        private val MIGRATION_3_4 =
            object : Migration(3, 4) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS activity_history (
                    id TEXT NOT NULL,
                    workOrderId TEXT NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    time TEXT NOT NULL,
                    type TEXT NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
                    )
                }
            }

        private val MIGRATION_4_5 =
            object : Migration(4, 5) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS work_order_notes (
                    id TEXT NOT NULL,
                    workOrderId TEXT NOT NULL,
                    note TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
                    )

                    database.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS work_order_photos (
                    id TEXT NOT NULL,
                    workOrderId TEXT NOT NULL,
                    title TEXT NOT NULL,
                    filePath TEXT NOT NULL,
                    createdAt INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
                """.trimIndent()
                    )
                }
            }

        private val MIGRATION_5_6 =
            object : Migration(5, 6) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                CREATE TABLE IF NOT EXISTS notifications (
                    id TEXT NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL,
                    time TEXT NOT NULL,
                    type TEXT NOT NULL,
                    isRead INTEGER NOT NULL,
                    createdAt INTEGER NOT NULL,
                    PRIMARY KEY(id)
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
                                MIGRATION_2_3,
                                MIGRATION_3_4,
                                MIGRATION_4_5,
                                MIGRATION_5_6
                            )
                            .build()
                            .also { database ->
                                INSTANCE = database
                            }
                }
        }
    }

}