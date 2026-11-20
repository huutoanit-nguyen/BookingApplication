package com.example.movieticket.data;

import com.example.movieticket.R;
import com.example.movieticket.model.Booking;
import com.example.movieticket.model.Movie;
import com.example.movieticket.model.Showtime;
import com.example.movieticket.model.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/** Single source of demo data. Nothing here is written to disk. */
public final class Repository {
    private static final Repository INSTANCE = new Repository();
    private final List<User> users = new ArrayList<>();
    private final List<Movie> movies = new ArrayList<>();
    private final List<Showtime> showtimes = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();

    private Repository() {
        users.add(new User(1, "Minh Anh", "test@gmail.com", "0901234567", "123456"));
        movies.add(new Movie(1, "Dune: Hành tinh cát", "Khoa học viễn tưởng", 166,
                "Denis Villeneuve", "Paul Atreides cùng Chani và người Fremen bước vào hành trình chống lại những kẻ đã hủy hoại gia đình. Giữa tình yêu và vận mệnh vũ trụ, Paul phải ngăn chặn một tương lai mà chỉ mình anh có thể nhìn thấy.", R.drawable.poster_1));
        movies.add(new Movie(2, "Thành phố không ngủ", "Hành động · Tâm lý", 128,
                "Nguyễn Minh Anh", "Một cuộc gặp gỡ tình cờ giữa lòng thành phố rực rỡ ánh đèn đưa hai người xa lạ vào hành trình tìm lại chính mình. Khi màn đêm buông xuống, mọi bí mật dần được hé lộ.", R.drawable.poster_2));
        movies.add(new Movie(3, "Ánh sáng cuối chân trời", "Phiêu lưu · Kỳ ảo", 142,
                "Trần Hoàng Nam", "Trong thế giới đang mất dần ánh sáng, một nhóm bạn trẻ lên đường tìm kiếm nguồn năng lượng cuối cùng. Một câu chuyện về lòng dũng cảm, tình bạn và niềm hy vọng.", R.drawable.poster_3));
        movies.add(new Movie(4, "Mật mã tương lai", "Khoa học viễn tưởng", 135,
                "Lê Quốc Huy", "Một lập trình viên phát hiện mật mã có thể thay đổi tương lai. Giữa thành phố công nghệ, anh phải lựa chọn giữa sự thật và sự an toàn của những người thân yêu.", R.drawable.poster_4));
        movies.add(new Movie(5, "Phía sau ánh đèn", "Tình cảm · Tâm lý", 115,
                "Phạm Thu Hà", "Phía sau sự náo nhiệt của thành phố là những ước mơ bình dị. Một cô gái trẻ theo đuổi đam mê nghệ thuật và tìm thấy tình yêu ở nơi ít ngờ tới nhất.", R.drawable.poster_5));
        movies.add(new Movie(6, "Chuyến xe cuối cùng", "Hành động · Giật gân", 122,
                "Vũ Đức Long", "Chuyến xe cuối cùng trong đêm trở thành cuộc đua với thời gian. Một tài xế bình thường phải vượt qua giới hạn để bảo vệ những hành khách xa lạ.", R.drawable.poster_6));
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN")).format(new Date());
        String[] times = {"14:30", "18:00", "20:30"};
        int[] prices = {85000, 95000, 110000};
        for (Movie movie : movies) {
            for (int i = 0; i < 3; i++) {
                showtimes.add(new Showtime(movie.getId() * 10 + i, movie.getId(),
                        String.format(Locale.ROOT, "Phòng 0%d", i + 1), date + " · " + times[i], prices[i]));
            }
        }
    }

    public static Repository getInstance() {
        // TODO: thay bằng SQLite
        return INSTANCE;
    }

    public synchronized User login(String email, String password) {
        // TODO: thay bằng SQLite
        if (email == null || password == null) return null;
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email.trim()) && user.getPassword().equals(password)) return user;
        }
        return null;
    }

    public synchronized boolean register(User user) {
        // TODO: thay bằng SQLite
        if (user == null || user.getEmail() == null) return false;
        String email = user.getEmail().trim().toLowerCase(Locale.ROOT);
        for (User existing : users) {
            if (existing.getEmail().equalsIgnoreCase(email)) return false;
        }
        users.add(new User(users.size() + 1, user.getName(), email, user.getPhone(), user.getPassword()));
        return true;
    }

    public List<Movie> getMovies() {
        // TODO: thay bằng SQLite
        return new ArrayList<>(movies);
    }

    public Movie getMovieById(int id) {
        // TODO: thay bằng SQLite
        for (Movie movie : movies) if (movie.getId() == id) return movie;
        return null;
    }

    public List<Showtime> getShowtimes(int movieId) {
        // TODO: thay bằng SQLite
        List<Showtime> result = new ArrayList<>();
        for (Showtime showtime : showtimes) if (showtime.getMovieId() == movieId) result.add(showtime);
        return result;
    }

    public Showtime getShowtimeById(int id) {
        // TODO: thay bằng SQLite
        for (Showtime showtime : showtimes) if (showtime.getId() == id) return showtime;
        return null;
    }

    public synchronized List<String> getBookedSeats(int showtimeId) {
        // TODO: thay bằng SQLite
        if (getShowtimeById(showtimeId) == null) return new ArrayList<>();
        List<String> result = new ArrayList<>(Arrays.asList("A1", "A2", "B3", "C4", "D5"));
        for (Booking booking : bookings) {
            if (booking.getShowtimeId() == showtimeId) result.addAll(booking.getSeats());
        }
        return result;
    }

    public synchronized boolean createBooking(int userId, int showtimeId, List<String> seats, int totalPrice) {
        // TODO: thay bằng SQLite
        Showtime showtime = getShowtimeById(showtimeId);
        if (userId <= 0 || showtime == null || seats == null || seats.isEmpty()
                || seats.size() > 6 || new HashSet<>(seats).size() != seats.size()
                || totalPrice != seats.size() * showtime.getPrice()) return false;
        List<String> occupied = getBookedSeats(showtimeId);
        for (String seat : seats) {
            if (seat == null || !seat.matches("[A-E][1-6]") || occupied.contains(seat)) return false;
        }
        List<String> sorted = new ArrayList<>(seats);
        Collections.sort(sorted);
        bookings.add(new Booking(bookings.size() + 1, userId, showtimeId,
                getMovieById(showtime.getMovieId()).getTitle(), showtime.getStartTime(),
                sorted, totalPrice, new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("vi-VN")).format(new Date())));
        return true;
    }

    public synchronized List<Booking> getBookingsByUser(int userId) {
        // TODO: thay bằng SQLite
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings) if (booking.getUserId() == userId) result.add(booking);
        Collections.reverse(result);
        return result;
    }

    public synchronized Booking getBookingById(int id) {
        // TODO: thay bằng SQLite
        for (Booking booking : bookings) if (booking.getId() == id) return booking;
        return null;
    }
}
