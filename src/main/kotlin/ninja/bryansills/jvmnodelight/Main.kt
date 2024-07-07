package ninja.bryansills.jvmnodelight

import app.cash.sqldelight.async.coroutines.awaitAsList
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
        )
        val database = Database(driver)

        val initialResults = database.userQueries.selectAll().awaitAsList()
        println(initialResults)

        database.userQueries.insert("Bob", 42, Clock.System.now().toString())
        database.userQueries.insert("Cate", 22, Clock.System.now().toString())
        database.userQueries.insert("Daniel", 15, Clock.System.now().toString())

        val subsequentResults = database.userQueries.selectAll().awaitAsList()
        println(subsequentResults)
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