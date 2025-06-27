-- Criação da tabela role
CREATE TABLE role (
    id BIGINT NOT NULL AUTO_INCREMENT,
    authority VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Criação da tabela intermediária client_role (Many-to-Many entre client e role)
CREATE TABLE client_role (
    client_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (client_id, role_id),
    CONSTRAINT fk_clientrole_client FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE,
    CONSTRAINT fk_clientrole_role FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB;

-- Inserção de roles
INSERT INTO role (authority) VALUES
('ROLE_ADMIN'),
('ROLE_CLIENT');

-- Associação de clientes às roles
-- Cliente 1 será ADMIN + CLIENT
INSERT INTO client_role (client_id, role_id) VALUES
(1, 1), -- ROLE_ADMIN
(1, 2), -- ROLE_CLIENT
(2, 2),
(3, 2),
(4, 2),
(5, 2);