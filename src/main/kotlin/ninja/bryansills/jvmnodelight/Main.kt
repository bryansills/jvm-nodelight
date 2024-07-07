package ninja.bryansills.jvmnodelight

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import java.util.*
import kotlin.system.exitProcess

fun main() {

    val mainScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    mainScope.launchBlocking {
        val driver: SqlDriver = JdbcSqliteDriver(
            url = "jdbc:sqlite:jvm-nodelight.db",
            schema = Database.Schema.synchronous(),
            migrateEmptySchema = true,
            properties = Properties().apply { put("foreign_keys", "true") },
        )
        val database = Database(driver)

        val allUsers = database.userQueries.selectAll().awaitAsList()
        println(allUsers)

        val randUserId = allUsers.random().id

        val initialLocations = database.locationQueries.selectAll().awaitAsList()
        println(initialLocations)

        database
            .locationQueries
            .insert("New location", Clock.System.now().toString(), randUserId)
            .awaitAsOne()

        val nowLocations = database.locationQueries.selectAll().awaitAsList()
        println(nowLocations)
    }

}

/**
 * Do some work and then kill the process. We don't want Android Studio to think that we are still
 * waiting for work to finish.
 */
private fun CoroutineScope.launchBlocking(block: suspend CoroutineScope.() -> Unit) {
    try {
        runBlocking(this.coroutineContext) {
            block()
            this.cancel("Time to die.")
        }
    } catch (ex: Exception) {
        if (ex !is CancellationException) {
            ex.printStackTrace()
        }
    }

    exitProcess(0)
}