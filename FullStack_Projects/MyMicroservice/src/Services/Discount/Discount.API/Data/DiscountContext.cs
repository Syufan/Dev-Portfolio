using Microsoft.EntityFrameworkCore;
using Discount.API.Data;
using Discount.API.Models;

namespace Discount.API.Data;

public class DiscountsContext : DbContext
{
    public DiscountsContext(DbContextOptions<DiscountsContext> options) : base(options) { }

    public DbSet<Voucher> Vouchers { get; set; }
    public DbSet<ProductDiscount> ProductDiscounts { get; set; }
}