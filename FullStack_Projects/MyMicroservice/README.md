项目介绍：

    本项目是一个 面向电商场景的全栈微服务实战项目
    涵盖商品管理、购物车、订单提交与折扣策略等核心模块，支持商家多角色使用。
    是一个典型的Saas项目software as a service

    因为用单体的话，部署难，扩展性能差，维护困难，技术栈不灵活
    但是传统单体还是用在MVP上，初创团队，或者小规模的系统，以及预算有限变动有限的系统。


采用的技术：

    由于当前项目具有持续迭代、灵活扩展、角色多样等特点，
    因此采用 SaaS 化的微服务架构更加契合，能够实现服务解耦、独立部署、模块弹性扩展，
    并为未来的多租户与云原生部署打下基础。

具体的技术寨：
    前端
    后段 ASP.NET Core Web API 可选的别的  Node.js、Spring Boot、Go
        ASP.NET Core 特别适合 中大型系统或企业级 SaaS 平台，它具有高性能、良好的工程规范、强生态支持（如 EF Core、Identity、gRPC），非常适合需要长期维护、可扩展性强、部署安全性高的项目。
        Node.js 适合构建轻量级、快速迭代的项目，Spring Boot 适用于金融、电信等 Java 技术栈成熟的大型团队，Go 则适合高并发、低延迟服务（如 API 网关、微服务通信、日志系统等）。
    数据库 PostgreSQL + EF Core 可选的别的 MySQL、SQL Server、MongoDB
        用PostgreSQL是因为，下单牵扯到了多方的数据，所以会涉及到事务。
        目前用PostgreSQL很好的支持啦事务。


🧩 架构模式
微服务架构（.NET）（单体架构，分层架构，微服务架构，事件驱动架构）
    拆分功能服务，提升扩展性与可维护性
    支持服务解耦、独立部署、团队协作
⚙️ 后端框架（Spring Boot, Express.js, Go, Node.js）
ASP.NET Core Web API
    构建 RESTful 微服务
    性能好、生态成熟、适合企业级服务
🗃 数据库
PostgreSQL + EF Core
    各服务独立数据库 + ORM 管理
    PostgreSQL 稳定开源；EF Core 提高开发效率
📦 容器化
Docker
    封装服务，统一部署
    保证运行环境一致，支持快速扩容
🔄 服务注册 / 网关
API Gateway（Ocelot / BFF）
    聚合多个服务入口
    实现统一访问控制、负载均衡、路由转发
📤 服务通信（可扩展）
REST API / 可接入 gRPC / RabbitMQ
    服务之间通信 / 异步解耦
    支持多协议，便于水平扩展与高并发
📋 配置管理
appsettings.json + DI
    管理连接串、服务配置等
    配合依赖注入支持灵活配置
📈 接口测试
Swagger
    接口文档 + 在线测试
    开发期间快速验证 API
🔁 自动化部署（可扩展）
Docker Compose
    一键部署多服务



每个微服务的功能
    Catalog
        •	管理商品目录（CRUD）
        •	负责商品的展示、上架、下架、价格描述等
        •	提供 /api/products 相关接口
        •	连接 PostgreSQL，通过 EF Core 管理数据
        •	供 Basket、Ordering 等服务查询商品信息
    Basket
        •	管理用户的购物车（每个用户一个购物车）
        •	提供添加商品、删除商品、清空购物车、更新数量、查看
        •	一般会将购物车信息保存在 Redis 中（高性能、低延迟）
        •	对接 Catalog 服务获取商品信息（如价格和名称）
    DIscount
        •	提供优惠券（Coupon）系统或折扣策略
        •	存储每个商品或用户可使用的折扣
        •	通常使用 gRPC 或 REST 提供接口
        •	被 Basket / Ordering 在结算时调用
    Ordering
        •	处理下单流程，包括从购物车生成订单
        •	存储订单记录（订单号、用户、商品清单、金额、状态等）
        •	可以包含订单状态流转（新建、已支付、已发货等）
        •	订单数据一般存储在数据库中（如 PostgreSQL）
        •	下单时会同时调用：
        •	Basket 服务：读取用户购物车
        •	Catalog 服务：确认商品价格与信息
        •	Discount 服务：计算应付金额

    用户下单的完整调用链为：
        1.	前端展示商品 → 调用 Catalog.API
        2.	用户加购商品 → 调用 Basket
        3.	用户使用优惠券 → 调用 Discount
        4.	提交订单 → Ordering 收集购物车 + 调用折扣 + 调用 Catalog → 最终生成订单



Catalog的开发步骤
    结构
        controller
            productsController.cs//控制器，处理API请求
        data
            CatalogContext.cs //数据，包含DbContext
        models
            Product.cs
        Migrations// EF Core 数据迁移自动生成
        program.cs//应用入口，配置中间件，服务注册
        appsetting.json//配置数据库连接字符串信息
1. 创建项目
    dotnet new webapi -n Catalog.API
    cd Catalog.API
2. 添加必要依赖
    dotnet add package Microsoft.EntityFrameworkCore
    dotnet add package Microsoft.EntityFrameworkCore.Design
    dotnet add package Npgsql.EntityFrameworkCore.PostgreSQL
3.定义模型，Models/Product.cs
    public class Product
    {
        public int Id { get; set; }
        public string Name { get; set; } = string.Empty;
        public string Description { get; set; } = string.Empty;
        public decimal Price { get; set; }
    }
4.配置 DbContext，Data/CatalogContext.cs
    public class CatalogContext : DbContext
    {
        public CatalogContext(DbContextOptions<CatalogContext> options) : base(options) {}
        public DbSet<Product> Products { get; set; }
    }
5.配置服务Program.cs
    builder.Services.AddDbContext<CatalogContext>(options =>
        options.UseNpgsql(builder.Configuration.GetConnectionString("CatalogConnection")));

    builder.Services.AddControllers();
    builder.Services.AddEndpointsApiExplorer();
    builder.Services.AddSwaggerGen();
6.appsettings.json 添加连接字符串：
    "ConnectionStrings": {
    "CatalogConnection": "Host=localhost;Port=5432;Database=CatalogDB;Username=postgres;Password=your_password"
    }
7. 数据库迁移与生成(此步骤会在 PostgreSQL 中创建 CatalogDB 数据库和 Products 表)
    dotnet ef migrations add InitialCreate
    dotnet ef database update
8. 创建控制器（CRUD）Controllers/ProductsController.cs
	•	GET /api/products
	•	GET /api/products/{id}
	•	POST /api/products
	•	PUT /api/products/{id}
	•	DELETE /api/products/{id}
9. 启用 Swagger & 启动项目
    if (app.Environment.IsDevelopment())
    {
        app.UseSwagger();
        app.UseSwaggerUI();
    }
10.运行项目
    dotnet run


语法
    [HttpXXX] // 请求类型，如 HttpGet, HttpPost 等
    public ActionResult<返回类型> 方法名(参数)
    {
        // 逻辑处理
        return Ok(返回内容);
    }
    🔁 添加依赖 ➜ 配置 appsettings ➜ 创建 DbContext + 模型 ➜ 注册服务 ➜ 控制器调用


Basket开发
    Basket.API/
    │
    ├── appsettings.json           ⬅ Redis 地址配置
    ├── Program.cs                 ⬅ 注册 Redis + Repository
    ├── Models/
    │   ├── ShoppingCart.cs        ⬅ 用户购物车结构
    │   └── ShoppingCartItem.cs    ⬅ 购物车内商品结构
    │
    ├── Data/
    │   ├── IBasketRepository.cs   ⬅ 接口定义
    │   └── BasketRepository.cs    ⬅ Redis 实现
    │
    └── Controllers/
        └── BasketController.cs    ⬅ API 入口

1. 创建一个新的 Web API 项目
    dotnet new webapi -n Basket.API
    cd Basket.API
2. 添加必要依赖
    dotnet add package StackExchange.Redis
    dotnet add package Swashbuckle.AspNetCore
3. 定义模型，Models
    写shoppingCartitem.cs
    写ShoppingCart.cs
4. 用 Redis 来存储 Basket（购物车）服务的数据，就不涉及到对DbContext的使用了
    1. 在Basket.API.csproj添加
        <PackageReference Include="StackExchange.Redis" Version="2.6.122" />
    2.安装
        dotnet add package StackExchange.Redis
    3.appsettings.json 配置 Redis 连接字符串
        {
            "ConnectionStrings": {
                "Redis": "localhost:6379"  // 如果你是用 Docker，则替换为容器名
            }
        }
    4.注册 Redis 服务与 Repository 在Program.cs 中添加：
        using StackExchange.Redis;
        using Basket.API.Data;

        builder.Services.AddSingleton<IConnectionMultiplexer>(sp =>
            ConnectionMultiplexer.Connect(builder.Configuration.GetConnectionString("Redis")!)
        );

        builder.Services.AddScoped<IBasketRepository, BasketRepository>();
    5.定义接口 Data/IBasketRepository.cs
        public interface IBasketRepository
        {
            Task<ShoppingCart?> GetBasketAsync(string username);
            Task<ShoppingCart> UpdateBasketAsync(ShoppingCart basket);
            Task DeleteBasketAsync(string username);
        }
    6.Data/BasketRepository.cs
        public class BasketRepository : IBasketRepository
        {
            private readonly IDatabase _database;

            public BasketRepository(IConnectionMultiplexer redis)
            {
                _database = redis.GetDatabase();
            }

            public async Task<ShoppingCart?> GetBasketAsync(string username)
            {
                var basketData = await _database.StringGetAsync(username);
                if (basketData.IsNullOrEmpty) return null;
                return JsonSerializer.Deserialize<ShoppingCart>(basketData!);
            }

            public async Task<ShoppingCart> UpdateBasketAsync(ShoppingCart basket)
            {
                var updated = await _database.StringSetAsync(
                    basket.Username,
                    JsonSerializer.Serialize(basket)
                );
                return updated ? basket : throw new Exception("Failed to save basket");
            }

            public async Task DeleteBasketAsync(string username)
            {
                await _database.KeyDeleteAsync(username);
            }
        }
    7.Models的开发-> Controllers 开发 -> Program.cs 注册




Ordering开发
    Ordering.API/
    │
    ├── appsettings.json           ⬅ Redis 地址配置
    ├── Program.cs                 ⬅ 注册 Redis + Repository
    ├── Models/
    │   ├──         ⬅ 用户购物车结构
    │   └──     ⬅ 购物车内商品结构
    │
    ├── Data/
    │   ├──    ⬅ 接口定义
    │   └──     ⬅ Redis 实现
    │
    └── Controllers/
        └──     ⬅ API 入口

1. 创建一个新的 Web API 项目
    dotnet new webapi -n Ordering.API
    cd Ordering.API

2. 创建文件夹
    mkdir Controllers
    mkdir Data
    mkdir Models

3. 添加必要依赖
    数据库支持
    dotnet add package Microsoft.EntityFrameworkCore
    dotnet add package Microsoft.EntityFrameworkCore.Design
    dotnet add package Npgsql.EntityFrameworkCore.PostgreSQL
    Rabbit 消息队列
    dotnet add package MassTransit
    dotnet add package MassTransit.AspNetCore
    dotnet add package MassTransit.RabbitMQ

4. 创建 OrderItem.cs -> Order.cs 