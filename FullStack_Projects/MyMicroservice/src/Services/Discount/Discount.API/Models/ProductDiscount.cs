namespace Discount.API.Models;

public class ProductDiscount
{
    public int ID {get; set;}
    public decimal price {get; set;}
    public DateTime ProductDisExpirationDate {get;set;}
}