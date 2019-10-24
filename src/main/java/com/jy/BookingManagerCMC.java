package com.jy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class BookingManagerCMC implements BookingManager{

    private final int numberOfRooms;

    private final ConcurrentMap<LocalDate, AtomicReferenceArray<Booking>> booked;

    public BookingManagerCMC(int numberOfRooms, int numberOfBookingDate) {
        this.numberOfRooms = numberOfRooms;
        booked = new ConcurrentHashMap<LocalDate, AtomicReferenceArray<Booking>>((int) (numberOfRooms * numberOfBookingDate * 1.5));
    }

    @Override
    public boolean storeBooking(Booking newBooking) {

        if (newBooking.getRoomNumber() > numberOfRooms || newBooking.getRoomNumber() <= 0 )
            return false;

        AtomicReferenceArray<Booking> bookings = booked.computeIfAbsent( newBooking.getBookingDate(), date -> new AtomicReferenceArray<>( numberOfRooms +1 ) );


        while ( bookings.get(newBooking.getRoomNumber() )== null) {
            if (bookings.compareAndSet(newBooking.getRoomNumber(), null, newBooking) )
                return true;
        };

        return false;

    }

    @Override
    public List<Room> findAvailableRoomsByDate(LocalDate bookingDate) {
        List<Room> rooms = new ArrayList<>();

        AtomicReferenceArray<Booking> bookings = booked.computeIfAbsent( bookingDate, date -> new AtomicReferenceArray<>( numberOfRooms +1 ) );
        for (int i = 1; i< bookings.length(); i++){
            if ( bookings.get(i) == null)
                rooms.add( new Room(i));
        }

        return rooms;
    }

    @Override
    public List<Booking> findAllBookingsByGuest(String guestName) {
        List<Booking> bookings = new ArrayList<>();

        booked.forEach( (k, v) -> {
                    for (int i = 1; i < v.length(); i++){
                        if (v.get(i) != null  && v.get(i).getGuestName().equals(guestName) )
                            bookings.add( v.get(i ));
                    }
                }
        );

        return bookings;
    }

    @Override
    public int bookingCnt() {
        final AtomicInteger cnt = new AtomicInteger();
        booked.forEach( (k, v) -> {
                    for (int i = 1; i < v.length(); i++){
                        if (v.get(i) != null   )
                            cnt.incrementAndGet();
                    }
                }
        );


        return cnt.get();
    }
}
