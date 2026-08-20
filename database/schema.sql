CREATE DATABASE IF NOT EXISTS fidness_rutinas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fidness_rutinas;

CREATE TABLE IF NOT EXISTS usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(120) NOT NULL,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR(120) NOT NULL,
    administrador BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS ejercicio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    nivel_dificultad VARCHAR(30) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    series INT NULL,
    repeticiones INT NULL,
    minutos INT NULL
);

CREATE TABLE IF NOT EXISTS rutina (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    usuario_id INT NOT NULL UNIQUE,
    CONSTRAINT fk_rutina_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS rutina_ejercicio (
    rutina_id INT NOT NULL,
    ejercicio_id INT NOT NULL,
    PRIMARY KEY (rutina_id, ejercicio_id),
    CONSTRAINT fk_rutina_ejercicio_rutina FOREIGN KEY (rutina_id) REFERENCES rutina(id) ON DELETE CASCADE,
    CONSTRAINT fk_rutina_ejercicio_ejercicio FOREIGN KEY (ejercicio_id) REFERENCES ejercicio(id)
);
