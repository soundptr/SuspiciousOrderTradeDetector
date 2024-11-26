package main.test;

import main.java.components.Order;
import main.java.components.Side;
import main.java.components.Trade;
import main.java.service.SuspiciousTradesAndOrdersDetector;
import main.java.service.impl.SuspiciousTradesAndOrdersDetectorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SuspiciousTradeAndOrderTest {

    private SuspiciousTradesAndOrdersDetector suspiciousTradesAndOrdersDetector;

    @BeforeEach
    void setUp() {
        // Initialize the detector before each test case
        suspiciousTradesAndOrdersDetector = new SuspiciousTradesAndOrdersDetectorImpl();
    }

    @Test
    void testFindSuspiciousTrades_whenNoSuspiciousTrades() {
        // Sample trades and orders with no suspicious behavior
        List<Trade> trades = List.of(
                new Trade(1, 90.0, 50, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 0, 0, 0)),
                new Trade(2, 150.0, 30, Side.SELL, LocalDateTime.of(2024, 11, 20, 10, 15, 0, 0))
        );
        List<Order> orders = List.of(
                new Order(1, 110.0, 10, Side.SELL, LocalDateTime.of(2024, 11, 20, 9, 40, 0, 0)),
                new Order(2, 110.0, 15, Side.BUY, LocalDateTime.of(2024, 11, 20, 9, 50, 0, 0))
        );

        // Find suspicious trades
        List<Trade> suspiciousTrades = suspiciousTradesAndOrdersDetector.detectSuspiciousTradesAndOrders(trades, orders);

        // No suspicious trades
        assertTrue(suspiciousTrades.isEmpty(), "Expected no suspicious trades");
    }

    @Test
    void testFindSuspiciousTrades_whenSuspiciousTradesExist() {
        // Sample trades and orders with suspicious behavior
        List<Trade> trades = List.of(
                new Trade(1, 100.0, 50, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 0, 0, 0)),
                new Trade(2, 50.0, 30, Side.SELL, LocalDateTime.of(2024, 11, 20, 10, 15, 0, 0)),
                new Trade(3, 75.0, 20, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 45, 0, 0))
        );
        List<Order> orders = List.of(
                new Order(1, 105.0, 10, Side.SELL, LocalDateTime.of(2024, 11, 20, 9, 40, 0, 0)), // Opposite side and within 30 mins
                new Order(2, 90.0, 15, Side.SELL, LocalDateTime.of(2024, 11, 20, 9, 50, 0, 0)), // Opposite side and within 30 mins
                new Order(3, 95.0, 10, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 10, 0, 0)), // Same side, should not be suspicious
                new Order(4, 105.0, 10, Side.SELL, LocalDateTime.of(2024, 11, 20, 10, 0, 0, 0))  // Opposite side and price within threshold
        );

        // Find suspicious trades
        List<Trade> suspiciousTrades = suspiciousTradesAndOrdersDetector.detectSuspiciousTradesAndOrders(trades, orders);

        // Verify the suspicious trades
        assertEquals(2, suspiciousTrades.size(), "Expected 2 suspicious trades");

        // Check if the expected suspicious trades are present
        assertTrue(suspiciousTrades.contains(new Trade(1, 100.0, 50, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 0, 0, 0))),
                "Trade 1 should be suspicious");

        assertTrue(suspiciousTrades.contains(new Trade(2, 50.0, 30, Side.SELL, LocalDateTime.of(2024, 11, 20, 10, 15, 0, 0))),
                "Trade 2 should be suspicious");
    }

    @Test
    void testFindSuspiciousTrades_withEdgeCase_noMatchingOrders() {
        // Sample trades with no matching orders in the time window
        List<Trade> trades = List.of(
                new Trade(1, 100.0, 50, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 20, 0, 0))
        );
        List<Order> orders = List.of(
                new Order(1, 90.0, 10, Side.SELL, LocalDateTime.of(2024, 11, 20, 9, 30, 0, 0)), // Outside the 30 min window
                new Order(2, 110.0, 15, Side.SELL, LocalDateTime.of(2024, 11, 20, 9, 45, 0, 0))  // Outside the 30 min window
        );

        // Find suspicious trades
        List<Trade> suspiciousTrades = suspiciousTradesAndOrdersDetector.detectSuspiciousTradesAndOrders(trades, orders);

        // No suspicious trades since no matching orders
        assertTrue(suspiciousTrades.isEmpty(), "Expected no suspicious trades");
    }

    @Test
    void testFindSuspiciousTrades_withPriceDifferenceEdgeCases() {
        // Sample trades with price difference testing
        List<Trade> trades = List.of(
                new Trade(1, 100.0, 50, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 0, 0, 0)),
                new Trade(2, 50.0, 30, Side.SELL, LocalDateTime.of(2024, 11, 20, 10, 15, 0, 0))
        );
        List<Order> orders = List.of(
                new Order(1, 95.0, 10, Side.SELL, LocalDateTime.of(2024, 11, 20, 9, 40, 0, 0)), // Price difference within 10%
                new Order(2, 110.0, 15, Side.SELL, LocalDateTime.of(2024, 11, 20, 9, 50, 0, 0)), // Price difference too high
                new Order(3, 105.0, 15, Side.SELL, LocalDateTime.of(2024, 11, 20, 9, 50, 0, 0))  // Price difference too high
        );

        // Find suspicious trades
        List<Trade> suspiciousTrades = suspiciousTradesAndOrdersDetector.detectSuspiciousTradesAndOrders(trades, orders);

        // Only the first trade is suspicious
        assertEquals(1, suspiciousTrades.size(), "Expected 1 suspicious trade");
        assertTrue(suspiciousTrades.contains(new Trade(1, 100.0, 50, Side.BUY, LocalDateTime.of(2024, 11, 20, 10, 0, 0, 0))),
                "Trade 1 should be suspicious due to the price threshold");
    }
}
