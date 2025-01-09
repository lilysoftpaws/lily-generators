CREATE TABLE IF NOT EXISTS players (id BINARY(16) NOT NULL PRIMARY KEY, last_login BIGINT NOT NULL);
CREATE TABLE IF NOT EXISTS generators (id INTEGER PRIMARY KEY AUTOINCREMENT, "type" VARCHAR(30) NOT NULL, x INT NOT NULL, y INT NOT NULL, z INT NOT NULL, player_id BINARY(16) NOT NULL, CONSTRAINT fk_generators_player_id__id FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE RESTRICT ON UPDATE RESTRICT, CONSTRAINT chk_generators_signed_integer_id CHECK (id BETWEEN -2147483648 AND 2147483647), CONSTRAINT chk_generators_signed_integer_x CHECK (x BETWEEN -2147483648 AND 2147483647), CONSTRAINT chk_generators_signed_integer_y CHECK (y BETWEEN -2147483648 AND 2147483647), CONSTRAINT chk_generators_signed_integer_z CHECK (z BETWEEN -2147483648 AND 2147483647));
CREATE UNIQUE INDEX generators_player_id_type_x_y_z ON generators (player_id, "type", x, y, z);