-- SCRIPT PARA INSERTAR LOS DATOS DE EJEMPLO EN LA BASE DE DATOS
-- INSERTAR ESTO EN APARTADO "SQL" AL CREAR LA BASE DE DATOS


-- Desactivar temporalmente la verificación de claves foráneas
SET FOREIGN_KEY_CHECKS=0;

-- Borrar datos de las tablas
-- Usaremos DELETE FROM para las tablas con restricciones directas que causan problemas con TRUNCATE
-- y mantendremos TRUNCATE para las demás.

-- Tablas de unión y tablas que referencian a otras con ON DELETE CASCADE (o SET NULL que podría causar problemas si el orden no es perfecto con TRUNCATE)
DELETE FROM `persona_arcana`;
DELETE FROM `persona_arma`;
DELETE FROM `persona_armadura`;
DELETE FROM `persona_herramienta`;
DELETE FROM `imperio_guerra`;
DELETE FROM `ciudad`;             -- Referencia Imperio, Bioma
DELETE FROM `bioma_continente`;   -- ESTA ES CLAVE: Referencia Bioma y Continente. BORRAR PRIMERO.

-- Tablas que son referenciadas por las de arriba y pueden tener restricciones
DELETE FROM `bioma`;              -- Referenciada por bioma_continente y ciudad
DELETE FROM `continente`;         -- Referenciada por bioma y bioma_continente
DELETE FROM `persona`;            -- Referenciada por persona_X
DELETE FROM `arma`;               -- Referenciada por persona_arma
DELETE FROM `armadura`;           -- Referenciada por persona_armadura
DELETE FROM `herramienta`;        -- Referenciada por persona_herramienta
DELETE FROM `arcana`;             -- Referenciada por persona_arcana
DELETE FROM `guerra`;             -- Referenciada por imperio_guerra
DELETE FROM `raza`;               -- Referenciada por persona
DELETE FROM `imperio`;            -- Referenciada por persona, ciudad, arma, armadura, herramienta, imperio_guerra
DELETE FROM `planeta`;            -- Referenciada por continente
DELETE FROM `estadisticas`;       -- Referenciada por persona, raza

-- Reactivar la verificación de claves foráneas
SET FOREIGN_KEY_CHECKS=1;

-- Insertar datos de ejemplo (COPIA Y PEGA AQUÍ TODOS LOS INSERTS QUE TE DI ANTERIORMENTE)


-- Insertar datos de ejemplo

-- ESTADISTICAS (Algunas base para razas, otras personales, otras para buffs)
INSERT INTO `estadisticas` (`ID`, `Tipo`, `ATK`, `DEF`, `HP`, `SPE`, `MAT`, `MDF`, `XP`, `LVL`) VALUES
(1, 'Base', 10, 10, 100, 5, 8, 7, 0, 1),   -- Para Raza Humana
(2, 'Base', 12, 8, 80, 7, 10, 5, 0, 1),    -- Para Raza Elfa
(3, 'Base', 15, 15, 120, 3, 3, 10, 0, 1),  -- Para Raza Enana
(4, 'Base', 8, 12, 90, 6, 12, 12, 0, 1),   -- Para Raza Gnomo
(5, 'Personal', 25, 20, 150, 10, 15, 12, 500, 5), -- Para Persona 1
(6, 'Personal', 18, 15, 100, 12, 20, 18, 200, 3), -- Para Persona 2
(7, 'Personal', 30, 30, 200, 8, 10, 25, 1200, 8), -- Para Persona 3
(8, 'BuffArma', 5, 0, 0, 1, 0, 0, 0, 1),          -- Buff para Espada Común
(9, 'BuffArmadura', 0, 10, 20, -1, 0, 5, 0, 1),   -- Buff para Armadura de Placas
(10, 'BuffHerramienta', 0, 0, 0, 0, 0, 0, 0, 1),  -- Buff para Pico
(11, 'BuffArcana', 0, 0, 0, 0, 10, 0, 0, 1);      -- Buff para Bola de Fuego (implícito)

-- PLANETAS
INSERT INTO `planeta` (`ID`, `Nombre`, `Ubicacion`, `Habitable`, `NivelAgua`, `FechaCreacion`, `Tamanio`, `Densidad`, `Descripcion`) VALUES
(1, 'Terra Nova', 'Sistema Solara, Brazo de Orión', 1, 0.7, '2240-01-15', 1.0, 1.0, 'Planeta similar a la Tierra, cuna de la humanidad expandida.'),
(2, 'Aethelgard', 'Nebulosa del Grifo', 1, 0.4, '2105-07-22', 1.3, 1.1, 'Mundo antiguo con vastos bosques y montañas elevadas, hogar de los elfos.'),
(3, 'Khaz Modan Prime', 'Cúmulo de Baruk', 1, 0.1, '1988-11-03', 0.8, 1.5, 'Planeta rocoso y rico en minerales, fortaleza de los enanos.'),
(4, 'Gnomeregan II', 'Sistema Tinkerton', 0, 0.05, '2301-05-10', 0.5, 0.9, 'Mundo tecnológicamente avanzado, aunque con problemas ambientales, hogar de los gnomos.');

-- IMPERIOS
INSERT INTO `imperio` (`ID`, `Nombre`, `Poblacion`, `Descripcion`, `FechaCreacion`, `Lider`, `Ideologia`, `GDP`, `Tamanio`, `EnGuerra`) VALUES
(1, 'Imperio Solar Unificado', 15000000000, 'La principal facción humana, abarcando docenas de sistemas.', '2280-03-10', 'Emperador Valerius III', 'Monarquía Constitucional', 75000.0, 120.0, 0),
(2, 'Alto Reino Élfico', 2000000000, 'Una antigua y sabia civilización élfica que valora la naturaleza y la magia.', '1500-01-01', 'Reina Elara Lunarwind', 'Monarquía Electiva', 30000.0, 45.0, 0),
(3, 'Consorcio Minero Enano', 5000000000, 'Una federación de clanes enanos, maestros artesanos y guerreros.', '1850-06-20', 'Gran Rey Thorin Stonebeard', 'Confederación de Clanes', 50000.0, 30.0, 1),
(4, 'Tecnocracia Gnoma', 1000000000, 'Sociedad impulsada por la invención y el descubrimiento científico.', '2310-10-01', 'Archimandrita Fizwidget', 'Tecnocracia', 25000.0, 15.0, 0);

-- RAZAS
INSERT INTO `raza` (`ID`, `Nombre`, `DescripcionFisica`, `FechaConcepcion`, `AlturaPromedia`, `AnchoPromedio`, `EstadisticasBaseID`) VALUES
(1, 'Humano', 'Versátiles y adaptables, con gran diversidad física.', '2230-01-01', 1.75, 0.7, 1),
(2, 'Elfo del Bosque', 'Altos, esbeltos, con orejas puntiagudas y gran agilidad. Ojos suelen ser verdes o azules.', '0500-01-01', 1.9, 0.6, 2),
(3, 'Enano de Montaña', 'Robustos, fuertes y barbudos. Altura promedio baja pero complexión ancha.', '0200-01-01', 1.3, 0.8, 3),
(4, 'Gnomo Inventor', 'Pequeños, con rasgos faciales curiosos y a menudo con gafas o herramientas.', '2290-01-01', 1.0, 0.5, 4);

-- CONTINENTES
INSERT INTO `continente` (`ID`, `Nombre`, `Hemisferio`, `Tamanio`, `Clima`, `Descripcion`, `Habitable`, `PlanetaID`) VALUES
(1, 'Elysium', 'Norte', 15000000, 'Templado', 'Continente principal de Terra Nova, densamente poblado.', 1, 1),
(2, 'Arcadia', 'Sur', 12000000, 'Tropical', 'Selvas exuberantes y costas cálidas en Terra Nova.', 1, 1),
(3, 'Sylvandell', 'Ecuadorial', 25000000, 'Bosque Templado Mágico', 'El corazón del Alto Reino Élfico en Aethelgard.', 1, 2),
(4, 'Grimstone', 'Norte', 10000000, 'Montañoso Frío', 'Región minera principal de Khaz Modan Prime.', 1, 3),
(5, 'Tinker Town Mayor', 'Norte', 500000, 'Controlado Artificialmente', 'La ciudad-continente principal de Gnomeregan II.', 1, 4);

-- BIOMAS
INSERT INTO `bioma` (`ID`, `Nombre`, `Clima`, `PorcentajeHumedad`, `Precipitaciones`, `TemperaturaMedia`, `ContinenteID`) VALUES
(1, 'Bosque Esmeralda', 'Templado Lluvioso', 75, 'Moderadas', 15, 1),
(2, 'Jungla Ecuatorial Zafiro', 'Tropical Húmedo', 90, 'Altas', 28, 2),
(3, 'Arboleda Ancestral', 'Templado Mágico', 60, 'Moderadas', 18, 3),
(4, 'Picos de Hierro Frío', 'Subártico Montañoso', 30, 'Bajas (nieve)', -5, 4),
(5, 'Distrito de Innovación', 'Clima Controlado', 50, 'Artificiales', 22, 5),
(6, 'Llanuras Verdes de Elysium', 'Templado', 65, 'Moderadas', 16, 1);

-- CIUDADES (Algunas)
INSERT INTO `ciudad` (`ID`, `Nombre`, `Poblacion`, `Descripcion`, `ImperioID`, `BiomaPredominanteID`) VALUES
(1, 'Nova Roma', 50000000, 'Capital del Imperio Solar Unificado.', 1, 1),
(2, 'Silvermoon City', 2000000, 'Capital de los elfos en Sylvandell.', 2, 3),
(3, 'Ironforge', 10000000, 'Principal ciudadela enana en Grimstone.', 3, 4),
(4, 'Cogsworth', 500000, 'Centro de invención en Tinker Town Mayor.', 4, 5);

-- PERSONAS
INSERT INTO `persona` (`ID`, `Nombre`, `Apellido`, `Ancho`, `Alto`, `DescripcionFisica`, `PorcentajeGrasaCorporal`, `Personalidad`, `Oro`, `FechaNacimiento`, `Profesion`, `Direccion`, `RazaID`, `ImperioID`, `EstadisticasID`) VALUES
(1, 'Marcus', 'Valerius', 0.8, 1.82, 'Cabello oscuro, ojos azules, constitución atlética.', 15, 'Valiente y honorable', 1500, '2305-08-12', 'Comandante Estelar', 'Ciudadela Imperial, Nova Roma', 1, 1, 5),
(2, 'Lyra', 'Silvanesti', 0.6, 1.90, 'Cabello plateado largo, ojos esmeralda, elegante.', 12, 'Sabia y reservada', 800, '1850-03-20', 'Archimaga', 'Torre de Marfil, Silvermoon City', 2, 2, 6),
(3, 'Borin', 'Ironhand', 0.9, 1.35, 'Barba roja trenzada, fuerte y corpulento.', 25, 'Testarudo pero leal', 50000, '2100-11-05', 'Maestro Herrero', 'La Gran Forja, Ironforge', 3, 3, 7),
(4, 'Fizz', 'Gearloose', 0.4, 0.95, 'Pelo revuelto color naranja, gafas de inventor.', 18, 'Curiosa y excéntrica', 1200, '2325-02-28', 'Inventora Jefe', 'Laboratorio Central, Cogsworth', 4, 4, NULL); -- Sin estadísticas personales por ahora

-- ARMAS
INSERT INTO `arma` (`ID`, `Nombre`, `BufoEstadisticas`, `Material`, `Descripcion`, `Peso`, `PVP`, `Origen`, `FechaCreacion`, `OrigenImperioID`) VALUES
(3, 'Espada Larga Común', '+5 ATK, +1 SPE', 'Acero', 'Una espada fiable para cualquier aventurero.', 2.5, 50, NULL, '2340-01-01', 1),
(4, 'Arco Élfico de Sylvandell', '+3 ATK, +3 SPE', 'Madera de Serbal Encantada', 'Ligero y preciso, imbuido con magia natural.', 1.5, 250, NULL, '2200-05-10', 2),
(5, 'Martillo de Guerra Enano', '+10 ATK, -1 SPE', 'Acero de Grimstone', 'Pesado y devastador, requiere gran fuerza.', 15.0, 180, NULL, '2050-10-10', 3),
(6, 'Blaster Gnomo Modelo X', '+7 MAT', 'Tecnoaleación Ligera', 'Dispara rayos de energía concentrada.', 1.0, 300, NULL, '2348-01-01', 4);

-- ARMADURAS
INSERT INTO `armadura` (`ID`, `Nombre`, `BufoEstadisticas`, `Material`, `Descripcion`, `Peso`, `PVP`, `Origen`, `FechaCreacion`, `OrigenImperioID`) VALUES
(2, 'Armadura de Placas Imperial', '+10 DEF, +20 HP, -1 SPE', 'Duracero', 'Protección estándar de los legionarios solares.', 25.0, 400, NULL, '2335-01-01', 1),
(3, 'Túnica de Archimago Élfica', '+5 MDF, +5 MAT, +10 HP', 'Seda Estelar Encantada', 'Ofrece protección mágica y potencia los hechizos.', 2.0, 600, NULL, '1900-01-01', 2),
(4, 'Cota de Mallas Rúnica Enana', '+12 DEF, +5 MDF', 'Hierro Rúnico', 'Resistente y grabada con runas protectoras.', 18.0, 350, NULL, '2000-01-01', 3);

-- HERRAMIENTAS
INSERT INTO `herramienta` (`ID`, `Nombre`, `BufoEstadisticas`, `Material`, `Descripcion`, `Peso`, `PVP`, `Uso`, `Origen`, `FechaCreacion`, `OrigenImperioID`) VALUES
(2, 'Pico de Minero Enano', NULL, 'Acero Reforzado', 'Herramienta esencial para la extracción de minerales.', 5.0, 20, 'Minería', NULL, '1900-01-01', 3),
(3, 'Kit de Herramientas Gnomo', NULL, 'Multi-aleación', 'Contiene todo lo necesario para reparaciones e invenciones rápidas.', 1.5, 75, 'Ingeniería', NULL, '2345-01-01', 4),
(4, 'Medikit de Campo', NULL, 'Plástico Esterilizado', 'Para primeros auxilios en combate.', 0.5, 30, 'Medicina', NULL, '2330-01-01', 1);

-- ARCANAS
INSERT INTO `arcana` (`ID`, `Tipo`, `Maestria`, `Dificultad`, `Fecha`) VALUES
(3, 'Bola de Fuego', 'Aprendiz', 'Fácil', '2340-01-01'),
(4, 'Curación Menor', 'Novato', 'Fácil', '2330-01-01'),
(5, 'Rayo Gélido', 'Competente', 'Intermedio', '2250-01-01'),
(6, 'Telequinesis', 'Maestro', 'Difícil', '2000-01-01');

-- GUERRAS
INSERT INTO `guerra` (`ID`, `Nombre`, `FechaInicio`, `FechaFin`, `Descripcion`) VALUES
(1, 'La Gran Guerra de la Secesión Minera', '2348-05-10', NULL, 'Conflicto entre el Consorcio Enano y facciones mineras rebeldes.'),
(2, 'Guerra de los Portales Antiguos', '2150-01-01', '2155-12-31', 'Conflicto entre el Alto Reino Élfico y una incursión de otra dimensión.'),
(3, 'Primera Guerra de Contacto Humano-Xylar', '2301-07-19', '2303-11-08', 'Primer conflicto a gran escala del Imperio Solar Unificado contra una especie alienígena hostil.');

-- ASIGNACIONES (Tablas de Unión)

-- PERSONA_ARMA (PersonaID, ArmaID, Equipada)
INSERT INTO `persona_arma` (`PersonaID`, `ArmaID`, `Equipada`) VALUES
(1, 3, 1), -- Marcus con Espada Larga Común, equipada
(2, 4, 1), -- Lyra con Arco Élfico, equipado
(3, 5, 1); -- Borin con Martillo de Guerra, equipado

-- PERSONA_ARMADURA (PersonaID, ArmaduraID, Equipada)
INSERT INTO `persona_armadura` (`PersonaID`, `ArmaduraID`, `Equipada`) VALUES
(1, 2, 1), -- Marcus con Armadura de Placas Imperial, equipada
(2, 3, 1), -- Lyra con Túnica de Archimago, equipada
(3, 4, 1); -- Borin con Cota de Mallas Rúnica, equipada

-- PERSONA_HERRAMIENTA (PersonaID, HerramientaID, Equipada)
INSERT INTO `persona_herramienta` (`PersonaID`, `HerramientaID`, `Equipada`) VALUES
(3, 2, 1), -- Borin con Pico de Minero, equipado
(4, 3, 1), -- Fizz con Kit de Herramientas Gnomo, equipado
(1, 4, 0); -- Marcus con Medikit, no equipado

-- PERSONA_ARCANA (PersonaID, ArcanaID, Maestria)
INSERT INTO `persona_arcana` (`PersonaID`, `ArcanaID`, `Maestria`) VALUES
(2, 3, 'Maestro'),      -- Lyra es Maestra en Bola de Fuego
(2, 5, 'Experto'),      -- Lyra es Experta en Rayo Gélido
(1, 4, 'Aprendiz');     -- Marcus sabe Curación Menor

-- IMPERIO_GUERRA (ImperioID, GuerraID, Rol, Ganador)
INSERT INTO `imperio_guerra` (`ImperioID`, `GuerraID`, `Rol`, `Ganador`) VALUES
(3, 1, 'Principal Beligerante', NULL), -- Consorcio Enano en la Guerra de Secesión Minera (activa)
(2, 2, 'Defensor Principal', 1),      -- Alto Reino Élfico ganó la Guerra de los Portales
(1, 3, 'Principal Beligerante', 1);   -- Imperio Solar ganó la Guerra Humano-Xylar

-- BIOMA_CONTINENTE (BiomaID, ContinenteID, AreaOcupada)
INSERT INTO `bioma_continente` (`BiomaID`, `ContinenteID`, `AreaOcupada`) VALUES
(1, 1, 5000000),
(6, 1, 10000000),
(2, 2, 12000000),
(3, 3, 25000000),
(4, 4, 10000000),
(5, 5, 500000);

COMMIT;