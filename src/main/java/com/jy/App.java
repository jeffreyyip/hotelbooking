package com.jy;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final int NUMBER_OF_ROOM = 100;
    private static final int NUMBER_OF_DATE = 3650;


    private static final BookingManager bookingManager = new BookingManagerCMC(NUMBER_OF_ROOM, NUMBER_OF_DATE);

    public static void main( String[] args )
    {

        String guest = "John";
        LocalDate bookingDate = LocalDate.now();

        List<Room> rooms = bookingManager.findAvailableRoomsByDate( bookingDate );

        Booking newBooking = new Booking(guest, rooms.get(1).getNumber(), bookingDate );

        bookingManager.storeBooking( newBooking );

        List<Booking> bookingByGuest = bookingManager.findAllBookingsByGuest(guest);


        bookingByGuest.forEach( b -> System.out.println( b.toString() ));

    }


}
