package com.example.movieticket.data;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.movieticket.model.Booking;
import com.example.movieticket.model.Movie;
import com.example.movieticket.model.Showtime;
import com.example.movieticket.model.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RepositoryTest {
    @Test public void sqliteCatalogAccountsAndAtomicPaidBooking() {
        Repository repository = Repository.getInstance(
                InstrumentationRegistry.getInstrumentation().getTargetContext());
        assertEquals(6, repository.getMovies().size());
        for (Movie movie : repository.getMovies()) {
            assertEquals(3, repository.getShowtimes(movie.getId()).size());
        }

        assertNotNull(repository.login(" TEST@gmail.com ", "123456"));
        assertNull(repository.login("test@gmail.com", "wrong"));
        assertFalse(repository.register(new User(0, "Test", "TEST@gmail.com", "0901234567", "123456")));
        String email = "new" + System.nanoTime() + "@gmail.com";
        assertTrue(repository.register(new User(0, "Test", email, "0901234567", "123456")));
        assertNotNull(repository.login(email, "123456"));

        Showtime showtime = repository.getShowtimes(6).get(2);
        assertTrue(repository.getBookedSeats(showtime.getId()).contains("A1"));
        assertTrue(repository.createBookingAndPayment(1, showtime.getId(),
                Arrays.asList("A1"), showtime.getPrice(), "Demo Visa") < 0);
        assertTrue(repository.createBookingAndPayment(1, showtime.getId(),
                new ArrayList<>(), 0, "Demo Visa") < 0);
        assertTrue(repository.createBookingAndPayment(1, showtime.getId(),
                Arrays.asList("Z1"), showtime.getPrice(), "Demo Visa") < 0);

        List<String> available = new ArrayList<>();
        for (char row = 'A'; row <= 'E' && available.size() < 2; row++) {
            for (int seat = 1; seat <= 6 && available.size() < 2; seat++) {
                String number = row + String.valueOf(seat);
                if (!repository.getBookedSeats(showtime.getId()).contains(number)) available.add(number);
            }
        }
        assertEquals(2, available.size());
        int bookingId = repository.createBookingAndPayment(1, showtime.getId(), available,
                showtime.getPrice() * available.size(), "Ví MoMo · mô phỏng");
        assertTrue(bookingId > 0);

        Booking booking = repository.getBookingById(bookingId);
        assertNotNull(booking);
        assertEquals("SUCCESS", booking.getPaymentStatus());
        assertEquals("Ví MoMo · mô phỏng", booking.getPaymentMethod());
        assertEquals(available.size(), booking.getSeats().size());
        assertTrue(repository.getBookedSeats(showtime.getId()).containsAll(available));
        assertEquals(bookingId, repository.getBookingsByUser(1).get(0).getId());
        assertTrue(repository.createBookingAndPayment(1, showtime.getId(), available,
                showtime.getPrice() * available.size(), "Demo Visa") < 0);

        String freeSeat = null;
        for (char row = 'A'; row <= 'E' && freeSeat == null; row++) {
            for (int seat = 1; seat <= 6; seat++) {
                String number = row + String.valueOf(seat);
                if (!repository.getBookedSeats(showtime.getId()).contains(number)) {
                    freeSeat = number;
                    break;
                }
            }
        }
        assertNotNull(freeSeat);
        int bookingCount = repository.getBookingsByUser(1).size();
        assertTrue(repository.recordFailedPayment(1, showtime.getId(),
                Arrays.asList(freeSeat), showtime.getPrice(), "Demo Visa"));
        assertEquals(bookingCount, repository.getBookingsByUser(1).size());
        assertFalse(repository.getBookedSeats(showtime.getId()).contains(freeSeat));
    }
}
