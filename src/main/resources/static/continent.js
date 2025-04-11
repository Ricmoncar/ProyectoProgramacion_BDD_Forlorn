document.addEventListener('DOMContentLoaded', function() {
    // Inicializar DataTables
    let continentsTable = $('#continentsTable').DataTable({
        language: {
            // Asegúrate de que esta URL sea correcta y accesible
            url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
        },
        responsive: true, // Habilitar la extensión Responsive
        columns: [
            { data: 'id' },
            { data: 'nombre' },
            { data: 'planetaNombre' },
            { data: 'hemisferio' },
            { data: 'clima' },
            { data: 'tamanio', render: function(data) { // Añadir formato si se desea
                    return data ? `${data} km²` : 'N/A'; // Muestra 'N/A' si es null o 0
                }
            },
            {
                data: 'habitable',
                render: function(data) {
                    // Asegurarse de que la comparación maneje 1/0 o true/false
                    return data == 1 || data === true ? 'Sí' : 'No';
                }
            },
            {
                data: null,
                orderable: false, // No permitir ordenar por esta columna
                searchable: false, // No permitir buscar por esta columna
                render: function(data, type, row) { // Usar row para obtener el ID
                    // Validar que data.id existe
                    const continentId = row.id;
                    if (continentId === undefined || continentId === null) {
                         console.error("ID de continente no encontrado en los datos de la fila:", row);
                         return 'Error ID'; // O un mensaje/icono indicativo
                    }
                    return `<div class="continent-actions">
                        <button class="view-btn" title="Ver detalles" onclick="verContinente(${continentId})"><i class="fas fa-eye"></i></button>
                        <button class="edit-btn" title="Editar" onclick="editarContinente(${continentId})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" title="Eliminar" onclick="eliminarContinente(${continentId})"><i class="fas fa-trash-alt"></i></button>
                    </div>`;
                },
                 className: 'dt-center' // Centrar contenido si se desea
            }
        ],
        // Configuración adicional para Responsive
        responsive: {
            details: {
                type: 'column', // O 'inline' según prefieras
                target: 'tr'    // Mostrar detalles al hacer clic en cualquier parte de la fila
            }
        },
        // Orden inicial (opcional)
        order: [[1, 'asc']] // Ordenar por nombre ascendentemente por defecto
    });

    // Cargar continentes y planetas al inicio
    cargarPlanetas();
    cargarContinentes();

    // Event listeners para modales
    const addBtn = document.getElementById('addContinentBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const closeViewBtn = document.getElementById('closeViewBtn');

    if (addBtn) addBtn.addEventListener('click', abrirModalAnadir);
    if (cancelBtn) cancelBtn.addEventListener('click', cerrarModal);
    if (closeViewBtn) closeViewBtn.addEventListener('click', cerrarModalDetalles);

    // Event listeners para los filtros
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    const resetFiltersBtn = document.getElementById('resetFiltersBtn');

    if (applyFiltersBtn) applyFiltersBtn.addEventListener('click', aplicarFiltros);
    if (resetFiltersBtn) resetFiltersBtn.addEventListener('click', resetearFiltros);

    // Event listeners para cerrar modales con el botón X
    let closeButtons = document.querySelectorAll('.close-modal');
    closeButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            // Ocultar ambos modales al hacer clic en cualquier 'X'
            const continentModal = document.getElementById('continentModal');
            const viewContinentModal = document.getElementById('viewContinentModal');
            if (continentModal) continentModal.style.display = 'none';
            if (viewContinentModal) viewContinentModal.style.display = 'none';
        });
    });

    // Event listener para formulario
    const continentForm = document.getElementById('continentForm');
    if (continentForm) {
        continentForm.addEventListener('submit', function(e) {
            e.preventDefault(); // Prevenir el envío tradicional del formulario
            guardarContinente();
        });
    }

    // Cerrar modal al hacer clic fuera del contenido (opcional pero recomendado)
    window.addEventListener('click', function(event) {
        const continentModal = document.getElementById('continentModal');
        const viewContinentModal = document.getElementById('viewContinentModal');
        if (event.target == continentModal) {
            cerrarModal();
        }
        if (event.target == viewContinentModal) {
            cerrarModalDetalles();
        }
    });

});

function cargarPlanetas() {
    fetch("http://localhost:8080/listar_planetas")
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
            }
            return res.json();
        })
        .then(planetas => {
            const selectPlaneta = document.getElementById('continentPlanet');
            const filterPlaneta = document.getElementById('filterPlaneta');

            if (!selectPlaneta || !filterPlaneta) {
                console.error("Elementos select de planeta no encontrados en el DOM.");
                return;
            }

            // Guardar valor seleccionado (si existe) para restaurarlo después de llenar
            const currentFilterValue = filterPlaneta.value;
            const currentFormValue = selectPlaneta.value;

            // Limpiar opciones existentes, manteniendo la opción por defecto
            selectPlaneta.innerHTML = '<option value="">Seleccione un planeta</option>';
            filterPlaneta.innerHTML = '<option value="">Todos</option>'; // Cambiado de "Seleccione" a "Todos" para filtro

            planetas.forEach(planeta => {
                if(planeta && planeta.id !== undefined && planeta.nombre) { // Validar datos del planeta
                    let option = document.createElement('option');
                    option.value = planeta.id;
                    option.textContent = planeta.nombre;
                    selectPlaneta.appendChild(option.cloneNode(true)); // Clonar para añadir a ambos selects

                    filterPlaneta.appendChild(option);
                } else {
                    console.warn("Planeta con datos inválidos recibido:", planeta);
                }
            });

            // Restaurar selección previa si es posible
            if (currentFormValue) selectPlaneta.value = currentFormValue;
            if (currentFilterValue) filterPlaneta.value = currentFilterValue;

        })
        .catch(error => {
            console.error('Error al cargar planetas:', error);
            mostrarAlerta('error', `No se pudieron cargar los planetas: ${error.message}`);
        });
}


function cargarContinentes() {
    fetch("http://localhost:8080/listar_continentes")
        .then(res => {
            if (!res.ok) {
                 // Intentar leer el cuerpo del error si es posible
                return res.text().then(text => {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                });
            }
            // Verificar si la respuesta es JSON
            const contentType = res.headers.get("content-type");
            if (!contentType || !contentType.includes("application/json")) {
                 return res.text().then(text => { // Mostrar el texto si no es JSON
                    throw new TypeError(`Respuesta inesperada del servidor (no es JSON): ${text}`);
                 });
            }
            return res.json();
        })
        .then(continentes => {
            let table = $('#continentsTable').DataTable();
            table.clear();
            if (Array.isArray(continentes)) { // Asegurarse de que es un array
                table.rows.add(continentes).draw();
            } else {
                console.error("La respuesta de listar_continentes no es un array:", continentes);
                mostrarAlerta('error', 'Error: Formato de datos de continentes inesperado.');
            }
        })
        .catch(error => {
            console.error('Error al cargar continentes:', error);
            mostrarAlerta('error', `No se pudieron cargar los continentes: ${error.message}`);
        });
}

function verContinente(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para verContinente");
        mostrarAlerta('error', 'No se puede mostrar el continente: ID inválido.');
        return;
    }
    fetch(`http://localhost:8080/obtener_continente?id=${id}`)
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => { throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`) });
            }
             const contentType = res.headers.get("content-type");
             if (!contentType || !contentType.includes("application/json")) {
                  return res.text().then(text => { throw new TypeError(`Respuesta inesperada del servidor (no es JSON): ${text}`) });
             }
            return res.json();
        })
        .then(continente => {
            if (!continente) {
                 throw new Error("No se recibió información del continente.");
            }
            // Usar el ID correcto para el título del modal de vista
            const viewTitle = document.getElementById('viewContinentTitle');
            const detailsContainer = document.getElementById('continentDetails');
            const viewModal = document.getElementById('viewContinentModal');

            if (!viewTitle || !detailsContainer || !viewModal) {
                console.error("Elementos del modal de vista no encontrados.");
                return;
            }

            viewTitle.textContent = continente.nombre || 'Detalles del Continente'; // Usar nombre o título genérico

            const esHabitable = continente.habitable == 1 || continente.habitable === true;
            const tamanioTexto = continente.tamanio ? `${continente.tamanio} km²` : 'No especificado';
            const descripcionTexto = continente.descripcion || 'No hay descripción disponible.';

            // Usar las clases CSS definidas en el HTML para consistencia
            const detallesHTML = `
                <div class="continent-details">
                    <div class="detail-row">
                        <span class="detail-label">Planeta:</span>
                        <span class="detail-value">${continente.planetaNombre || 'N/A'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Hemisferio:</span>
                        <span class="detail-value">${continente.hemisferio || 'N/A'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Clima:</span>
                        <span class="detail-value">${continente.clima || 'N/A'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Tamaño:</span>
                        <span class="detail-value">${tamanioTexto}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Habitable:</span>
                        <span class="detail-value">${esHabitable ? 'Sí' : 'No'}</span>
                    </div>
                    <div class="detail-row full-width">
                        <span class="detail-label">Descripción:</span>
                        <div class="detail-description">${descripcionTexto}</div>
                    </div>
                </div>
            `;

            detailsContainer.innerHTML = detallesHTML;
            viewModal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al obtener el continente:', error);
            mostrarAlerta('error', `Error al obtener el continente: ${error.message}`);
        });
}

function editarContinente(id) {
     if (id === undefined || id === null) {
        console.error("ID inválido para editarContinente");
        mostrarAlerta('error', 'No se puede editar el continente: ID inválido.');
        return;
    }
    const form = document.getElementById('continentForm');
    const modalTitle = document.getElementById('continentModalTitle');
    const continentIdInput = document.getElementById('continentId');
    const modal = document.getElementById('continentModal');

    if (!form || !modalTitle || !continentIdInput || !modal) {
        console.error("Elementos del modal de edición no encontrados.");
        return;
    }

    form.reset(); // Limpiar formulario antes de cargar datos
    modalTitle.textContent = 'Editar Continente';
    continentIdInput.value = id; // Establecer el ID oculto

    fetch(`http://localhost:8080/obtener_continente?id=${id}`)
        .then(res => {
            if (!res.ok) {
                 return res.text().then(text => { throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`) });
            }
             const contentType = res.headers.get("content-type");
             if (!contentType || !contentType.includes("application/json")) {
                  return res.text().then(text => { throw new TypeError(`Respuesta inesperada del servidor (no es JSON): ${text}`) });
             }
            return res.json();
        })
        .then(continente => {
             if (!continente) {
                 throw new Error("No se recibió información del continente para editar.");
             }

            // Rellenar el formulario con los datos del continente
            document.getElementById('continentName').value = continente.nombre || '';
            document.getElementById('continentPlanet').value = continente.planetaId || '';
            document.getElementById('continentHemisphere').value = continente.hemisferio || 'Norte'; // Default si es null
            document.getElementById('continentClimate').value = continente.clima || 'Templado'; // Default si es null
            document.getElementById('continentSize').value = continente.tamanio || '';

            // *** CORRECCIÓN: Establecer el valor del SELECT para 'habitable' ***
            const habitableSelect = document.getElementById('continentHabitable');
            if (habitableSelect) {
                // Convertir booleano o 1/0 a string "1" o "0" para el valor del select
                habitableSelect.value = (continente.habitable == 1 || continente.habitable === true) ? '1' : '0';
            } else {
                console.error("Elemento select 'continentHabitable' no encontrado.");
            }

            document.getElementById('continentDescription').value = continente.descripcion || '';

            modal.style.display = 'block'; // Mostrar modal
        })
        .catch(error => {
            console.error('Error al cargar datos del continente para editar:', error);
            mostrarAlerta('error', `Error al cargar datos del continente: ${error.message}`);
            cerrarModal(); // Cerrar modal si falla la carga
        });
}

function eliminarContinente(id) {
     if (id === undefined || id === null) {
        console.error("ID inválido para eliminarContinente");
        mostrarAlerta('error', 'No se puede eliminar el continente: ID inválido.');
        return;
    }
    // Usar un modal de confirmación más robusto sería ideal, pero confirm() es funcional
    if (confirm(`¿Está seguro que desea eliminar el continente con ID ${id}? Esta acción no se puede deshacer.`)) {
        // Usar método DELETE si la API lo soporta, si no, seguir con GET
        // fetch(`http://localhost:8080/eliminar_continente?id=${id}`, { method: 'DELETE' })
        fetch(`http://localhost:8080/eliminar_continente?id=${id}`) // Asumiendo GET por ahora
            .then(res => {
                if (!res.ok) {
                    // Intentar leer el cuerpo del error
                    return res.text().then(text => {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                    });
                }
                 // Podría devolver texto o estar vacío en éxito
                return res.text();
            })
            .then(resultado => {
                mostrarAlerta('success', `Continente ${id} eliminado correctamente. ${resultado}`);
                cargarContinentes(); // Recargar la tabla
            })
            .catch(error => {
                console.error('Error al eliminar continente:', error);
                mostrarAlerta('error', `Error al eliminar continente: ${error.message}`);
            });
    }
}

function abrirModalAnadir() {
    const form = document.getElementById('continentForm');
    const modalTitle = document.getElementById('continentModalTitle');
    const continentIdInput = document.getElementById('continentId');
    const modal = document.getElementById('continentModal');

    if (!form || !modalTitle || !continentIdInput || !modal) {
        console.error("Elementos del modal de añadir no encontrados.");
        return;
    }

    form.reset(); // Limpiar el formulario
    modalTitle.textContent = 'Añadir Nuevo Continente';
    continentIdInput.value = ''; // Asegurarse de que el ID esté vacío
    modal.style.display = 'block';
}

function cerrarModal() {
    const modal = document.getElementById('continentModal');
    if (modal) {
        modal.style.display = 'none';
        // Opcional: Limpiar el formulario al cerrar
        // const form = document.getElementById('continentForm');
        // if (form) form.reset();
    }
}

function cerrarModalDetalles() {
    const modal = document.getElementById('viewContinentModal');
    if (modal) {
        modal.style.display = 'none';
        // Limpiar contenido dinámico al cerrar
        const detailsContainer = document.getElementById('continentDetails');
        if (detailsContainer) detailsContainer.innerHTML = '';
    }
}

function guardarContinente() {
    const id = document.getElementById('continentId').value;
    const nombre = document.getElementById('continentName').value.trim();
    const planetaId = document.getElementById('continentPlanet').value;
    const hemisferio = document.getElementById('continentHemisphere').value;
    const clima = document.getElementById('continentClimate').value;
    const tamanioInput = document.getElementById('continentSize');
    const tamanio = tamanioInput.value ? parseFloat(tamanioInput.value) : null; // Convertir a número o null
    const descripcion = document.getElementById('continentDescription').value.trim();

    // *** CORRECCIÓN: Leer el valor del SELECT 'habitable' y convertir a booleano ***
    const habitableSelect = document.getElementById('continentHabitable');
    const habitable = habitableSelect ? habitableSelect.value === '1' : false; // Convertir "1" a true, otro a false

    // Validaciones básicas
    if (!nombre) {
        mostrarAlerta('warning', 'El nombre del continente es obligatorio.');
        document.getElementById('continentName').focus();
        return;
    }
     if (!planetaId) {
        mostrarAlerta('warning', 'Debe seleccionar un planeta.');
        document.getElementById('continentPlanet').focus();
        return;
    }
    if (tamanio !== null && isNaN(tamanio)) {
        mostrarAlerta('warning', 'El tamaño debe ser un número válido.');
         tamanioInput.focus();
         return;
    }
     if (tamanio !== null && tamanio < 0) {
        mostrarAlerta('warning', 'El tamaño no puede ser negativo.');
         tamanioInput.focus();
         return;
    }


    let url;
    let method; // Para futura implementación con POST/PUT

    const params = new URLSearchParams();
    params.append('nombre', nombre);
    params.append('planetaId', planetaId);
    params.append('hemisferio', hemisferio);
    params.append('clima', clima);
    if (tamanio !== null) {
        params.append('tamanio', tamanio);
    }
    params.append('habitable', habitable); // Enviar true/false
    params.append('descripcion', descripcion);

    if (id) {
        // Actualizar
        method = 'PUT'; // O 'POST' si la API usa POST para actualizar via params
        url = `http://localhost:8080/actualizar_continente?id=${id}&${params.toString()}`;
    } else {
        // Añadir nuevo
        method = 'POST'; // O 'GET' si la API usa GET para añadir via params
        url = `http://localhost:8080/aniadir_continente?${params.toString()}`;
    }

    // Mostrar un indicador de carga sería ideal aquí

    // fetch(url, { method: method }) // Usar método correcto si la API lo soporta
    fetch(url) // Asumiendo GET/POST via query params por ahora
        .then(res => {
            if (!res.ok) {
                 return res.text().then(text => { throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`) });
            }
            return res.text(); // O res.json() si devuelve el objeto guardado
        })
        .then(resultado => {
            const accion = id ? 'actualizado' : 'añadido';
            mostrarAlerta('success', `Continente ${accion} correctamente. ${resultado}`);
            cargarContinentes(); // Recargar tabla
            cerrarModal(); // Cerrar modal de edición/adición
        })
        .catch(error => {
            console.error('Error al guardar continente:', error);
            mostrarAlerta('error', `Error al guardar continente: ${error.message}`);
            // No cerrar el modal en caso de error para que el usuario corrija
        });
}

function aplicarFiltros() {
    const planetaId = document.getElementById('filterPlaneta').value;
    const hemisferio = document.getElementById('filterHemisferio').value;
    const clima = document.getElementById('filterClima').value;
    // *** CORRECCIÓN: Leer valor del filtro 'habitable' que es "true" o "false" ***
    const habitableFilterValue = document.getElementById('filterHabitable').value;

    let url = 'http://localhost:8080/filtrar_continentes?';
    const params = new URLSearchParams();

    if (planetaId) {
        params.append('planetaId', planetaId);
    }
    if (hemisferio) {
        params.append('hemisferio', hemisferio);
    }
    if (clima) {
        params.append('clima', clima);
    }
    // Añadir parámetro 'habitable' solo si se seleccionó "Sí" o "No"
    if (habitableFilterValue !== '') {
         // El valor ya es "true" o "false", como espera la API (según el código original)
        params.append('habitable', habitableFilterValue);
    }

    url += params.toString();

    // Quitar el '&' final si la URL termina con él (aunque URLSearchParams no lo añade si no hay params)
    // No es estrictamente necesario con URLSearchParams
    // if (url.endsWith('&')) {
    //     url = url.slice(0, -1);
    // }

    // Mostrar indicador de carga sería útil
    const filterResults = document.getElementById('filterResults');
    if(filterResults) filterResults.textContent = 'Filtrando...';


    fetch(url)
        .then(res => {
            if (!res.ok) {
                 return res.text().then(text => { throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`) });
            }
             const contentType = res.headers.get("content-type");
             if (!contentType || !contentType.includes("application/json")) {
                  return res.text().then(text => { throw new TypeError(`Respuesta inesperada del servidor (no es JSON): ${text}`) });
             }
            return res.json();
        })
        .then(continentes => {
            let table = $('#continentsTable').DataTable();
            table.clear();
             if (Array.isArray(continentes)) {
                 table.rows.add(continentes).draw();
                 if(filterResults) filterResults.textContent = `${continentes.length} resultado(s) encontrado(s).`;
             } else {
                 console.error("La respuesta de filtrar_continentes no es un array:", continentes);
                 mostrarAlerta('error', 'Error: Formato de datos filtrados inesperado.');
                 if(filterResults) filterResults.textContent = 'Error al mostrar resultados.';
             }
        })
        .catch(error => {
            console.error('Error al filtrar continentes:', error);
            mostrarAlerta('error', `Error al filtrar continentes: ${error.message}`);
             if(filterResults) filterResults.textContent = 'Error al aplicar filtros.';
        });
}

function resetearFiltros() {
    // Resetear valores de los selects de filtro
    const filterPlaneta = document.getElementById('filterPlaneta');
    const filterHemisferio = document.getElementById('filterHemisferio');
    const filterClima = document.getElementById('filterClima');
    const filterHabitable = document.getElementById('filterHabitable');
    const filterResults = document.getElementById('filterResults');

    if (filterPlaneta) filterPlaneta.value = '';
    if (filterHemisferio) filterHemisferio.value = '';
    if (filterClima) filterClima.value = '';
    if (filterHabitable) filterHabitable.value = '';
    if (filterResults) filterResults.textContent = ''; // Limpiar texto de resultados

    // Recargar todos los continentes
    cargarContinentes();
}

function mostrarAlerta(tipo, mensaje) {
    const alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) {
        console.error("Contenedor de alertas 'alertContainer' no encontrado en el DOM.");
        // Como fallback, usar alert() del navegador
        alert(`[${tipo.toUpperCase()}] ${mensaje}`);
        return;
    }

    const alert = document.createElement('div');
    // Asegurarse de que las clases coinciden con las definidas en el CSS
    alert.className = `alert alert-${tipo}`; // e.g., alert-success, alert-error

    // Crear elementos internos para mejor control y accesibilidad
    const messageSpan = document.createElement('span');
    messageSpan.className = 'alert-message';
    messageSpan.textContent = mensaje;

    const closeButton = document.createElement('button');
    closeButton.className = 'close-alert';
    closeButton.innerHTML = '×'; // Carácter 'x' HTML
    closeButton.setAttribute('aria-label', 'Cerrar alerta');
    closeButton.onclick = function() {
        // Añadir transición de desvanecimiento antes de remover (opcional)
        alert.style.opacity = '0';
        setTimeout(() => {
             if (alert.parentElement) { // Verificar que todavía existe
                 alert.remove();
             }
        }, 300); // Duración de la transición CSS
    };

    alert.appendChild(messageSpan);
    alert.appendChild(closeButton);

    alertContainer.appendChild(alert);

    // Auto-cerrar después de 5 segundos
    const autoCloseTimeout = setTimeout(() => {
        closeButton.onclick(); // Simular clic en el botón de cerrar
    }, 5000);

    // Opcional: Pausar el temporizador si el usuario pasa el ratón por encima
    alert.addEventListener('mouseenter', () => clearTimeout(autoCloseTimeout));
    alert.addEventListener('mouseleave', () => {
        // Reanudar temporizador (o simplemente dejar que el botón de cerrar funcione)
        // setTimeout(() => closeButton.onclick(), 5000); // Reanudar con tiempo restante sería más complejo
    });
}