package danilov.roman.bookingMeetingRoom.controller;

import danilov.roman.bookingMeetingRoom.entity.MeetingRoom;
import danilov.roman.bookingMeetingRoom.entity.Reservation;
import danilov.roman.bookingMeetingRoom.entity.User;
import danilov.roman.bookingMeetingRoom.service.MeetingRoomService;
import danilov.roman.bookingMeetingRoom.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
public class ReservationController {

    private final ReservationService reservationService;
    private final MeetingRoomService meetingRoomService;

    public ReservationController(ReservationService reservationService, MeetingRoomService meetingRoomService) {
        this.reservationService = reservationService;
        this.meetingRoomService = meetingRoomService;
    }

    /**
     * Show information about all reservations for the selected week.
     * If this is page "/", then show current page, else selected week.
     * Take any day of the week and use it to get the first day of the week.
     * Next, we create an hashmap where the keys are the day of the week,
     * and the values are arrays with bookings on that day.
     * If a reservation starts on one day and ends on another day,
     * it will be included in the hashmap in two days.
     *
     * @param year  Year selected week
     * @param month  Month selected week
     * @param day  Any day selected week
     * @param user  Current logged user
     * @param model  Model with data for the template
     * @return Template to display
     */
    @GetMapping(value={"/", "/week/{year}/{month}/{day}"})
    public String mainCalendar(@PathVariable(required = false) Optional<Integer> year,
                               @PathVariable(required = false) Optional<Integer> month,
                               @PathVariable(required = false) Optional<Integer> day,
                               @AuthenticationPrincipal User user,
                               Model model) {

        LocalDateTime today;
        if (year.isPresent() && month.isPresent() && day.isPresent()) {
            today = LocalDateTime.of(year.get(), month.get(), day.get(), 0, 0);
        } else {
            today = LocalDateTime.now();
        }

        LocalDateTime monday = today.with(DayOfWeek.MONDAY).withHour(0).withMinute(0);
        LocalDateTime sunday = today.with(DayOfWeek.SUNDAY).withHour(23).withMinute(59);

        List<Reservation> reservations = reservationService.getBetween(monday, sunday);

        HashMap<LocalDate, List<Reservation>> reservationByDays = new LinkedHashMap<>();
        do {
            reservationByDays.put(monday.toLocalDate(), new ArrayList<>());
            monday = monday.plusDays(1);
        } while (monday.getDayOfWeek() != DayOfWeek.MONDAY);

        LocalDate ds;
        LocalDate df;
        LocalTime tf;
        List<Reservation> reservationsList;
        for (Reservation reservation : reservations) {
            ds = reservation.getDateStart().toLocalDate();
            df = reservation.getDateFinish().toLocalDate();
            tf = reservation.getDateFinish().toLocalTime();
            if (!ds.equals(df) && !tf.equals(LocalTime.MIN)) {
                reservationsList = reservationByDays.get(df);
                if (reservationsList != null) {
                    reservationsList.add(reservation);
                }
            }
            reservationsList = reservationByDays.get(ds);
            if (reservationsList != null) {
                reservationsList.add(reservation);
            }

        }
        model.addAttribute("meetingRooms", meetingRoomService.getAll());
        model.addAttribute("reservations", reservationByDays);
        model.addAttribute("thirstDayNextWeek", monday);
        model.addAttribute("thirstDayPrevWeek", monday.minusDays(14));
        model.addAttribute("currentUserId", user.getId());
        return "index";
    }

    /**
     * Add new reservation.
     * Checks for intersections with other reservations.
     *
     * @param reservation  Reservation's object with data from form
     * @param errors  Object with errors after validation of reservation's object
     * @return Success if add or error if already a reservation for this time
     */
    @PostMapping("/add-reservation")
    @ResponseBody
    public ResponseEntity<String> addReservation(@Valid @ModelAttribute Reservation reservation, BindingResult errors) {
        if (errors.hasErrors()) {
            return new ResponseEntity<>("Invalid data!", HttpStatus.BAD_REQUEST);
        }
        List<Reservation> crossingReservations = reservationService.getCrossing(
                reservation.getDateStart(), reservation.getDateFinish()
        );
        if (crossingReservations.size() > 0) {
            return new ResponseEntity<>("There is already a reservation for this time!", HttpStatus.CONFLICT);
        }
        reservationService.create(reservation);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    /**
     * Update reservation.
     * Checks for intersections with other reservations.
     *
     * @param reservation  Reservation's object with data from form
     * @param errors  Object with errors after validation of reservation's object
     * @return Success if add or error if already a reservation for this time
     */
    @PostMapping("/update-reservation")
    @ResponseBody
    public ResponseEntity<String> updateReservation(@Valid @ModelAttribute Reservation reservation, BindingResult errors) {
        if (errors.hasErrors()) {
            return new ResponseEntity<>("Invalid data!", HttpStatus.BAD_REQUEST);
        }
        List<Reservation> crossingReservations = reservationService.getCrossingWithoutId(
                reservation.getDateStart(), reservation.getDateFinish(), reservation.getId()
        );
        if (crossingReservations.size() > 0) {
            return new ResponseEntity<>("There is already a reservation for this time!", HttpStatus.CONFLICT);
        }
        reservationService.update(reservation);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    /**
     * Returns information about the selected reservation.
     *
     * @param id  ID of the reservation to select
     * @return Object of reservation or null
     */
    @GetMapping("/getOne/{id}")
    @ResponseBody
    public Reservation getOne(@PathVariable int id) {
        return reservationService.getOne(id).orElse(null);
    }

    /**
     * Deletes the selected reservation
     *
     * @param id  ID of the reservation to delete
     * @return ID deleted reservation and error if not found it
     */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Integer> delete(@PathVariable("id") int id) {
        if (reservationService.delete(id) == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

}
