package model;

import java.util.Date;
import java.util.List;

public interface Snapshotable<T> {
    List<T> getBeforeDate(Date date);
}
