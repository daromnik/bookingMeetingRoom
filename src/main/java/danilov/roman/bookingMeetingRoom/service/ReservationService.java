package danilov.roman.bookingMeetingRoom.service;

import danilov.roman.bookingMeetingRoom.entity.Reservation;
import danilov.roman.bookingMeetingRoom.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Reservation create(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation update(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Transactional
    public int delete(int id) {
        return reservationRepository.removeById(id);
    }

    /**
     * Returns all reservations that have dates that overlap with the selected ones
     *
     * @param startDate Start date
     * @param endDate Finish date
     * @return List of reservations
     */
    public List<Reservation> getCrossing(LocalDateTime startDate, LocalDateTime endDate) {
        return reservationRepository.findDateBetweenStartAndFinish(startDate, endDate);
    }

    /**
     * Returns all reservations that have dates that overlap with the selected ones and not equals ID
     *
     * @param startDate Start date
     * @param endDate Finish date
     * @param id ID reservation that should be excluded
     * @return List of reservations
     */
    public List<Reservation> getCrossingWithoutId(LocalDateTime startDate, LocalDateTime endDate, int id) {
        return reservationRepository.findDateBetweenStartAndFinishWithoutId(startDate, endDate, id);
    }

    /**
     * Returns all reservations between the selected dates
     *
     * @param startDate  Start date
     * @param endDate  Finish date
     * @return List of reservations
     */
    public List<Reservation> getBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return reservationRepository.findDateInInterval(startDate, endDate);
    }

    public Optional<Reservation> getOne(int id) {
        return Optional.of(reservationRepository.getOne(id));
    }

    /**
     * Return all reservations where Date Finish more than given date
     * @param date
     * @return List of reservations
     */
    public List<Reservation> getAllFromDate(LocalDateTime date) {
        return reservationRepository.findByDateFinishAfter(date);
    }
}
