document.addEventListener('DOMContentLoaded', function() {
    /* Inicialización de DataTables */
    let continentsTable = $('#continentsTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
        },
        responsive: true,
        columns: [
            { data: 'id' },
            { data: 'nombre' },
            { data: 'planetaNombre' },
            { data: 'hemisferio' },
            { data: 'clima' },
            { data: 'tamanio', render: function(data) {
                return data ? `${data} km²` : 'N/A';
            }},
            {
                data: 'habitable',
                render: function(data) {
                    return data == 1 || data === true ? 'Sí' : 'No';
                }
            },
            {
                data: null,
                orderable: false,
                searchable: false,
                render: function(data, type, row) {
                    const continentId = row.id;
                    if (continentId === undefined || continentId === null) {
                        console.error("ID de continente no encontrado en los datos de la fila:", row);
                        return 'Error ID';
                    }
                    return `<div class="continent-actions">
                        <button class="view-btn" title="Ver detalles" onclick="verContinente(${continentId})"><i class="fas fa-eye"></i></button>
                        <button class="edit-btn" title="Editar" onclick="editarContinente(${continentId})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" title="Eliminar" onclick="eliminarContinente(${continentId})"><i class="fas fa-trash-alt"></i></button>
                    </div>`;
                },
                className: 'dt-center'
            }
        ],
        responsive: {
            details: {
                type: 'column',
                target: 'tr'
            }
        },
        order: [[1, 'asc']], // Ordenar por nombre en lugar de ID para evitar problemas de visualización
        // Solución al problema de visualización con colores de fondo
        createdRow: function(row, data, dataIndex) {
            // Garantiza que todas las filas tengan el fondo correcto
            $(row).css('background-color', dataIndex % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
        }
    });

    /* Carga inicial de datos */
    cargarPlanetas();
    cargarContinentes();

    /* Configuración de eventos para modales */
    const addBtn = document.getElementById('addContinentBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const closeViewBtn = document.getElementById('closeViewBtn');

    if (addBtn) addBtn.addEventListener('click', abrirModalAnadir);
    if (cancelBtn) cancelBtn.addEventListener('click', cerrarModal);
    if (closeViewBtn) closeViewBtn.addEventListener('click', cerrarModalDetalles);

    /* Configuración de eventos para filtros */
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    const resetFiltersBtn = document.getElementById('resetFiltersBtn');

    if (applyFiltersBtn) applyFiltersBtn.addEventListener('click', aplicarFiltros);
    if (resetFiltersBtn) resetFiltersBtn.addEventListener('click', resetearFiltros);

    /* Configuración de eventos apara cerrar modales */
    let closeButtons = document.querySelectorAll('.close-modal');
    closeButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            const continentModal = document.getElementById('continentModal');
            const viewContinentModal = document.getElementById('viewContinentModal');
            if (continentModal) continentModal.style.display = 'none';
            if (viewContinentModal) viewContinentModal.style.display = 'none';
        });
    });

    /* Configuración del formulario */
    const continentForm = document.getElementById('continentForm');
    if (continentForm) {
        continentForm.addEventListener('submit', function(e) {
            e.preventDefault();
            guardarContinente();
        });
    }

    /* Cerrar modal al hacer clic fuera del contenido */
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
    fixTableDisplay();
});

function fixTableDisplay() {
    setTimeout(function() {
        $('table.dataTable tbody td').css('background-color', 'inherit');
        
        $('table.dataTable tbody tr:odd').css('background-color', 'rgba(30, 30, 30, 0.8)');
        $('table.dataTable tbody tr:even').css('background-color', 'var(--dark-secondary)');
        
        $('.dataTable').on('draw.dt', function() {
            $(this).find('tbody tr:odd').css('background-color', 'rgba(30, 30, 30, 0.8)');
            $(this).find('tbody tr:even').css('background-color', 'var(--dark-secondary)');
            $(this).find('tbody td').css('background-color', 'inherit');
        });
    }, 100);
}
/**
 * Carga la lista de planetas desde el servidor
 */
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

            // Guardar valores seleccionados actualmente
            const currentFilterValue = filterPlaneta.value;
            const currentFormValue = selectPlaneta.value;

            // Limpiar y rellenar las opciones
            selectPlaneta.innerHTML = '<option value="">Seleccione un planeta</option>';
            filterPlaneta.innerHTML = '<option value="">Todos</option>';

            planetas.forEach(planeta => {
                if(planeta && planeta.id !== undefined && planeta.nombre) {
                    let option = document.createElement('option');
                    option.value = planeta.id;
                    option.textContent = planeta.nombre;
                    selectPlaneta.appendChild(option.cloneNode(true));
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

/**
 * Carga la lista de continentes desde el servidor
 */
function cargarContinentes() {
    fetch("http://localhost:8080/listar_continentes")
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                });
            }
            const contentType = res.headers.get("content-type");
            if (!contentType || !contentType.includes("application/json")) {
                 return res.text().then(text => {
                    throw new TypeError(`Respuesta inesperada del servidor (no es JSON): ${text}`);
                 });
            }
            return res.json();
        })
        .then(continentes => {
            let table = $('#continentsTable').DataTable();
            table.clear();
            if (Array.isArray(continentes)) {
                table.rows.add(continentes).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#continentsTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
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

/**
 * Muestra los detalles de un continente específico
 */
function verContinente(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para verContinente");
        mostrarAlerta('error', 'No se puede mostrar el continente: ID inválido.');
        return;
    }
    fetch(`http://localhost:8080/obtener_continente?id=${id}`)
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => { 
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`)
                });
            }
            const contentType = res.headers.get("content-type");
            if (!contentType || !contentType.includes("application/json")) {
                return res.text().then(text => { 
                    throw new TypeError(`Respuesta inesperada del servidor (no es JSON): ${text}`)
                });
            }
            return res.json();
        })
        .then(continente => {
            if (!continente) {
                throw new Error("No se recibió información del continente.");
            }
            
            const viewTitle = document.getElementById('viewContinentTitle');
            const detailsContainer = document.getElementById('continentDetails');
            const viewModal = document.getElementById('viewContinentModal');

            if (!viewTitle || !detailsContainer || !viewModal) {
                console.error("Elementos del modal de vista no encontrados.");
                return;
            }

            viewTitle.textContent = continente.nombre || 'Detalles del Continente';

            const esHabitable = continente.habitable == 1 || continente.habitable === true;
            const tamanioTexto = continente.tamanio ? `${continente.tamanio} km²` : 'No especificado';
            const descripcionTexto = continente.descripcion || 'No hay descripción disponible.';

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

/**
 * Abre el modal de edición con los datos del continente seleccionado
 */
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

    form.reset();
    modalTitle.textContent = 'Editar Continente';
    continentIdInput.value = id;

    fetch(`http://localhost:8080/obtener_continente?id=${id}`)
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => { 
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`)
                });
            }
            const contentType = res.headers.get("content-type");
            if (!contentType || !contentType.includes("application/json")) {
                return res.text().then(text => { 
                    throw new TypeError(`Respuesta inesperada del servidor (no es JSON): ${text}`)
                });
            }
            return res.json();
        })
        .then(continente => {
            if (!continente) {
                throw new Error("No se recibió información del continente para editar.");
            }

            document.getElementById('continentName').value = continente.nombre || '';
            document.getElementById('continentPlanet').value = continente.planetaId || '';
            document.getElementById('continentHemisphere').value = continente.hemisferio || 'Norte';
            document.getElementById('continentClimate').value = continente.clima || 'Templado';
            document.getElementById('continentSize').value = continente.tamanio || '';

            const habitableSelect = document.getElementById('continentHabitable');
            if (habitableSelect) {
                habitableSelect.value = (continente.habitable == 1 || continente.habitable === true) ? '1' : '0';
            } else {
                console.error("Elemento select 'continentHabitable' no encontrado.");
            }

            document.getElementById('continentDescription').value = continente.descripcion || '';

            modal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al cargar datos del continente para editar:', error);
            mostrarAlerta('error', `Error al cargar datos del continente: ${error.message}`);
            cerrarModal();
        });
}

/**
 * Elimina un continente tras confirmación del usuario
 */
function eliminarContinente(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para eliminarContinente");
        mostrarAlerta('error', 'No se puede eliminar el continente: ID inválido.');
        return;
    }
    
    if (confirm(`¿Está seguro que desea eliminar el continente con ID ${id}? Esta acción no se puede deshacer.`)) {
        fetch(`http://localhost:8080/eliminar_continente?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                    });
                }
                return res.text();
            })
            .then(resultado => {
                mostrarAlerta('success', `Continente ${id} eliminado correctamente. ${resultado}`);
                cargarContinentes();
            })
            .catch(error => {
                console.error('Error al eliminar continente:', error);
                mostrarAlerta('error', `Error al eliminar continente: ${error.message}`);
            });
    }
}

/**
 * Abre el modal para añadir un nuevo continente
 */
function abrirModalAnadir() {
    const form = document.getElementById('continentForm');
    const modalTitle = document.getElementById('continentModalTitle');
    const continentIdInput = document.getElementById('continentId');
    const modal = document.getElementById('continentModal');

    if (!form || !modalTitle || !continentIdInput || !modal) {
        console.error("Elementos del modal de añadir no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Añadir Nuevo Continente';
    continentIdInput.value = '';
    modal.style.display = 'block';
}

/**
 * Cierra el modal de añadir/editar continente
 */
function cerrarModal() {
    const modal = document.getElementById('continentModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

/**
 * Cierra el modal de detalles del continente
 */
function cerrarModalDetalles() {
    const modal = document.getElementById('viewContinentModal');
    if (modal) {
        modal.style.display = 'none';
        const detailsContainer = document.getElementById('continentDetails');
        if (detailsContainer) detailsContainer.innerHTML = '';
    }
}

/**
 * Guarda un nuevo continente o actualiza uno existente
 */
function guardarContinente() {
    const id = document.getElementById('continentId').value;
    const nombre = document.getElementById('continentName').value.trim();
    const planetaId = document.getElementById('continentPlanet').value;
    const hemisferio = document.getElementById('continentHemisphere').value;
    const clima = document.getElementById('continentClimate').value;
    const tamanioInput = document.getElementById('continentSize');
    const tamanio = tamanioInput.value ? parseFloat(tamanioInput.value) : null;
    const descripcion = document.getElementById('continentDescription').value.trim();

    // Validación básica
    if (!nombre) {
        mostrarAlerta('error', 'El nombre del continente es obligatorio.');
        return;
    }

    if (!planetaId) {
        mostrarAlerta('error', 'Debe seleccionar un planeta.');
        return;
    }

    const habitableSelect = document.getElementById('continentHabitable');
    const habitable = habitableSelect ? habitableSelect.value === '1' : false;

    // Construir la URL con los parámetros
    let url = id ? 'http://localhost:8080/actualizar_continente?' : 'http://localhost:8080/aniadir_continente?';
    
    const params = new URLSearchParams();
    if (id) params.append('id', id);
    params.append('nombre', nombre);
    params.append('planetaId', planetaId);
    params.append('hemisferio', hemisferio);
    params.append('clima', clima);
    if (tamanio !== null) params.append('tamanio', tamanio);
    params.append('habitable', habitable);
    params.append('descripcion', descripcion);
    
    url += params.toString();

    // Enviar la petición
    fetch(url)
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                });
            }
            return res.text();
        })
        .then(resultado => {
            mostrarAlerta('success', id ? 'Continente actualizado correctamente.' : 'Continente añadido correctamente.');
            cerrarModal();
            cargarContinentes();
        })
        .catch(error => {
            console.error('Error al guardar continente:', error);
            mostrarAlerta('error', `Error al guardar continente: ${error.message}`);
        });
}

/**
 * Aplica filtros seleccionados para filtrar continentes
 */
function aplicarFiltros() {
    const planetaId = document.getElementById('filterPlaneta').value;
    const hemisferio = document.getElementById('filterHemisferio').value;
    const clima = document.getElementById('filterClima').value;
    const habitableSelect = document.getElementById('filterHabitable');
    const habitable = habitableSelect && habitableSelect.value ? habitableSelect.value === 'true' : null;
    
    // Construir la URL con los parámetros
    let url = 'http://localhost:8080/filtrar_continentes?';
    const params = new URLSearchParams();
    
    if (planetaId) params.append('planetaId', planetaId);
    if (hemisferio) params.append('hemisferio', hemisferio);
    if (clima) params.append('clima', clima);
    if (habitable !== null) params.append('habitable', habitable);
    
    url += params.toString();
    
    // Mostrar indicador de carga
    const filterResults = document.getElementById('filterResults');
    if (filterResults) filterResults.textContent = 'Aplicando filtros...';
    
    fetch(url)
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                });
            }
            const contentType = res.headers.get("content-type");
            if (!contentType || !contentType.includes("application/json")) {
                return res.text().then(text => {
                    throw new TypeError(`Respuesta inesperada del servidor (no es JSON): ${text}`);
                });
            }
            return res.json();
        })
        .then(continentes => {
            let table = $('#continentsTable').DataTable();
            table.clear();
            
            if (Array.isArray(continentes)) {
                table.rows.add(continentes).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#continentsTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
                
                if (filterResults) {
                    filterResults.textContent = `Se encontraron ${continentes.length} continentes con los filtros seleccionados.`;
                }
            } else {
                console.error("La respuesta de filtrar_continentes no es un array:", continentes);
                mostrarAlerta('error', 'Error: Formato de datos de continentes inesperado.');
                if (filterResults) filterResults.textContent = '';
            }
        })
        .catch(error => {
            console.error('Error al filtrar continentes:', error);
            mostrarAlerta('error', `Error al filtrar continentes: ${error.message}`);
            if (filterResults) filterResults.textContent = '';
        });
}

/**
 * Resetea todos los filtros y carga todos los continentes
 */
function resetearFiltros() {
    const filterPlaneta = document.getElementById('filterPlaneta');
    const filterHemisferio = document.getElementById('filterHemisferio');
    const filterClima = document.getElementById('filterClima');
    const filterHabitable = document.getElementById('filterHabitable');
    
    if (filterPlaneta) filterPlaneta.value = '';
    if (filterHemisferio) filterHemisferio.value = '';
    if (filterClima) filterClima.value = '';
    if (filterHabitable) filterHabitable.value = '';
    
    const filterResults = document.getElementById('filterResults');
    if (filterResults) filterResults.textContent = '';
    
    cargarContinentes();
}

/**
 * Muestra alertas en la parte superior derecha de la pantalla
 */
function mostrarAlerta(tipo, mensaje) {
    const alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) {
        console.error("Contenedor de alertas no encontrado");
        alert(mensaje);
        return;
    }

    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${tipo}`;
    
    // Color según el tipo de alerta
    switch (tipo) {
        case 'success':
            alertDiv.style.backgroundColor = 'var(--success)';
            break;
        case 'error':
            alertDiv.style.backgroundColor = 'var(--error)';
            break;
        case 'info':
            alertDiv.style.backgroundColor = 'var(--info)';
            break;
        case 'warning':
            alertDiv.style.backgroundColor = 'var(--warning)';
            break;
    }
    
    alertDiv.innerHTML = `
        <span>${mensaje}</span>
        <button class="close-alert">&times;</button>
    `;
    
    alertContainer.appendChild(alertDiv);
    
    // Añadir evento para cerrar la alerta
    const closeBtn = alertDiv.querySelector('.close-alert');
    if (closeBtn) {
        closeBtn.addEventListener('click', function() {
            alertDiv.remove();
        });
    }
    
    // Auto-cerrar después de 5 segundos
    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}