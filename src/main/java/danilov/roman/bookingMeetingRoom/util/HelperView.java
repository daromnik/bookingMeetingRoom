package danilov.roman.bookingMeetingRoom.util;

import danilov.roman.bookingMeetingRoom.entity.Reservation;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Assistant class with functions for html templates.
 */
@Component
public class HelperView {

    private final static int MINUTES_IN_DAY = 24 * 60;

    /**
     * Returns the block's height (as a percentage) of the reservation event relative to one day.
     * If an event starts on one day and ends on another,
     * its time is limited relative to the passed day (00.00 or 23.59).
     *
     * @param reservation  The reservation for which need to calculate the height
     * @param dayOfMonth  The day for which need to calculate the height of the reservation
     * @return Block's height as a percentage
     */
    public static float getHeightReservationBlock(Reservation reservation, int dayOfMonth) {
        LocalDateTime dateTimeStart = reservation.getDateStart();
        LocalDateTime dateTimeFinish = reservation.getDateFinish();

        LocalTime timeStart;
        LocalTime timeFinish;

        if (dateTimeStart.getDayOfMonth() != dayOfMonth) {
            timeStart = LocalTime.MIN;
            timeFinish = dateTimeFinish.toLocalTime();
        } else if (dateTimeFinish.getDayOfMonth() != dayOfMonth) {
            timeStart = dateTimeStart.toLocalTime();
            timeFinish = LocalTime.MAX;
        } else {
            timeStart = dateTimeStart.toLocalTime();
            timeFinish = dateTimeFinish.toLocalTime();
        }

        int minutesStart = timeStart.getHour() * 60 + timeStart.getMinute();
        int minutesFinish = timeFinish.getHour() * 60 + timeFinish.getMinute();

        return (float) 100 * (minutesFinish - minutesStart) / MINUTES_IN_DAY;
    }

    /**
     * Returns the value that should be sets the top margin of the block
     * of the passed day to display the booking event.
     *
     * @param reservation  The reservation for which need to calculate the top margin
     * @param dayOfMonth  The day for which need to calculate the top margin of the reservation
     * @return The top margin as a percentage
     */
    public static float getReservationStartMinutes(Reservation reservation, int dayOfMonth) {
        LocalDateTime dateTimeStart = reservation.getDateStart();
        LocalTime timeStart;
        if (dateTimeStart.getDayOfMonth() != dayOfMonth) {
            timeStart = LocalTime.MIN;
        } else {
            timeStart = dateTimeStart.toLocalTime();
        }
        return (float) 100 * (timeStart.getHour() * 60 + timeStart.getMinute()) / MINUTES_IN_DAY;
    }

    /**
     * @param date  Date to get the day of the week from
     * @return String name of week's day on current local.
     */
    public static String getDayOfWeek(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Locale locale = Locale.getDefault();
        return dayOfWeek.getDisplayName(TextStyle.FULL, locale);
    }

    /**
     * @param date Date to format
     * @return Date in needed format
     */
    public static String getDateByFormat(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    /**
     * @param date Date to format for url
     * @return Date in needed format
     */
    public static String getDateForWeek(LocalDateTime date) {
        return String.format("%s/%s/%s", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

}
