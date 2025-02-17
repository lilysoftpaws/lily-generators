CREATE TABLE players (
    id TEXT PRIMARY KEY
);

CREATE TABLE generators (
    id TEXT PRIMARY KEY,
    type VARCHAR(32) NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    z INT NOT NULL,
    player_id TEXT NOT NULL,
    CONSTRAINT fk_player FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE,
    CONSTRAINT unique_location UNIQUE (x, y, z)
);

CREATE INDEX idx_player_id ON generators (player_id);
