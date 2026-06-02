# Game Time — 项目结构文档

> 游戏预约课表系统，前后端分离架构

---

## 目录结构总览

```
game-time-ui/                      # 前端项目 (UniApp)
├── api/                           # API 模块
├── pages/                         # 页面组件
├── static/                        # 静态资源
├── pages.json                     # 页面路由 & Tab 配置
├── main.js                        # 入口
├── App.vue                        # 根组件
└── ...

game-time/                         # 后端项目 (Spring Boot)
├── src/main/java/com/yuyu/game_time/
│   ├── config/                    # 配置类
│   ├── controller/                # 控制器 (API 接口)
│   ├── dto/                       # 数据传输对象
│   ├── entity/                    # 实体类
│   ├── exception/                 # 全局异常处理
│   ├── repository/                # 数据访问层
│   └── service/                   # 业务逻辑层
├── src/main/resources/
│   └── application.yaml           # 配置文件
├── pom.xml                        # Maven 依赖
└── ...

docker/
├── docker-compose.yml             # Redis
└── ...
```

---

## 1️⃣ 前端架构 (`Game-Time-UI/`)

### 1.1 技术栈

| 技术 | 说明 |
|------|------|
| UniApp 2.x | 跨平台框架 (Vue 2) |
| Vue 2.6 | MVVM 框架 |
| Axios 1.x | HTTP 请求库 |
| SCSS | CSS 预处理器 |

### 1.2 目录说明

```
api/
├── index.js           # 统一导出所有 API 模块
├── config.js          # API 基础地址配置
├── request.js         # Axios 实例 (含拦截器、withCredentials)
└── modules/
    ├── auth.js        # 登录/注册 API
    ├── game.js        # 游戏列表 API
    ├── schedule.js    # 课表/预约 API
    └── user.js        # 用户信息 API

pages/
├── index/             # 首页 — 周视图课表 (Tab 页)
│   ├── index.vue
│   ├── ScheduleGrid.vue
│   ├── ReservationModal.vue
│   ├── ReservationListModal.vue
│   └── ReservationChip.vue
├── login/             # 登录页
│   └── login.vue
├── register/          # 注册页
│   └── register.vue
└── profile/           # 个人中心 (Tab 页)
    └── profile.vue

static/
├── logo.png
├── tab_home.svg            # 首页 Tab 图标
├── tab_home_active.svg     # 首页 Tab 选中图标
├── tab_profile.svg         # 我的 Tab 图标
└── tab_profile_active.svg  # 我的 Tab 选中图标
```

### 1.3 页面路由 (Tab 导航)

| Tab | 路径 | 说明 |
|-----|------|------|
| 🏠 首页 | `pages/index/index` | 周视图课表 + 预约管理 |
| 👤 我的 | `pages/profile/profile` | 个人中心 + 退出登录 |

**次要页面**（非 Tab）：登录页、注册页

### 1.4 API 调用流程

```
页面 (Vue) 
  → api/modules/xxx.js (API 封装)
    → api/request.js (Axios 实例, withCredentials: true)
      → Spring Boot 后端 (Cookie 传递 Session)
        → Controller → Service → Repository → MySQL
```

### 1.5 用户认证流程

```
登录成功
  → 后端将 userId 存入 Redis Session
  → 响应返回 { success, data: { userId, username, email, phone, avatar } }
  → 前端存入 uni.setStorageSync("userInfo", {...})
  → 后续请求自动携带 Cookie (Session)
  → 个人中心通过 GET /api/user/profile 拉取最新信息
```

---

## 2️⃣ 后端架构 (`game-time/`)

### 2.1 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.x | 应用框架 |
| Java | 17 | 运行环境 |
| Spring Data JPA | — | ORM 持久层 |
| MySQL Connector | 9.7.x | 数据库驱动 |
| Spring Security Crypto | — | BCrypt 密码加密 |
| Spring Data Redis | — | Redis 客户端 |
| Spring Session Data Redis | — | Redis Session 管理 |
| Maven | — | 构建工具 |
| Docker | — | Redis 容器 |

### 2.2 目录说明

```
src/main/java/com/yuyu/game_time/
├── GameTimeApplication.java           # 启动类

├── config/
│   └── WebConfig.java                 # CORS 跨域配置

├── controller/
│   ├── AuthController.java            # 认证接口 (login/register/logout/me)
│   ├── UserController.java            # 用户信息接口 (profile)
│   ├── GamesController.java           # 游戏列表接口
│   └── ReservationController.java     # 预约管理接口

├── dto/
│   ├── ApiResponse.java               # 统一响应包装
│   ├── AuthResponse.java              # 认证响应 (含 email/phone/avatar)
│   ├── LoginRequest.java              # 登录请求
│   ├── RegisterRequest.java           # 注册请求
│   ├── UpdateProfileRequest.java      # 更新个人信息请求
│   ├── ReservationRequest.java        # 创建预约请求
│   ├── ReservationResponse.java       # 预约响应
│   └── GameDto.java                   # 游戏 DTO

├── entity/
│   ├── User.java                      # 用户实体 (id, username, password, email, phone, avatar, createdAt)
│   ├── Game.java                      # 游戏实体
│   └── Reservation.java               # 预约实体

├── repository/
│   ├── UserRepository.java            # 用户数据访问
│   ├── GameRepository.java            # 游戏数据访问
│   └── ReservationRepository.java     # 预约数据访问

├── service/
│   ├── UserService.java               # 用户业务 (register/login/getById/updateProfile)
│   ├── GameService.java               # 游戏业务
│   └── ReservationService.java        # 预约业务

└── exception/
    └── GlobalExceptionHandler.java    # 全局异常处理
```

### 2.3 API 接口清单

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| POST | `/api/auth/login` | 用户登录 | ❌ |
| POST | `/api/auth/register` | 用户注册 | ❌ |
| POST | `/api/auth/logout` | 退出登录 | ✅ |
| GET | `/api/auth/me` | 获取当前用户信息 | ✅ |
| GET | `/api/user/profile` | 获取个人信息 | ✅ |
| PUT | `/api/user/profile` | 更新个人信息 | ✅ |
| GET | `/api/games` | 获取游戏列表 | ❌ |
| GET | `/api/games/:id` | 获取游戏详情 | ❌ |
| GET | `/api/schedule?weekStart=` | 获取周课表 | ❌ |
| GET | `/api/schedule/day?date=` | 获取日课表 | ❌ |
| POST | `/api/reservations` | 创建预约 | ✅ |
| GET | `/api/reservations` | 查询预约 | ✅ |
| GET | `/api/reservations/:id` | 查询预约详情 | ✅ |
| DELETE | `/api/reservations/:id` | 取消预约 | ✅ |
| PATCH | `/api/reservations/:id` | 更新预约备注 | ✅ |

### 2.4 认证机制

```
Spring Session Data Redis
  └── HttpSession (Cookie: GAMETIME_SESSION)
        └── session.setAttribute("userId", user.getId())
              └── 后续请求通过 session.getAttribute("userId") 获取登录用户
```

- **Session 存储**: Redis（`localhost:6379`，密码 `1qaz!QAZ`）
- **Session 过期**: 24 小时
- **Cookie**: `GAMETIME_SESSION`，HttpOnly，SameSite=Lax

### 2.5 数据库

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `users` | 用户表 | id, username, password, email, phone, avatar, created_at |
| `games` | 游戏表 | id, name, color, meta |
| `reservations` | 预约表 | id, user_id, username, date, timeslot, game_id, game_name, note, created_at |

---

## 3️⃣ 基础设施

### 3.1 Docker 服务

```bash
# Redis (Session 存储)
docker run -d --name gametime-redis \
  -p 6379:6379 \
  -e REDIS_PASSWORD=1qaz!QAZ \
  redis:7-alpine \
  redis-server --requirepass 1qaz!QAZ

# MySQL (数据存储) — 需要手动部署
# 端口: 33061, 用户: root, 密码: 1qaz!QAZ, 数据库: gametime
```

### 3.2 配置文件

- **后端**: `game-time/src/main/resources/application.yaml`
  - MySQL: `jdbc:mysql://localhost:33061/gametime`
  - Redis: `localhost:6379`，密码 `1qaz!QAZ`
  - 服务端口: `8080`

- **前端**: `Game-Time-UI/api/config.js`
  - API 基础地址: `http://localhost:8080`

---

## 4️⃣ 启动流程

### 4.1 启动基础设施

```bash
# 启动 Redis
docker start gametime-redis

# 确保 MySQL 已运行 (端口 33061)
```

### 4.2 启动后端

```bash
cd game-time
./mvnw clean spring-boot:run
```

### 4.3 启动前端

使用 HBuilder X 打开 `Game-Time-UI` 目录，运行到浏览器或手机模拟器。

---

## 5️⃣ 数据流示例

### 登录流程

```
用户输入账号密码
  → POST /api/auth/login (axios withCredentials)
    → AuthController.login()
      → UserService.login() — 校验密码
      → session.setAttribute("userId", user.getId()) — 存入 Redis
      → 返回 { success, data: { userId, username, email, phone, avatar } }
  → 前端保存到 localStorage
  → 跳转首页 Tab
```

### 个人中心加载流程

```
点击「我的」Tab
  → profile.vue onShow()
    → 读取本地缓存显示
    → GET /api/user/profile (自动携带 Cookie)
      → UserController.getProfile()
        → session.getAttribute("userId") → 获取登录用户
        → UserService.getUserById()
        → 返回 { success, data: { userId, username, email, phone, avatar } }
    → 合并后端数据更新显示
```
