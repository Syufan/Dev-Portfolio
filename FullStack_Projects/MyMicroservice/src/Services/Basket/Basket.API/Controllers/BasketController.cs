using Microsoft.AspNetCore.Mvc;
using Basket.API.Models;
using Basket.API.Data;
using MassTransit;
using System.Linq;
using Shared.Events;
using Microsoft.AspNetCore.Diagnostics;

namespace Basket.API.Controllers;

[ApiController]
[Route("api/[controller]")]
public class BasketController : ControllerBase
{
    private readonly IBasketRepository _repository;
    private readonly IPublishEndpoint _publishEndpoint;//MassTransit 框架提供的接口，用于 向消息中间件（RabbitMQ）发送消息
    private readonly ILogger<BasketController> _logger;
    private readonly ICatalogService _catalogService;
    private readonly IDiscountService _discountService;

    // 构造函数注入依赖
    public BasketController(
        IBasketRepository repository, 
        IPublishEndpoint publishEndpoint, 
        ILogger<BasketController> logger, 
        ICatalogService catalogService,
        IDiscountService discountService)
    {
        _repository = repository;
        _publishEndpoint = publishEndpoint;
        _logger = logger;
        _catalogService = catalogService;
        _discountService = discountService;
    }

    // 获取购物车
    [HttpGet("{username}")]
    public async Task<ActionResult<ShoppingCart>> GetBasket(string username){
        var basket = await _repository.GetBasketAsync(username) 
           ?? new ShoppingCart { Username = username };
        
        foreach (var item in basket.Items)
        {
            var product = await _catalogService.GetCatalogItemByIdAsync(item.ProductId);
            Console.WriteLine($"item.ProductId: {item.ProductId}");
            if (product != null)
            {   
                var discountProduct = await _discountService.GetDiscountPriceForProduct(item.ProductId);
                Console.WriteLine("yes");
                
                item.ProductName = product.Name;
                item.Price = product.Price;
                if (discountProduct!=null)
                {
                    item.Price = discountProduct.price;
                }
            }
        }
        return Ok(basket);
    }

    // 更新购物车
    [HttpPost]
    public async Task<ActionResult<ShoppingCart>> UpdateBasket([FromBody] ShoppingCart basket)
    {
        var updatedBasket = await _repository.UpdateBasketAsync(basket);
        return Ok(updatedBasket);
    }

    // 删除购物车
    [HttpDelete("{username}")]
    public async Task<IActionResult> DeleteBasket(string username)
    {
        await _repository.DeleteBasketAsync(username);
        return NoContent();
    }

    // 结账
    [HttpPost("Checkout")]
    public async Task<IActionResult> CheckoutBasket([FromBody] BasketCheckout basketCheckout)//方法名 + 参数类型 + 参数名
    {
        try
        {
            if (basketCheckout == null || string.IsNullOrEmpty(basketCheckout.Username))
                return BadRequest("Invalid checkout data");

            var basket = await _repository.GetBasketAsync(basketCheckout.Username);//获取当前购物车
            
            if (basket == null || !basket.Items.Any())
            {
                return BadRequest("Basket is empty");
            }
            Console.WriteLine("Finish Check basket...");

            //每个购物项（更新商品名和最新价格）
            foreach (var item in basket.Items)
            {
                var product = await _catalogService.GetCatalogItemByIdAsync(item.ProductId);
                if (product != null)
                {
                    var discountProduct = await _discountService.GetDiscountPriceForProduct(item.ProductId);
                    item.ProductName = product.Name;
                    item.Price = product.Price;
                    if (discountProduct!=null)
                    {
                        item.Price = discountProduct.price;
                    }
                }
            }

            Console.WriteLine("Finish update Iteam...");

            var CheckedTotalPrice = basket.Items.Sum(i=> i.Price *i.Quantity);
            decimal voucherDiscount = 0;
            if (!string.IsNullOrEmpty(basketCheckout.VoucherCode))
            {
                var voucher = await _discountService.GetVoucherByIdAsync(basketCheckout.VoucherCode);
                if (voucher != null && voucher.Value > 0)
                {
                     voucherDiscount = voucher.Value;
                     CheckedTotalPrice = CheckedTotalPrice - voucherDiscount;
                }
            }
            Console.WriteLine("Finish Checked Total Price...");

            var checkoutEvent = new SharedBasketCheckout
            {
                Username = basketCheckout.Username,
                Address = basketCheckout.Address,
                PaymentMethod = basketCheckout.PaymentMethod,
                Email = basketCheckout.Email,
                TotalPrice = CheckedTotalPrice,
                Items = basket.Items.Select(item => new BasketItem
                {
                    ProductId = item.ProductId,
                    ProductName = item.ProductName,
                    Quantity = item.Quantity,
                    Price = item.Price
                }).ToList()
            };

            await _publishEndpoint.Publish(checkoutEvent);
            Console.WriteLine("Publishing checkout event...");

            await _repository.DeleteBasketAsync(basketCheckout.Username);

            return Accepted();
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Checkout error: {ex.Message}");
            return StatusCode(500, "Internal server error");
        }
    }

    [HttpGet("/error")]
    public IActionResult Error()
    {
        var context = HttpContext.Features.Get<IExceptionHandlerFeature>();
        var exception = context?.Error;
        return Problem(
            title: "Unexpected Error",
            statusCode: 500,
            detail: exception?.Message
        );
    }
}