package com.marketguard;
public class Main {
    public static void main(String[] args) {
        MarketEvent event = new MarketEvent(
            101, 501, EventType.ADD, 3500, 100
        );
        System.out.println("MarketEvent created successfully:");
        System.out.println(event);
    }
}
