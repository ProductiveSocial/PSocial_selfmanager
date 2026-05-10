package com.productivesocial.psocial_selfmanager.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/** Execute a block within a database transaction on the IO dispatcher. */
suspend fun <T> query(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}
