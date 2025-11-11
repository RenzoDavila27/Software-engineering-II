
-- Paises
INSERT INTO paises (id, nombre, eliminado) VALUES
('a4d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', 'Argentina', false),
('b4d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', 'Brasil', false);

-- Provincias
INSERT INTO provincias (id, nombre, pais_id, eliminado) VALUES
('a5d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', 'Buenos Aires', 'a4d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', false),
('b5d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', 'Córdoba', 'a4d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', false);

-- Departamentos
INSERT INTO departamentos (id, nombre, provincia_id, eliminado) VALUES
('a6d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', 'La Plata', 'a5d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', false),
('b6d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', 'Punilla', 'b5d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', false);

-- Localidades
INSERT INTO localidades (id, nombre, codigo_postal, departamento_id, eliminado) VALUES
('a7d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', 'La Plata', '1900', 'a6d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', false),
('b7d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', 'Villa Carlos Paz', '5152', 'b6d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', false);

-- Direcciones
INSERT INTO direcciones (id, calle, numeracion, barrio, manzana_piso, casa_departamento, referencia, localidad_id, eliminado) VALUES
('a8d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', 'Calle Falsa', '123', 'Centro', 'A', '1', 'Frente a la plaza', 'a7d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', false),
('b8d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', 'Avenida Siempre Viva', '742', 'Springfield', 'B', '2', 'Cerca del Kwik-E-Mart', 'b7d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', false);

-- Contactos (abstract, so we insert into concrete tables)
-- Contactos Correo
INSERT INTO contactos (id, tipo_contacto, observacion, eliminado) VALUES
('a9d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', 'PERSONAL', 'Email personal', false),
('b9d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', 'LABORAL', 'Email laboral', false);

INSERT INTO contactos_correo (id, email) VALUES
('a9d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', 'juan.perez@example.com'),
('b9d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', 'ana.gomez@work.com');

-- Contactos Telefonicos
INSERT INTO contactos (id, tipo_contacto, observacion, eliminado) VALUES
('c9d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', 'PERSONAL', 'Celular personal', false),
('d9d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', 'LABORAL', 'Teléfono laboral', false);

INSERT INTO contactos_telefonicos (id, telefono, tipo_telefono) VALUES
('c9d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', '1122334455', 'CELULAR'),
('d9d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', '5544332211', 'FIJO');

-- Imagenes
INSERT INTO imagenes (id, nombre, mime, contenido, tipo_imagen, eliminado) VALUES
('a1b2c3d4-e5f6-7890-1234-567890abcdef', 'foto_juan.jpg', 'image/jpeg', null, 'PERSONA', false),
('b1c2d3e4-f5a6-b7c8-d9e0-f1a2b3c4d5e6', 'foto_ana.jpg', 'image/jpeg', null, 'PERSONA', false),
('c1d2e3f4-a5b6-c7d8-e9f0-a1b2c3d4e5f6', 'auto_01.jpg', 'image/jpeg', null, 'VEHICULO', false),
('d1e2f3a4-b5c6-d7e8-f9a0-b1c2d3e4f5a6', 'auto_02.jpg', 'image/jpeg', null, 'VEHICULO', false);

-- Personas
INSERT INTO personas (id, nombre, apellido, fecha_nacimiento, tipo_documento, numero_documento, contacto_id, direccion_id, imagen_id, eliminado) VALUES
('a2b3c4d5-e6f7-8901-2345-678901bcdefa', 'Juan', 'Perez', '1990-01-15', 'DNI', '12345678', 'a9d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', 'a8d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3d', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', false),
('b2c3d4e5-f6a7-b8c9-d0e1-f2a3b4c5d6e7', 'Ana', 'Gomez', '1985-05-20', 'DNI', '87654321', 'b9d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', 'b8d3b3e0-3e3f-4c3d-9c3d-3e3f4c3d9c3e', 'b1c2d3e4-f5a6-b7c8-d9e0-f1a2b3c4d5e6', false);

-- Usuarios
INSERT INTO usuario (id, nombre_usuario, clave, rol_usuario, persona_id, eliminado) VALUES
('a3b4c5d6-e7f8-9012-3456-789012cdefab', 'jperez', 'clave123', 'CLIENTE', 'a2b3c4d5-e6f7-8901-2345-678901bcdefa', false),
('b3c4d5e6-f7a8-b9c0-d1e2-f3a4b5c6d7e8', 'agomez', 'clave456', 'ADMINISTRATIVO', 'b2c3d4e5-f6a7-b8c9-d0e1-f2a3b4c5d6e7', false);

-- CostoVehiculo
INSERT INTO costo_vehiculo (id, fecha_desde, fecha_hasta, costo, eliminado) VALUES
('c4d5e6f7-a8b9-c0d1-e2f3-a4b5c6d7e8f9', '2024-01-01', '2024-12-31', 100.00, false),
('d4e5f6a7-b8c9-d0e1-f2a3-b4c5d6e7f8a9', '2024-01-01', '2024-12-31', 150.00, false);

-- CaracteristicaVehiculo
INSERT INTO caracteristica_vehiculo (id, marca, modelo, anio, cantidad_asientos, cantidad_puertas, cantidad_total_vehiculos, cantidad_total_vehiculos_alquilados, imagen_id, costo_vehiculo_id, eliminado) VALUES
('e5f6a7b8-c9d0-e1f2-a3b4-c5d6e7f8a9b0', 'Ford', 'Focus', 2020, 5, 4, 10, 2, 'c1d2e3f4-a5b6-c7d8-e9f0-a1b2c3d4e5f6', 'c4d5e6f7-a8b9-c0d1-e2f3-a4b5c6d7e8f9', false),
('f5a6b7c8-d9e0-f1a2-b3c4-d5e6f7a8b9c0', 'Chevrolet', 'Cruze', 2021, 5, 4, 8, 1, 'd1e2f3a4-b5c6-d7e8-f9a0-b1c2d3e4f5a6', 'd4e5f6a7-b8c9-d0e1-f2a3-b4c5d6e7f8a9', false);

-- Vehiculo
INSERT INTO vehiculo (id, estado_vehiculo, patente, caracteristica_vehiculo_id, eliminado) VALUES
('a1a2a3a4-a5a6-a7a8-a9a0-a1a2a3a4a5a6', 'DISPONIBLE', 'AB123CD', 'e5f6a7b8-c9d0-e1f2-a3b4-c5d6e7f8a9b0', false),
('b1b2b3b4-b5b6-b7b8-b9b0-b1b2b3b4b5b6', 'ALQUILADO', 'CD456EF', 'f5a6b7c8-d9e0-f1a2-b3c4-d5e6f7a8b9c0', false);
