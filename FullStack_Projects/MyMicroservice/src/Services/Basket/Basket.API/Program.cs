using Basket.API.Data;
using StackExchange.Redis;
using MassTransit;

// 构建 WebApplication 对象（ASP.NET Core 标准入口）
var builder = WebApplication.CreateBuilder(args);

// 注册服务
// 添加 Redis 配置和服务
builder.Services.AddSingleton<IConnectionMultiplexer>(sp =>
    ConnectionMultiplexer.Connect(builder.Configuration.GetConnectionString("Redis")!)
);

// 注册 MassTransit 服务
builder.Services.AddMassTransit(x =>
{
    x.UsingRabbitMq((context, cfg) =>
    {
        cfg.Host("localhost", "/", h =>
        {
            h.Username("guest");
            h.Password("guest");
        });
    });
});

// 注入 Basket 仓储 业务逻辑层（Basket Repository）
builder.Services.AddScoped<IBasketRepository, BasketRepository>();

// 添加控制器与 Swagger 服务
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var catalogBaseUrl = builder.Configuration["CatalogService:BaseUrl"]
builder.Services.AddHttpClient<ICatalogService, CatalogService>(c =>{
    c.BaseAddress = new Uri(catalogBaseUrl);
});

// 构建应用对象并配置中间件
var app = builder.Build();

// 开始设置 Web 请求处理流程：
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();

if(!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/error");
};

app.Run();