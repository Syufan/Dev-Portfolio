using Basket.API.Models;

namespace Basket.API.Data;

public interface ICatalogService
{
    Task<CatalogItemDto?> GetCatalogItemByIdAsync(int id);
}