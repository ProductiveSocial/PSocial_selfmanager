package com.productivesocial.psocial_selfmanager.feature.sync

import com.productivesocial.psocial_selfmanager.model.requests.SyncRequest
import com.productivesocial.psocial_selfmanager.model.responses.SyncResponse

interface SyncRepository {
    suspend fun sync(request: SyncRequest): SyncResponse
}
