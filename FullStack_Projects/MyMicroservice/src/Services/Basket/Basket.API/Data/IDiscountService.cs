using Basket.API.Models;

namespace Basket.API.Data;

public interface IDiscountService
{
    Task<ProductDiscountDto> GetDiscountPriceForProduct(int productId);
    Task<VoucherDto?> GetVoucherByIdAsync(string voucherId);
}