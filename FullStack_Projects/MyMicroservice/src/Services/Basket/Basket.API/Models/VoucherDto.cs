namespace Basket.API.Models;

public class VoucherDto
{
    public string Id { get; set; }
    public decimal Value { get; set; }
    public int Quantity { get; set; }
    public DateTime VoucherExpirationDate { get; set; }
}
