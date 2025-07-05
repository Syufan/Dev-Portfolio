namespace Basket.API.Models;

public class BasketCheckout{
    public string Username { get; set; }
    public string Address { get; set; }
    public string PaymentMethod { get; set; }
    public string Email { get; set; }
    public decimal TotalPrice { get; set; }
    public List<ShoppingCartItem> Items { get; set; }
}