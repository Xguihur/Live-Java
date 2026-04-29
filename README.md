# Live Chat MVP

这是一个基于 `React + Vite + TypeScript` 和 `Spring Boot + MyBatis + MySQL + Redis + WebSocket` 的直播间消息流 MVP。

当前版本优先打通四条主链路：

- 游客昵称登录
- 房间列表与房间详情
- 历史消息拉取
- WebSocket 实时聊天

## 目录结构

```text
live-dev
├── docker-compose.yml
├── live-chat-server
└── live-chat-web
```

## 启动依赖

先启动 MySQL 和 Redis：

```bash
docker compose up -d
```

默认容器配置：

- MySQL: `localhost:3306`
- Redis: `localhost:6379`
- Database: `live_chat`
- Username: `live`
- Password: `live1234`

## 启动后端

```bash
cd live-chat-server
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

后端默认端口：

- `http://localhost:8091`

可用接口：

- `POST /api/auth/login`
- `GET /api/rooms`
- `GET /api/rooms/{roomId}`
- `GET /api/rooms/{roomId}/messages`
- `POST /api/rooms/{roomId}/ban`
- `POST /api/rooms/{roomId}/unban`
- `GET /api/health`

WebSocket 地址：

- `ws://localhost:8091/ws/chat?token=xxx&roomId=1`

## 启动前端

```bash
cd live-chat-web
npm install
npm run dev
```

前端默认端口：

- `http://localhost:5173`

## 前端环境变量

可选配置：

```bash
VITE_API_BASE_URL=http://localhost:8091
VITE_WS_BASE_URL=ws://localhost:8091/ws/chat
```

## 当前权限模型

这一版没有引入完整 RBAC，而是采用更贴近聊天室的两层模型：

- `accountType`
  区分游客与注册用户
- `roomRole`
  区分房主、房管和普通成员

当前默认数据中：

- `榕树主播` 是房主
- `房管小叶` 是房间管理员

## 后续建议迭代

- Redis Pub/Sub 跨实例广播
- 敏感词过滤
- 消息撤回
- 图片 / 表情消息
- 平台管理员后台
