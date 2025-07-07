using Ordering.API.Models;

namespace Ordering.API.Data;

public class OrderRepository : IOrderRepository
{
    private readonly OrderingDbContext _dbContext;

    public OrderRepository(OrderingDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task CreateOrderAsync(Order order)
    {
        _dbContext.Orders.Add(order);
        await _dbContext.SaveChangesAsync();
    }

    public async Task<Order?> GetOrderByIdAsync(int id)
    {
        return await _dbContext.Orders.FindAsync(id);
    }

    public async Task<IEnumerable<Order>> GetOrdersByUsernameAsync(string username)
    {
        return await Task.FromResult(_dbContext.Orders.Where(o => o.Username == username));
    }
}