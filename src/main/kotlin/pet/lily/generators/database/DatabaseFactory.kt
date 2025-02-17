package pet.lily.generators.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.flywaydb.core.Flyway
import pet.lily.generators.Generators

object DatabaseFactory {
    fun initialize(dbPath: String) {
        val flyway = Flyway.configure(Generators::class.java.classLoader)
            .dataSource("jdbc:sqlite:$dbPath", "", "")
            .locations("classpath:db/migration")
            .load()
        flyway.migrate()

        Database.connect(
            url = "jdbc:sqlite:$dbPath",
            driver = "org.sqlite.JDBC"
        )

        transaction {
            addLogger(StdOutSqlLogger)
        }
    }
}