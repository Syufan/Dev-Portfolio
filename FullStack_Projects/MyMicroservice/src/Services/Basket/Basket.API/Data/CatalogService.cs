using System.Text.Json;
using Basket.API.Models;
using StackExchange.Redis;

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
        return await _httpClient.GetFromJsonAsync<CatalogItemDto>($"api/catalog/{id}");
    }

}