-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 16-05-2025 a las 10:16:49
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `mundo_fantasia`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `arcana`
--

CREATE TABLE `arcana` (
  `ID` int(11) NOT NULL,
  `Tipo` varchar(100) NOT NULL,
  `Maestria` varchar(50) DEFAULT NULL,
  `Dificultad` varchar(50) DEFAULT NULL,
  `Fecha` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `arma`
--

CREATE TABLE `arma` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `BufoEstadisticas` varchar(255) DEFAULT NULL,
  `Material` varchar(100) DEFAULT NULL,
  `Descripcion` text DEFAULT NULL,
  `Peso` float DEFAULT NULL,
  `PVP` float DEFAULT NULL,
  `Origen` varchar(100) DEFAULT NULL,
  `FechaCreacion` date DEFAULT NULL,
  `OrigenImperioID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `armadura`
--

CREATE TABLE `armadura` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `BufoEstadisticas` varchar(255) DEFAULT NULL,
  `Material` varchar(100) DEFAULT NULL,
  `Descripcion` text DEFAULT NULL,
  `Peso` float DEFAULT NULL,
  `PVP` float DEFAULT NULL,
  `Origen` varchar(100) DEFAULT NULL,
  `FechaCreacion` date DEFAULT NULL,
  `OrigenImperioID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bioma`
--

CREATE TABLE `bioma` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `Clima` varchar(100) DEFAULT NULL,
  `PorcentajeHumedad` float DEFAULT NULL,
  `Precipitaciones` varchar(50) DEFAULT NULL,
  `TemperaturaMedia` float DEFAULT NULL,
  `ContinenteID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `bioma`
--

INSERT INTO `bioma` (`ID`, `Nombre`, `Clima`, `PorcentajeHumedad`, `Precipitaciones`, `TemperaturaMedia`, `ContinenteID`) VALUES
(1, 'holi', 'Tropical', 100, 'Escasas', 22, 1),
(2, 'wot', 'Tropical', 34, 'Extremas', 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bioma_continente`
--

CREATE TABLE `bioma_continente` (
  `BiomaID` int(11) NOT NULL,
  `ContinenteID` int(11) NOT NULL,
  `AreaOcupada` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ciudad`
--

CREATE TABLE `ciudad` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `Poblacion` int(11) DEFAULT NULL,
  `Descripcion` text DEFAULT NULL,
  `ImperioID` int(11) DEFAULT NULL,
  `BiomaPredominanteID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `continente`
--

CREATE TABLE `continente` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `Hemisferio` varchar(50) DEFAULT NULL,
  `Tamanio` float DEFAULT NULL,
  `Clima` varchar(100) DEFAULT NULL,
  `Descripcion` text DEFAULT NULL,
  `Habitable` tinyint(1) DEFAULT 0,
  `PlanetaID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `continente`
--

INSERT INTO `continente` (`ID`, `Nombre`, `Hemisferio`, `Tamanio`, `Clima`, `Descripcion`, `Habitable`, `PlanetaID`) VALUES
(1, 'weagweag', 'Sur', 234234, 'Templado', '234234', 1, 1),
(2, 'aa', 'Este', 353, 'Templado', 't', 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estadisticas`
--

CREATE TABLE `estadisticas` (
  `ID` int(11) NOT NULL,
  `Tipo` varchar(50) DEFAULT NULL,
  `ATK` int(11) DEFAULT NULL,
  `DEF` int(11) DEFAULT NULL,
  `HP` int(11) DEFAULT NULL,
  `SPE` int(11) DEFAULT NULL,
  `MAT` int(11) DEFAULT NULL,
  `MDF` int(11) DEFAULT NULL,
  `XP` int(11) DEFAULT 0,
  `LVL` int(11) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `estadisticas`
--

INSERT INTO `estadisticas` (`ID`, `Tipo`, `ATK`, `DEF`, `HP`, `SPE`, `MAT`, `MDF`, `XP`, `LVL`) VALUES
(1, 'Base', 10, 10, 10, 10, 10, 10, 0, 1),
(2, 'Base', 10, 10, 10, 10, 10, 10, 0, 1),
(3, 'Personal', 55, 10, 10, 10, 10, 10, 0, 1),
(4, 'Personal', 10, 10, 10, 10, 10, 10, 0, 1),
(5, 'Personal', 10, 10, 10, 10, 10, 10, 0, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `guerra`
--

CREATE TABLE `guerra` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `FechaInicio` date DEFAULT NULL,
  `FechaFin` date DEFAULT NULL,
  `Descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `herramienta`
--

CREATE TABLE `herramienta` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `BufoEstadisticas` varchar(255) DEFAULT NULL,
  `Material` varchar(100) DEFAULT NULL,
  `Descripcion` text DEFAULT NULL,
  `Peso` float DEFAULT NULL,
  `PVP` float DEFAULT NULL,
  `Uso` varchar(255) DEFAULT NULL,
  `Origen` varchar(100) DEFAULT NULL,
  `FechaCreacion` date DEFAULT NULL,
  `OrigenImperioID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `imperio`
--

CREATE TABLE `imperio` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `Poblacion` int(11) DEFAULT NULL,
  `Descripcion` text DEFAULT NULL,
  `FechaCreacion` date DEFAULT NULL,
  `Lider` varchar(100) DEFAULT NULL,
  `Ideologia` varchar(100) DEFAULT NULL,
  `GDP` float DEFAULT NULL,
  `Tamanio` float DEFAULT NULL,
  `EnGuerra` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `imperio`
--

INSERT INTO `imperio` (`ID`, `Nombre`, `Poblacion`, `Descripcion`, `FechaCreacion`, `Lider`, `Ideologia`, `GDP`, `Tamanio`, `EnGuerra`) VALUES
(1, 'gewa', 2000, 'rer', '0222-02-22', 'pepe', 'Monarquía', 235235, 2000, 1),
(2, 'waef', 32000, '3523', '0022-02-22', 'pee', 'Monarquía', 23532, 1000, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `imperio_guerra`
--

CREATE TABLE `imperio_guerra` (
  `ImperioID` int(11) NOT NULL,
  `GuerraID` int(11) NOT NULL,
  `Rol` varchar(50) DEFAULT NULL,
  `Ganador` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona`
--

CREATE TABLE `persona` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `Apellido` varchar(100) DEFAULT NULL,
  `Ancho` float DEFAULT NULL,
  `Alto` float DEFAULT NULL,
  `DescripcionFisica` text DEFAULT NULL,
  `PorcentajeGrasaCorporal` float DEFAULT NULL,
  `Personalidad` text DEFAULT NULL,
  `Oro` int(11) DEFAULT 0,
  `FechaNacimiento` date DEFAULT NULL,
  `Profesion` varchar(100) DEFAULT NULL,
  `Direccion` varchar(255) DEFAULT NULL,
  `RazaID` int(11) DEFAULT NULL,
  `ImperioID` int(11) DEFAULT NULL,
  `EstadisticasID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `persona`
--

INSERT INTO `persona` (`ID`, `Nombre`, `Apellido`, `Ancho`, `Alto`, `DescripcionFisica`, `PorcentajeGrasaCorporal`, `Personalidad`, `Oro`, `FechaNacimiento`, `Profesion`, `Direccion`, `RazaID`, `ImperioID`, `EstadisticasID`) VALUES
(1, 'test', 'test', 2, 3, '3432', 25, 'wera', 324, '0235-05-23', 'war', 'wera', 1, 1, 3),
(2, 'aweg', 'aweg', NULL, NULL, NULL, NULL, NULL, 23, NULL, '23', 'ware', 1, 1, 4),
(3, 'eh', 'erh', NULL, NULL, NULL, NULL, NULL, 34, '0002-02-22', '43', 'erge', 2, NULL, 5);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona_arcana`
--

CREATE TABLE `persona_arcana` (
  `PersonaID` int(11) NOT NULL,
  `ArcanaID` int(11) NOT NULL,
  `Maestria` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona_arma`
--

CREATE TABLE `persona_arma` (
  `PersonaID` int(11) NOT NULL,
  `ArmaID` int(11) NOT NULL,
  `Equipada` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona_armadura`
--

CREATE TABLE `persona_armadura` (
  `PersonaID` int(11) NOT NULL,
  `ArmaduraID` int(11) NOT NULL,
  `Equipada` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona_herramienta`
--

CREATE TABLE `persona_herramienta` (
  `PersonaID` int(11) NOT NULL,
  `HerramientaID` int(11) NOT NULL,
  `Equipada` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `planeta`
--

CREATE TABLE `planeta` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `Ubicacion` varchar(255) DEFAULT NULL,
  `Habitable` tinyint(1) DEFAULT 0,
  `NivelAgua` float DEFAULT NULL,
  `FechaCreacion` date DEFAULT NULL,
  `Tamanio` float DEFAULT NULL,
  `Densidad` float DEFAULT NULL,
  `Descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `planeta`
--

INSERT INTO `planeta` (`ID`, `Nombre`, `Ubicacion`, `Habitable`, `NivelAgua`, `FechaCreacion`, `Tamanio`, `Densidad`, `Descripcion`) VALUES
(1, 'aerh', 'awhh', 1, 22, '0022-03-23', 23, 23, '2');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `raza`
--

CREATE TABLE `raza` (
  `ID` int(11) NOT NULL,
  `Nombre` varchar(100) NOT NULL,
  `DescripcionFisica` text DEFAULT NULL,
  `FechaConcepcion` date DEFAULT NULL,
  `AlturaPromedia` float DEFAULT NULL,
  `AnchoPromedio` float DEFAULT NULL,
  `EstadisticasBaseID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `raza`
--

INSERT INTO `raza` (`ID`, `Nombre`, `DescripcionFisica`, `FechaConcepcion`, `AlturaPromedia`, `AnchoPromedio`, `EstadisticasBaseID`) VALUES
(1, 'wow', 'taret', '0222-02-22', 3.01, 4, 1),
(2, 'w', 'te', '0022-02-22', 5, 5, 2);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `arcana`
--
ALTER TABLE `arcana`
  ADD PRIMARY KEY (`ID`);

--
-- Indices de la tabla `arma`
--
ALTER TABLE `arma`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `idx_arma_nombre` (`Nombre`),
  ADD KEY `idx_arma_origen` (`OrigenImperioID`);

--
-- Indices de la tabla `armadura`
--
ALTER TABLE `armadura`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `idx_armadura_nombre` (`Nombre`),
  ADD KEY `idx_armadura_origen` (`OrigenImperioID`);

--
-- Indices de la tabla `bioma`
--
ALTER TABLE `bioma`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `ContinenteID` (`ContinenteID`),
  ADD KEY `idx_bioma_nombre` (`Nombre`);

--
-- Indices de la tabla `bioma_continente`
--
ALTER TABLE `bioma_continente`
  ADD PRIMARY KEY (`BiomaID`,`ContinenteID`),
  ADD KEY `ContinenteID` (`ContinenteID`);

--
-- Indices de la tabla `ciudad`
--
ALTER TABLE `ciudad`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `ImperioID` (`ImperioID`),
  ADD KEY `BiomaPredominanteID` (`BiomaPredominanteID`),
  ADD KEY `idx_ciudad_nombre` (`Nombre`);

--
-- Indices de la tabla `continente`
--
ALTER TABLE `continente`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `PlanetaID` (`PlanetaID`),
  ADD KEY `idx_continente_nombre` (`Nombre`);

--
-- Indices de la tabla `estadisticas`
--
ALTER TABLE `estadisticas`
  ADD PRIMARY KEY (`ID`);

--
-- Indices de la tabla `guerra`
--
ALTER TABLE `guerra`
  ADD PRIMARY KEY (`ID`);

--
-- Indices de la tabla `herramienta`
--
ALTER TABLE `herramienta`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `idx_herramienta_nombre` (`Nombre`),
  ADD KEY `idx_herramienta_origen` (`OrigenImperioID`);

--
-- Indices de la tabla `imperio`
--
ALTER TABLE `imperio`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `idx_imperio_nombre` (`Nombre`);

--
-- Indices de la tabla `imperio_guerra`
--
ALTER TABLE `imperio_guerra`
  ADD PRIMARY KEY (`ImperioID`,`GuerraID`),
  ADD KEY `GuerraID` (`GuerraID`);

--
-- Indices de la tabla `persona`
--
ALTER TABLE `persona`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `RazaID` (`RazaID`),
  ADD KEY `ImperioID` (`ImperioID`),
  ADD KEY `EstadisticasID` (`EstadisticasID`),
  ADD KEY `idx_persona_nombre` (`Nombre`,`Apellido`);

--
-- Indices de la tabla `persona_arcana`
--
ALTER TABLE `persona_arcana`
  ADD PRIMARY KEY (`PersonaID`,`ArcanaID`),
  ADD KEY `ArcanaID` (`ArcanaID`);

--
-- Indices de la tabla `persona_arma`
--
ALTER TABLE `persona_arma`
  ADD PRIMARY KEY (`PersonaID`,`ArmaID`),
  ADD KEY `ArmaID` (`ArmaID`);

--
-- Indices de la tabla `persona_armadura`
--
ALTER TABLE `persona_armadura`
  ADD PRIMARY KEY (`PersonaID`,`ArmaduraID`),
  ADD KEY `ArmaduraID` (`ArmaduraID`);

--
-- Indices de la tabla `persona_herramienta`
--
ALTER TABLE `persona_herramienta`
  ADD PRIMARY KEY (`PersonaID`,`HerramientaID`),
  ADD KEY `HerramientaID` (`HerramientaID`);

--
-- Indices de la tabla `planeta`
--
ALTER TABLE `planeta`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `idx_planeta_nombre` (`Nombre`);

--
-- Indices de la tabla `raza`
--
ALTER TABLE `raza`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `EstadisticasBaseID` (`EstadisticasBaseID`),
  ADD KEY `idx_raza_nombre` (`Nombre`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `arcana`
--
ALTER TABLE `arcana`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `arma`
--
ALTER TABLE `arma`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `armadura`
--
ALTER TABLE `armadura`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `bioma`
--
ALTER TABLE `bioma`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `ciudad`
--
ALTER TABLE `ciudad`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `continente`
--
ALTER TABLE `continente`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `estadisticas`
--
ALTER TABLE `estadisticas`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `guerra`
--
ALTER TABLE `guerra`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `herramienta`
--
ALTER TABLE `herramienta`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `imperio`
--
ALTER TABLE `imperio`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `persona`
--
ALTER TABLE `persona`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `planeta`
--
ALTER TABLE `planeta`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `raza`
--
ALTER TABLE `raza`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `arma`
--
ALTER TABLE `arma`
  ADD CONSTRAINT `arma_ibfk_1` FOREIGN KEY (`OrigenImperioID`) REFERENCES `imperio` (`ID`) ON DELETE SET NULL;

--
-- Filtros para la tabla `armadura`
--
ALTER TABLE `armadura`
  ADD CONSTRAINT `armadura_ibfk_1` FOREIGN KEY (`OrigenImperioID`) REFERENCES `imperio` (`ID`) ON DELETE SET NULL;

--
-- Filtros para la tabla `bioma`
--
ALTER TABLE `bioma`
  ADD CONSTRAINT `bioma_ibfk_1` FOREIGN KEY (`ContinenteID`) REFERENCES `continente` (`ID`) ON DELETE SET NULL;

--
-- Filtros para la tabla `bioma_continente`
--
ALTER TABLE `bioma_continente`
  ADD CONSTRAINT `bioma_continente_ibfk_1` FOREIGN KEY (`BiomaID`) REFERENCES `bioma` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `bioma_continente_ibfk_2` FOREIGN KEY (`ContinenteID`) REFERENCES `continente` (`ID`) ON DELETE CASCADE;

--
-- Filtros para la tabla `ciudad`
--
ALTER TABLE `ciudad`
  ADD CONSTRAINT `ciudad_ibfk_1` FOREIGN KEY (`ImperioID`) REFERENCES `imperio` (`ID`) ON DELETE SET NULL,
  ADD CONSTRAINT `ciudad_ibfk_2` FOREIGN KEY (`BiomaPredominanteID`) REFERENCES `bioma` (`ID`) ON DELETE SET NULL;

--
-- Filtros para la tabla `continente`
--
ALTER TABLE `continente`
  ADD CONSTRAINT `continente_ibfk_1` FOREIGN KEY (`PlanetaID`) REFERENCES `planeta` (`ID`) ON DELETE CASCADE;

--
-- Filtros para la tabla `herramienta`
--
ALTER TABLE `herramienta`
  ADD CONSTRAINT `herramienta_ibfk_1` FOREIGN KEY (`OrigenImperioID`) REFERENCES `imperio` (`ID`) ON DELETE SET NULL;

--
-- Filtros para la tabla `imperio_guerra`
--
ALTER TABLE `imperio_guerra`
  ADD CONSTRAINT `imperio_guerra_ibfk_1` FOREIGN KEY (`ImperioID`) REFERENCES `imperio` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `imperio_guerra_ibfk_2` FOREIGN KEY (`GuerraID`) REFERENCES `guerra` (`ID`) ON DELETE CASCADE;

--
-- Filtros para la tabla `persona`
--
ALTER TABLE `persona`
  ADD CONSTRAINT `persona_ibfk_1` FOREIGN KEY (`RazaID`) REFERENCES `raza` (`ID`) ON DELETE SET NULL,
  ADD CONSTRAINT `persona_ibfk_2` FOREIGN KEY (`ImperioID`) REFERENCES `imperio` (`ID`) ON DELETE SET NULL,
  ADD CONSTRAINT `persona_ibfk_3` FOREIGN KEY (`EstadisticasID`) REFERENCES `estadisticas` (`ID`) ON DELETE SET NULL;

--
-- Filtros para la tabla `persona_arcana`
--
ALTER TABLE `persona_arcana`
  ADD CONSTRAINT `persona_arcana_ibfk_1` FOREIGN KEY (`PersonaID`) REFERENCES `persona` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `persona_arcana_ibfk_2` FOREIGN KEY (`ArcanaID`) REFERENCES `arcana` (`ID`) ON DELETE CASCADE;

--
-- Filtros para la tabla `persona_arma`
--
ALTER TABLE `persona_arma`
  ADD CONSTRAINT `persona_arma_ibfk_1` FOREIGN KEY (`PersonaID`) REFERENCES `persona` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `persona_arma_ibfk_2` FOREIGN KEY (`ArmaID`) REFERENCES `arma` (`ID`) ON DELETE CASCADE;

--
-- Filtros para la tabla `persona_armadura`
--
ALTER TABLE `persona_armadura`
  ADD CONSTRAINT `persona_armadura_ibfk_1` FOREIGN KEY (`PersonaID`) REFERENCES `persona` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `persona_armadura_ibfk_2` FOREIGN KEY (`ArmaduraID`) REFERENCES `armadura` (`ID`) ON DELETE CASCADE;

--
-- Filtros para la tabla `persona_herramienta`
--
ALTER TABLE `persona_herramienta`
  ADD CONSTRAINT `persona_herramienta_ibfk_1` FOREIGN KEY (`PersonaID`) REFERENCES `persona` (`ID`) ON DELETE CASCADE,
  ADD CONSTRAINT `persona_herramienta_ibfk_2` FOREIGN KEY (`HerramientaID`) REFERENCES `herramienta` (`ID`) ON DELETE CASCADE;

--
-- Filtros para la tabla `raza`
--
ALTER TABLE `raza`
  ADD CONSTRAINT `raza_ibfk_1` FOREIGN KEY (`EstadisticasBaseID`) REFERENCES `estadisticas` (`ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
