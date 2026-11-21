package com.example.movieticket.data;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.example.movieticket.R;
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
        assertTrue(repository.getMovies().size() >= 6);
        for (Movie movie : repository.getMovies()) {
            assertEquals(3, repository.getShowtimes(movie.getId()).size());
        }

        assertNotNull(repository.login(" TEST@gmail.com ", "123456"));
        assertNull(repository.login("test@gmail.com", "wrong"));
        User admin = repository.login("admin@example.com", "123456");
        assertNotNull(admin);
        assertTrue(admin.isAdmin());
        assertTrue(repository.isAdmin(admin.getId()));
        assertFalse(repository.register(new User(0, "Test", "TEST@gmail.com", "0901234567", "123456")));
        String email = "new" + System.nanoTime() + "@gmail.com";
        assertTrue(repository.register(new User(0, "Test", email, "0901234567", "123456")));
        assertNotNull(repository.login(email, "123456"));

        int movieId = repository.createMovie(new Movie(0, "Test movie " + System.nanoTime(),
                "Drama", 100, "Test director", "Test description", R.drawable.poster_2));
        assertTrue(movieId > 0);
        Movie movie = repository.getMovieById(movieId);
        assertNotNull(movie);
        assertEquals(3, repository.getShowtimes(movieId).size());
        movie.setTitle("Edited test movie");
        assertTrue(repository.updateMovie(movie));
        assertEquals("Edited test movie", repository.getMovieById(movieId).getTitle());

        Showtime showtime = repository.getShowtimes(movieId).get(0);
        assertTrue(repository.createBookingAndPayment(1, showtime.getId(),
                Arrays.asList("A1"), showtime.getPrice() + 1, "Demo Visa") < 0);
        assertTrue(repository.createBookingAndPayment(1, showtime.getId(),
                new ArrayList<>(), 0, "Demo Visa") < 0);
        assertTrue(repository.createBookingAndPayment(1, showtime.getId(),
                Arrays.asList("Z1"), showtime.getPrice(), "Demo Visa") < 0);
        List<String> available = Arrays.asList("A1", "A2");
        assertTrue(repository.recordFailedPayment(1, showtime.getId(), Arrays.asList("A3"),
                showtime.getPrice(), "Demo Visa"));
        assertFalse(repository.getBookedSeats(showtime.getId()).contains("A3"));
        int bookingId = repository.createBookingAndPayment(1, showtime.getId(), available,
                showtime.getPrice() * available.size(), "Ví MoMo · mô phỏng");
        assertTrue(bookingId > 0);

        Booking booking = repository.getBookingById(bookingId);
        assertNotNull(booking);
        assertEquals("SUCCESS", booking.getPaymentStatus());
        assertEquals("Ví MoMo · mô phỏng", booking.getPaymentMethod());
        assertEquals(available.size(), booking.getSeats().size());
        assertTrue(repository.getBookedSeats(showtime.getId()).containsAll(available));
        boolean listed = false;
        for (Booking userBooking : repository.getBookingsByUser(1)) {
            if (userBooking.getId() == bookingId) listed = true;
        }
        assertTrue(listed);
        assertTrue(repository.createBookingAndPayment(1, showtime.getId(), available,
                showtime.getPrice() * available.size(), "Demo Visa") < 0);
        assertFalse(repository.deleteMovie(movieId));

        int deletableMovieId = repository.createMovie(new Movie(0, "Delete test " + System.nanoTime(),
                "Drama", 90, "Test director", "Test description", R.drawable.poster_3));
        assertTrue(deletableMovieId > 0);
        assertTrue(repository.deleteMovie(deletableMovieId));
        assertNull(repository.getMovieById(deletableMovieId));
        assertTrue(repository.getShowtimes(deletableMovieId).isEmpty());
    }
}
