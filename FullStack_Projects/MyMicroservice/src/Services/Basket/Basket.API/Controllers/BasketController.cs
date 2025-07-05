using Microsoft.AspNetCore.Mvc;
using Basket.API.Models;
using Basket.API.Data;
using MassTransit;

namespace Basket.API.Controllers;

[ApiController]
[Route("api/[controller]")]
public class BasketController : ControllerBase
{
    private readonly IBasketRepository _repository;
    private readonly IPublishEndpoint _publishEndpoint;//MassTransit 框架提供的接口，用于 向消息中间件（RabbitMQ）发送消息

    // 构造函数注入依赖
    public BasketController(IBasketRepository repository, IPublishEndpoint publishEndpoint){
        _repository = repository;
        _publishEndpoint = publishEndpoint;
    }

    // 获取购物车
    [HttpGet("{username}")]
    public async Task<ActionResult<ShoppingCart>> GetBasket(string username){
        var basket = await _repository.GetBasketAsync(username);
        return Ok(basket ?? new ShoppingCart { Username = username });
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
        // 获取购物车
        var basket = await _repository.GetBasketAsync(basketCheckout.Username);
        if(basket == null){
            return BadRequest("Basket not found");
        }

        // 创建结账事件
        var checkoutEvent = new
        {
            basketCheckout.Username,
            basketCheckout.Address,
            basketCheckout.PaymentMethod,
            basketCheckout.Email,
            basket.TotalPrice,
            Items = basket.Items
        };

        // 发布结账事件 
        await _publishEndpoint.Publish(checkoutEvent);

        // 删除购物车
        await _repository.DeleteBasketAsync(basketCheckout.Username);

        return Accepted();
    }
}