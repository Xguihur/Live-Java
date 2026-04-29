CREATE TABLE IF NOT EXISTS live_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nickname VARCHAR(64) NOT NULL,
    avatar VARCHAR(255) NOT NULL,
    account_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS live_room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_name VARCHAR(128) NOT NULL,
    owner_user_id BIGINT NOT NULL,
    cover_url VARCHAR(255) NOT NULL,
    room_status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS live_room_member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    room_role VARCHAR(32) NOT NULL,
    joined_at DATETIME NOT NULL,
    UNIQUE KEY uk_room_user (room_id, user_id)
);

CREATE TABLE IF NOT EXISTS live_room_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_name VARCHAR(64) NOT NULL,
    message_type VARCHAR(32) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    status VARCHAR(32) NOT NULL,
    send_time DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    KEY idx_room_send_time (room_id, send_time DESC),
    KEY idx_sender_send_time (sender_id, send_time DESC)
);

CREATE TABLE IF NOT EXISTS live_room_ban (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    ban_until DATETIME NOT NULL,
    reason VARCHAR(255) NOT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    KEY idx_room_user_ban (room_id, user_id, ban_until)
);
