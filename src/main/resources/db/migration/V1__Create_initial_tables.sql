CREATE TABLE players (
    id TEXT PRIMARY KEY
);

CREATE TABLE generators (
    id TEXT PRIMARY KEY,
    type VARCHAR(32) NOT NULL,
    world TEXT NOT NULL DEFAULT 'world',
    x REAL NOT NULL DEFAULT 0.0,
    y REAL NOT NULL DEFAULT 0.0,
    z REAL NOT NULL DEFAULT 0.0,
    player_id TEXT NOT NULL,
    CONSTRAINT fk_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    CONSTRAINT unique_location UNIQUE (world, x, y, z)
);

CREATE INDEX idx_player_id ON generators (player_id);
