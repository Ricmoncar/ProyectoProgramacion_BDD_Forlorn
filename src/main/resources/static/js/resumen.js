document.addEventListener("DOMContentLoaded", function () {
  // WIDGETS:
  cargarEstadisticasGlobales();
  cargarActividadReciente();
});

function cargarEstadisticasGlobales() {
  const listaStatsEl = document.getElementById("listaEstadisticasGlobales");
  if (!listaStatsEl) {
    console.warn(
      "Elemento 'listaEstadisticasGlobales' no encontrado en el DOM."
    );
    return;
  }

  fetch("/universo/estadisticas_globales")
    .then((response) => {
      if (!response.ok) {
        // Intenta obtener más detalles del error si es posible
        return response.text().then((text) => {
          throw new Error(
            `Error HTTP ${response.status} (${response.statusText}) al cargar estadísticas globales. Detalle: ${text}`
          );
        });
      }
      return response.json();
    })
    .then((stats) => {
      listaStatsEl.innerHTML = `
                <li>Planetas Descubiertos: <strong>${
                  stats.totalPlanetas !== undefined
                    ? stats.totalPlanetas
                    : "N/A"
                }</strong></li>
                <li>Continentes Cartografiados: <strong>${
                  stats.totalContinentes !== undefined
                    ? stats.totalContinentes
                    : "N/A"
                }</strong></li>
                <li>Imperios Establecidos: <strong>${
                  stats.totalImperios !== undefined
                    ? stats.totalImperios
                    : "N/A"
                }</strong></li>
                <li>Razas Conocidas: <strong>${
                  stats.totalRazas !== undefined ? stats.totalRazas : "N/A"
                }</strong></li>
                <li>Conflictos Activos: <strong>${
                  stats.guerrasActivas !== undefined
                    ? stats.guerrasActivas
                    : "N/A"
                }</strong></li>
                <li>Población Total Estimada en Imperios: <strong>${
                  stats.poblacionTotalImperios !== undefined &&
                  stats.poblacionTotalImperios !== null
                    ? stats.poblacionTotalImperios.toLocaleString("es-ES")
                    : "N/A"
                }</strong></li>
            `;
    })
    .catch((error) => {
      console.error("Error al cargar estadísticas globales:", error);
      listaStatsEl.innerHTML = `<li>Error al cargar estadísticas: ${error.message}</li>`;
    });
}

// Cargar y mostrar la actividad reciente
function cargarActividadReciente() {
  const limiteItems = 3; // Número de ítems recientes a mostrar por categoría

  function renderizarLista(
    ulId,
    data,
    formateadorItem,
    mensajeVacio,
    mensajeError
  ) {
    const ul = document.getElementById(ulId);
    if (!ul) {
      console.warn(`Elemento '${ulId}' no encontrado en el DOM.`);
      return;
    }
    ul.innerHTML = ""; // Limpiar contenido anterior (como "Cargando...")
    if (data && data.length > 0) {
      data.forEach((item) => {
        ul.innerHTML += formateadorItem(item);
      });
    } else {
      ul.innerHTML = `<li>${mensajeVacio}</li>`;
    }
  }

  // Cargar Planetas Recientes
  fetch(`/universo/planetas_recientes?limite=${limiteItems}`)
    .then((res) =>
      res.ok
        ? res.json()
        : Promise.reject(new Error(`Error ${res.status} en planetas_recientes`))
    )
    .then((data) => {
      renderizarLista(
        "listaPlanetasRecientes",
        data,
        (p) => {
          const fecha = p.fechaCreacion
            ? new Date(p.fechaCreacion).toLocaleDateString("es-ES")
            : "Fecha N/A";
          const habitable = p.habitable ? "Sí" : "No";
          return `<li><a href="planet.html#${p.id || ""}" title="Ver ${
            p.nombre
          }">${
            p.nombre || "Planeta sin nombre"
          }</a> - ${fecha} (Habitable: ${habitable})</li>`;
        },
        "No hay planetas recientes.",
        "Error al cargar planetas."
      );
    })
    .catch((error) => {
      console.error("Error fetching planetas recientes:", error);
      const ul = document.getElementById("listaPlanetasRecientes");
      if (ul) ul.innerHTML = `<li>Error al cargar planetas.</li>`;
    });

  // Cargar Imperios Recientes
  fetch(`/universo/imperios_recientes?limite=${limiteItems}`)
    .then((res) =>
      res.ok
        ? res.json()
        : Promise.reject(new Error(`Error ${res.status} en imperios_recientes`))
    )
    .then((data) => {
      renderizarLista(
        "listaImperiosRecientes",
        data,
        (i) => {
          const fecha = i.fechaCreacion
            ? new Date(i.fechaCreacion).toLocaleDateString("es-ES")
            : "Fecha N/A";
          return `<li><a href="empire.html#${i.id || ""}" title="Ver ${
            i.nombre
          }">${i.nombre || "Imperio sin nombre"}</a> (Líder: ${
            i.lider || "N/A"
          }) - ${fecha}</li>`;
        },
        "No hay imperios recientes.",
        "Error al cargar imperios."
      );
    })
    .catch((error) => {
      console.error("Error fetching imperios recientes:", error);
      const ul = document.getElementById("listaImperiosRecientes");
      if (ul) ul.innerHTML = `<li>Error al cargar imperios.</li>`;
    });

  // Cargar Personas Recientes
  fetch(`/universo/personas_recientes?limite=${limiteItems}`)
    .then((res) =>
      res.ok
        ? res.json()
        : Promise.reject(new Error(`Error ${res.status} en personas_recientes`))
    )
    .then((data) => {
      renderizarLista(
        "listaPersonasRecientes",
        data,
        (p) => {
          const nombreCompleto =
            `${p.nombre || ""} ${p.apellido || ""}`.trim() ||
            "Persona sin nombre";
          const raza = p.raza && p.raza.nombre ? p.raza.nombre : "Raza Desc.";
          const nivel =
            p.estadisticas && p.estadisticas.lvl !== undefined
              ? p.estadisticas.lvl
              : 1;
          return `<li><a href="person.html#${
            p.id || ""
          }" title="Ver ${nombreCompleto}">${nombreCompleto}</a> (${raza}) - Prof: ${
            p.profesion || "N/A"
          }, Nvl: ${nivel}</li>`;
        },
        "No hay personas recientes.",
        "Error al cargar personas."
      );
    })
    .catch((error) => {
      console.error("Error fetching personas recientes:", error);
      const ul = document.getElementById("listaPersonasRecientes");
      if (ul) ul.innerHTML = `<li>Error al cargar personas.</li>`;
    });

  // Cargar Guerras Recientes
  fetch(`/universo/guerras_recientes?limite=${limiteItems}`)
    .then((res) =>
      res.ok
        ? res.json()
        : Promise.reject(new Error(`Error ${res.status} en guerras_recientes`))
    )
    .then((data) => {
      renderizarLista(
        "listaGuerrasRecientes",
        data,
        (g) => {
          const fecha = g.fechaInicio
            ? new Date(g.fechaInicio).toLocaleDateString("es-ES")
            : "Fecha N/A";
          let participantesStr = "Participantes: N/A";
          if (g.imperiosParticipantes && g.imperiosParticipantes.length > 0) {
            participantesStr = g.imperiosParticipantes
              .slice(0, 2)
              .map((participante) => participante.imperioNombre || "Desc.")
              .join(", ");
            if (g.imperiosParticipantes.length > 2) {
              participantesStr += ` y ${
                g.imperiosParticipantes.length - 2
              } más`;
            }
          }
          return `<li><a href="war.html#${g.id || ""}" title="Ver ${
            g.nombre
          }">${
            g.nombre || "Guerra sin nombre"
          }</a> - Iniciada: ${fecha} (${participantesStr})</li>`;
        },
        "No hay guerras recientes.",
        "Error al cargar guerras."
      );
    })
    .catch((error) => {
      console.error("Error fetching guerras recientes:", error);
      const ul = document.getElementById("listaGuerrasRecientes");
      if (ul) ul.innerHTML = `<li>Error al cargar guerras.</li>`;
    });
}
