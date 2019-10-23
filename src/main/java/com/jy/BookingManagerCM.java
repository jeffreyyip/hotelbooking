package com.jy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class BookingManagerCM implements BookingManager {

    private final int numberOfRooms;
    private final int numberOfBookingDate;  // obsolete

    private final ConcurrentMap<LocalDate, AtomicReferenceArray<Booking> > booked = new ConcurrentHashMap<>();

    public BookingManagerCM(int numberOfRooms, int numberOfBookingDate) {
        this.numberOfRooms = numberOfRooms;
        this.numberOfBookingDate = numberOfBookingDate;
    }

    @Override
    public boolean storeBooking(Booking newBooking) {

        if (newBooking.getRoomNumber() > numberOfRooms || newBooking.getRoomNumber() < 0 )
            return false;

        AtomicReferenceArray<Booking> bookings = booked.computeIfAbsent( newBooking.getBookingDate(), date -> new AtomicReferenceArray<>( numberOfRooms ) );

        Booking booking = bookings.get(newBooking.getRoomNumber());

        if (booking == null)
            return bookings.compareAndSet( newBooking.getRoomNumber(), booking, newBooking );

        return false;
    }

    @Override
    public List<Room> findAvailableRoomsByDate(LocalDate bookingDate) {
        List<Room> rooms = new ArrayList<>();

        AtomicReferenceArray<Booking> bookings = booked.computeIfAbsent( bookingDate, date -> new AtomicReferenceArray<>( numberOfRooms ) );
        for (int i = 0; i< bookings.length(); i++){
            if ( bookings.get(i) == null)
                rooms.add( new Room(i));
        }

        return rooms;
    }

    @Override
    public List<Booking> findAllBookingsByGuest(String guestName) {
        List<Booking> bookings = new ArrayList<>();

        booked.forEach( (k, v) -> {
                    for (int i = 0; i < v.length(); i++){
                        if (v.get(i) != null  && v.get(i).getGuestName().equals(guestName) )
                            bookings.add( v.get(i ));
                    }
                }
                );

        return bookings;
    }
}
