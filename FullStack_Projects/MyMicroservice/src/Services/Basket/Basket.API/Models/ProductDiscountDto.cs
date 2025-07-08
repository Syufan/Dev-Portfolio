namespace Basket.API.Models;

public class ProductDiscountDto
{
    public int ID { get; set; }
    public decimal price { get; set; }
    public DateTime ProductDisExpirationDate { get; set; }
}