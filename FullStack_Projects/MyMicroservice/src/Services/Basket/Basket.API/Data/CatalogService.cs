using System.Text.Json;
using Basket.API.Models;
using StackExchange.Redis;
using System.Net;

namespace Basket.API.Data;

public class CatalogService: ICatalogService
{
    private readonly HttpClient _httpClient;

    public CatalogService(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }


    public async Task<CatalogItemDto?> GetCatalogItemByIdAsync(int id){
        //这个方法是用来获取数据item数据的
        //获得id后去 catalog去调用request id这个方法
        try{
            // return await _httpClient.GetFromJsonAsync<CatalogItemDto>($"api/catalog/{id}");
            var response = await _httpClient.GetAsync($"api/products/{id}");
            if (response.StatusCode == HttpStatusCode.NotFound)
            {
                return null;
            }

            response.EnsureSuccessStatusCode(); // 如果不是 2xx，会抛异常

            var product = await response.Content.ReadFromJsonAsync<CatalogItemDto>();
            return product;

        }catch (Exception ex){
            Console.WriteLine($"Catalog API 调用失败: {ex.Message}");
            return null;
        }
    }

}