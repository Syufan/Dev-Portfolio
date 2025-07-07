namespace Ordering.API.Models;

public class Order
{
    public int OrderId{get;set;}
    public string Username{get; set;}
    public DateTime OrderDate{get; set;} = DateTime.UtcNow; 
    public List<OrderItem> Items{get; set;} = new();
    public string FirstName{get; set;}
    public string LastName{get; set;}
    public Card CardInformation { get; set; } = new();
    public string CVV{get; set;}
    public Address ShippingAddress { get; set; } = new();
}

public class Card
{
    public string CardName{get; set;}
    public string CardNumber{get; set;}
    public string Expiration{get; set;}
}

public class Address
{
    public string Street { get; set; } = string.Empty;
    public string City { get; set; } = string.Empty;
    public string State { get; set; } = string.Empty;
    public string ZipCode { get; set; } = string.Empty;
    public string Country { get; set; } = string.Empty;
}