# 技术选型

| 技术                 | 版本   | 用途               |
| -------------------- | ------ | ------------------ |
| JWT (JSON Web Token) | 0.11.5 | 生成和解析 Token   |
| JJWT                 | 0.11.5 | JWT 的 Java 实现库 |
| Spring Boot          | 2.7.x  | Web 框架           |
| SLF4J + Logback      | 内置   | 日志记录           |

---

## Token 实现方式

### 3.1 Token 生成

用户登录成功后，服务端使用 **JWT（JSON Web Token）** 生成一个加密的 Token 字符串。

**Token 包含的信息（Payload）**：
```json
{
  "userId": 1001,
  "account": "2024001",
  "role": "student",
  "iat": 1743235200,
  "exp": 1743321600
}
```

| 字段      | 说明                      |
| --------- | ------------------------- |
| `userId`  | 用户唯一标识              |
| `account` | 用户账号                  |
| `role`    | 用户角色（student/admin） |
| `iat`     | 签发时间（Issued At）     |
| `exp`     | 过期时间（Expiration）    |

### Token 签名算法

使用 **HS256（HMAC-SHA256）** 对称加密算法对 Token 进行签名，确保 Token 不可篡改。

```java
// 签名密钥（存储在 application.yml 中）
jwt.secret: mySecretKey123456789012345678901234567890
```

### Token 有效期

Token 有效期为 **24 小时**（86400000 毫秒）。过期后需要重新登录获取新 Token。

```yaml
jwt.expiration: 86400000  # 24小时
```

---

## Token 存储方式

###  服务端存储

**服务端不存储 Token**，采用无状态设计：
- 不将 Token 存入数据库
- 不将 Token 存入内存或 Redis
- 每次请求通过验证 Token 签名来确认用户身份

**优点**：
- 无需维护会话状态，便于水平扩展
- 减少数据库查询压力
- 服务端重启不影响已登录用户

### 客户端存储

还没想好（）

## Token 使用流程

### 登录获取 Token

```http
POST /api/public/sessions
Content-Type: application/json

{
  "account": "2024001",
  "password": "123456"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "account": "2024001",
    "role": "student",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImFjY291bnQiOiIyMDI0MDAxIiwicm9sZSI6InN0dWRlbnQiLCJpYXQiOjE3NDMyMzUyMDAsImV4cCI6MTc0MzMyMTYwMH0.abc123..."
  }
}
```

### 后续请求携带 Token

客户端将 Token 放在 **Authorization** 请求头中：

```http
GET /api/private/repairs
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImFjY291bnQiOiIyMDI0MDAxIiwicm9sZSI6InN0dWRlbnQiLCJpYXQiOjE3NDMyMzUyMDAsImV4cCI6MTc0MzMyMTYwMH0.abc123...
```

### 服务端验证 Token

1. 拦截器拦截所有 `/api/private/**` 请求
2. 从 `Authorization` 头中提取 Token
3. 验证 Token 签名是否有效
4. 检查 Token 是否过期
5. 解析 Token 中的 `userId`，存入 `request` 属性供后续使用

```java
// 验证通过后，Controller 中获取用户 ID
Long userId = (Long) request.getAttribute("userId");
```

---

## 安全说明

### 密钥管理

- JWT 签名密钥存储在 `application.yml` 中，不提交到代码仓库（实际项目应使用环境变量或配置中心）
- 密钥长度至少 256 位（32 字符），示例密钥仅用于开发测试

### Token 泄露风险

- Token 有效期设为 24 小时，降低泄露风险
- 如发现 Token 泄露，用户可修改密码使旧 Token 失效（服务端可维护黑名单，本系统暂未实现）

### 传输安全

- 生产环境必须使用 **HTTPS** 传输，防止 Token 被中间人截获

---

## 异常处理

| 异常类型       | HTTP 状态码 | 响应信息                 |
| -------------- | ----------- | ------------------------ |
| 未提供 Token   | 401         | 未登录，请先登录         |
| Token 格式错误 | 401         | Token 格式错误           |
| Token 签名无效 | 401         | Token 无效               |
| Token 已过期   | 401         | Token 已过期，请重新登录 |
| 账号或密码错误 | 401         | 账号或密码错误           |
| 注册失败       | 500         | 具体错误信息             |

---

## 相关代码文件

| 文件                   | 说明                              |
| ---------------------- | --------------------------------- |
| `JwtConfig.java`       | JWT 配置类，读取密钥和过期时间    |
| `JwtUtils.java`        | JWT 工具类，生成/解析/验证 Token  |
| `AuthInterceptor.java` | 拦截器，验证 Token 并存入用户信息 |
| `WebConfig.java`       | 拦截器注册配置                    |
| `LoginResponse.java`   | 登录响应 DTO，包含 Token          |

---

## 总结

本系统采用 **JWT（JSON Web Token）** 实现用户登录认证：

| 项目           | 说明                                            |
| -------------- | ----------------------------------------------- |
| **Token 生成** | 用户登录成功后服务端生成 JWT Token              |
| **Token 存储** | 服务端不存储，由客户端保管                      |
| **Token 传输** | 通过 `Authorization: Bearer <token>` 请求头传递 |
| **Token 验证** | 拦截器统一验证签名和有效期                      |
| **用户识别**   | 从 Token 中解析 `userId` 确定当前用户           |

这种无状态的认证方式便于系统扩展和维护，符合现代 Web 应用的设计规范。

---

**文档版本**：1.0  
**更新日期**：2026-03-30  
**作者**：口服液体猫