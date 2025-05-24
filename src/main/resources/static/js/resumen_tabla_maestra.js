document.addEventListener("DOMContentLoaded", function () {
  // Verifica si la tabla maestra existe en la página actual antes de intentar inicializarla
  if (document.getElementById("tablaMaestra")) {
    inicializarTablaMaestra();
  } else {
    console.warn("Elemento 'tablaMaestra' no encontrado.");
  }
});

function inicializarTablaMaestra() {
  let tablaMaestraDT = $("#tablaMaestra").DataTable({
    language: {
      url: "https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json",
      search: "Buscar en tabla maestra:",
      lengthMenu: "Mostrar _MENU_ registros",
      info: "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
      infoEmpty: "Mostrando registros del 0 al 0 de un total de 0 registros",
      infoFiltered: "(filtrado de un total de _MAX_ registros)",
      paginate: {
        first: "Primero",
        last: "Último",
        next: "Siguiente",
        previous: "Anterior",
      },
    },
    responsive: true,
    pageLength: 10, // Número de filas por página por defecto
    lengthMenu: [
      [10, 25, 50, -1],
      [10, 25, 50, "Todos"],
    ], // Opciones para mostrar registros
    columns: [
      { data: "id", title: "ID" },
      { data: "tipoEntidad", title: "Tipo" },
      { data: "nombrePrincipal", title: "Nombre / Identificador" },
      {
        data: "fechaRelevante",
        title: "Fecha Relevante",
        render: function (data, type, row) {
          if (type === "display" || type === "filter") {
            return data ? new Date(data).toLocaleDateString("es-ES") : "N/A";
          }
          return data; // Para ordenación, DataTables usa el dato original
        },
      },
      {
        data: "infoClave1",
        title: "Info Clave 1",
        render: function (data) {
          return data || "N/A";
        },
      },
      {
        data: "infoClave2",
        title: "Info Clave 2",
        render: function (data) {
          return data || "N/A";
        },
      },
      {
        data: null, // No se basa en un campo de datos específico
        title: "Acciones",
        orderable: false,
        searchable: false,
        render: function (data, type, row) {
          // El backend en FilaTablaMaestra lo está construyendo como "planet.html#ID"
          if (row.urlDetalle) {
            // El enlace ya está completo desde el backend
            return `<a href="${row.urlDetalle}" class="view-btn action-icon" title="Ver detalles de ${row.tipoEntidad}: ${row.nombrePrincipal}"><i class="fas fa-eye"></i></a>`;
          }
          return "N/A";
        },
        className: "dt-center actions-column", // Clase para centrar y estilizar si es necesario
      },
    ],
    order: [[3, "desc"]], // Ordenar por la columna 'Fecha Relevante' (índice 3) descendente

    // Esta opción es para aplicar estilos personalizados a las filas... Pero no funciona bien con DataTables.
    // Como no causa ningun problema, la dejo aquí.
    createdRow: function (row, data, dataIndex) {
      // 'row' es el elemento <tr>
      // 'data' es el objeto de datos para esta fila (nuestro FilaTablaMaestra)

      // Aplicar color de fondo alterno (complicado porq me he liado a meter !important en el CSS)
      if (dataIndex % 2 === 0) {
        $(row).css("background-color", "rgba(40, 40, 40, 0.9)");
      } else {
        $(row).css("background-color", "rgba(30, 30, 30, 0.9)");
      }

      // Aplicar borde izquierdo específico para el tipo de entidad
      let borderColor = "transparent"; // Color por defecto o para tipos no definidos
      switch (data.tipoEntidad) {
        case "Planeta":
          borderColor = "#2980b9";
          break; // Azul
        case "Imperio":
          borderColor = "#8e44ad";
          break; // Púrpura
        case "Guerra":
          borderColor = "var(--crimson, #DC143C)";
          break; // Carmesí
        case "Persona":
          borderColor = "#27ae60";
          break; // Verde
        case "Raza":
          borderColor = "#f39c12";
          break; // Naranja
        case "Continente":
          borderColor = "#16a085";
          break; // Turquesa
        case "Bioma":
          borderColor = "#d35400";
          break; // Naranja oscuro
        case "Arma": // Intencionalmente cae al siguiente caso
        case "Armadura": // Intencionalmente cae al siguiente caso
        case "Herramienta": // Intencionalmente cae al siguiente caso
        case "Arcana":
          borderColor = "#7f8c8d";
          break; // Gris para equipamiento
      }
      $(row).css("border-left", `4px solid ${borderColor}`);
    },

    // Aplicar estilos a las filas después de cada dibujo de la tabla
    drawCallback: function (settings) {
      // Asegurar que los botones de responsividad se vean bien
      if (tablaMaestraDT && tablaMaestraDT.responsive) {
        tablaMaestraDT.responsive.recalc();
      }
    },
  });

  // Cargar datos en la Tabla Maestra
  fetch("/universo/tabla_maestra?limitePorEntidad=7") // Carga 7 de cada tipo de entidad por defecto
    .then((response) => {
      if (!response.ok) {
        return response.text().then((text) => {
          throw new Error(
            `Error HTTP ${response.status} (${response.statusText}) al cargar tabla maestra. Detalle: ${text}`
          );
        });
      }
      return response.json();
    })
    .then((datos) => {
      if (Array.isArray(datos)) {
        tablaMaestraDT.clear().rows.add(datos).draw();
      } else {
        console.error("Formato de datos inesperado para tabla maestra:", datos);

        $("#tablaMaestra tbody").html(
          '<tr><td colspan="7">Error: Formato de datos incorrecto.</td></tr>'
        );
      }
    })
    .catch((error) => {
      console.error("Error al cargar datos para tabla maestra:", error);
      $("#tablaMaestra tbody").html(
        `<tr><td colspan="7">Error al cargar datos: ${error.message}</td></tr>`
      );
    });
}
