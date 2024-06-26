DROP TABLE IF EXISTS users, films, rating, likes, genre, film_genre, friends;

CREATE table if not exists users (
  id INTEGER GENERATED BY DEFAULT AS identity not null PRIMARY KEY,
  email varchar(300),
  login varchar(300),
  name varchar(300),
  birthday date
);

CREATE TABLE if not exists films (
  id INTEGER GENERATED BY DEFAULT AS identity not null PRIMARY KEY,
  name varchar(300),
  description varchar(200),
  release_date date,
  duration integer,
  rating integer
);

CREATE TABLE if not exists rating (
  id INTEGER GENERATED BY DEFAULT AS identity not null PRIMARY KEY,
  name varchar(200)
);

CREATE TABLE if not exists likes (
  user_id integer,
  film_id integer,
  PRIMARY KEY (user_id, film_id)
);

CREATE table if not exists genre (
  id INTEGER GENERATED BY DEFAULT AS identity not null PRIMARY KEY,
  name varchar(300)
);

CREATE table if not exists film_genre (
  film_id integer,
  genre_id integer,
  PRIMARY KEY (film_id, genre_id)
);

CREATE table if not exists friends (
  user_id integer,
  friend_id integer,
  PRIMARY KEY (user_id, friend_id)
);

ALTER TABLE likes ADD FOREIGN KEY (user_id) REFERENCES users (id) on delete cascade;

ALTER TABLE friends ADD FOREIGN KEY (user_id) REFERENCES users (id) on delete cascade;

ALTER TABLE friends ADD FOREIGN KEY (friend_id) REFERENCES users (id) on delete cascade;

ALTER TABLE likes ADD FOREIGN KEY (film_id) REFERENCES films (id) on delete cascade;

ALTER TABLE film_genre ADD FOREIGN KEY (film_id) REFERENCES films (id) on delete cascade;

ALTER TABLE film_genre ADD FOREIGN KEY (genre_id) REFERENCES genre (id) on delete cascade;

ALTER TABLE films ADD FOREIGN KEY (rating) REFERENCES rating (id) on delete restrict;