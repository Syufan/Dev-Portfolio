namespace Catalog.API.Models;

public class Product
{
    public int Id { get; set; }                         // 主键
    public string Name { get; set; } = string.Empty;    // 名称
    public string Description { get; set; } = string.Empty; // 描述
    public decimal Price { get; set; }                  // 价格
}