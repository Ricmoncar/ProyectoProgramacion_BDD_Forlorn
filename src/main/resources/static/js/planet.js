function mostrarAlerta(tipo, mensaje) {
  const alertContainer = document.getElementById("alertContainer");
  if (!alertContainer) {
    console.error(
      "Contenedor de alertas no encontrado. Mostrando alerta nativa."
    );
    alert(`${tipo.toUpperCase()}: ${mensaje}`);
    return;
  }

  const alertDiv = document.createElement("div");
  alertDiv.className = `custom-alert alert-${tipo}`; // Usa una clase base para estilos comunes

  // Puedes definir colores en CSS para .alert-success, .alert-error, etc.
  // Ejemplo: alertDiv.style.backgroundColor = tipo === 'success' ? 'lightgreen' : 'lightcoral';

  alertDiv.innerHTML = `
        <span>${mensaje}</span>
        <button class="close-alert-btn">×</button>
    `;

  alertContainer.appendChild(alertDiv);

  const closeBtn = alertDiv.querySelector(".close-alert-btn");
  if (closeBtn) {
    closeBtn.addEventListener("click", function () {
      alertDiv.remove();
    });
  }

  setTimeout(() => {
    if (alertDiv.parentNode) {
      alertDiv.remove();
    }
  }, 5000);
}

document.addEventListener("DOMContentLoaded", function () {
  /* Inicialización de DataTables */
  let planetsTable = $("#planetsTable").DataTable({
    language: {
      url: "https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json",
    },
    responsive: true,
    columns: [
      { data: "id" },
      { data: "nombre" },
      { data: "ubicacion" },
      {
        data: "habitable",
        render: function (data, type, row) {
          // Suponiendo que el backend devuelve true/false o 1/0 para habitable
          return data === true || data === 1 ? "Sí" : "No";
        },
      },
      {
        data: "nivelAgua",
        render: function (data) {
          return data !== null ? `${data}%` : "N/A";
        },
      },
      {
        data: "tamanio",
        render: function (data) {
          return data !== null ? `${data} km` : "N/A";
        },
      },
      {
        data: null,
        orderable: false,
        searchable: false,
        render: function (data, type, row) {
          return `<div class="planet-actions">
                        <button class="view-btn" title="Ver detalles" onclick="verPlaneta(${row.id})"><i class="fas fa-eye"></i></button>
                        <button class="edit-btn" title="Editar" onclick="editarPlaneta(${row.id})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" title="Eliminar" onclick="eliminarPlaneta(${row.id})"><i class="fas fa-trash-alt"></i></button>
                    </div>`;
        },
        className: "dt-center",
      },
    ],
    createdRow: function (row, data, dataIndex) {
      $(row).css(
        "background-color",
        dataIndex % 2 === 0 ? "rgba(30, 30, 30, 0.8)" : "var(--dark-secondary)"
      );
    },
  });

  /* Carga inicial de planetas */
  cargarPlanetas();

  /* Configuración de eventos para modales */
  document
    .getElementById("addPlanetBtn")
    .addEventListener("click", abrirModalAnadir);
  document.getElementById("cancelBtn").addEventListener("click", cerrarModal); // Para el modal de añadir/editar
  document
    .getElementById("closeViewBtn")
    .addEventListener("click", cerrarModalDetalles); // Para el modal de ver

  /* Configuración de eventos para cerrar modales con la clase .close-modal */
  document.querySelectorAll(".close-modal").forEach(function (btn) {
    btn.addEventListener("click", function () {
      // Cierra ambos modales por si acaso
      cerrarModal();
      cerrarModalDetalles();
    });
  });

  /* Configuración del formulario */
  document
    .getElementById("planetForm")
    .addEventListener("submit", function (e) {
      e.preventDefault();
      guardarPlaneta();
    });

  /* Cerrar modal al hacer clic fuera del contenido */
  window.addEventListener("click", function (event) {
    const planetModal = document.getElementById("planetModal");
    const viewPlanetModal = document.getElementById("viewPlanetModal");
    if (event.target == planetModal) {
      cerrarModal();
    }
    if (event.target == viewPlanetModal) {
      cerrarModalDetalles();
    }
  });

  fixTableDisplay();
});

function fixTableDisplay() {
  setTimeout(function () {
    $("table.dataTable tbody td").css("background-color", "inherit");
    $("table.dataTable tbody tr:odd").css(
      "background-color",
      "rgba(30, 30, 30, 0.8)"
    );
    $("table.dataTable tbody tr:even").css(
      "background-color",
      "var(--dark-secondary)"
    );

    $(".dataTable").on("draw.dt", function () {
      $(this)
        .find("tbody tr:odd")
        .css("background-color", "rgba(30, 30, 30, 0.8)");
      $(this)
        .find("tbody tr:even")
        .css("background-color", "var(--dark-secondary)");
      $(this).find("tbody td").css("background-color", "inherit");
    });
  }, 100);
}

/**
 * Carga la lista de planetas desde el servidor
 */
function cargarPlanetas() {
  fetch("/listar_planetas")
    .then((res) => {
      if (!res.ok) {
        return res.text().then((text) => {
          throw new Error(
            `Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`
          );
        });
      }
      const contentType = res.headers.get("content-type");
      if (!contentType || !contentType.includes("application/json")) {
        return res.text().then((text) => {
          throw new TypeError(
            `Respuesta inesperada del servidor (no es JSON): ${text}`
          );
        });
      }
      return res.json();
    })
    .then((planetas) => {
      let table = $("#planetsTable").DataTable();
      table.clear();
      if (Array.isArray(planetas)) {
        table.rows.add(planetas).draw();
        $("#planetsTable tbody tr").each(function (index) {
          // Reaplicar estilos de fila
          $(this).css(
            "background-color",
            index % 2 === 0 ? "rgba(30, 30, 30, 0.8)" : "var(--dark-secondary)"
          );
        });
      } else {
        console.error(
          "La respuesta de listar_planetas no es un array:",
          planetas
        );
        mostrarAlerta(
          "error",
          "Error: Formato de datos de planetas inesperado."
        );
      }
    })
    .catch((error) => {
      console.error("Error al cargar los planetas:", error);
      mostrarAlerta(
        "error",
        `No se pudieron cargar los planetas: ${error.message}`
      );
    });
}

/**
 * Abre el modal para añadir un nuevo planeta
 */
function abrirModalAnadir() {
  document.getElementById("planetForm").reset(); // Resetea el formulario
  document.getElementById("modalTitle").textContent = "Añadir Nuevo Planeta";
  document.getElementById("planetId").value = ""; // Asegura que no haya ID
  document.getElementById("planetModal").style.display = "block";
}

/**
 * Cierra el modal de añadir/editar planeta
 */
function cerrarModal() {
  const modal = document.getElementById("planetModal");
  if (modal) {
    modal.style.display = "none";
  }
}

/**
 * Cierra el modal de detalles del planeta
 */
function cerrarModalDetalles() {
  const modal = document.getElementById("viewPlanetModal");
  if (modal) {
    modal.style.display = "none";
    const detailsContainer = document.getElementById("planetDetails");
    if (detailsContainer) detailsContainer.innerHTML = ""; // Limpiar detalles
  }
}

/**
 * Abre el modal de edición con los datos del planeta seleccionado
 */
function editarPlaneta(id) {
  if (id === undefined || id === null) {
    mostrarAlerta("error", "No se puede editar el planeta: ID inválido.");
    return;
  }

  fetch(`/consulta_planetas?id=${id}`)
    .then((res) => {
      if (!res.ok) {
        return res.text().then((text) => {
          throw new Error(
            `Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`
          );
        });
      }
      return res.json();
    })
    .then((planetaDataArray) => {
      const planetaData = Array.isArray(planetaDataArray)
        ? planetaDataArray[0]
        : planetaDataArray;

      if (planetaData) {
        document.getElementById("planetForm").reset();
        document.getElementById("modalTitle").textContent = "Editar Planeta";

        document.getElementById("planetId").value = planetaData.id;
        document.getElementById("planetName").value = planetaData.nombre || "";
        document.getElementById("planetLocation").value =
          planetaData.ubicacion || "";

        // Para el select de habitable
        document.getElementById("planetHabitable").value =
          planetaData.habitable === true || planetaData.habitable === 1
            ? "1"
            : "0";

        document.getElementById("planetWaterLevel").value =
          planetaData.nivelAgua || "";
        document.getElementById("planetSize").value = planetaData.tamanio || "";
        document.getElementById("planetDensity").value =
          planetaData.densidad || "";

        if (planetaData.fechaCreacion) {
          try {
            const fecha = new Date(planetaData.fechaCreacion);
            // Comprobar si la fecha es válida antes de formatear
            if (!isNaN(fecha.getTime())) {
              document.getElementById("planetCreationDate").value = fecha
                .toISOString()
                .split("T")[0];
            } else {
              document.getElementById("planetCreationDate").value = "";
            }
          } catch (e) {
            console.error(
              "Error al parsear fechaCreacion:",
              planetaData.fechaCreacion,
              e
            );
            document.getElementById("planetCreationDate").value = "";
          }
        } else {
          document.getElementById("planetCreationDate").value = "";
        }
        document.getElementById("planetDescription").value =
          planetaData.descripcion || "";

        document.getElementById("planetModal").style.display = "block";
      } else {
        mostrarAlerta("error", `No se encontró el planeta con ID: ${id}`);
      }
    })
    .catch((error) => {
      console.error("Error al cargar datos del planeta para editar:", error);
      mostrarAlerta(
        "error",
        `Error al cargar datos del planeta: ${error.message}`
      );
    });
}

/**
 * Guarda un nuevo planeta o actualiza uno existente
 */
function guardarPlaneta() {
  const params = new URLSearchParams();
  const id = document.getElementById("planetId").value;

  const nombre = document.getElementById("planetName").value.trim();
  if (!nombre) {
    mostrarAlerta("error", "El nombre del planeta es obligatorio.");
    return;
  }
  params.append("nombre", nombre);

  const fechaCreacion = document.getElementById("planetCreationDate").value;
  // La fecha de creación es requerida
  if (!fechaCreacion) {
    mostrarAlerta("error", "La fecha de creación es obligatoria.");
    return;
  }
  params.append("fechaCreacion", fechaCreacion);

  params.append(
    "ubicacion",
    document.getElementById("planetLocation").value.trim()
  );
  params.append(
    "descripcion",
    document.getElementById("planetDescription").value.trim()
  );

  // Manejo del booleano 'habitable'
  const habitableValue = document.getElementById("planetHabitable").value;
  if (
    habitableValue === "1" ||
    habitableValue === "0" ||
    habitableValue === "true" ||
    habitableValue === "false"
  ) {
    params.append("habitable", habitableValue);
  } else {
    mostrarAlerta("error", "Debe seleccionar si el planeta es habitable o no.");
    return;
  }

  const nivelAgua = document.getElementById("planetWaterLevel").value.trim();
  if (nivelAgua) params.append("nivelAgua", nivelAgua);

  const tamanio = document.getElementById("planetSize").value.trim();
  if (tamanio) params.append("tamanio", tamanio);

  const densidad = document.getElementById("planetDensity").value.trim();
  if (densidad) params.append("densidad", densidad);

  let urlBase;
  if (id) {
    params.append("id", id);
    urlBase = "/actualizar_planeta";
  } else {
    urlBase = "/aniadir_planeta";
  }

  const fullUrl = `${urlBase}?${params.toString()}`;
  console.log("Enviando a URL:", fullUrl);

  fetch(fullUrl)
    .then((res) => {
      if (!res.ok) {
        return res.text().then((text) => {
          let errorDetail = text;
          try {
            const jsonError = JSON.parse(text);
            errorDetail = jsonError.message || jsonError.error || text;
          } catch (e) {
            /* No era JSON */
          }
          console.error("Detalle del error del servidor:", text);
          throw new Error(
            `Error HTTP ${res.status}: ${res.statusText}. Detalle: ${errorDetail}`
          );
        });
      }
      return res.text();
    })
    .then((resultado) => {
      mostrarAlerta(
        "success",
        id
          ? "Planeta actualizado correctamente."
          : "Planeta añadido correctamente."
      );
      cerrarModal();
      cargarPlanetas();
    })
    .catch((error) => {
      console.error("Error al guardar planeta:", error);
      mostrarAlerta("error", `Error al guardar planeta: ${error.message}`);
    });
}

/**
 * Elimina un planeta tras confirmación del usuario
 */
function eliminarPlaneta(id) {
  if (id === undefined || id === null) {
    mostrarAlerta("error", "No se puede eliminar el planeta: ID inválido.");
    return;
  }
  if (
    confirm(
      `¿Está seguro que desea eliminar el planeta con ID ${id}? Esta acción no se puede deshacer.`
    )
  ) {
    fetch(`/eliminar_planeta?id=${id}`) // Asume GET por defecto
      .then((res) => {
        if (!res.ok) {
          return res.text().then((text) => {
            throw new Error(
              `Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`
            );
          });
        }
        return res.text();
      })
      .then((resultado) => {
        mostrarAlerta(
          "success",
          `Planeta ${id} eliminado correctamente. ${resultado}`
        );
        cargarPlanetas();
      })
      .catch((error) => {
        console.error("Error al eliminar planeta:", error);
        mostrarAlerta("error", `Error al eliminar planeta: ${error.message}`);
      });
  }
}

/**
 * Muestra los detalles de un planeta específico
 */
function verPlaneta(id) {
  if (id === undefined || id === null) {
    mostrarAlerta("error", "No se pueden mostrar detalles: ID inválido.");
    return;
  }
  let planetaData = $("#planetsTable")
    .DataTable()
    .rows()
    .data()
    .toArray()
    .find((planeta) => planeta.id == id);

  if (!planetaData) {
    fetch(`/consulta_planetas?id=${id}`)
      .then((res) => {
        if (!res.ok) throw new Error("Planeta no encontrado o error al cargar");
        return res.json();
      })
      .then((data) => {
        const planeta = Array.isArray(data) ? data[0] : data;
        if (planeta) {
          mostrarDetallesPlaneta(planeta);
        } else {
          mostrarAlerta(
            "error",
            `No se encontraron datos para el planeta con ID: ${id}`
          );
        }
      })
      .catch((error) => {
        console.error("Error al cargar detalles del planeta:", error);
        mostrarAlerta("error", "Error al cargar detalles del planeta.");
      });
    return;
  }
  mostrarDetallesPlaneta(planetaData);
}

function mostrarDetallesPlaneta(planetaData) {
  const viewTitle = document.getElementById("viewPlanetTitle");
  const detailsContainer = document.getElementById("planetDetails");
  const viewModal = document.getElementById("viewPlanetModal");

  if (!viewTitle || !detailsContainer || !viewModal) {
    console.error("Elementos del modal de vista no encontrados.");
    return;
  }

  viewTitle.textContent = planetaData.nombre || "Detalles del Planeta";

  let fechaFormateada = "No especificada";
  if (planetaData.fechaCreacion) {
    try {
      const fecha = new Date(planetaData.fechaCreacion);
      if (!isNaN(fecha.getTime())) {
        fechaFormateada = fecha.toLocaleDateString("es-ES", {
          year: "numeric",
          month: "long",
          day: "numeric",
        });
      }
    } catch (e) {
      console.error("Error al formatear fecha de creación en vista:", e);
    }
  }

  const esHabitable =
    planetaData.habitable === true || planetaData.habitable === 1 ? "Sí" : "No";

  const detallesHTML = `
        <div class="planet-detail-grid">
            <div class="detail-item"><strong>ID:</strong> ${
              planetaData.id
            }</div>
            <div class="detail-item"><strong>Ubicación:</strong> ${
              planetaData.ubicacion || "No especificada"
            }</div>
            <div class="detail-item"><strong>Habitable:</strong> ${esHabitable}</div>
            <div class="detail-item"><strong>Nivel de Agua:</strong> ${
              planetaData.nivelAgua !== null
                ? planetaData.nivelAgua + " %"
                : "N/A"
            }</div>
            <div class="detail-item"><strong>Fecha de Creación:</strong> ${fechaFormateada}</div>
            <div class="detail-item"><strong>Tamaño:</strong> ${
              planetaData.tamanio !== null ? planetaData.tamanio + " km" : "N/A"
            }</div>
            <div class="detail-item"><strong>Densidad:</strong> ${
              planetaData.densidad !== null
                ? planetaData.densidad + " g/cm³"
                : "N/A"
            }</div>
        </div>
        <div class="detail-description">
            <h4>Descripción:</h4>
            <p>${
              planetaData.descripcion || "No hay descripción disponible."
            }</p>
        </div>
    `;

  detailsContainer.innerHTML = detallesHTML;
  viewModal.style.display = "block";
}
