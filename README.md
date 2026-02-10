## Spring Boot 3 MyBatis-Plus Template

基于 **Spring Boot 3 / MyBatis-Plus / MySQL / Redis / Knife4j** 的通用后端基础模板，用于快速搭建业务项目起步工程。

### 技术栈

- **后端框架**：Spring Boot 3.x（JDK 17）
- **ORM**：MyBatis-Plus 3.5.x（分页、乐观锁、逻辑删除、代码生成器）
- **数据库**：MySQL 8.x + Druid 连接池
- **缓存**：Redis（Spring Data Redis，统一 JSON 序列化）
- **工具**：Lombok、Apache Commons Lang3、Jackson
- **文档**：Knife4j（基于 OpenAPI3 的增强 UI）
- **构建**：Maven

---

## 项目结构与约定

主要包结构（仅列出核心）：

- `common`：通用基础模块
  - `api`：统一响应体 `Result<T>`
  - `config`：全局配置（MyBatis-Plus、Redis、WebMvc、OpenAPI/Knife4j 等）
  - `constant`：通用常量、错误码 `ErrorCode`
  - `entity`：基础实体 `BaseEntity`（id/createTime/updateTime/deleted/version）
  - `exception`：`BusinessException` + `GlobalExceptionHandler`
  - `interceptor`：`LoggingInterceptor` 请求日志拦截
  - `page`：`PageQuery`、`PageResult`
  - `service`：`BaseService`、`BaseServiceImpl`
  - `util`：`JsonUtil`、`RedisUtil`、`ValidationUtil` 等
- `controller`：控制层（示例：`UserController`）
- `service` / `service.impl`：业务服务层（示例：`UserService` / `UserServiceImpl`）
- `mapper`：MyBatis-Plus `Mapper` 接口（示例：`UserMapper`）
- `entity`：实体类（示例：`User`，继承 `BaseEntity`）
- `dto`：**请求 DTO（入参统一放这里）**
  - `UserCreateReq`：创建用户请求
  - `UserUpdateReq`：更新用户请求
  - `UserPageReq`：用户分页查询请求，继承 `PageQuery`
- `vo`：**视图对象 VO（对外返回统一放这里）**
  - `UserVO`：用户对外返回数据模型
- `generator`：MyBatis-Plus 代码生成器示例

### DTO / VO 约定

- **DTO（Data Transfer Object）**：只做“入参”
  - Controller 接收请求体时统一使用 DTO，例如：
    - `UserCreateReq`、`UserUpdateReq`、`UserPageReq`
  - 通过 Jakarta Validation（`@NotBlank`、`@Email`、`@Size` 等）做参数校验。
- **VO（View Object）**：只做“返回值”
  - Controller 返回统一使用 VO 作为 `Result<T>` 的 `data`：
    - `Result<UserVO>`、`Result<PageResult<UserVO>>`
  - 内部由 Service 层从实体 `User` 转为 `UserVO`，只暴露对外需要的字段。

> 统一约定：**Controller：入参 DTO → 内部处理（实体/服务）→ 出参 VO**。

---

## 数据库脚本规范

所有 SQL 脚本统一放在 `db/sql` 目录，按用途拆分：

- `db/sql/full/`：**全量初始化脚本**
  - `001_init_schema.sql`：创建数据库 `demo_db` 与基础表结构（含 `t_user`）
- `db/sql/migration/`：**增量变更脚本**
  - `20260209_01_create_t_user.sql`：示例增量脚本（仅创建 `t_user` 表）
- `db/sql/data/`：**模拟数据脚本**
  - `001_demo_user_data.sql`：插入多条示例用户数据

新环境初始化推荐顺序：

```bash
mysql -h127.0.0.1 -P3306 -uroot -proot < db/sql/full/001_init_schema.sql
mysql -h127.0.0.1 -P3306 -uroot -proot < db/sql/data/001_demo_user_data.sql
```

---

## 本地开发运行

1. **启动 MySQL 和 Redis**

   - 方式一：使用 `docker-compose.yml`
     ```bash
     docker-compose up -d mysql redis
     ```
   - 方式二：使用本地已安装的 MySQL / Redis（端口与 `application.yml` 中 dev 配置保持一致）。

2. **初始化数据库（首次或新环境）**

   执行上文中的全量和模拟数据脚本（`001_init_schema.sql` + `001_demo_user_data.sql`）。

3. **启动应用**

   在项目根目录执行：
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```

   默认：
   - 端口：`8080`
   - Profile：`dev`

---

## 示例接口说明

所有接口均返回统一结构 `Result<T>`，包含：

- `code`：业务状态码（参考 `ErrorCode`）
- `message`：提示信息
- `data`：返回数据（VO / 分页结果）
- `timestamp`：时间戳

**用户模块示例接口：**

- **创建用户**：`POST /api/users`
  - 请求体：`UserCreateReq`（DTO）
  - 响应：`Result<Long>`（新建用户 ID）
- **更新用户**：`PUT /api/users`
  - 请求体：`UserUpdateReq`（DTO）
  - 响应：`Result<Void>`
- **删除用户（逻辑删除）**：`DELETE /api/users/{id}`
  - 响应：`Result<Void>`
- **根据 ID 查询用户（带 Redis 缓存示例）**：`GET /api/users/{id}`
  - 响应：`Result<UserVO>`
- **查询全部用户（示例）**：`GET /api/users`
  - 响应：`Result<List<User>>`（直接返回实体，仅作简单示例）
- **分页查询用户**：`POST /api/users/page`
  - 请求体：`UserPageReq`（DTO，包含分页参数和筛选条件）
  - 响应：`Result<PageResult<UserVO>>`

用户查询接口中，`UserServiceImpl` 内置了 **Redis 缓存示例**：  
`getByIdWithCache` 会优先从 Redis 读取 `UserVO`，未命中则查询数据库并写入缓存。

---

## 在线接口文档（Knife4j）

应用启动后，可通过 Knife4j 查看和调试接口：

- Knife4j UI：
  - `http://localhost:8080/doc.html`
- OpenAPI 文档（JSON）：
  - `http://localhost:8080/v3/api-docs`

在文档页面中可以查看：

- 所有 Controller/接口列表
- 请求 DTO 和返回 VO 的结构说明
- 直接在线发起测试请求

---

## 多环境配置

使用主配置文件 `application.yml` 管理多环境：

- `dev`：本地开发环境（默认激活）
- `test`：测试环境
- `prod`：生产 / 容器环境（默认连接 `docker-compose` 中的 `mysql` 和 `redis` 服务）

切换环境方式：

- 修改 `application.yml` 中：
  ```yaml
  spring:
    profiles:
      active: dev
  ```
  将 `dev` 替换为 `test` / `prod` 即可。

---

## Docker 部署

1. **构建 Jar 包**

   ```bash
   mvn clean package
   ```

2. **使用 Docker Compose 一键启动（MySQL + Redis + 应用）**

   ```bash
   docker-compose up -d
   ```

   - 数据库和缓存使用 `docker-compose.yml` 中配置的服务名（`mysql`、`redis`）
   - 应用容器通过环境变量 `SPRING_PROFILES_ACTIVE=prod` 启动

---

## MyBatis-Plus 代码生成器

`generator/MybatisPlusCodeGenerator` 提供简单可执行的代码生成示例：

1. 根据实际数据库配置修改类中的常量：
   - `URL`
   - `USERNAME`
   - `PASSWORD`
2. 运行 `main` 方法：
   - 可在 `entity/mapper/service/service.impl/controller` 等目录下生成指定表对应的基础代码。

你可以在此模板基础上继续扩展：权限与认证（如 Spring Security）、多模块拆分、多租户支持、统一日志链路追踪等。
