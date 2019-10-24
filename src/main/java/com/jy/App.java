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
    public static final int NUMBER_OF_ROOM = 100;
    public static final int NUMBER_OF_DATE = 365;
    public static final int PARAM_BOOKING = 30000;
    public static final int THREAD_CNT = 10;

    public static final ExecutorService service = Executors.newFixedThreadPool(THREAD_CNT);


    public static void main( String[] args ) throws InterruptedException
    {
        List<Booking> bookings = generateBooking();
        Set<Booking> uniqueBooking = new HashSet<>( bookings );
        System.out.println( uniqueBooking.size() );


        runBM(new BookingManagerCMC(NUMBER_OF_ROOM, NUMBER_OF_DATE), bookings);


        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

    }

    private static void runBM(BookingManager bookingManager, List<Booking> bookings) throws InterruptedException{
        CountDownLatch latch1 = new CountDownLatch(bookings.size());

        runWithThread(bookingManager, genCallable(bookingManager, bookings, latch1), latch1 );

    }

    private static void runWithThread(BookingManager bookingManager, List<Callable<Boolean>> callables, CountDownLatch latch) throws InterruptedException{
        long start = System.currentTimeMillis();

        service.invokeAll(callables);
        latch.await();
        System.out.println( bookingManager.bookingCnt() + " " + (System.currentTimeMillis() - start));

    }

    private static List<Callable<Boolean>> genCallable(BookingManager bookingManager, List<Booking> booking, CountDownLatch latch){

        List<Callable<Boolean>> runnables = new ArrayList<>();

        booking.forEach( b -> runnables.add( () -> { bookingManager.storeBooking(b); latch.countDown(); return true; } ) );

        return runnables;
    }



    private static List<Booking> generateBooking(){


        List<Booking> bookings = new ArrayList<>();

        for (int i = 0; i< PARAM_BOOKING; i++){
            bookings.add( new Booking("J",
                    (int) ( Math.random() * NUMBER_OF_ROOM ) +1,
                    LocalDate.now().plusDays((int) (Math.random() * NUMBER_OF_DATE) ) ) );
        }

        return bookings;
    }


}
