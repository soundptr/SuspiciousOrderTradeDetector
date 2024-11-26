package main.java.components;

import java.time.LocalDateTime;
import java.util.Objects;

public class Order {
    long id;
    double price;
    double volume;
    Side side;
    LocalDateTime timestamp;


    public Order(long id, double price, double volume, Side side, LocalDateTime timestamp) {
        this.id = id;
        this.price = price;
        this.volume = volume;
        this.side = side;
        this.timestamp = timestamp;
    }


    public double getPrice() {
        return price;
    }

    public Side getSide() {
        return side;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", price=" + price +
                ", volume=" + volume +
                ", side=" + side +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id &&
                Double.compare(order.price, price) == 0 &&
                Double.compare(order.volume, volume) == 0 &&
                side == order.side &&
                Objects.equals(timestamp, order.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, volume, side, timestamp);
    }
}
