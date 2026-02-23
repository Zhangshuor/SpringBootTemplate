# Spring Boot 3 MyBatis-Plus Template

基于 **Spring Boot 3 / MyBatis-Plus / MySQL / Redis / Knife4j** 的通用后端基础模板，用于快速搭建业务项目起步工程。本文档面向接手项目的开发者，便于理解结构、约定与本地开发流程，并在此基础上进行业务开发。

---

## 一、技术栈与环境要求

| 类别     | 技术 / 版本说明 |
|----------|------------------|
| 后端框架 | Spring Boot 3.x（JDK 17） |
| ORM      | MyBatis-Plus 3.5.x（分页、乐观锁、逻辑删除、代码生成器） |
| 数据库   | MySQL 8.x + Druid 连接池 |
| 缓存     | Redis（Spring Data Redis，统一 JSON 序列化） |
| 工具     | Lombok、Apache Commons Lang3、Jackson |
| 接口文档 | Knife4j（OpenAPI3 增强 UI） |
| 构建     | Maven |

**本地开发建议：**

- JDK 17+
- Maven 3.6+
- MySQL 8.x、Redis（或使用项目提供的 Docker 编排）

---

## 二、快速开始（本地运行）

### 1. 启动 MySQL 与 Redis

**方式一：使用 Docker Compose（推荐）**

```bash
docker-compose up -d mysql redis
```

首次启动 MySQL 时，会自动执行 `mysql/init/001_init_schema.sql`，创建库 `demo_db` 和表 `t_user`。

**方式二：使用本机已安装的 MySQL / Redis**

确保端口与 `application.yml` 中 `dev` 配置一致（MySQL 3306、Redis 6379），并手动执行初始化脚本（见下文「数据库初始化」）。

### 2. 初始化数据库（仅在使用本机 MySQL 且未自动执行时）

```bash
mysql -h127.0.0.1 -P3306 -uroot -proot < mysql/init/001_init_schema.sql
```

若有其他脚本（如 `002_xxx_data.sql`），按需追加执行。

### 3. 启动应用

```bash
mvn clean package
mvn spring-boot:run
```

默认：端口 **8080**，Profile **dev**。

### 4. 验证与接口文档

- 健康/接口：访问 `http://localhost:8080`
- **Knife4j 文档**：`http://localhost:8080/doc.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

---

## 三、项目结构与约定

### 3.1 包结构概览

```
com.example.demo
├── DemoApplication.java          # 启动类
├── common                         # 通用基础模块（不建议随意修改）
│   ├── api                        # 统一响应 Result<T>
│   ├── config                     # 全局配置（MyBatis-Plus、Redis、WebMvc、OpenAPI、自动填充等）
│   ├── constant                   # 错误码 ErrorCode
│   ├── entity                     # 基础实体 BaseEntity（id/createTime/updateTime/deleted/version）
│   ├── exception                  # BusinessException + GlobalExceptionHandler
│   ├── interceptor                # LoggingInterceptor 请求日志
│   ├── page                       # PageQuery、PageResult
│   ├── service                    # BaseService、BaseServiceImpl
│   └── util                       # JsonUtil、RedisUtil、ValidationUtil
├── controller                     # 控制层（按业务模块划分）
├── service / service.impl         # 业务接口与实现
├── mapper                         # MyBatis-Plus Mapper 接口
├── entity                         # 数据库实体（继承 BaseEntity）
├── dto                            # 请求 DTO（入参）
├── vo                             # 视图对象 VO（出参）
└── generator                      # MyBatis-Plus 代码生成器示例
```

### 3.2 分层与数据流约定

- **Controller**：只做参数接收、校验（`@Valid`）和调用 Service，返回 `Result<T>`。
- **入参**：统一使用 **DTO**（如 `UserCreateReq`、`UserUpdateReq`、`UserPageReq`），放在 `dto` 包下，配合 Jakarta Validation（`@NotBlank`、`@Email`、`@Size` 等）做校验。
- **出参**：统一使用 **VO** 作为 `Result<T>` 的 `data`（如 `Result<UserVO>`、`Result<PageResult<UserVO>>`），放在 `vo` 包下；由 Service 层将实体转换为 VO，不直接暴露实体。
- **统一约定**：**Controller：入参 DTO → Service（实体/业务）→ 出参 VO**。

### 3.3 数据库初始化规范（mysql/init）

- 脚本统一放在 `mysql/init` 目录，按序号命名，如：
  - `001_init_schema.sql`：建库、建表
  - `002_xxx_data.sql`：可选初始化数据
- Docker Compose 已将 `mysql/init` 挂载到 MySQL 的 `/docker-entrypoint-initdb.d`，**首次**启动容器时会自动执行该目录下脚本。
- 使用本机 MySQL 时，需手动执行上述 SQL 文件。

---

## 四、示例接口与统一响应

### 4.1 统一响应结构 Result&lt;T&gt;

所有接口返回格式一致：

```json
{
  "code": 0,
  "message": "OK",
  "data": { ... },
  "timestamp": 1698123456789
}
```

- `code`：业务状态码（见 `ErrorCode`）
- `message`：提示信息
- `data`：业务数据（VO 或分页结果）
- `timestamp`：响应时间戳（毫秒）

### 4.2 用户模块示例接口

| 说明           | 方法 | 路径              | 请求体 / 说明                    | 响应 |
|----------------|------|-------------------|----------------------------------|------|
| 创建用户       | POST | /api/users        | UserCreateReq                    | Result&lt;Long&gt;（用户 ID） |
| 更新用户       | PUT  | /api/users        | UserUpdateReq                    | Result&lt;Void&gt; |
| 逻辑删除用户   | DELETE | /api/users/{id} | 路径参数 id                       | Result&lt;Void&gt; |
| 按 ID 查询（带缓存） | GET  | /api/users/{id}   | -                                | Result&lt;UserVO&gt; |
| 查询全部（示例） | GET  | /api/users        | -                                | Result&lt;List&lt;User&gt;&gt;（示例用） |
| 分页查询       | POST | /api/users/page   | UserPageReq（含分页与筛选）      | Result&lt;PageResult&lt;UserVO&gt;&gt; |

用户查询中，`UserServiceImpl.getByIdWithCache` 会优先从 Redis 读取 `UserVO`，未命中再查库并写入缓存（5 分钟过期），更新/删除时会清理对应缓存。

---

## 五、多环境配置

主配置：`src/main/resources/application.yml`。

| Profile | 说明           | 端口 | 数据库 / Redis 说明 |
|---------|----------------|------|----------------------|
| dev     | 本地开发（默认） | 8080 | localhost / demo_db、Redis db0 |
| test    | 测试环境       | 8081 | demo_db_test、Redis db1 |
| prod    | 生产/容器      | 8080 | 主机名 mysql、redis（配合 docker-compose） |

切换方式：修改 `application.yml` 中 `spring.profiles.active` 为 `dev` / `test` / `prod`。

---

## 六、Docker 部署

1. **打包**

   ```bash
   mvn clean package
   ```

2. **一键启动（MySQL + Redis + 应用）**

   ```bash
   docker-compose up -d
   ```

   应用容器通过环境变量 `SPRING_PROFILES_ACTIVE=prod` 使用 prod 配置，连接 compose 中的 `mysql`、`redis` 服务。

---

## 七、MyBatis-Plus 代码生成器

`generator/MybatisPlusCodeGenerator` 提供按表生成 entity/mapper/service/controller 的示例：

1. 根据实际库修改类中的常量：`URL`、`USERNAME`、`PASSWORD`。
2. 运行其 `main` 方法，代码将生成到当前工程的 `entity`、`mapper`、`service`、`service.impl`、`controller` 等包下；若配置了 XML 路径，会生成到 `src/main/resources/mapper`。
3. 生成后请按项目约定：**入参改为 DTO、出参改为 VO、Controller 返回 Result&lt;T&gt;**，并补充校验与注释。

---

## 八、接手后进行业务开发的建议步骤

1. **熟悉约定**：阅读本文「项目结构与约定」「分层与数据流约定」，保持 DTO/VO/Result 使用方式一致。
2. **新增业务模块（以「订单」为例）**：
   - 在 `entity` 下新增 `Order`（继承 `BaseEntity`），表名与字段与数据库一致。
   - 在 `mapper` 下新增 `OrderMapper` 继承 `BaseMapper<Order>`。
   - 在 `service` 与 `service.impl` 下新增 `OrderService`、`OrderServiceImpl`（可继承 `BaseService`/`BaseServiceImpl`）。
   - 在 `dto` 下新增创建/更新/分页请求 DTO（如 `OrderCreateReq`、`OrderPageReq` 继承 `PageQuery`）。
   - 在 `vo` 下新增 `OrderVO`。
   - 在 `controller` 下新增 `OrderController`，路径如 `/api/orders`，入参 DTO、出参 `Result&lt;OrderVO&gt;` 或 `Result&lt;PageResult&lt;OrderVO&gt;&gt;`。
3. **数据库**：在 `mysql/init` 下新增脚本（如 `002_order.sql`）建表，并确保本地/测试/生产环境执行到该脚本。
4. **错误码与异常**：业务异常使用 `BusinessException`；如需新错误类型，在 `ErrorCode` 中增加枚举并在异常处理中复用。
5. **接口文档**：Controller 上使用 `@Tag`、接口方法上使用 `@Operation`，Knife4j 会自动展示；DTO/VO 字段可加 `@Schema` 等注解增强可读性。

按上述步骤即可在保持模板风格的前提下扩展新业务。

---

## 九、常见问题

- **Q：首次 docker-compose 启动 MySQL 后，没有建表？**  
  A：确认 `mysql/init` 已挂载到容器的 `/docker-entrypoint-initdb.d`，且仅首次启动时会执行；若数据卷已存在，需删除卷或新容器再试。

- **Q：如何关闭 SQL 控制台打印？**  
  A：在 `application.yml` 的 `mybatis-plus.configuration.log-impl` 中改为其他实现（如 `org.apache.ibatis.logging.nologging.NoLoggingImpl`），或删除该配置使用默认。

- **Q：createTime / updateTime 没有自动填充？**  
  A：项目已提供 `MybatisMetaObjectHandler`，实体需继承 `BaseEntity` 并使用其中的 `createTime`、`updateTime` 字段（已配置 `FieldFill.INSERT` / `INSERT_UPDATE`）。

- **Q：Knife4j 文档地址？**  
  A：应用启动后访问 `http://localhost:8080/doc.html`。

---

## 十、后续可扩展方向

在现有模板上可继续扩展：权限与认证（如 Spring Security/JWT）、多模块拆分、多租户、统一日志与链路追踪（如 Sleuth/Micrometer）、消息队列、定时任务等。业务开发时建议先保持分层与 DTO/VO/Result 约定，再按需引入上述能力。
