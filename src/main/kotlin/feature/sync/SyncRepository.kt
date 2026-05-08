package com.productivesocial.feature.sync

import com.productivesocial.model.requests.SyncRequest
import com.productivesocial.model.responses.SyncResponse

interface SyncRepository {
    suspend fun sync(request: SyncRequest): SyncResponse
}
