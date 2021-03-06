package com.jy;

import java.time.LocalDate;
import java.util.Objects;

public final class Booking {
    private final String guestName;
    private final int roomNumber;
    private final LocalDate bookingDate;

    public Booking(String guestName, int roomNumber, LocalDate bookingDate) {
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.bookingDate = bookingDate;
    }

    public String getGuestName() {
        return guestName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return roomNumber == booking.roomNumber &&
                guestName.equals(booking.guestName) &&
                bookingDate.equals(booking.bookingDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guestName, roomNumber, bookingDate);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "guestName='" + guestName + '\'' +
                ", roomNumber=" + roomNumber +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
