package com.productivesocial.psocial_selfmanager.feature.sync

import com.productivesocial.psocial_selfmanager.model.requests.SyncRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.syncRoutes(syncService: SyncService) {
    /**
     * Sync offline-buffered data with the server.
     *
     * The client sends all creates, updates, and deletes accumulated while offline.
     * New entities must include a `clientId` (UUID string). Entities that reference
     * other newly created entities use `projectClientId` instead of `projectId`.
     *
     * The response contains:
     * - `idMappings`: maps each `clientId` to its server-assigned ID so the client
     *   can reconcile its local database.
     * - `errors`: per-entity errors. A non-empty errors list does NOT mean the entire
     *   sync failed — successfully processed entities are committed regardless.
     */
    post("/sync") {
        val request = call.receive<SyncRequest>()
        val response = syncService.sync(request)
        val status = if (response.errors.isEmpty()) HttpStatusCode.OK else HttpStatusCode.MultiStatus
        call.respond(status, response)
    }
}
