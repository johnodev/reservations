package com.johnpoulakos.reservations.business.service;

import com.johnpoulakos.reservations.business.domain.RoomReservation;
import com.johnpoulakos.reservations.data.entity.Guest;
import com.johnpoulakos.reservations.data.entity.Reservation;
import com.johnpoulakos.reservations.data.entity.Room;
import com.johnpoulakos.reservations.data.repository.GuestRepository;
import com.johnpoulakos.reservations.data.repository.ReservationRepository;
import com.johnpoulakos.reservations.data.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReservationService {
    private RoomRepository roomRepository;
    private GuestRepository guestRepository;
    private ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(RoomRepository roomRepository, GuestRepository guestRepository,
                              ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<RoomReservation> getRoomReservationsForDate(Date date) {
        Iterable<Room> rooms = this.roomRepository.findAll();
        Map<Long, RoomReservation> roomReservationMap = new HashMap<>();

        rooms.forEach( room -> {
            RoomReservation roomReservation = new RoomReservation();
            roomReservation.setRoomId(room.getId());
            roomReservation.setRoomName(room.getName());
            roomReservation.setRoomNumber(room.getNumber());
            roomReservationMap.put(room.getId(), roomReservation);
        });

        Iterable<Reservation> reservations = this.reservationRepository.findByDate(new java.sql.Date(date.getTime()));

        if(reservations != null){

            reservations.forEach( reservation -> {
                Guest guest = this.guestRepository.findOne(reservation.getId());

                if(guest != null){
                    RoomReservation roomReservation =  roomReservationMap.get(reservation.getId());
                    roomReservation.setDate(date);
                    roomReservation.setFirstName(guest.getFirstName());
                    roomReservation.setLastName(guest.getLastName());
                    roomReservation.setGuestId(guest.getId());
                }
            });
        }

        List<RoomReservation> roomReservations = new ArrayList<>();

        for (Long roomId: roomReservationMap.keySet() ) {
            roomReservations.add(roomReservationMap.get(roomId));
        }

        return roomReservations;
    }
}
