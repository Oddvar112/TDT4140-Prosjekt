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
    description VARCHAR(1000),
    type VARCHAR(255) NOT NULL,
    is_private BOOLEAN NOT NULL DEFAULT FALSE,
    owner_id BINARY(16) NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES appuser(id)
);

CREATE TABLE IF NOT EXISTS activity_participants (
    activity_id BINARY(16),
    participant_id BINARY(16),
    PRIMARY KEY (activity_id, participant_id),
    FOREIGN KEY (activity_id) REFERENCES activity(id),
    FOREIGN KEY (participant_id) REFERENCES appuser(id)
);

CREATE TABLE IF NOT EXISTS user_friends (
    user_id BINARY(16),
    friend_id BINARY(16),
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES appuser(id),
    FOREIGN KEY (friend_id) REFERENCES appuser(id),
    CHECK (user_id != friend_id)
);

CREATE TABLE IF NOT EXISTS friend_request (
    id BINARY(16) PRIMARY KEY,
    sender_id BINARY(16) NOT NULL,
    receiver_id BINARY(16) NOT NULL,
    accepted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES appuser(id),
    FOREIGN KEY (receiver_id) REFERENCES appuser(id),
    CHECK (sender_id != receiver_id)
);

CREATE TABLE IF NOT EXISTS activity_invitees (
    activity_id BINARY(16) NOT NULL,
    invitee_id BINARY(16) NOT NULL,
    PRIMARY KEY (activity_id, invitee_id),
    FOREIGN KEY (activity_id) REFERENCES activity(id),
    FOREIGN KEY (invitee_id) REFERENCES appuser(id)
);


CREATE TABLE IF NOT EXISTS activity_comments (
    id BINARY(16) PRIMARY KEY,
    activity_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    FOREIGN KEY (activity_id) REFERENCES activity(id),
    FOREIGN KEY (user_id) REFERENCES appuser(id)
);

CREATE TABLE IF NOT EXISTS activity_images (
    id BINARY(16) PRIMARY KEY,
    activity_id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    image_data LONGBLOB NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (activity_id) REFERENCES activity(id),
    FOREIGN KEY (user_id) REFERENCES appuser(id)
);