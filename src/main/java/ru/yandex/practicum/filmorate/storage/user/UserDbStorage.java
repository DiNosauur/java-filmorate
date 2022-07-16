package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Qualifier("db")
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "insert into users(email, name, login, birthday) " +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "update users set email = ?, " +
                "name = ?, login = ?, birthday = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery
                , user.getEmail()
                , user.getName()
                , user.getLogin()
                , Date.valueOf(user.getBirthday())
                , user.getId());
        return user;
    }

    @Override
    public User getUser(Long id) {
        String sql = "select * from users where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::makeUser, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        String sqlQuery = "insert into FRIENDS (FRIEND_ID_FROM, FRIEND_ID_TO) " +
                "values (?, ?)";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public void delFriend(Long id, Long friendId) {
        String sqlQuery = "delete from FRIENDS " +
                "where FRIEND_ID_FROM = ? and FRIEND_ID_TO = ?";
        jdbcTemplate.update(sqlQuery, id, friendId);
    }

    @Override
    public Collection<Long> getFriends(Long id) {
        String sql = "select FRIEND_ID_TO as ID from FRIENDS where FRIEND_ID_FROM = ?";
        return jdbcTemplate.query(sql, this::makeUserId, id);
    }

    private Long makeUserId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("id");
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
