using System;
using System.Threading.Tasks;
using MassTransit;
using Ordering.API.Models;
using Shared.Events;
using Ordering.API.Data;

namespace Ordering.API.Consumers;

public class BasketCheckoutConsumer : IConsumer<SharedBasketCheckout>
{
    private readonly IOrderRepository _orderRepository;
    public BasketCheckoutConsumer(IOrderRepository orderRepository)
    {
        _orderRepository = orderRepository;
    }

    public async Task Consume(ConsumeContext<SharedBasketCheckout> context)
    {
        var basket = context.Message;

        // 映射 BasketCheckout 到 Order
        var order = new Order
        {
            Username = basket.Username,
            OrderDate = DateTime.UtcNow,
            CVV = "123", // 示例值，实际应来自 BasketCheckout
            ShippingAddress = new Address
            {
                Street = basket.Address, // 假定 basket.Address 有
                City = "Unknown", // 示例
                State = "Unknown",
                ZipCode = "00000",
                Country = "Unknown"
            },
            CardInformation = new Card
            {
                CardName = "User Card",
                CardNumber = "0000-0000-0000-0000",
                Expiration = "12/30"
            }
        };

        // 保存订单
        await _orderRepository.CreateOrderAsync(order);
    }
}