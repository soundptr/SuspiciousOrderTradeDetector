package main.java.components;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Trade {
    long id;
    double price;
    double volume;
    Side side;
    LocalDateTime timestamp;

    public Trade(long id, double price, double volume, Side side, LocalDateTime timestamp) {
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
        return "Trade{" +
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
        Trade trade = (Trade) o;
        return id == trade.id &&
                Double.compare(trade.price, price) == 0 &&
                Double.compare(trade.volume, volume) == 0 &&
                side == trade.side &&
                Objects.equals(timestamp, trade.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, price, volume, side, timestamp);
    }
}
