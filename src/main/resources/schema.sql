CREATE TABLE IF NOT EXISTS `users` (
                         `id` int PRIMARY KEY AUTO_INCREMENT,
                         `email` varchar(255),
                         `name` varchar(255),
                         `login` varchar(255) UNIQUE NOT NULL,
                         `birthday` date
);

CREATE TABLE IF NOT EXISTS `friends` (
                           `friend_id_from` int,
                           `friend_id_to` int,
                           `confirmed` boolean
);

CREATE TABLE IF NOT EXISTS `films` (
                         `id` int PRIMARY KEY AUTO_INCREMENT,
                         `name` varchar(255) NOT NULL,
                         `description` varchar(255),
                         `releaseDate` date,
                         `duration` int,
                         `rate` int,
                         `mpa_id` int
);

CREATE TABLE IF NOT EXISTS `genres` (
                          `id` int PRIMARY KEY,
                          `name` varchar(255)
);

CREATE TABLE IF NOT EXISTS `film_genres` (
                               `film_id` int,
                               `genre_id` int
);

CREATE TABLE IF NOT EXISTS `mpa` (
                       `id` int PRIMARY KEY,
                       `name` varchar(255)
);

CREATE TABLE IF NOT EXISTS `likes` (
                         `film_id` int,
                         `user_id` int
);

ALTER TABLE `friends` ADD FOREIGN KEY (`friend_id_from`) REFERENCES `users` (`id`);

ALTER TABLE `friends` ADD FOREIGN KEY (`friend_id_to`) REFERENCES `users` (`id`);

ALTER TABLE `film_genres` ADD FOREIGN KEY (`film_id`) REFERENCES `films` (`id`);

ALTER TABLE `film_genres` ADD FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`);

ALTER TABLE `films` ADD FOREIGN KEY (`mpa_id`) REFERENCES `mpa` (`id`);

ALTER TABLE `likes` ADD FOREIGN KEY (`film_id`) REFERENCES `films` (`id`);

ALTER TABLE `likes` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);