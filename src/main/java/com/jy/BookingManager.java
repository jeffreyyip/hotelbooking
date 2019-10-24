package com.jy;

import java.time.LocalDate;
import java.util.List;

public interface BookingManager {

    boolean storeBooking(Booking booking);

    List<Room> findAvailableRoomsByDate(LocalDate bookingDate);

    List<Booking> findAllBookingsByGuest(String guestName);

    int bookingCnt();
}
