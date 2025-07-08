using System.Net.Http.Json;
using Basket.API.Models;
using System.Net;

namespace Basket.API.Data;

public class DiscountService: IDiscountService
{
    private readonly HttpClient _httpClient;

    public DiscountService(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<ProductDiscountDto> GetDiscountPriceForProduct(int productId)
    {
        // var productDis = await _httpClient.GetFromJsonAsync<ProductDiscountDto>($"api/discounts/product-discounts/{productId}");
        // return productDis;
        var response = await _httpClient.GetAsync($"api/discounts/product-discounts/{productId}");

        if (response.StatusCode == HttpStatusCode.NotFound)
        {
            return null; // 没有找到折扣，返回 null
        }

        response.EnsureSuccessStatusCode(); // 其他错误会继续抛出异常（比如 500）

        var discount = await response.Content.ReadFromJsonAsync<ProductDiscountDto>();
        return discount;
    }

    public async Task<VoucherDto?> GetVoucherByIdAsync(string voucherId)
    {
        // var voucher = await _httpClient.GetFromJsonAsync<VoucherDto>($"api/discounts/vouchers/{voucherId}");
        // return voucher;
        var response = await _httpClient.GetAsync($"api/discounts/vouchers/{voucherId}");

        if (response.StatusCode == HttpStatusCode.NotFound)
        {
            return null; // 优惠券不存在，手动返回 null
        }

        response.EnsureSuccessStatusCode(); // 如果是其它错误（如 500），会抛出异常

        return await response.Content.ReadFromJsonAsync<VoucherDto>();
    }
}