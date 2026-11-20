package com.example.movieticket.data;

import com.example.movieticket.model.Booking;
import com.example.movieticket.model.Movie;
import com.example.movieticket.model.Showtime;
import com.example.movieticket.model.User;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RepositoryTest {
    private final Repository repository = Repository.getInstance();

    @Test public void demoHasSixMoviesAndThreeShowtimesEach() {
        assertEquals(6, repository.getMovies().size());
        for (Movie movie : repository.getMovies()) assertEquals(3, repository.getShowtimes(movie.getId()).size());
    }
    @Test public void loginChecksCredentials() {
        assertNotNull(repository.login(" TEST@gmail.com ", "123456"));
        assertNull(repository.login("test@gmail.com", "wrong"));
        assertNull(repository.login(null, null));
    }
    @Test public void duplicateEmailIsRejected() {
        assertFalse(repository.register(new User(0, "Test", "TEST@gmail.com", "0901234567", "123456")));
        String email = "new" + System.nanoTime() + "@gmail.com";
        assertTrue(repository.register(new User(0, "Test", email, "0901234567", "123456")));
        assertNotNull(repository.login(email, "123456"));
    }
    @Test public void bookingValidatesAndUpdatesOccupiedSeats() {
        Showtime s = repository.getShowtimes(6).get(2);
        assertTrue(repository.getBookedSeats(s.getId()).contains("A1"));
        assertFalse(repository.createBooking(1, s.getId(), Arrays.asList("A1"), s.getPrice()));
        assertFalse(repository.createBooking(1, s.getId(), new ArrayList<>(), 0));
        assertFalse(repository.createBooking(1, s.getId(), Arrays.asList("E1", "E1"), s.getPrice() * 2));
        assertFalse(repository.createBooking(1, s.getId(), Arrays.asList("Z1"), s.getPrice()));
        assertFalse(repository.createBooking(1, s.getId(), Arrays.asList("E2"), 1));
        assertFalse(repository.createBooking(1, s.getId(), Arrays.asList("A3", "A4", "A5", "A6", "B1", "B2", "B4"), s.getPrice() * 7));
        List<String> seats = new ArrayList<>(Arrays.asList("E3", "E4"));
        assertTrue(repository.createBooking(1, s.getId(), seats, s.getPrice() * 2));
        seats.clear();
        assertTrue(repository.getBookedSeats(s.getId()).contains("E3"));
        Booking booking = repository.getBookingsByUser(1).get(0);
        assertEquals(2, booking.getSeats().size());
        assertEquals(s.getPrice() * 2, booking.getTotalPrice());
        assertSame(booking, repository.getBookingById(booking.getId()));
        assertFalse(repository.createBooking(1, s.getId(), Arrays.asList("E3"), s.getPrice()));
    }
}
