package com.marketguard;


public class MarketEvent {

    private final long sequenceNumber;
    private final long orderId;
    private final EventType eventType;
    private final long price;
    private final int quantity;

   public MarketEvent(long sequenceNumber,
                   long orderId,
                   EventType eventType,
                   long price,
                   int quantity) {
    this.sequenceNumber = sequenceNumber;//this refers to the current object.
    this.price = price;
    this.quantity = quantity;
    this.eventType = eventType;
    this.orderId = orderId;


}
public long getSequenceNumber() {
    return sequenceNumber;
}
public long getOrderId() {
    return orderId;
}
public long getPrice(){
    return price;
}
public int getQuantity() { // used getter here because our variable is private so it means we are using encapsulation where we are making method public the getting value. basically keeping method private providing controlled access
    return quantity;
}
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return "MarketEvent{" +
                "sequenceNumber=" + sequenceNumber +
                ", orderId=" + orderId +
                ", eventType=" + eventType +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}