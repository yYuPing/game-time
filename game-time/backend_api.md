# 后端 API 文档 — 游戏预约课表

说明：本文件列出前端需要调用的后端接口、请求/响应示例、权限与校验规则，便于后端实现或 Mock 服务。

通用约定
- 时区与日期：后端采用 UTC 存储时间字段，前端以 `YYYY-MM-DD`（日期）与 `timeslot`（如 `13-14`）为交互单位。createdAt/updatedAt 使用 ISO 8601 UTC 时间字符串。
- 认证：使用 `Authorization: Bearer <token>`（JWT 或 session token）。未认证用户只能调用公开接口（如游戏列表、查看课表）。
- 返回格式：JSON，标准字段：`{ success: boolean, code?: number, message?: string, data?: any }` 或直接返回 `data` 结构（需团队约定）。
- 时段枚举（固定）：`["08-09","09-10","10-11","11-12","12-13","13-14","14-15","15-16","16-17","17-18","18-19","19-20","20-21","21-22"]`。

数据模型（简要）
- User
```
{ id: string, username: string, displayName?: string, role?: 'user'|'admin', avatarUrl?: string }
```
- Game
```
{ id: string, name: string, color?: string, meta?: {...} }
```
- Reservation
```
{
  id: string,
  userId: string,
  username: string,
  date: string,        // YYYY-MM-DD
  timeslot: string,    // 如 "13-14"
  gameId: string,
  gameName: string,
  note?: string,
  createdAt: string,
  updatedAt?: string
}
```

接口清单

1. 鉴权相关

- POST /api/auth/login
  - 功能：用户登录，返回 token 与用户信息
  - Body: `{ username: string, password: string }`
  - Response: `200 OK` `{ success:true, data: { token: string, user: User } }`

- POST /api/auth/logout
  - 功能：登出（可选）
  - Headers: `Authorization` 
  - Response: `200 OK` `{ success:true }`

- GET /api/auth/me
  - 功能：获取当前登录用户信息
  - Headers: `Authorization`
  - Response: `200 OK` `{ success:true, data: User }`

2. 游戏列表

- GET /api/games
  - 功能：列出或搜索游戏，用于预约弹窗选择
  - Query: `query`（可选），`page`，`pageSize`
  - Response: `200 OK` `{ success:true, data: { items: Game[], total: number } }`

- GET /api/games/:id
  - 功能：获取单个游戏详情
  - Response: `200 OK` `{ success:true, data: Game }`

3. 获取课表/预约数据

- GET /api/schedule?weekStart=YYYY-MM-DD
  - 功能：获取某周（以 weekStart（周一）为起点）所有预约数据
  - Query: `weekStart`（必需，格式 `YYYY-MM-DD`）
  - Response: `200 OK` `{ success:true, data: Reservation[] }
  - 备注：后端可返回数组，也可分组（按 date/timeslot 聚合）

- GET /api/schedule/day?date=YYYY-MM-DD
  - 功能：获取某日所有预约（用于按日加载）
  - Query: `date`（必需）
  - Response: `200 OK` `{ success:true, data: Reservation[] }`

4. 预约管理（核心）

- POST /api/reservations
  - 功能：新增预约
  - Headers: `Authorization`（必需）
  - Body:
    ```json
    {
      "date": "2026-06-02",
      "timeslot": "13-14",
      "gameId": "g1",
      "note": "可选备注"
    }
    ```
  - Response:
    - `201 Created` `{ success:true, data: Reservation }`
    - `400 Bad Request`（非法 timeslot/缺字段）
    - `401 Unauthorized`（未登录）
  - 行为说明：后端使用 authenticated user 的 id 作为 `userId`，并将 `username` 填入返回数据。
  - 并发说明：如果允许堆叠（默认），直接插入；如需限制名额，后端需在事务中检查并原子减配额。

- GET /api/reservations
  - 功能：按条件查询预约（可用于管理员或过滤）
  - Query 可选项：`date`, `timeslot`, `userId`, `page`, `pageSize`
  - Response: `200 OK` `{ success:true, data: { items: Reservation[], total } }`

- GET /api/reservations/:id
  - 功能：获取预约详情
  - Response: `200 OK` `{ success:true, data: Reservation }`

- DELETE /api/reservations/:id
  - 功能：取消/删除预约
  - Headers: `Authorization`（必需）
  - 权限：仅预约所属用户或 `admin` 可删除
  - Response:
    - `200 OK` `{ success:true }`
    - `403 Forbidden`（无权限）
    - `404 Not Found`
  - 备注：前端执行删除后应刷新当前格子数据或从本地合并删除。

- PATCH /api/reservations/:id
  - 功能：更新预约（例如备注），权限同 DELETE
  - Body: `{ note?: string }`
  - Response: `200 OK` `{ success:true, data: Reservation }`

5. 管理/运维（可选）
- GET /api/admin/reservations/export?weekStart=...
- POST /api/admin/games （添加/编辑游戏）

错误码与常见响应
- 200 OK / 201 Created: 成功
- 400 Bad Request: 参数校验失败（返回 message）
- 401 Unauthorized: 未认证
- 403 Forbidden: 权限不足
- 404 Not Found: 资源不存在
- 409 Conflict: 冲突（如实现了唯一约束并发生冲突）
- 500 Internal Server Error: 服务端异常

前端校验建议
- 提交预约前检查：date 格式合法、timeslot 在枚举内、gameId 存在。
- 防止重复点击：禁用提交按钮并显示加载态。

示例交互流程（前端）
1. 加载周视图：调用 `GET /api/schedule?weekStart=YYYY-MM-DD`，渲染 14×7 单元格。
2. 点击单元格：打开详情 `ReservationListModal`（已在前端实现），由前端从 `reservations` 中筛选或调用 `GET /api/schedule/day` 拉取当天全部预约。
3. 新增预约：在 `ReservationModal` 提交时调用 `POST /api/reservations`，请求成功后合并返回记录到前端状态，失败则回滚并显示错误。
4. 取消预约：调用 `DELETE /api/reservations/:id`，成功后从前端列表移除。

性能与扩展性建议
- 批量加载：若预约数量非常大，后端可按日期分片或按 timeslot 聚合返回计数与示例；前端按需拉取完整列表。
- 缓存：对 `GET /api/games` 使用短期缓存；对周数据做 ETag/If-None-Match 减少带宽。
- 并发控制（可选）：若未来要限制同格预约人数，建议在 DB 添加 `slot_capacity` 表/字段，并在事务中检查/扣减。

安全注意
- 对用户输入（note、gameId）做严格校验与转义，防止 XSS/注入。
- 对删除/修改接口做鉴权与审计日志（记录谁在何时删除了哪条预约）。

部署/Mock 建议
- 可先用 JSON Server / Mock 服务模拟上述接口，便于前端联调。
- 示例 mock 响应请参考前端目前使用的 `loadWeek()`、`ReservationModal` 的接口格式。

---
如需，我可以：
- 生成 Node.js + Express 的示例实现（含 SQLite/低门槛 DB）以便本地运行；
- 或生成一份 OpenAPI (Swagger) YAML 文件以便后端直接生成接口骨架。 

文件位置：`design/backend_api.md`。