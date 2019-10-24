package com.jy;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static java.time.temporal.ChronoUnit.DAYS;

public class BookingManagerArray implements BookingManager {

    private final int numberOfRooms;
    private final int numberOfDates;
    //private final AtomicReference<Booking>[][] bookings;

    public BookingManagerArray(int numberOfRooms, int numberOfDates) {
            this.numberOfRooms = numberOfRooms;
            this.numberOfDates = numberOfDates;
           // bookings = new AtomicReference<Booking>[numberOfDates][numberOfRooms];

            for (int i = 0; i < numberOfDates; i++){
                for (int j = 0; j <= numberOfRooms; j ++){
                    //bookings[i][j] = new AtomicReference();
                }
            }
    }

    @Override
    public boolean storeBooking(Booking booking) {
        int index = getBookingIndex(booking);
       // Booking booking = bookings[index][booking.getRoomNumber()].get();
        return true;
    }

    @Override
    public List<Room> findAvailableRoomsByDate(LocalDate bookingDate) {
        return null;
    }

    @Override
    public List<Booking> findAllBookingsByGuest(String guestName) {
        return null;
    }

    @Override
    public int bookingCnt() {
        return 0;
    }

    private int getBookingIndex(Booking booking){
        return (int)DAYS.between(LocalDate.now(), booking.getBookingDate());
    }
}
