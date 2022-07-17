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

    private static final String SQL_FIND_ALL_USERS =
            "select * from users";
    private static final String SQL_CREATE_USER =
            "insert into users(email, name, login, birthday) values (?, ?, ?, ?)";
    private static final String SQL_UPDATE_USER =
            "update users set email = ?, name = ?, login = ?, birthday = ? where id = ?";
    private static final String SQL_GET_USER =
            "select * from users where id = ?";
    private static final String SQL_ADD_FRIEND =
            "insert into FRIENDS (FRIEND_ID_FROM, FRIEND_ID_TO) values (?, ?)";
    private static final String SQL_DEL_FRIEND =
            "delete from FRIENDS where FRIEND_ID_FROM = ? and FRIEND_ID_TO = ?";
    private static final String SQL_GET_FRIENDS =
            "select FRIEND_ID_TO as ID from FRIENDS where FRIEND_ID_FROM = ?";

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL_USERS, this::makeUser);
    }

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection
                    .prepareStatement(SQL_CREATE_USER, new String[]{"id"});
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
        jdbcTemplate.update(SQL_UPDATE_USER
                , user.getEmail()
                , user.getName()
                , user.getLogin()
                , Date.valueOf(user.getBirthday())
                , user.getId());
        return user;
    }

    @Override
    public User getUser(Long id) {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_USER, this::makeUser, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        jdbcTemplate.update(SQL_ADD_FRIEND, id, friendId);
    }

    @Override
    public void delFriend(Long id, Long friendId) {
        jdbcTemplate.update(SQL_DEL_FRIEND, id, friendId);
    }

    @Override
    public Collection<Long> getFriends(Long id) {
        return jdbcTemplate.query(SQL_GET_FRIENDS, this::makeUserId, id);
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
