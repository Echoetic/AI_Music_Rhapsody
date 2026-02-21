# 爱乐狂想 · AiMusicRhapsody

<p align="center">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-3.5.3-6DB33F?style=for-the-badge&logo=springboot"/>
  <img alt="Java" src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk"/>
  <img alt="Thymeleaf" src="https://img.shields.io/badge/Thymeleaf-3.x-005F0F?style=for-the-badge&logo=thymeleaf"/>
  <img alt="DeepSeek" src="https://img.shields.io/badge/LLM-DeepSeek_Reasoner-4B8BBE?style=for-the-badge"/>
  <img alt="MySQL" src="https://img.shields.io/badge/Database-MySQL-4479A1?style=for-the-badge&logo=mysql"/>
  <img alt="License" src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge"/>
</p>

---

## 📖 项目简介

**爱乐狂想（AiMusicRhapsody）** 是一款基于 **Spring Boot MVC 架构**、集成大语言模型的个性化音乐推荐 Web 应用，由作者**独立完成从前端界面到后端逻辑、再到数据库设计的全栈开发**。

项目的核心创新在于：将 **DeepSeek Reasoner 大语言模型**引入音乐推荐场景，以用户输入的歌单为上下文，通过精心设计的 Prompt Engineering 驱动 LLM 从**流派匹配、情绪风格、歌词主题、流行度**四个维度进行深度分析，输出结构化 JSON 推荐结果，再经后端解析渲染至前端，实现了一套完整的"自然语言驱动的音乐发现"闭环。区别于传统协同过滤推荐算法，本项目将 LLM 的语义理解能力引入个性化推荐，是对 AI 与垂直场景深度融合的一次有益探索。

---

## ✨ 功能特性

- 🎵 **AI 音乐推荐**：用户输入最多 5 首喜欢的歌曲，DeepSeek Reasoner 基于四维度分析，精准推荐 3 首个性化新歌
- 📊 **多维度详细分析**：每首推荐歌曲附带推荐理由、流派匹配说明、流行度分析、歌词主题解读、情绪风格匹配及 1–5 星推荐指数
- 👤 **用户账号系统**：完整的注册、登录、Session 管理与退出登录功能
- 🔒 **登录态守卫**：未登录用户访问主页自动重定向至登录页，保护系统资源
- 💾 **推荐结果持久化**：借助 Spring `@SessionAttributes` 将推荐结果跨请求保留，支持从推荐结果页跳转至详细分析页
- 🎨 **响应式现代界面**：全站 6 个页面均采用纯 HTML + CSS 手工实现，配备动画效果、移动端适配与中文字体优化

---

## 🏗 系统架构

本项目严格遵循 **Spring Boot MVC 三层架构**，各层职责清晰分离：

```
┌─────────────────────────────────────────────────────────┐
│                      View 层（前端）                      │
│   home  /  login  /  register  /  recommend             │
│   recommend_result  /  recommend_detail                  │
│             Thymeleaf 模板引擎渲染                        │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                   Controller 层（后端）                   │
│  ┌─────────────┐ ┌──────────────┐ ┌──────────────────┐  │
│  │HomeController│ │UserController│ │RecommendController│ │
│  │  路由 /      │ │注册/登录/登出 │ │  推荐主逻辑       │  │
│  └─────────────┘ └──────────────┘ └────────┬─────────┘  │
└───────────────────────────────────────────┼─────────────┘
                                            │
┌───────────────────────────────────────────▼─────────────┐
│                    Service / 外部 API 层                  │
│         DeepSeekService  ←→  DeepSeek Reasoner API       │
│         WebClient（Spring WebFlux）异步调用               │
└───────────────────────────────────────────┬─────────────┘
                                            │
┌───────────────────────────────────────────▼─────────────┐
│                      Model / 数据层                       │
│   User.java（JPA 实体）  ←→  MySQL（users 表）            │
│   RecommendedSong.java（DTO，JSON 反序列化）              │
│   UserRepository（Spring Data JPA）                      │
└─────────────────────────────────────────────────────────┘
```

---

## 🗂 项目结构

```
src/
├── main/
│   ├── java/com/ailove/music/aimusicrhapsody/
│   │   ├── AiMusicRhapsodyApplication.java   # Spring Boot 启动入口
│   │   ├── controller/
│   │   │   ├── HomeController.java           # 根路由控制器
│   │   │   ├── UserController.java           # 用户注册/登录/登出/主页
│   │   │   └── RecommendController.java      # AI 推荐核心控制器
│   │   ├── model/
│   │   │   ├── User.java                     # 用户 JPA 实体（含时间戳）
│   │   │   └── RecommendedSong.java          # 推荐歌曲 DTO
│   │   ├── repository/
│   │   │   └── UserRepository.java           # Spring Data JPA 数据访问层
│   │   └── service/
│   │       └── DeepSeekService.java          # DeepSeek API 封装服务
│   └── resources/
│       └── templates/
│           ├── home.html                     # 主页（登录态展示）
│           ├── login.html                    # 登录页
│           ├── register.html                 # 注册页
│           ├── recommend.html                # 歌曲输入与推荐触发页
│           ├── recommend_result.html         # 推荐结果展示页
│           └── recommend_detail.html         # 详细多维度分析页
```

---

## 💡 核心设计亮点

### 1. Prompt Engineering 驱动的结构化 LLM 输出

`RecommendController` 中最具创新性的设计是对大模型输出的精确约束。Prompt 不仅描述了推荐任务，还强制规定了 JSON Schema，要求模型直接输出可被 Jackson 反序列化的结构化数组，避免了自然语言输出带来的解析不确定性：

```java
String prompt = "你是一个专业的音乐推荐与分析专家。\n" +
    "输入：我最近喜欢的歌曲有：" + songList + "。\n" +
    "请基于以下四大维度推荐 3 首新歌，每首以 JSON 对象形式返回……\n" +
    "最终只输出一个 JSON 数组，不带任何多余文字。";
```

这种"输出格式即协议"的设计思路，使后端能够以零额外处理成本直接消费 LLM 输出，体现了对 LLM 工程化落地的深入思考。

### 2. 跨请求 Session 状态管理

推荐结果需要在"推荐结果页"与"详细分析页"之间共享，而 HTTP 的无状态性使这一需求并不平凡。通过 `@SessionAttributes("recommendations")` 注解将推荐数据绑定到 Spring MVC 的 Session 作用域，配合 `@SessionAttribute` 在详情页 Controller 中安全读取，实现了优雅的跨请求状态传递，避免了重复调用 LLM API 的资源浪费。

### 3. Spring WebFlux WebClient 异步 HTTP 调用

调用 DeepSeek API 采用 Spring WebFlux 的 `WebClient` 而非传统 `RestTemplate`，以响应式编程模型发起异步 HTTP 请求，通过 `.block()` 在必要时转为同步等待。这一设计为后续接入流式（Streaming）响应预留了扩展空间。

### 4. 健壮的用户数据模型

`User` 实体使用 `@CreationTimestamp` / `@UpdateTimestamp` 自动管理时间字段，`@Column(unique = true)` 约束邮箱唯一性，`equals()` / `hashCode()` 基于业务主键（email）实现，体现了对 JPA 实体设计规范的严格遵循。

### 5. 全站手工实现的精品 UI

6 个页面的 CSS 均从零手写，无前端框架依赖，实现了：渐变背景与毛玻璃效果、卡片悬停动画、输入框聚焦过渡、页面加载淡入动画、Font Awesome 图标集成、移动端响应式布局以及中文字体（Noto Sans SC）优化。在保持轻量的同时达到了接近商业产品的视觉质量。

---

## 🔄 核心业务流程

```
用户登录
    │
    ▼
输入最多 5 首喜欢的歌曲名称（recommend.html）
    │
    ▼
RecommendController 过滤空值，拼接结构化 Prompt
    │
    ▼
WebClient POST → DeepSeek Reasoner API
    │
    ▼
解析响应 JSON → Jackson 反序列化为 List<RecommendedSong>
    │
    ├──→ 存入 Spring Session（@SessionAttributes）
    │
    ▼
recommend_result.html（概览：标题 + 推荐理由 + 星级）
    │
    ▼
recommend_detail.html（详细：流派 / 流行度 / 歌词主题 / 情绪）
```

---

## 🛠 技术栈

| 层次 | 技术选型 |
|---|---|
| **后端框架** | Spring Boot 3.5.3 |
| **MVC 模板引擎** | Thymeleaf 3.x |
| **数据持久层** | Spring Data JPA + Hibernate |
| **数据库** | MySQL |
| **HTTP 客户端** | Spring WebFlux WebClient |
| **AI 推理引擎** | DeepSeek Reasoner（`deepseek-reasoner` 模型）|
| **JSON 处理** | Jackson（ObjectMapper + TypeReference）|
| **构建工具** | Apache Maven |
| **前端** | 原生 HTML5 + CSS3 + JavaScript（无框架）|
| **图标库** | Font Awesome 6.4.0 |
| **字体** | Google Fonts（Noto Sans SC + Poppins）|

---

## 🚀 快速开始

### 前置条件

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- DeepSeek API Key（[申请地址](https://platform.deepseek.com/)）

### 数据库初始化

```sql
CREATE DATABASE ai_music_rhapsody DEFAULT CHARACTER SET utf8mb4;
```

### 配置

在 `src/main/resources/application.properties` 中配置：

```properties
# 数据库
spring.datasource.url=jdbc:mysql://localhost:3306/ai_music_rhapsody
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# DeepSeek API
ai.api.base-url=https://api.deepseek.com/v1
ai.api.key=your_deepseek_api_key
```

### 启动

```bash
# 克隆仓库
git clone https://github.com/your-username/AiMusicRhapsody.git
cd AiMusicRhapsody

# 编译并运行
mvn spring-boot:run
```

访问 `http://localhost:8080` 即可使用。

---

## 📋 API 路由一览

| 路径 | 方法 | 描述 |
|---|---|---|
| `/` | GET | 根路由，跳转至 home |
| `/register` | GET / POST | 用户注册 |
| `/login` | GET / POST | 用户登录 |
| `/logout` | GET | 退出登录，清除 Session |
| `/home` | GET | 主页（需登录） |
| `/recommend` | GET | 推荐表单页 |
| `/recommend` | POST | 提交歌曲，触发 AI 推荐 |
| `/recommend/detail` | GET | 详细多维度分析页 |

---

## 📄 许可证

本项目基于 [MIT License](LICENSE) 开源。

---

<p align="center">
  <strong>爱乐狂想 · 让 AI 读懂你的音乐灵魂</strong><br/>
  北京邮电大学 · 数据科学与大数据技术
</p>
