using System.Text.Json;
using Basket.API.Models;
using StackExchange.Redis;

namespace Basket.API.Data;

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