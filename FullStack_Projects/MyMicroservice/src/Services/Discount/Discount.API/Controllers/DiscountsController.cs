using Microsoft.AspNetCore.Mvc;
using Discount.API.Data;
using Discount.API.Models;
using Microsoft.EntityFrameworkCore;

namespace Discount.API.Controllers;


[ApiController]
[Route("api/[controller]")]
public class DiscountsController : ControllerBase
{
    private readonly DiscountsContext _context;

    public DiscountsController(DiscountsContext context)
    {
        _context = context;
    }

    // 所有的优惠券
    [HttpGet("vouchers")]
    public ActionResult<IEnumerable<Voucher>> GetAllVouchers()
    {
        return Ok(_context.Vouchers.AsNoTracking().Take(100).ToList());
    }

    // 所有的产品折扣
    [HttpGet("product-discounts")]
    public ActionResult<IEnumerable<ProductDiscount>> GetAllProductDiscounts()
    {
        return Ok(_context.ProductDiscounts.AsNoTracking().Take(100).ToList());
    }

    // 单个优惠券
    [HttpGet("vouchers/{id}")]
    public ActionResult<Voucher> GetVoucherById(string id)
    {
        var voucherDis = _context.Vouchers.Find(id);
        if (voucherDis == null){
            return NotFound($"No ProductDiscount found with id {id}");
        }
        return Ok(voucherDis);
    }

    // 单个产品
    [HttpGet("product-discounts/{id:int}")]
    public ActionResult<ProductDiscount> GetProductDiscountById(int id)
    {
        var productDis = _context.ProductDiscounts.Find(id);
        if (productDis == null){
            return NotFound($"No ProductDiscount found with id {id}");
        }
        return Ok(productDis);
    }

    // 创建优惠券
    [HttpPost("vouchers")]
    public ActionResult<Voucher> CreateVoucher([FromBody] Voucher voucher)
    {
        _context.Vouchers.Add(voucher);
        _context.SaveChanges();
        return CreatedAtAction(nameof(GetVoucherById), new { id = voucher.Id }, voucher);
    }

    // 创建优惠产品
    [HttpPost("product-discounts")]
    public ActionResult<ProductDiscount> CreateProductDiscount([FromBody] ProductDiscount productDiscount)
    {
        _context.ProductDiscounts.Add(productDiscount);
        _context.SaveChanges();
        return CreatedAtAction(nameof(GetProductDiscountById), new { id = productDiscount.ID }, productDiscount);
    }

    // 删除产品
    [HttpDelete("product-discounts/{id}")]
    public IActionResult DeleteProductDiscount(int id)
    {
        // var product = GetProductDiscountById(id);
        var product = _context.ProductDiscounts.Find(id);
        if (product == null)return NotFound();
        _context.ProductDiscounts.Remove(product);
        _context.SaveChanges();
        return NoContent();
    }

    // 删除优惠券
    [HttpDelete("vouchers/{id}")]
    public IActionResult DeleteVoucher(string id)
    {
        // var voucher = GetVoucherById(id);
        var voucher = _context.Vouchers.Find(id);
        if (voucher == null) return NotFound();
        _context.Vouchers.Remove(voucher);
        _context.SaveChanges();
        return NoContent();
    }

    // 更新产品
    [HttpPut("product-discounts/{id}")]
    public IActionResult UpdateProductDiscount(int id, ProductDiscount updated)
    {   
        // var oldProduct = _context.ProductDiscounts.GetProductDiscountById(id);
        var oldProduct = _context.ProductDiscounts.Find(id);
        if (oldProduct == null)return NotFound();
        oldProduct.price = updated.price;
        oldProduct.ProductDisExpirationDate = updated.ProductDisExpirationDate;
        
        _context.SaveChanges();
        return NoContent();
    }

     // 更新优惠券
     [HttpPut("vouchers/{id}")]
     public IActionResult UpdateVoucher(string id, Voucher updated)
     {
        var oldVoucher = _context.Vouchers.Find(id);
        if (oldVoucher == null) return NotFound();
        oldVoucher.Value = updated.Value;
        oldVoucher.VoucherExpirationDate = updated.VoucherExpirationDate;
        oldVoucher.Quantity = updated.Quantity;

        _context.SaveChanges();
        return NoContent();
     }

}