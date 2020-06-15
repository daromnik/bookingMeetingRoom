package danilov.roman.bookingMeetingRoom.controller;

import danilov.roman.bookingMeetingRoom.entity.Reservation;
import danilov.roman.bookingMeetingRoom.service.ReservationService;
import danilov.roman.bookingMeetingRoom.entity.google.GoogleCalendarEntity;
import danilov.roman.bookingMeetingRoom.service.google.SynchronizeEventsWithGoogleCalendar;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GoogleController {

    private final ReservationService reservationService;
    private final SynchronizeEventsWithGoogleCalendar synchronizeEventsWithGoogleCalendar;

    public GoogleController(ReservationService reservationService, SynchronizeEventsWithGoogleCalendar synchronizeEventsWithGoogleCalendar) {
        this.reservationService = reservationService;
        this.synchronizeEventsWithGoogleCalendar = synchronizeEventsWithGoogleCalendar;
    }

    /**
     * Send all reservations after current time to google calendar.
     *
     * @return Success if all reservations were sent or error if with google calendar api was problems.
     */
    @PostMapping("google_send")
    @ResponseBody
    public ResponseEntity<String> sendEventsToGoogleCalendar() {
        List<Reservation> allFromDate = reservationService.getAllFromDate(LocalDateTime.now());
        if (allFromDate.size() == 0) {
            return new ResponseEntity<>("No event to send.", HttpStatus.OK);
        }
        List<GoogleCalendarEntity> googleCalendarEntities = allFromDate.stream()
                .map(reservation -> new GoogleCalendarEntity(
                        reservation.getMeetingRoom().getName(),
                        reservation.getDescriptions(),
                        reservation.getDateStart(),
                        reservation.getDateFinish()
                ))
                .collect(Collectors.toList());
        try {
            synchronizeEventsWithGoogleCalendar.googleSend(googleCalendarEntities);
        } catch (IOException | GeneralSecurityException e) {
            return new ResponseEntity<>("Google service error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

}
