CREATE TABLE cliente (
    id INT PRIMARY KEY,
    nombre VARCHAR(255),
    email VARCHAR(255)
);

CREATE TABLE promocion (
    id INT PRIMARY KEY,
    descripcion VARCHAR(255)
);

CREATE TABLE cliente_promocion (
    id INT PRIMARY KEY,
    id_cliente INT,
    id_promocion INT,
    fecha DATE,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id),
    FOREIGN KEY (id_promocion) REFERENCES promocion(id)
);
