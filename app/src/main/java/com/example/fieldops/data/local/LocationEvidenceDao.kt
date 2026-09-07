package com.example.fieldops.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationEvidenceDao {

    @Query(
        """
        SELECT * FROM location_evidence
        WHERE workOrderId = :workOrderId
        LIMIT 1
        """
    )
    fun observeLocationEvidence(
        workOrderId: String
    ): Flow<LocationEvidenceEntity?>

    @Query(
        """
        SELECT * FROM location_evidence
        WHERE workOrderId = :workOrderId
        LIMIT 1
        """
    )
    suspend fun getLocationEvidence(
        workOrderId: String
    ): LocationEvidenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocationEvidence(
        evidence: LocationEvidenceEntity
    )

    @Query(
        """
        DELETE FROM location_evidence
        WHERE workOrderId = :workOrderId
        """
    )
    suspend fun deleteLocationEvidence(
        workOrderId: String
    )
}