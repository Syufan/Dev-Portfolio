using Microsoft.EntityFrameworkCore;
using Ordering.API.Models;

namespace Ordering.API.Data;

public class OrderingDbContext : DbContext
{
    public OrderingDbContext(DbContextOptions<OrderingDbContext> options)
        : base(options) { }

    public DbSet<Order> Orders => Set<Order>();
}