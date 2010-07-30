

create schema authentication;



CREATE SEQUENCE authentication.user_sequence START 1;
create table authentication.user (
    user_id int PRIMARY KEY DEFAULT nextval('authentication.user_sequence'), 
    username text NOT NULL, 
    email varchar NOT NULL, 
    salt varchar NOT NULL, 
    passhash varchar NOT NULL, 
    CONSTRAINT user_uniques UNIQUE (username)
);


CREATE SEQUENCE authentication.role_sequence START 1;
CREATE table authentication.role (
    role_id int PRIMARY KEY DEFAULT nextval('authentication.role_sequence'),
    rolename text NOT NULL
);

CREATE table authentication.user_role (
    role_id int REFERENCES authentication.role (role_id) NOT NULL,
    user_id int REFERENCES authentication.user (user_id) NOT NULL,
    CONSTRAINT user_role_uniques UNIQUE (role_id, user_id)
);

CREATE SEQUENCE authentication.session_sequence START 1;
create table authentication.session (
    session_id int PRIMARY KEY DEFAULT nextval('authentication.session_sequence'),
    user_id int REFERENCES authentication.user (user_id) NOT NULL,
    token varchar NOT NULL UNIQUE,
    started timestamp without time zone NOT NULL,
    refreshed timestamp without time zone 
);


