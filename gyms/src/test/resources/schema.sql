CREATE TABLE machine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    available BOOLEAN,
    description VARCHAR(255),
    gym_id BIGINT,
    USER_SUB VARCHAR(255)
);
