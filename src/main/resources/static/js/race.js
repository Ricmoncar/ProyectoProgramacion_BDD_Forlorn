document.addEventListener('DOMContentLoaded', function() {
    /* Inicialización de DataTables */
    let racesTable = $('#racesTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
        },
        responsive: true,
        columns: [
            { data: 'id' },
            { data: 'nombre' },
            { 
                data: 'alturaPromedia',
                render: function(data) {
                    return data !== null ? `${data.toFixed(2)} m` : 'N/A';
                }
            },
            { 
                data: 'anchoPromedio',
                render: function(data) {
                    return data !== null ? `${data.toFixed(2)} m` : 'N/A';
                }
            },
            { 
                data: 'fechaConcepcion',
                render: function(data) {
                    if (!data) return 'N/A';
                    const fecha = new Date(data);
                    return fecha.toLocaleDateString('es-ES');
                }
            },
            { 
                data: 'estadisticasBase',
                render: function(data, type, row) {
                    if (!data) return 'N/A';
                    return `<button class="stats-btn" onclick="verEstadisticas(${row.id})">Ver estadísticas</button>`;
                }
            },
            {
                data: null,
                orderable: false,
                searchable: false,
                render: function(data, type, row) {
                    const razaId = row.id;
                    if (razaId === undefined || razaId === null) {
                        console.error("ID de raza no encontrado en los datos de la fila:", row);
                        return 'Error ID';
                    }
                    return `<div class="race-actions">
                        <button class="view-btn" title="Ver detalles" onclick="verRaza(${razaId})"><i class="fas fa-eye"></i></button>
                        <button class="edit-btn" title="Editar" onclick="editarRaza(${razaId})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" title="Eliminar" onclick="eliminarRaza(${razaId})"><i class="fas fa-trash-alt"></i></button>
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
    cargarRazas();

    /* Configuración de eventos para modales */
    const addBtn = document.getElementById('addRaceBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const closeViewBtn = document.getElementById('closeViewBtn');

    if (addBtn) addBtn.addEventListener('click', abrirModalAnadir);
    if (cancelBtn) cancelBtn.addEventListener('click', cerrarModal);
    if (closeViewBtn) closeViewBtn.addEventListener('click', cerrarModalDetalles);

    /* Configuración de eventos para filtros */
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    const resetFiltersBtn = document.getElementById('resetFiltersBtn');
    const alturaSlider = document.getElementById('filterAltura');
    const anchoSlider = document.getElementById('filterAncho');
    const alturaValue = document.getElementById('alturaValue');
    const anchoValue = document.getElementById('anchoValue');

    if (applyFiltersBtn) applyFiltersBtn.addEventListener('click', aplicarFiltros);
    if (resetFiltersBtn) resetFiltersBtn.addEventListener('click', resetearFiltros);
    
    /* Configuración de los sliders */
    if (alturaSlider && alturaValue) {
        alturaSlider.addEventListener('input', function() {
            alturaValue.textContent = `${this.value}m`;
        });
    }
    
    if (anchoSlider && anchoValue) {
        anchoSlider.addEventListener('input', function() {
            anchoValue.textContent = `${this.value}m`;
        });
    }

    /* Configuración de eventos para cerrar modales */
    let closeButtons = document.querySelectorAll('.close-modal');
    closeButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            const raceModal = document.getElementById('raceModal');
            const viewRaceModal = document.getElementById('viewRaceModal');
            if (raceModal) raceModal.style.display = 'none';
            if (viewRaceModal) viewRaceModal.style.display = 'none';
        });
    });

    /* Configuración del formulario */
    const raceForm = document.getElementById('raceForm');
    if (raceForm) {
        raceForm.addEventListener('submit', function(e) {
            e.preventDefault();
            guardarRaza();
        });
    }

    /* Cerrar modal al hacer clic fuera del contenido */
    window.addEventListener('click', function(event) {
        const raceModal = document.getElementById('raceModal');
        const viewRaceModal = document.getElementById('viewRaceModal');
        if (event.target == raceModal) {
            cerrarModal();
        }
        if (event.target == viewRaceModal) {
            cerrarModalDetalles();
        }
    });
    
    // Corregir visualización de la tabla
    fixTableDisplay();
});

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
 * Carga la lista de razas desde el servidor
 */
function cargarRazas() {
    fetch("/listar_razas")
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
        .then(razas => {
            let table = $('#racesTable').DataTable();
            table.clear();
            if (Array.isArray(razas)) {
                table.rows.add(razas).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#racesTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
            } else {
                console.error("La respuesta de listar_razas no es un array:", razas);
                mostrarAlerta('error', 'Error: Formato de datos de razas inesperado.');
            }
        })
        .catch(error => {
            console.error('Error al cargar razas:', error);
            mostrarAlerta('error', `No se pudieron cargar las razas: ${error.message}`);
        });
}

/**
 * Muestra los detalles de una raza específica
 */
function verRaza(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para verRaza");
        mostrarAlerta('error', 'No se puede mostrar la raza: ID inválido.');
        return;
    }
    fetch(`/obtener_raza?id=${id}`)
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
        .then(raza => {
            if (!raza) {
                throw new Error("No se recibió información de la raza.");
            }
            
            const viewTitle = document.getElementById('viewRaceTitle');
            const detailsContainer = document.getElementById('raceDetails');
            const viewModal = document.getElementById('viewRaceModal');

            if (!viewTitle || !detailsContainer || !viewModal) {
                console.error("Elementos del modal de vista no encontrados.");
                return;
            }

            viewTitle.textContent = raza.nombre || 'Detalles de la Raza';

            // Formatear fecha
            let fechaTexto = 'No especificada';
            if (raza.fechaConcepcion) {
                const fecha = new Date(raza.fechaConcepcion);
                fechaTexto = fecha.toLocaleDateString('es-ES');
            }

            // Formatear altura y ancho
            const alturaTexto = raza.alturaPromedia !== null ? `${raza.alturaPromedia.toFixed(2)} m` : 'No especificada';
            const anchoTexto = raza.anchoPromedio !== null ? `${raza.anchoPromedio.toFixed(2)} m` : 'No especificado';

            let estadisticasHTML = 'No disponible';
            if (raza.estadisticasBase) {
                const stats = raza.estadisticasBase;
                estadisticasHTML = `
                    <div class="stats-display">
                        <div class="stat-item">ATK: <span>${stats.atk || 0}</span></div>
                        <div class="stat-item">DEF: <span>${stats.def || 0}</span></div>
                        <div class="stat-item">HP: <span>${stats.hp || 0}</span></div>
                        <div class="stat-item">SPE: <span>${stats.spe || 0}</span></div>
                        <div class="stat-item">MAT: <span>${stats.mat || 0}</span></div>
                        <div class="stat-item">MDF: <span>${stats.mdf || 0}</span></div>
                        <div class="stat-item">XP: <span>${stats.xp || 0}</span></div>
                        <div class="stat-item">LVL: <span>${stats.lvl || 1}</span></div>
                    </div>
                `;
            }

            const detallesHTML = `
                <div class="race-details">
                    <div class="detail-row">
                        <span class="detail-label">Altura Promedio:</span>
                        <span class="detail-value">${alturaTexto}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Ancho Promedio:</span>
                        <span class="detail-value">${anchoTexto}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Fecha Concepción:</span>
                        <span class="detail-value">${fechaTexto}</span>
                    </div>
                    <div class="detail-row full-width">
                        <span class="detail-label">Descripción Física:</span>
                        <div class="detail-description">${raza.descripcionFisica || 'No hay descripción disponible.'}</div>
                    </div>
                    <div class="detail-row full-width">
                        <span class="detail-label">Estadísticas Base:</span>
                        <div class="detail-stats">${estadisticasHTML}</div>
                    </div>
                </div>
            `;

            detailsContainer.innerHTML = detallesHTML;
            viewModal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al obtener la raza:', error);
            mostrarAlerta('error', `Error al obtener la raza: ${error.message}`);
        });
}

/**
 * Abre el modal de edición con los datos de la raza seleccionada
 */
function editarRaza(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para editarRaza");
        mostrarAlerta('error', 'No se puede editar la raza: ID inválido.');
        return;
    }
    
    const form = document.getElementById('raceForm');
    const modalTitle = document.getElementById('raceModalTitle');
    const raceIdInput = document.getElementById('raceId');
    const modal = document.getElementById('raceModal');

    if (!form || !modalTitle || !raceIdInput || !modal) {
        console.error("Elementos del modal de edición no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Editar Raza';
    raceIdInput.value = id;

    fetch(`/obtener_raza?id=${id}`)
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
        .then(raza => {
            if (!raza) {
                throw new Error("No se recibió información de la raza para editar.");
            }

            document.getElementById('raceName').value = raza.nombre || '';
            document.getElementById('raceHeight').value = raza.alturaPromedia || '';
            document.getElementById('raceWidth').value = raza.anchoPromedio || '';
            document.getElementById('racePhysicalDesc').value = raza.descripcionFisica || '';
            
            // Formatear fecha
            if (raza.fechaConcepcion) {
                const fecha = new Date(raza.fechaConcepcion);
                const fechaFormateada = fecha.toISOString().split('T')[0];
                document.getElementById('raceConceptionDate').value = fechaFormateada;
            } else {
                document.getElementById('raceConceptionDate').value = '';
            }
            
            // Cargar estadísticas
            if (raza.estadisticasBase) {
                const stats = raza.estadisticasBase;
                document.getElementById('statATK').value = stats.atk || 10;
                document.getElementById('statDEF').value = stats.def || 10;
                document.getElementById('statHP').value = stats.hp || 10;
                document.getElementById('statSPE').value = stats.spe || 10;
                document.getElementById('statMAT').value = stats.mat || 10;
                document.getElementById('statMDF').value = stats.mdf || 10;
            }

            modal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al cargar datos de la raza para editar:', error);
            mostrarAlerta('error', `Error al cargar datos de la raza: ${error.message}`);
            cerrarModal();
        });
}

/**
 * Elimina una raza tras confirmación del usuario
 */
function eliminarRaza(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para eliminarRaza");
        mostrarAlerta('error', 'No se puede eliminar la raza: ID inválido.');
        return;
    }
    
    if (confirm(`¿Está seguro que desea eliminar la raza con ID ${id}? Esta acción no se puede deshacer.`)) {
        fetch(`/eliminar_raza?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                    });
                }
                return res.text();
            })
            .then(resultado => {
                mostrarAlerta('success', `Raza ${id} eliminada correctamente. ${resultado}`);
                cargarRazas();
            })
            .catch(error => {
                console.error('Error al eliminar raza:', error);
                mostrarAlerta('error', `Error al eliminar raza: ${error.message}`);
            });
    }
}

/**
 * Abre el modal para añadir una nueva raza
 */
function abrirModalAnadir() {
    const form = document.getElementById('raceForm');
    const modalTitle = document.getElementById('raceModalTitle');
    const raceIdInput = document.getElementById('raceId');
    const modal = document.getElementById('raceModal');

    if (!form || !modalTitle || !raceIdInput || !modal) {
        console.error("Elementos del modal de añadir no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Añadir Nueva Raza';
    raceIdInput.value = '';
    
    // Establecer valores predeterminados para estadísticas
    document.getElementById('statATK').value = 10;
    document.getElementById('statDEF').value = 10;
    document.getElementById('statHP').value = 10;
    document.getElementById('statSPE').value = 10;
    document.getElementById('statMAT').value = 10;
    document.getElementById('statMDF').value = 10;
    
    modal.style.display = 'block';
}

/**
 * Cierra el modal de añadir/editar raza
 */
function cerrarModal() {
    const modal = document.getElementById('raceModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

/**
 * Cierra el modal de detalles de la raza
 */
function cerrarModalDetalles() {
    const modal = document.getElementById('viewRaceModal');
    if (modal) {
        modal.style.display = 'none';
        const detailsContainer = document.getElementById('raceDetails');
        if (detailsContainer) detailsContainer.innerHTML = '';
    }
}

/**
 * Muestra las estadísticas de la raza
 */
function verEstadisticas(id) {
    verRaza(id);
}

/**
 * Guarda una nueva raza o actualiza una existente
 */
function guardarRaza() {
    const id = document.getElementById('raceId').value;
    const nombre = document.getElementById('raceName').value.trim();
    const alturaPromedia = document.getElementById('raceHeight').value;
    const anchoPromedio = document.getElementById('raceWidth').value;
    const fechaConcepcion = document.getElementById('raceConceptionDate').value;
    const descripcionFisica = document.getElementById('racePhysicalDesc').value.trim();
    
    // Estadísticas
    const atk = document.getElementById('statATK').value;
    const def = document.getElementById('statDEF').value;
    const hp = document.getElementById('statHP').value;
    const spe = document.getElementById('statSPE').value;
    const mat = document.getElementById('statMAT').value;
    const mdf = document.getElementById('statMDF').value;

    // Validación básica
    if (!nombre) {
        mostrarAlerta('error', 'El nombre de la raza es obligatorio.');
        return;
    }

    // Construir la URL con los parámetros
    let url = id ? '/actualizar_raza?' : '/aniadir_raza?';
    
    const params = new URLSearchParams();
    if (id) params.append('id', id);
    params.append('nombre', nombre);
    if (alturaPromedia) params.append('alturaPromedia', alturaPromedia);
    if (anchoPromedio) params.append('anchoPromedio', anchoPromedio);
    if (fechaConcepcion) params.append('fechaConcepcion', fechaConcepcion);
    if (descripcionFisica) params.append('descripcionFisica', descripcionFisica);
    
    // Parámetros de estadísticas
    params.append('atk', atk || 10);
    params.append('def', def || 10);
    params.append('hp', hp || 10);
    params.append('spe', spe || 10);
    params.append('mat', mat || 10);
    params.append('mdf', mdf || 10);
    
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
            mostrarAlerta('success', id ? 'Raza actualizada correctamente.' : 'Raza añadida correctamente.');
            cerrarModal();
            cargarRazas();
        })
        .catch(error => {
            console.error('Error al guardar raza:', error);
            mostrarAlerta('error', `Error al guardar raza: ${error.message}`);
        });
}

/**
 * Aplica filtros seleccionados para filtrar razas
 */
function aplicarFiltros() {
    const alturaMinima = document.getElementById('filterAltura').value;
    const anchoMinimo = document.getElementById('filterAncho').value;
    
    // Construir la URL con los parámetros
    let url = '/filtrar_razas?';
    const params = new URLSearchParams();
    
    if (alturaMinima && parseFloat(alturaMinima) > 0) params.append('alturaMinima', alturaMinima);
    if (anchoMinimo && parseFloat(anchoMinimo) > 0) params.append('anchoMinimo', anchoMinimo);
    
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
        .then(razas => {
            let table = $('#racesTable').DataTable();
            table.clear();
            
            if (Array.isArray(razas)) {
                table.rows.add(razas).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#racesTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
                
                if (filterResults) {
                    filterResults.textContent = `Se encontraron ${razas.length} razas con los filtros seleccionados.`;
                }
            } else {
                console.error("La respuesta de filtrar_razas no es un array:", razas);
                mostrarAlerta('error', 'Error: Formato de datos de razas inesperado.');
                if (filterResults) filterResults.textContent = '';
            }
        })
        .catch(error => {
            console.error('Error al filtrar razas:', error);
            mostrarAlerta('error', `Error al filtrar razas: ${error.message}`);
            if (filterResults) filterResults.textContent = '';
        });
}

/**
 * Resetea todos los filtros y carga todas las razas
 */
function resetearFiltros() {
    const filterAltura = document.getElementById('filterAltura');
    const filterAncho = document.getElementById('filterAncho');
    const alturaValue = document.getElementById('alturaValue');
    const anchoValue = document.getElementById('anchoValue');
    
    if (filterAltura) {
        filterAltura.value = 0;
        if (alturaValue) alturaValue.textContent = '0m';
    }
    
    if (filterAncho) {
        filterAncho.value = 0;
        if (anchoValue) anchoValue.textContent = '0m';
    }
    
    const filterResults = document.getElementById('filterResults');
    if (filterResults) filterResults.textContent = '';
    
    cargarRazas();
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
