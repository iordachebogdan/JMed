package model;

import java.util.Date;

public class Event<T> {
    public enum EventTypeEnum {
        ADD, REMOVE
    }

    private final EventTypeEnum type;
    private final T item;
    private final Date date;

    public Event(EventTypeEnum type, T item, Date date) {
        this.type = type;
        this.item = item;
        this.date = date;
    }

    public T getItem() {
        return item;
    }

    public EventTypeEnum getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }
}
