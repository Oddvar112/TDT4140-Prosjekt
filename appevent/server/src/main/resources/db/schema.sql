CREATE TABLE IF NOT EXISTS appuser (
    id BINARY(16) PRIMARY KEY,
    brukernavn VARCHAR(50) UNIQUE NOT NULL,
    passord VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS activity (
    id BINARY(16) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    date_time DATETIME NOT NULL,
    location VARCHAR(255) NOT NULL,
    description VARCHAR(1000)
);

CREATE TABLE IF NOT EXISTS activity_participants (
    activity_id BINARY(16),
    participant_id BINARY(16),
    PRIMARY KEY (activity_id, participant_id),
    FOREIGN KEY (activity_id) REFERENCES activity(id),
    FOREIGN KEY (participant_id) REFERENCES appuser(id)
);