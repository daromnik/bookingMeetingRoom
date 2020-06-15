package danilov.roman.bookingMeetingRoom.service;

import danilov.roman.bookingMeetingRoom.entity.MeetingRoom;
import danilov.roman.bookingMeetingRoom.repository.MeetingRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public MeetingRoomService(MeetingRoomRepository meetingRoomRepository) {
        this.meetingRoomRepository = meetingRoomRepository;
    }

    public List<MeetingRoom> getAll() {
        return meetingRoomRepository.findAll();
    }
}
