CREATE TABLE IF NOT EXISTS personas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BIT(1) NOT NULL DEFAULT 0,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS empresas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BIT(1) NOT NULL DEFAULT 0,
    nombre VARCHAR(255) NOT NULL,
    persona_id BIGINT,
    CONSTRAINT fk_empresas_persona
        FOREIGN KEY (persona_id) REFERENCES personas(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS contactos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BIT(1) NOT NULL DEFAULT 0,
    tipo_contacto VARCHAR(20) NOT NULL,
    observacion VARCHAR(1024),
    persona_id BIGINT,
    empresa_id BIGINT,
    CONSTRAINT fk_contactos_persona
        FOREIGN KEY (persona_id) REFERENCES personas(id),
    CONSTRAINT fk_contactos_empresa
        FOREIGN KEY (empresa_id) REFERENCES empresas(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS contactos_correo (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT fk_contactos_correo_contacto
        FOREIGN KEY (id) REFERENCES contactos(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS contactos_telefonicos (
    id BIGINT PRIMARY KEY,
    telefono VARCHAR(255) NOT NULL,
    tipo_telefono VARCHAR(20) NOT NULL,
    CONSTRAINT fk_contactos_telefonicos_contacto
        FOREIGN KEY (id) REFERENCES contactos(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eliminado BIT(1) NOT NULL DEFAULT 0,
    cuenta VARCHAR(255) NOT NULL,
    clave VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    persona_id BIGINT,
    CONSTRAINT uk_usuarios_cuenta UNIQUE (cuenta),
    CONSTRAINT fk_usuarios_persona
        FOREIGN KEY (persona_id) REFERENCES personas(id)
) ENGINE=InnoDB;
