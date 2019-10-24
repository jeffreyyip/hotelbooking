package com.jy;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.print.Book;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class BookingManagerCMCTest {

    private static final int NUMBER_OF_ROOMS = 10;
    private static final int NUMBER_OF_BOOKING_DATE_EXPECTED = 365;
    private BookingManager bookingManager;

    @Before
    public void setUp() {
        bookingManager = new BookingManagerCMC(NUMBER_OF_ROOMS, NUMBER_OF_BOOKING_DATE_EXPECTED);
    }


    @Test
    public void storeBooking() {
        Booking booking = new Booking("John", 1, LocalDate.now() );
        boolean rtn = bookingManager.storeBooking( booking );

        assertTrue(rtn);
    }



    @Test
    public void findAvailableRoomsByDate() {
        List<Room> rooms = bookingManager.findAvailableRoomsByDate( LocalDate.now() );

        assertNotNull(rooms);
        assertEquals(NUMBER_OF_ROOMS, rooms.size());
    }


    @Test
    public void findAllBookingsByGuest() {

        Booking booking = new Booking("John", 1, LocalDate.now() );

        bookingManager.storeBooking(booking);

        List<Booking> bookings = bookingManager.findAllBookingsByGuest("John");

        assertNotNull(bookings);
        assertEquals(1, bookings.size());

    }

    @Test
    public void findAvailableRooms_storeBooking_findBookingByGuest(){

        LocalDate bookingDate = LocalDate.now();

        List<Room> rooms = bookingManager.findAvailableRoomsByDate( bookingDate );

        Booking newBooking = new Booking("John", rooms.get(0).getNumber(), bookingDate );

        boolean rtn = bookingManager.storeBooking( newBooking );

        assertTrue(rtn);
    }

    @Test
    public void findAllBookingsByGuestForTwoBooking() {
        Booking booking;

        booking = new Booking("John", 1, LocalDate.now() );

        bookingManager.storeBooking(booking);

        booking = new Booking("John", 1, LocalDate.now().plusDays(1) );

        bookingManager.storeBooking(booking);

        List<Booking> bookings = bookingManager.findAllBookingsByGuest("JOHN");

        assertNotNull(bookings);
        assertEquals(2, bookings.size());

    }

    @Test
    public void givenRoomIsBooked_whenFindingAvailableRooms_thenNotReturn() {

        LocalDate bookingDate = LocalDate.now();
        int roomNumber = 1;

        Booking booking = new Booking("John", roomNumber, bookingDate );

        bookingManager.storeBooking(booking);

        List<Room> rooms = bookingManager.findAvailableRoomsByDate( bookingDate );

        assertTrue( rooms.stream().noneMatch( r -> r.getNumber() == roomNumber) );

    }

    @Test
    public void givenBookingIsStored_whenSameBookingIsStored_thenFalse() {

        Booking booking = new Booking("John", 1, LocalDate.now() );

        boolean rtn;

        rtn = bookingManager.storeBooking(booking);

        assertTrue(rtn);

        rtn = bookingManager.storeBooking(booking);

        assertFalse(rtn);


    }


    @Test
    public void givenRoomNumberIsNotAvailable_whenBookingIsStored_thenFalse() {
        LocalDate bookingDate = LocalDate.now();
        int roomNumber = NUMBER_OF_ROOMS + 1;

        boolean rtn = bookingManager.storeBooking( new Booking( "John", roomNumber, bookingDate ));

        assertFalse(rtn);

    }

    @Test
    public void systemTest() {
        List<Room> rooms1 = bookingManager.findAvailableRoomsByDate( LocalDate.now() );
        List<Room> rooms2 = bookingManager.findAvailableRoomsByDate( LocalDate.now().plusDays(1) );

        for (int i= 0; i < rooms1.size(); i++) {
            assertNotSame( rooms1.get(i) , rooms2.get(i) );
        }

    }

    @Test
    public void givenBookingOnOneDate_whenFindRoomsOnAnotherDate_thenRoomsCountIsCorrect() {
        LocalDate bookingDate1 = LocalDate.now();
        LocalDate bookingDate2 = LocalDate.now().plusDays(1);

        bookingManager.storeBooking( new Booking("j", 1, bookingDate1 ));

        List<Room> rooms1 = bookingManager.findAvailableRoomsByDate( bookingDate1 );
        List<Room> rooms2 = bookingManager.findAvailableRoomsByDate( bookingDate2 );

        assertEquals( NUMBER_OF_ROOMS - 1, rooms1.size() );
        assertEquals( NUMBER_OF_ROOMS, rooms2.size() );

    }

    @Test
    public void testCnt(){

        for (int i = 0; i<100; i++){
            bookingManager.storeBooking( new Booking("J", 1, LocalDate.now().plusDays(i)));
        }

        for (int i = 0; i<100; i++){
            bookingManager.storeBooking( new Booking("J", 1, LocalDate.now().plusDays(i)));
        }

        assertEquals(100, bookingManager.bookingCnt());
    }

}