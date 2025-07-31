# E-Commerce Microservices Backend (.NET Core)

This project is a practical full-stack SaaS-oriented microservices backend tailored for e-commerce scenarios. It demonstrates a modular and cloud-ready system for managing products, shopping carts, discounts, and orders.

---

## ✨ Project Overview

This system simulates a SaaS e-commerce platform where merchants and customers interact through core modules:

* **Catalog Service**: Product listing and CRUD
* **Basket Service**: User shopping carts with Redis caching
* **Discount Service**: Coupons and discount strategies
* **Ordering Service**: Checkout workflows and order tracking

The project avoids monolithic architecture due to its limitations in scalability and maintainability. Instead, it adopts a microservices-based architecture for better modularity and cloud-native readiness.

---

## 🧱 Tech Stack

### Core Technologies

* **Backend Framework**: ASP.NET Core Web API
* **Database**: PostgreSQL with Entity Framework Core (EF Core)
* **Caching**: Redis (for Basket service)
* **Message Queue**: RabbitMQ (for event-driven Ordering service)
* **API Gateway**: Ocelot (optional)
* **Documentation & Testing**: Swagger
* **Containerization**: Docker, Docker Compose

### Why These Choices

* **ASP.NET Core**: High performance, DI, strong typing
* **PostgreSQL**: Robust transaction support
* **Redis**: Low-latency cache
* **RabbitMQ**: Async event-driven design

---

## 📊 Architecture

* **Microservices**: Independently deployable with isolated DBs
* **Event-Driven Design**: Decoupling via message brokers
* **RESTful APIs**: Inter-service communication
* **Domain Isolation**: Service-specific logic encapsulation

### Service Flow (Example)

1. Frontend fetches products from Catalog.API
2. User adds items to Basket.API
3. Discount checked via Discount.API
4. Ordering.API handles checkout with data from other services

---

## 🧹 Service Responsibilities

### 📦 Catalog.API

* Product CRUD
* Endpoint: `/api/products`
* PostgreSQL + EF Core

### 🍎 Basket.API

* User cart stored in Redis
* Add/remove/clear/update items
* Queries Catalog for product details

### 🎫 Discount.API

* Manages product/user discounts
* Coupon validation
* Expandable via REST or gRPC

### 📁 Ordering.API

* Processes checkout
* Persists order details
* Uses RabbitMQ for async flow

---

## ⚙ Setup & Development

### ✅ Prerequisites

* Docker + Docker Compose
* PostgreSQL instance
* .NET SDK (>= 7.0)

### 🚀 Run the Services

```bash
docker-compose up -d
```

To stop and remove volumes:

```bash
docker-compose down -v
```

To clean all Docker cache:

```bash
docker system prune -a --volumes
```

---

## 🔧 Catalog.API Quick Setup

1. Create the project:

```bash
dotnet new webapi -n Catalog.API
cd Catalog.API
```

2. Add EF Core + PostgreSQL packages:

```bash
dotnet add package Microsoft.EntityFrameworkCore

dotnet add package Npgsql.EntityFrameworkCore.PostgreSQL
```

3. Define models: `Product.cs`, and `CatalogContext.cs`
4. Register EF Core in `Program.cs`:

```csharp
builder.Services.AddDbContext<CatalogContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("CatalogConnection")));
```

5. Enable Swagger & run migrations:

```bash
dotnet ef migrations add InitialCreate
dotnet ef database update
```

6. Run API:

```bash
dotnet run
```

---

## 📚 Resources

* GitHub Reference: [aspnetrun/run-aspnetcore-microservices](https://github.com/aspnetrun/run-aspnetcore-microservices)

---

## ✅ TODO (Enhancements)

* Add authentication (e.g., IdentityServer)
* Implement API Gateway
* Add integration tests (TDD)
* Frontend demo (React/Vue)
* Setup CI/CD with GitHub Actions

---

This project serves as a learning resource and a scalable template for modern distributed systems with .NET Core. Contributions welcome.
