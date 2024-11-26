package main.java.service;

import main.java.components.Order;
import main.java.components.Trade;

import java.util.List;

public interface SuspiciousTradesAndOrdersDetector {
    List<Trade> detectSuspiciousTradesAndOrders (List<Trade> trades, List<Order> orders);
}
