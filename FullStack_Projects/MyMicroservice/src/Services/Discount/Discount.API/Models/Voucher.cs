namespace Discount.API.Models;

public class Voucher
{
    public string Id { get; set; }
    public decimal Value { get; set; }
    public int Quantity { get; set; }
    public DateTime VoucherExpirationDate { get; set; }= DateTime.UtcNow;
}