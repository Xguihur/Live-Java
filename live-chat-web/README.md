# live-chat-web 目录说明

这个文件用来帮助第一次进入前端工程的人快速了解每个文件夹的作用。当前前端基于 `React + Vite + TypeScript + Tailwind CSS`，业务主线是“登录 -> 房间列表 -> 房间详情 -> WebSocket 聊天”。

## 目录总览

```text
live-chat-web
├── dist/
│   └── assets/
├── node_modules/
└── src/
    ├── app/
    │   ├── providers/
    │   ├── router/
    │   └── styles/
    ├── components/
    │   ├── chat/
    │   ├── common/
    │   └── room/
    ├── hooks/
    ├── lib/
    ├── pages/
    │   ├── login/
    │   ├── room-detail/
    │   └── room-list/
    ├── services/
    │   ├── api/
    │   └── ws/
    ├── stores/
    └── types/
```

## 每个文件夹的作用

| 文件夹 | 作用 | 当前情况 |
| --- | --- | --- |
| `live-chat-web/` | 前端工程根目录，放前端依赖、构建配置、源码和打包产物。 | `package.json`、`vite.config.ts`、`tsconfig.json`、`index.html` 都在这一层。 |
| `dist/` | 前端执行打包命令后生成的构建产物目录。 | 一般不手改，重新构建会覆盖。 |
| `dist/assets/` | 打包后的静态资源目录。 | 存放 JS、CSS 等产物文件。 |
| `node_modules/` | npm 安装下来的第三方依赖目录。 | 只作为依赖来源，不维护业务代码。 |
| `src/` | 前端业务源码主目录。 | 绝大多数日常开发都在这里完成。 |
| `src/app/` | 应用级基础设施目录。 | 放全局 Provider、路由和全局样式。 |
| `src/app/providers/` | 应用级 Provider 配置。 | 当前主要是 `React Query` 的 `QueryClientProvider`。 |
| `src/app/router/` | 路由配置与路由守卫。 | 定义登录页、房间列表页、房间详情页，以及基于 token 的受保护路由。 |
| `src/app/styles/` | 全局样式入口。 | 当前引入 Tailwind，并补充全局背景、字体和基础重置。 |
| `src/components/` | 可复用 UI 组件目录。 | 这里的组件会被多个页面或多个业务点复用。 |
| `src/components/chat/` | 聊天区相关组件。 | 包含消息列表、单条消息、消息输入框。 |
| `src/components/common/` | 通用组件目录。 | 当前主要是页面外壳 `PageShell`。 |
| `src/components/room/` | 房间展示相关组件。 | 当前有房间卡片 `RoomCard`。 |
| `src/hooks/` | 自定义 Hook 目录。 | 当前核心是 `use-chat-socket`，负责 WebSocket 建连、收消息、发消息和状态管理。 |
| `src/lib/` | 底层工具和通用能力。 | 当前包括 Axios 实例封装和通用类名拼接工具。 |
| `src/pages/` | 路由级页面目录。 | 每个子目录通常对应一个完整页面。 |
| `src/pages/login/` | 登录页。 | 负责昵称校验、登录请求和登录成功后的跳转。 |
| `src/pages/room-detail/` | 房间详情页。 | 负责房间信息、历史消息、在线人数和实时聊天区域。 |
| `src/pages/room-list/` | 房间列表页。 | 负责加载房间列表、展示房间卡片和退出登录。 |
| `src/services/` | 服务层目录。 | 当前用于集中放置接口请求和后续可扩展的连接能力。 |
| `src/services/api/` | HTTP 接口服务目录。 | 当前已承载登录、房间、消息相关的请求封装。 |
| `src/services/ws/` | 预留的 WebSocket 服务目录。 | 现在未使用，现有 WebSocket 逻辑集中在 `hooks/use-chat-socket.ts`。 |
| `src/stores/` | Zustand 状态管理目录。 | 当前有登录态、当前房间和消息草稿等状态。 |
| `src/types/` | 全局 TypeScript 类型目录。 | 统一放接口响应、房间、消息、WebSocket 数据结构类型。 |

## 目前这套目录怎么理解

可以先用下面这条线理解整个前端：

1. `pages/` 负责组装页面和页面流程。
2. `components/` 负责把页面里的可复用 UI 拆出来。
3. `services/api/` 负责集中管理 HTTP 接口请求。
4. `hooks/` 负责封装像 WebSocket 这样带生命周期的交互逻辑。
5. `stores/` 负责跨组件共享状态。
6. `lib/` 和 `types/` 负责给上面这些目录提供公共基础能力。

## 当前代码里的一个小提示

`src/services/api/` 现在已经是接口封装的正式入口，所以如果你现在要找“登录接口、房间接口、消息接口”，应该优先去看 `src/services/api/`；如果要找实时聊天连接逻辑，应该优先去看 `src/hooks/use-chat-socket.ts`。
