namespace Ordering.API.Data;
using Ordering.API.Models;

public interface IOrderRepository
{
    Task CreateOrderAsync(Order order);
    Task<Order?> GetOrderByIdAsync(int id);
    Task<IEnumerable<Order>> GetOrdersByUsernameAsync(string username);
}