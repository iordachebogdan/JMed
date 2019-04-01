package model;

import java.util.Date;

public class Event<T> implements Comparable<Event<T>> {
    public enum EventTypeEnum {
        ADD, REMOVE
    }

    private final EventTypeEnum type;
    private final T item;
    private final Date date;

    Event(EventTypeEnum type, T item, Date date) {
        this.type = type;
        this.item = item;
        this.date = date;
    }

    T getItem() {
        return item;
    }

    EventTypeEnum getType() {
        return type;
    }

    Date getDate() {
        return date;
    }

    @Override
    public int compareTo(Event<T> o) {
        if (this.date.before(o.date))
            return -1;
        if (this.date.after(o.date))
            return 1;
        return 0;
    }
}
