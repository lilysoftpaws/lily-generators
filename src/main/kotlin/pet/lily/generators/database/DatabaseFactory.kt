package pet.lily.generators.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.flywaydb.core.Flyway
import pet.lily.generators.database.tables.Generators
import pet.lily.generators.database.tables.Players
import java.util.concurrent.locks.ReentrantReadWriteLock

object DatabaseFactory {
    val dbLock = ReentrantReadWriteLock()

    fun initialize(dbPath: String) {
        // flyway migrations
        val flyway = Flyway.configure(pet.lily.generators.Generators::class.java.classLoader)
            .dataSource("jdbc:sqlite:$dbPath", "", "")
            .locations("classpath:db/migration")
            .load()

        flyway.migrate()

        // connect
        Database.connect(
            url = "jdbc:sqlite:$dbPath",
            driver = "org.sqlite.JDBC"
        )

        // init tables
        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(Players, Generators)
        }
    }
}