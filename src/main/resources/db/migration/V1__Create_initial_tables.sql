CREATE TABLE players (
    id TEXT PRIMARY KEY
);

CREATE TABLE generators (
    id TEXT PRIMARY KEY,
    type VARCHAR(32) NOT NULL,
    world TEXT NOT NULL,
    x REAL NOT NULL,
    y REAL NOT NULL,
    z REAL NOT NULL,
    player_id TEXT NOT NULL,
    CONSTRAINT fk_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    CONSTRAINT unique_location UNIQUE (world, x, y, z)
);

CREATE INDEX idx_player_id ON generators (player_id);
