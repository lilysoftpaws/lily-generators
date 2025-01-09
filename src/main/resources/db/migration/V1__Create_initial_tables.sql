CREATE TABLE IF NOT EXISTS players (
    id BINARY(16) NOT NULL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS generators (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    "type" VARCHAR(30) NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    z INT NOT NULL,
    player_id BINARY(16) NOT NULL,
    CONSTRAINT fk_generators_player_id FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE UNIQUE INDEX generators_player_id_type_x_y_z ON generators (player_id, "type", x, y, z);
