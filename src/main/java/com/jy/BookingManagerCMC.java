package com.jy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookingManagerCMC implements BookingManager{

    private final int numberOfRooms;

    /**
     * mapping between date to array of booking
     * the array size equals to the number of rooms
     * each array element is corresponding to a room for particular date
     * if the array element is null; the room is available; otherwise it is booked
     * Implementation assumption :
     * array index is mapped by the room number for this implementation (array index equals room number)
     * assume that room number started from 0 to (max number of rooms - 1)
     * change this implementation of array index and room number if needed
     */
    private final ConcurrentMap<LocalDate, AtomicReferenceArray<Booking>> booked;

    /**
     *
     * @param numberOfRooms : total number of rooms
     * @param numberOfBookingDateExpected : number of booking date expected;
     *                                    it is to set initial capacity of the map;
     *                                    in order to minimize the cost of resizing
     */
    public BookingManagerCMC(int numberOfRooms, int numberOfBookingDateExpected) {
        this.numberOfRooms = numberOfRooms;
        booked = new ConcurrentHashMap<>((int) (numberOfBookingDateExpected * 1.4));
    }

    @Override
    public boolean storeBooking(Booking newBooking) {

        if (newBooking.getRoomNumber() >= numberOfRooms || newBooking.getRoomNumber() < 0 )
            return false;

        AtomicReferenceArray<Booking> bookings = booked.computeIfAbsent( newBooking.getBookingDate(), date -> new AtomicReferenceArray<>( numberOfRooms  ) );


        while ( bookings.get(newBooking.getRoomNumber() )== null) {
            if (bookings.compareAndSet(newBooking.getRoomNumber(), null, new Booking(newBooking.getGuestName(), newBooking.getRoomNumber(), newBooking.getBookingDate()) ))
                return true;
        }

        return false;

    }

    /**
     * it is weakly consistent as the implementation is concurrent to individual room
     */
    @Override
    public List<Room> findAvailableRoomsByDate(LocalDate bookingDate) {
        List<Room> rooms = new ArrayList<>();

        AtomicReferenceArray<Booking> bookings = booked.computeIfAbsent( bookingDate, date -> new AtomicReferenceArray<>( numberOfRooms  ) );

        for (int i = 0; i < bookings.length(); i++){
            if ( bookings.get(i) == null)
                rooms.add( new Room(i));
        }

        return rooms;
    }

    @Override
    public List<Booking> findAllBookingsByGuest(String guestName) {
        return booked.values().parallelStream().flatMap( v -> Stream.of(asArray(v)) )
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    private Booking[] asArray (AtomicReferenceArray<Booking> refArray) {
        Booking[] bookings = new Booking[refArray.length()];
        for (int i = 0; i< refArray.length(); i++){
            bookings[i] = refArray.get(i);
        }
        return bookings;
    }


}
