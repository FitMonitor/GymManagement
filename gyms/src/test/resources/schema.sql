CREATE TABLE machine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    available BOOLEAN,
    description VARCHAR(255),
    gym_id BIGINT,
    USER_SUB VARCHAR(255)
);

CREATE TABLE gyms (
    gym_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    gym_name VARCHAR(255) NOT NULL,
    capacity INT NOT NULL,
    occupancy INT NOT NULL
);