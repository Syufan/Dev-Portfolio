using Microsoft.EntityFrameworkCore;
using Ordering.API.Models;

namespace Ordering.API.Data;

public class OrderingDbContext : DbContext
{
    public OrderingDbContext(DbContextOptions<OrderingDbContext> options)
        : base(options) { }

    public DbSet<Order> Orders => Set<Order>();
    public DbSet<OrderItem> OrderItems => Set<OrderItem>(); // 可选但推荐添加

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // 👇 配置嵌套值对象
        modelBuilder.Entity<Order>().OwnsOne(o => o.ShippingAddress);
        modelBuilder.Entity<Order>().OwnsOne(o => o.CardInformation);

        // 👇 配置一对多关系：Order -> OrderItems
        modelBuilder.Entity<Order>()
            .HasMany(o => o.Items)
            .WithOne()
            .HasForeignKey("OrderId"); // OrderItem 表中会有 OrderId 列作为外键

        base.OnModelCreating(modelBuilder);
    }
}