package main.java.service.impl;

import main.java.components.Order;
import main.java.components.Side;
import main.java.components.Trade;
import main.java.service.SuspiciousTradesAndOrdersDetector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SuspiciousTradesAndOrdersDetectorImpl implements SuspiciousTradesAndOrdersDetector {

    final double SUSPICIOUS_PRICE_THRESHOLD_PERCENTAGE = 0.10; // 10%
    final long SUSPICIOUS_TIME_WINDOW_MINUTES = 30; // 30 minutes

    @Override
    public List<Trade> detectSuspiciousTradesAndOrders(List<Trade> trades, List<Order> orders) {

        // List<Trade> suspiciousTrades = new ArrayList<>();

        return trades.stream()
                .filter(trade -> isSuspicious(trade, orders))
                .collect(Collectors.toList());
    }

    private boolean isSuspicious(Trade trade, List<Order> orders) {
        LocalDateTime tradeTime = trade.getTimestamp();

        // Get orders that are within the suspicious time window before the trade
        List<Order> relatedOrders = orders.stream()
                .filter(order -> order.getTimestamp().isBefore(tradeTime) && order.getTimestamp().isAfter(tradeTime.minusMinutes(SUSPICIOUS_TIME_WINDOW_MINUTES)))
                .collect(Collectors.toList());

        // Find orders that are of the opposite side
        List<Order> oppositeSideOrders = relatedOrders.stream()
                .filter(order -> order.getSide() != trade.getSide())
                .collect(Collectors.toList());

        // Check if any of the opposite side orders have a price within 10% of the trade price
        for (Order order : oppositeSideOrders) {
            double priceThreshold = trade.getSide() == Side.BUY ?
                    trade.getPrice() * (1 + SUSPICIOUS_PRICE_THRESHOLD_PERCENTAGE) : trade.getPrice() * (1 - SUSPICIOUS_PRICE_THRESHOLD_PERCENTAGE);

            // For buy trades, check if sell order price is not more than 10% higher.
            // For sell trades, check if buy order price is not more than 10% lower.
            if ((trade.getSide() == Side.BUY && order.getPrice() <= priceThreshold) ||
                    (trade.getSide() == Side.SELL && order.getPrice() >= priceThreshold)) {
                return true;
            }
        }
        return false;
    }
}
