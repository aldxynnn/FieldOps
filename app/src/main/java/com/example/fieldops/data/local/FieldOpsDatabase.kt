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
        NotificationEntity::class,
        CustomerEntity::class
    ],
    version = 8,
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

    abstract fun customerDao(): CustomerDao

    companion object {

        private const val DATABASE_NAME =
            "fieldops_database"

        private val MIGRATION_1_2 =
            object : Migration(1, 2) {

                override fun migrate(
                    db: SupportSQLiteDatabase
                ) {

                    db.execSQL(
                        """
                    CREATE TABLE IF NOT EXISTS sync_operations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        workOrderId TEXT NOT NULL,
                        operationType TEXT NOT NULL,
                        payload TEXT NOT NULL,
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
                    db: SupportSQLiteDatabase
                ) {

                    db.execSQL(
                        """
                    CREATE TABLE IF NOT EXISTS location_evidence (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        workOrderId TEXT NOT NULL,
                        latitude REAL NOT NULL,
                        longitude REAL NOT NULL,
                        capturedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                    )
                }
            }

        private val MIGRATION_3_4 =
            object : Migration(3, 4) {

                override fun migrate(
                    db: SupportSQLiteDatabase
                ) {

                    db.execSQL(
                        """
                    CREATE TABLE IF NOT EXISTS activity_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        workOrderId TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        type TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                    )
                }
            }

        private val MIGRATION_4_5 =
            object : Migration(4, 5) {

                override fun migrate(
                    db: SupportSQLiteDatabase
                ) {

                    db.execSQL(
                        """
                    CREATE TABLE IF NOT EXISTS work_order_notes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        workOrderId TEXT NOT NULL,
                        note TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                    )

                    db.execSQL(
                        """
                    CREATE TABLE IF NOT EXISTS work_order_photos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        workOrderId TEXT NOT NULL,
                        filePath TEXT NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                    )
                }
            }

        private val MIGRATION_5_6 =
            object : Migration(5, 6) {

                override fun migrate(
                    db: SupportSQLiteDatabase
                ) {

                    db.execSQL(
                        """
                    CREATE TABLE IF NOT EXISTS notifications (
                        id TEXT NOT NULL PRIMARY KEY,
                        title TEXT NOT NULL,
                        message TEXT NOT NULL,
                        type TEXT NOT NULL,
                        createdAt INTEGER NOT NULL,
                        isRead INTEGER NOT NULL
                    )
                    """.trimIndent()
                    )
                }
            }

        private val MIGRATION_6_7 =
            object : Migration(6, 7) {

                override fun migrate(
                    db: SupportSQLiteDatabase
                ) {

                    db.execSQL(
                        """
                    ALTER TABLE work_orders
                    ADD COLUMN assetId TEXT
                    """.trimIndent()
                    )
                }
            }


        private val MIGRATION_7_8 =
            object : Migration(7, 8) {

                override fun migrate(
                    db: SupportSQLiteDatabase
                ) {

                    db.execSQL(
                        """
                    CREATE TABLE IF NOT EXISTS customers (
                        companyId TEXT NOT NULL,
                        customerId TEXT NOT NULL,
                        payload TEXT NOT NULL,
                        PRIMARY KEY(companyId, customerId)
                    )
                    """.trimIndent()
                    )
                }
            }

        @Volatile
        private var INSTANCE: FieldOpsDatabase? =
            null

        fun getInstance(
            context: Context
        ): FieldOpsDatabase {

            return INSTANCE
                ?: synchronized(this) {

                    INSTANCE
                        ?: Room.databaseBuilder(
                            context.applicationContext,
                            FieldOpsDatabase::class.java,
                            DATABASE_NAME
                        )
                            .addMigrations(
                                MIGRATION_1_2,
                                MIGRATION_2_3,
                                MIGRATION_3_4,
                                MIGRATION_4_5,
                                MIGRATION_5_6,
                                MIGRATION_6_7,
                                MIGRATION_7_8
                            )
                            .build()
                            .also {
                                INSTANCE = it
                            }
                }
        }
    }

}