package danilov.roman.bookingMeetingRoom.entity.google;

import com.google.api.client.util.DateTime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

public class GoogleCalendarEntity {
    private String title;
    private String desc;
    private DateTime startDateTime;
    private DateTime endDateTime;

    public GoogleCalendarEntity(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    public GoogleCalendarEntity(String title, String desc, DateTime startDateTime, DateTime endDateTime) {
        this(title, desc);
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public GoogleCalendarEntity(String title, String desc, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this(title, desc);
        setStartDateTime(startDateTime);
        setEndDateTime(endDateTime);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(DateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        Date date = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        this.startDateTime = new DateTime(date);
    }

    public DateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(DateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        Date date = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        this.endDateTime = new DateTime(date);;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoogleCalendarEntity that = (GoogleCalendarEntity) o;
        return title.equals(that.title) &&
                desc.equals(that.desc) &&
                startDateTime.equals(that.startDateTime) &&
                endDateTime.equals(that.endDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, desc, startDateTime, endDateTime);
    }
}
