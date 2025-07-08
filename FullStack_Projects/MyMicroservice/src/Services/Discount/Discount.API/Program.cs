using Discount.API.Data;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// 注册服务
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// 添加数据库服务（使用 PostgreSQL）
builder.Services.AddDbContext<DiscountsContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DiscountDb")));

var app = builder.Build();

// 中间件配置
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.UseAuthorization();

app.MapControllers();

app.Urls.Add("http://localhost:5500");
app.Run();