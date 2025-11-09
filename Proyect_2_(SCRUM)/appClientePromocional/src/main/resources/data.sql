INSERT INTO cliente (id, nombre, email) VALUES (1, 'Juan Perez', 'juan.perez@example.com');
INSERT INTO cliente (id, nombre, email) VALUES (2, 'Maria Lopez', 'maria.lopez@example.com');

INSERT INTO promocion (id, descripcion) VALUES (1, 'Descuento del 10% en tu proximo alquiler');
INSERT INTO promocion (id, descripcion) VALUES (2, 'Dias gratis de alquiler');

INSERT INTO cliente_promocion (id, id_cliente, id_promocion, fecha) VALUES (1, 1, 1, '2025-11-08');
INSERT INTO cliente_promocion (id, id_cliente, id_promocion, fecha) VALUES (2, 2, 2, '2025-11-08');
