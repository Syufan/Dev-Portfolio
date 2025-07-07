using Basket.API.Models;

namespace Basket.API.Data;

public interface IBasketRepository
{
    Task<ShoppingCart?> GetBasketAsync(string username);
    Task<ShoppingCart> UpdateBasketAsync(ShoppingCart basket);
    Task DeleteBasketAsync(string username);
    // grammar 
    // ReturnType MethodName(ParameterType parameterName);
}