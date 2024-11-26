package main.java.service;

import main.java.components.Order;
import main.java.components.Side;
import main.java.components.Trade;
import main.java.service.impl.SuspiciousTradesAndOrdersDetectorImpl;

import java.time.LocalDateTime;
import java.util.List;

public class TradeAndOrderService {
    public static void main(String[] args) {
        List<Trade> trades = List.of(
                new Trade(1, 100.0, 50, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 0, 0, 0)),
                new Trade(2, 50.0, 30, Side.SELL, LocalDateTime.of(2024, 11, 20, 10, 15, 0, 0)),
                new Trade(3, 75.0, 20, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 45, 0, 0))
        );
        List<Order> orders = List.of(
                new Order(1, 105.0, 10, Side.SELL, LocalDateTime.of(2024, 11, 20, 9, 40, 0, 0)), // Opposite side and within 30 mins
                new Order(2, 90.0, 15, Side.SELL, LocalDateTime.of(2024, 11, 20, 9, 50, 0, 0)), // Opposite side and within 30 mins
                new Order(3, 95.0, 10, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 10, 0, 0)), // Same side, should not be suspicious
                new Order(4, 105.0, 10, Side.SELL, LocalDateTime.of(2024, 11, 20, 10, 0, 0, 0)) // Opposite side and price within threshold)
        );

        SuspiciousTradesAndOrdersDetector detector = new SuspiciousTradesAndOrdersDetectorImpl();

        List<Trade> suspiciousTrades = detector.detectSuspiciousTradesAndOrders(trades, orders);
        
        System.out.println("Detected suspicious trades from the list provided : ");
        suspiciousTrades.forEach(System.out::println);
    }
}
