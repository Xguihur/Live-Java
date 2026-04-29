INSERT INTO live_user (id, nickname, avatar, account_type, status, created_at, updated_at)
VALUES
    (1, '榕树主播', 'https://api.dicebear.com/9.x/initials/svg?seed=anchor', 'REGISTERED', 'NORMAL', NOW(), NOW()),
    (2, '房管小叶', 'https://api.dicebear.com/9.x/initials/svg?seed=admin', 'REGISTERED', 'NORMAL', NOW(), NOW())
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname);

INSERT INTO live_room (id, room_name, owner_user_id, cover_url, room_status, created_at, updated_at)
VALUES
    (1, '大榕树下深夜聊天室', 1, 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1200&q=80', 'ONLINE', NOW(), NOW()),
    (2, '代码随想直播间', 1, 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1200&q=80', 'ONLINE', NOW(), NOW())
ON DUPLICATE KEY UPDATE room_name = VALUES(room_name);

INSERT INTO live_room_member (id, room_id, user_id, room_role, joined_at)
VALUES
    (1, 1, 1, 'OWNER', NOW()),
    (2, 1, 2, 'ADMIN', NOW()),
    (3, 2, 1, 'OWNER', NOW())
ON DUPLICATE KEY UPDATE room_role = VALUES(room_role);

INSERT INTO live_room_message (id, room_id, sender_id, sender_name, message_type, content, status, send_time, created_at)
VALUES
    (1, 1, 1, '榕树主播', 'TEXT', '欢迎来到大榕树下，今晚我们聊聊后端和实时消息系统。', 'NORMAL', NOW(), NOW()),
    (2, 1, 2, '房管小叶', 'TEXT', '新来的同学先打个招呼，我们马上开始。', 'NORMAL', NOW(), NOW()),
    (3, 2, 1, '榕树主播', 'TEXT', '这里是代码随想直播间，欢迎一起写点能跑的项目。', 'NORMAL', NOW(), NOW())
ON DUPLICATE KEY UPDATE content = VALUES(content);
