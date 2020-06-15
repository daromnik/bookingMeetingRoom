package danilov.roman.bookingMeetingRoom.repository;

import danilov.roman.bookingMeetingRoom.entity.MeetingRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Integer> {

}
