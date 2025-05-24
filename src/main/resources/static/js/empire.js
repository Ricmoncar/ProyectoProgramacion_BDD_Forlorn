// Variables globales
let empiresTable;

/**
 * Corrige problemas de visualización en las tablas con tema oscuro
 */
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
 * Carga la lista de imperios desde el servidor
 */
function cargarImperios() {
    fetch("/listar_imperios")
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
        .then(imperios => {
            let table = $('#empiresTable').DataTable();
            table.clear();
            if (Array.isArray(imperios)) {
                table.rows.add(imperios).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#empiresTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
            } else {
                console.error("La respuesta de listar_imperios no es un array:", imperios);
                mostrarAlerta('error', 'Error: Formato de datos de imperios inesperado.');
            }
        })
        .catch(error => {
            console.error('Error al cargar imperios:', error);
            mostrarAlerta('error', `No se pudieron cargar los imperios: ${error.message}`);
        });
}

/**
 * Muestra los detalles de un imperio específico
 */
function verImperio(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para verImperio");
        mostrarAlerta('error', 'No se puede mostrar el imperio: ID inválido.');
        return;
    }
    fetch(`/obtener_imperio?id=${id}`)
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
        .then(imperio => {
            if (!imperio) {
                throw new Error("No se recibió información del imperio.");
            }
            
            const viewTitle = document.getElementById('viewEmpireTitle');
            const detailsContainer = document.getElementById('empireDetails');
            const viewModal = document.getElementById('viewEmpireModal');

            if (!viewTitle || !detailsContainer || !viewModal) {
                console.error("Elementos del modal de vista no encontrados.");
                return;
            }

            viewTitle.textContent = imperio.nombre || 'Detalles del Imperio';

            // Formatear fecha
            let fechaTexto = 'No especificada';
            if (imperio.fechaCreacion) {
                const fecha = new Date(imperio.fechaCreacion);
                fechaTexto = fecha.toLocaleDateString('es-ES');
            }

            // Formatear poblacion y GDP
            const poblacionTexto = imperio.poblacion ? imperio.poblacion.toLocaleString('es-ES') + ' habitantes' : 'No especificada';
            const gdpTexto = imperio.gdp ? imperio.gdp.toLocaleString('es-ES') + ' millones' : 'No especificado';
            const tamanioTexto = imperio.tamanio ? imperio.tamanio.toLocaleString('es-ES') + ' km²' : 'No especificado';

            const detallesHTML = `
                <div class="empire-details">
                    <div class="detail-row">
                        <span class="detail-label">Líder:</span>
                        <span class="detail-value">${imperio.lider || 'No especificado'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Población:</span>
                        <span class="detail-value">${poblacionTexto}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Ideología:</span>
                        <span class="detail-value">${imperio.ideologia || 'No especificada'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">GDP:</span>
                        <span class="detail-value">${gdpTexto}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Tamaño:</span>
                        <span class="detail-value">${tamanioTexto}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Fecha de Fundación:</span>
                        <span class="detail-value">${fechaTexto}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">En Guerra:</span>
                        <span class="detail-value">${imperio.enGuerra ? 'Sí' : 'No'}</span>
                    </div>
                    <div class="detail-row full-width">
                        <span class="detail-label">Descripción:</span>
                        <div class="detail-description">${imperio.descripcion || 'No hay descripción disponible.'}</div>
                    </div>
                </div>
            `;

            detailsContainer.innerHTML = detallesHTML;
            viewModal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al obtener el imperio:', error);
            mostrarAlerta('error', `Error al obtener el imperio: ${error.message}`);
        });
}

/**
 * Abre el modal de edición con los datos del imperio seleccionado
 */
function editarImperio(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para editarImperio");
        mostrarAlerta('error', 'No se puede editar el imperio: ID inválido.');
        return;
    }
    
    const form = document.getElementById('empireForm');
    const modalTitle = document.getElementById('empireModalTitle');
    const empireIdInput = document.getElementById('empireId');
    const modal = document.getElementById('empireModal');

    if (!form || !modalTitle || !empireIdInput || !modal) {
        console.error("Elementos del modal de edición no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Editar Imperio';
    empireIdInput.value = id;

    fetch(`/obtener_imperio?id=${id}`)
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
        .then(imperio => {
            if (!imperio) {
                throw new Error("No se recibió información del imperio para editar.");
            }

            document.getElementById('empireName').value = imperio.nombre || '';
            document.getElementById('empireLeader').value = imperio.lider || '';
            document.getElementById('empirePopulation').value = imperio.poblacion || '';
            document.getElementById('empireIdeology').value = imperio.ideologia || 'Monarquía';
            document.getElementById('empireGDP').value = imperio.gdp || '';
            document.getElementById('empireSize').value = imperio.tamanio || '';
            document.getElementById('empireDescription').value = imperio.descripcion || '';
            document.getElementById('empireAtWar').value = imperio.enGuerra ? 'true' : 'false';
            
            // Formatear fecha
            if (imperio.fechaCreacion) {
                const fecha = new Date(imperio.fechaCreacion);
                const fechaFormateada = fecha.toISOString().split('T')[0];
                document.getElementById('empireFoundationDate').value = fechaFormateada;
            } else {
                document.getElementById('empireFoundationDate').value = '';
            }

            modal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al cargar datos del imperio para editar:', error);
            mostrarAlerta('error', `Error al cargar datos del imperio: ${error.message}`);
            cerrarModal();
        });
}

/**
 * Elimina un imperio tras confirmación del usuario
 */
function eliminarImperio(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para eliminarImperio");
        mostrarAlerta('error', 'No se puede eliminar el imperio: ID inválido.');
        return;
    }
    
    if (confirm(`¿Está seguro que desea eliminar el imperio con ID ${id}? Esta acción no se puede deshacer.`)) {
        fetch(`/eliminar_imperio?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                    });
                }
                return res.text();
            })
            .then(resultado => {
                mostrarAlerta('success', `Imperio ${id} eliminado correctamente. ${resultado}`);
                cargarImperios();
            })
            .catch(error => {
                console.error('Error al eliminar imperio:', error);
                mostrarAlerta('error', `Error al eliminar imperio: ${error.message}`);
            });
    }
}

/**
 * Abre el modal para añadir un nuevo imperio
 */
function abrirModalAnadir() {
    const form = document.getElementById('empireForm');
    const modalTitle = document.getElementById('empireModalTitle');
    const empireIdInput = document.getElementById('empireId');
    const modal = document.getElementById('empireModal');

    if (!form || !modalTitle || !empireIdInput || !modal) {
        console.error("Elementos del modal de añadir no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Añadir Nuevo Imperio';
    empireIdInput.value = '';
    modal.style.display = 'block';
}

/**
 * Cierra el modal de añadir/editar imperio
 */
function cerrarModal() {
    const modal = document.getElementById('empireModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

/**
 * Cierra el modal de detalles del imperio
 */
function cerrarModalDetalles() {
    const modal = document.getElementById('viewEmpireModal');
    if (modal) {
        modal.style.display = 'none';
        const detailsContainer = document.getElementById('empireDetails');
        if (detailsContainer) detailsContainer.innerHTML = '';
    }
}

/**
 * Guarda un nuevo imperio o actualiza uno existente
 */
function guardarImperio() {
    const id = document.getElementById('empireId').value;
    const nombre = document.getElementById('empireName').value.trim();
    const lider = document.getElementById('empireLeader').value.trim();
    const poblacion = document.getElementById('empirePopulation').value;
    const ideologia = document.getElementById('empireIdeology').value;
    const gdp = document.getElementById('empireGDP').value;
    const tamanio = document.getElementById('empireSize').value;
    const fechaCreacion = document.getElementById('empireFoundationDate').value;
    const enGuerra = document.getElementById('empireAtWar').value;
    const descripcion = document.getElementById('empireDescription').value.trim();

    // Validación básica
    if (!nombre) {
        mostrarAlerta('error', 'El nombre del imperio es obligatorio.');
        return;
    }

    // Construir la URL con los parámetros
    let url = id ? '/actualizar_imperio?' : '/aniadir_imperio?';
    
    const params = new URLSearchParams();
    if (id) params.append('id', id);
    params.append('nombre', nombre);
    if (lider) params.append('lider', lider);
    if (poblacion) params.append('poblacion', poblacion);
    params.append('ideologia', ideologia);
    if (gdp) params.append('gdp', gdp);
    if (tamanio) params.append('tamanio', tamanio);
    if (fechaCreacion) params.append('fechaCreacion', fechaCreacion);
    params.append('enGuerra', enGuerra);
    if (descripcion) params.append('descripcion', descripcion);
    
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
            mostrarAlerta('success', id ? 'Imperio actualizado correctamente.' : 'Imperio añadido correctamente.');
            cerrarModal();
            cargarImperios();
        })
        .catch(error => {
            console.error('Error al guardar imperio:', error);
            mostrarAlerta('error', `Error al guardar imperio: ${error.message}`);
        });
}

/**
 * Aplica filtros seleccionados para filtrar imperios
 */
function aplicarFiltros() {
    const poblacionMinima = document.getElementById('filterPoblacion').value;
    const ideologia = document.getElementById('filterIdeologia').value;
    const enGuerraSelect = document.getElementById('filterEnGuerra');
    const enGuerra = enGuerraSelect && enGuerraSelect.value ? enGuerraSelect.value === 'true' : null;
    
    // Construir la URL con los parámetros
    let url = '/filtrar_imperios?';
    const params = new URLSearchParams();
    
    if (poblacionMinima && parseInt(poblacionMinima) > 0) params.append('poblacionMinima', poblacionMinima);
    if (ideologia) params.append('ideologia', ideologia);
    if (enGuerra !== null) params.append('enGuerra', enGuerra);
    
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
        .then(imperios => {
            let table = $('#empiresTable').DataTable();
            table.clear();
            
            if (Array.isArray(imperios)) {
                table.rows.add(imperios).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#empiresTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
                
                if (filterResults) {
                    filterResults.textContent = `Se encontraron ${imperios.length} imperios con los filtros seleccionados.`;
                }
            } else {
                console.error("La respuesta de filtrar_imperios no es un array:", imperios);
                mostrarAlerta('error', 'Error: Formato de datos de imperios inesperado.');
                if (filterResults) filterResults.textContent = '';
            }
        })
        .catch(error => {
            console.error('Error al filtrar imperios:', error);
            mostrarAlerta('error', `Error al filtrar imperios: ${error.message}`);
            if (filterResults) filterResults.textContent = '';
        });
}

/**
 * Resetea todos los filtros y carga todos los imperios
 */
function resetearFiltros() {
    const filterPoblacion = document.getElementById('filterPoblacion');
    const filterIdeologia = document.getElementById('filterIdeologia');
    const filterEnGuerra = document.getElementById('filterEnGuerra');
    
    if (filterPoblacion) filterPoblacion.value = '';
    if (filterIdeologia) filterIdeologia.value = '';
    if (filterEnGuerra) filterEnGuerra.value = '';
    
    const filterResults = document.getElementById('filterResults');
    if (filterResults) filterResults.textContent = '';
    
    cargarImperios();
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

// Inicialización cuando el DOM esté cargado
document.addEventListener('DOMContentLoaded', function() {
    /* Inicialización de DataTables */
    empiresTable = $('#empiresTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
        },
        responsive: true,
        columns: [
            { data: 'id' },
            { data: 'nombre' },
            { data: 'lider' },
            { 
                data: 'poblacion',
                render: function(data) {
                    if (data === null || data === undefined) return 'N/A';
                    return data.toLocaleString('es-ES') + ' habitantes';
                }
            },
            { data: 'ideologia' },
            { 
                data: 'gdp',
                render: function(data) {
                    if (data === null || data === undefined) return 'N/A';
                    return data.toLocaleString('es-ES') + ' M';
                }
            },
            {
                data: 'enGuerra',
                render: function(data) {
                    return data == 1 || data === true ? 'Sí' : 'No';
                }
            },
            {
                data: null,
                orderable: false,
                searchable: false,
                render: function(data, type, row) {
                    const imperioId = row.id;
                    if (imperioId === undefined || imperioId === null) {
                        console.error("ID de imperio no encontrado en los datos de la fila:", row);
                        return 'Error ID';
                    }
                    return `<div class="empire-actions">
                        <button class="view-btn" title="Ver detalles" onclick="verImperio(${imperioId})"><i class="fas fa-eye"></i></button>
                        <button class="edit-btn" title="Editar" onclick="editarImperio(${imperioId})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" title="Eliminar" onclick="eliminarImperio(${imperioId})"><i class="fas fa-trash-alt"></i></button>
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
        order: [[1, 'asc']], // Ordenar por nombre en lugar de ID
        // Configuración para solucionar problemas de visualización
        createdRow: function(row, data, dataIndex) {
            // Garantiza que todas las filas tengan el fondo correcto
            $(row).css('background-color', dataIndex % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
        }
    });

    /* Carga inicial de datos */
    cargarImperios();

    /* Configuración de eventos para modales */
    const addBtn = document.getElementById('addEmpireBtn');
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

    /* Configuración de eventos para cerrar modales */
    let closeButtons = document.querySelectorAll('.close-modal');
    closeButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            const empireModal = document.getElementById('empireModal');
            const viewEmpireModal = document.getElementById('viewEmpireModal');
            if (empireModal) empireModal.style.display = 'none';
            if (viewEmpireModal) viewEmpireModal.style.display = 'none';
        });
    });

    /* Configuración del formulario */
    const empireForm = document.getElementById('empireForm');
    if (empireForm) {
        empireForm.addEventListener('submit', function(e) {
            e.preventDefault();
            guardarImperio();
        });
    }

    /* Cerrar modal al hacer clic fuera del contenido */
    window.addEventListener('click', function(event) {
        const empireModal = document.getElementById('empireModal');
        const viewEmpireModal = document.getElementById('viewEmpireModal');
        if (event.target == empireModal) {
            cerrarModal();
        }
        if (event.target == viewEmpireModal) {
            cerrarModalDetalles();
        }
    });
    
    // Corregir visualización de la tabla
    fixTableDisplay();
});