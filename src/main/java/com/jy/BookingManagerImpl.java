package com.jy;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BookingManagerImpl implements BookingManager {

    private final Map<LocalDate, List<Room>> availableRoomsOnDate = new HashMap<>();
    private final List<Booking> bookings = new ArrayList<>();

    public BookingManagerImpl(int numberOfRoom, int numberOBookingDate){


        for (int i = 0; i < numberOBookingDate; i++) {

            List<Room> rooms = IntStream.range(1, numberOfRoom+1).mapToObj(Room::new).collect(Collectors.toList());

            availableRoomsOnDate.put( LocalDate.now().plusDays(i), rooms );
        }


    }
    @Override
    public boolean storeBooking(Booking booking) {
        if (bookings.contains( booking ))
            return false;

        if (! availableRoomsOnDate.containsKey(booking.getBookingDate()) )
            return false;


        if (availableRoomsOnDate.get(booking.getBookingDate()).parallelStream().noneMatch( r -> r.getNumber() == booking.getRoomNumber()))
            return false;


        synchronized (this) {
            bookings.add(booking);
            availableRoomsOnDate.get(booking.getBookingDate()).remove(new Room(booking.getRoomNumber()));
        }
        return true;
    }

    @Override
    public List<Room> findAvailableRoomsByDate(LocalDate bookingDate) {
        return   availableRoomsOnDate.getOrDefault(bookingDate, new ArrayList<>());
    }

    @Override
    public List<Booking> findAllBookingsByGuest(String guestName) {
        return bookings.parallelStream().filter( b -> b.getGuestName().equals(guestName)).collect(Collectors.toList());
    }


}
