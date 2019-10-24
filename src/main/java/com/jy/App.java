package com.jy;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static final int NUMBER_OF_ROOM = 50;
    public static final int NUMBER_OF_DATE = 365;
    public static final int PARAM_BOOKING = 10000;
    public static final int THREAD_CNT = 50;
    public static final BookingManager bm1 = new BookingManagerImpl(NUMBER_OF_ROOM, NUMBER_OF_DATE);
    public static final BookingManager bm2 = new BookingManagerCM(NUMBER_OF_ROOM, NUMBER_OF_DATE);
    public static final BookingManager bm3 = new BookingManagerCMC(NUMBER_OF_ROOM, NUMBER_OF_DATE);


    public static void main( String[] args ) throws InterruptedException
    {
        List<Booking> bookings = generateBooking();
        Set<Booking> uniqueBooking = new HashSet<>( bookings );
        System.out.println( uniqueBooking.size() );


        long start1 = System.currentTimeMillis();
        runWithThread(THREAD_CNT, genRunnables(bm1, bookings) );

        System.out.println( bm1.bookingCnt() + " " + (System.currentTimeMillis() - start1));


        long start2 = System.currentTimeMillis();
        runWithThread(THREAD_CNT, genRunnables(bm2, bookings) );

        System.out.println( bm2.bookingCnt() + " " + (System.currentTimeMillis() - start2));

        long start3 = System.currentTimeMillis();
        runWithThread(THREAD_CNT, genRunnables(bm3, bookings) );

        System.out.println( bm3.bookingCnt() + " " + (System.currentTimeMillis() - start3));

    }
    public static void runWithThread(int threadCnt, List<Callable<Boolean>> callables) throws InterruptedException{
        ExecutorService service = Executors.newFixedThreadPool(threadCnt);
        service.invokeAll(callables);
        service.shutdown();
        service.awaitTermination(100, TimeUnit.SECONDS);
    }

    public static List<Callable<Boolean>> genRunnables(BookingManager bookingManager, List<Booking> booking){

        List<Callable<Boolean>> runnables = new ArrayList<>();

        booking.forEach( b -> runnables.add( () -> { bookingManager.storeBooking(b); return true; } ) );

        return runnables;
    }

    public static void run(BookingManager bookingManager, List<Booking> booking){
        booking.forEach( b -> bookingManager.storeBooking(b));
    }


    public static List<Booking> generateBooking(){


        List<Booking> bookings = new ArrayList<>();

        for (int i = 0; i< PARAM_BOOKING; i++){
            bookings.add( new Booking("J",
                    (int) ( Math.random() * NUMBER_OF_ROOM ) +1,
                    LocalDate.now().plusDays((int) (Math.random() * NUMBER_OF_DATE) ) ) );
        }

        return bookings;
    }


}
