package danilov.roman.bookingMeetingRoom.repository;

import danilov.roman.bookingMeetingRoom.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM Reservation r WHERE " +
            "(:dateStart >= r.dateStart AND :dateStart < r.dateFinish) OR " +
            "(:dateFinish > r.dateStart AND :dateFinish <= r.dateFinish)")
    List<Reservation> findDateBetweenStartAndFinish(
            @Param("dateStart") LocalDateTime dateStart,
            @Param("dateFinish") LocalDateTime dateFinish
    );

    @Query("SELECT r FROM Reservation r WHERE r.id <> :id AND " +
            "((:dateStart >= r.dateStart AND :dateStart < r.dateFinish) OR " +
            "(:dateFinish > r.dateStart AND :dateFinish <= r.dateFinish))")
    List<Reservation> findDateBetweenStartAndFinishWithoutId(
            @Param("dateStart") LocalDateTime dateStart,
            @Param("dateFinish") LocalDateTime dateFinish,
            @Param("id") int id
    );

    @Query("SELECT r FROM Reservation r WHERE (:dateStart < r.dateFinish) OR (:dateFinish > r.dateStart)")
    List<Reservation> findDateInInterval(
            @Param("dateStart") LocalDateTime dateStart,
            @Param("dateFinish") LocalDateTime dateFinish
    );

    List<Reservation> findByDateFinishAfter(LocalDateTime date);

    int removeById(int id);
}
