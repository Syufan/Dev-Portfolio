namespace Shared.Events;
using System.Collections.Generic;

public class SharedBasketCheckout
{
    public string Username { get; set; } = string.Empty;
    public string Address { get; set; } = string.Empty;
    public string PaymentMethod { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public decimal TotalPrice { get; set; }
    public List<BasketItem> Items { get; set; } = new();
}

public class BasketItem
{
    public string ProductId { get; set; } = string.Empty;
    public string ProductName { get; set; } = string.Empty;
    public decimal Price { get; set; }
    public int Quantity { get; set; }
}