-- Criação da tabela client
CREATE TABLE client (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BOOLEAN NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    phone VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Criação da tabela room
CREATE TABLE room (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BOOLEAN NOT NULL,
    description TEXT,
    price FLOAT NOT NULL,
    image_url VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Criação da tabela daily com chave primária simples
CREATE TABLE daily (
    id BIGINT NOT NULL AUTO_INCREMENT,
    daily_date DATETIME(6),
    client_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE,
    CONSTRAINT fk_room FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Inserção de clientes
INSERT INTO client (active, name, email, password, phone) VALUES
(true, 'Ana Souza', 'ana.souza@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', '31999990001'),
(true, 'Carlos Lima', 'carlos.lima@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', '31999990002'),
(true, 'Marina Alves', 'marina.alves@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', '31999990003'),
(true, 'João Pedro', 'joao.pedro@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', '31999990004'),
(true, 'Beatriz Silva', 'beatriz.silva@email.com', '$2a$10$eACCYoNOHEqXve8aIWT8Nu3PkMXWBaOxJ9aORUYzfMQCbVBIhZ8tG', '31999990005');

-- Inserção de quartos
INSERT INTO room (active, description, price, image_url) VALUES
(true, 'Quarto simples com cama de solteiro e ventilador.', 120.00, 'https://example.com/quarto1.jpg'),
(true, 'Quarto duplo com ar-condicionado e TV.', 180.50, 'https://example.com/quarto2.jpg'),
(true, 'Suíte com cama king-size, hidromassagem e varanda.', 320.00, 'https://example.com/quarto3.jpg'),
(true, 'Quarto triplo ideal para famílias.', 250.75, 'https://example.com/quarto4.jpg'),
(true, 'Quarto executivo com mesa de trabalho e Wi-Fi rápido.', 200.00, 'https://example.com/quarto5.jpg');

-- Inserção de diárias
INSERT INTO daily (daily_date, client_id, room_id) VALUES
('2025-06-01 00:00:00', 1, 1),
('2025-06-02 00:00:00', 1, 2),
('2025-06-03 00:00:00', 2, 2),
('2025-06-04 00:00:00', 3, 3),
('2025-06-05 00:00:00', 4, 4),
('2025-06-06 00:00:00', 5, 5),
('2025-06-07 00:00:00', 5, 1);
