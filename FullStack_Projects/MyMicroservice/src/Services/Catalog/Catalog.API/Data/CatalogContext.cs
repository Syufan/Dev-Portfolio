using Microsoft.EntityFrameworkCore;
using Catalog.API.Models;

namespace Catalog.API.Data;

public class CatalogContext : DbContext
{
    public CatalogContext(DbContextOptions<CatalogContext> options) : base(options) { }

    public DbSet<Product> Products => Set<Product>();
}