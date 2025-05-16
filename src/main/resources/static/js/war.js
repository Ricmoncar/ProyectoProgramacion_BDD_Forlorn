document.addEventListener('DOMContentLoaded', function() {
    /* Inicialización de DataTables */
    let warsTable = $('#warsTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
        },
        responsive: true,
        columns: [
            { data: 'id' },
            { data: 'nombre' },
            { 
                data: 'fechaInicio',
                render: function(data) {
                    if (!data) return 'N/A';
                    const fecha = new Date(data);
                    return fecha.toLocaleDateString('es-ES');
                }
            },
            { 
                data: 'fechaFin',
                render: function(data) {
                    if (!data) return 'Activo';
                    const fecha = new Date(data);
                    return fecha.toLocaleDateString('es-ES');
                }
            },
            {
                data: null,
                render: function(data, type, row) {
                    if (!row.fechaFin) {
                        return '<span class="status-active">Activo</span>';
                    } else {
                        return '<span class="status-ended">Finalizado</span>';
                    }
                }
            },
            {
                data: 'imperiosParticipantes',
                render: function(data) {
                    if (!data || !Array.isArray(data) || data.length === 0) return 'N/A';
                    // Mostrar solo los nombres de los imperios, máximo 3
                    const nombres = data.map(p => p.imperioNombre).slice(0, 3);
                    const totalImperios = data.length;
                    return nombres.join(', ') + (totalImperios > 3 ? ` y ${totalImperios - 3} más` : '');
                }
            },
            {
                data: null,
                orderable: false,
                searchable: false,
                render: function(data, type, row) {
                    const guerraId = row.id;
                    if (guerraId === undefined || guerraId === null) {
                        console.error("ID de guerra no encontrado en los datos de la fila:", row);
                        return 'Error ID';
                    }
                    return `<div class="war-actions">
                        <button class="view-btn" title="Ver detalles" onclick="verGuerra(${guerraId})"><i class="fas fa-eye"></i></button>
                        <button class="edit-btn" title="Editar" onclick="editarGuerra(${guerraId})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" title="Eliminar" onclick="eliminarGuerra(${guerraId})"><i class="fas fa-trash-alt"></i></button>
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

    /* Variables para manejar los participantes en una guerra */
    let imperiosDisponibles = [];
    let participantesGuerra = [];

    /* Carga inicial de datos */
    cargarImperios();
    cargarGuerras();

    /* Configuración de eventos para modales */
    const addBtn = document.getElementById('addWarBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const closeViewBtn = document.getElementById('closeViewBtn');
    const addParticipantBtn = document.getElementById('addParticipantBtn');
    const cancelParticipantBtn = document.getElementById('cancelParticipantBtn');

    if (addBtn) addBtn.addEventListener('click', abrirModalAnadir);
    if (cancelBtn) cancelBtn.addEventListener('click', cerrarModal);
    if (closeViewBtn) closeViewBtn.addEventListener('click', cerrarModalDetalles);
    if (addParticipantBtn) addParticipantBtn.addEventListener('click', abrirModalParticipante);
    if (cancelParticipantBtn) cancelParticipantBtn.addEventListener('click', cerrarModalParticipante);

    /* Configuración de eventos para filtros */
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    const resetFiltersBtn = document.getElementById('resetFiltersBtn');

    if (applyFiltersBtn) applyFiltersBtn.addEventListener('click', aplicarFiltros);
    if (resetFiltersBtn) resetFiltersBtn.addEventListener('click', resetearFiltros);

    /* Configuración de eventos para cerrar modales */
    let closeButtons = document.querySelectorAll('.close-modal');
    closeButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            const warModal = document.getElementById('warModal');
            const viewWarModal = document.getElementById('viewWarModal');
            const participantModal = document.getElementById('participantModal');
            if (warModal) warModal.style.display = 'none';
            if (viewWarModal) viewWarModal.style.display = 'none';
            if (participantModal) participantModal.style.display = 'none';
        });
    });

    /* Configuración de los formularios */
    const warForm = document.getElementById('warForm');
    if (warForm) {
        warForm.addEventListener('submit', function(e) {
            e.preventDefault();
            guardarGuerra();
        });
    }

    const participantForm = document.getElementById('participantForm');
    if (participantForm) {
        participantForm.addEventListener('submit', function(e) {
            e.preventDefault();
            agregarParticipante();
        });
    }

    /* Cerrar modal al hacer clic fuera del contenido */
    window.addEventListener('click', function(event) {
        const warModal = document.getElementById('warModal');
        const viewWarModal = document.getElementById('viewWarModal');
        const participantModal = document.getElementById('participantModal');
        if (event.target == warModal) {
            cerrarModal();
        }
        if (event.target == viewWarModal) {
            cerrarModalDetalles();
        }
        if (event.target == participantModal) {
            cerrarModalParticipante();
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
 * Carga la lista de imperios desde el servidor
 */
function cargarImperios() {
    fetch("http://localhost:8080/listar_imperios")
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
            imperiosDisponibles = imperios;
            
            // Añadir imperios a los selectores
            const filterEmpire = document.getElementById('filterEmpire');
            const participantEmpire = document.getElementById('participantEmpire');
            
            if (filterEmpire) {
                filterEmpire.innerHTML = '<option value="">Todos</option>';
                imperios.forEach(imperio => {
                    const option = document.createElement('option');
                    option.value = imperio.id;
                    option.textContent = imperio.nombre;
                    filterEmpire.appendChild(option);
                });
            }
            
            if (participantEmpire) {
                participantEmpire.innerHTML = '<option value="">Seleccione un imperio</option>';
                imperios.forEach(imperio => {
                    const option = document.createElement('option');
                    option.value = imperio.id;
                    option.textContent = imperio.nombre;
                    participantEmpire.appendChild(option);
                });
            }
        })
        .catch(error => {
            console.error('Error al cargar imperios:', error);
            mostrarAlerta('error', `No se pudieron cargar los imperios: ${error.message}`);
        });
}

/**
 * Carga la lista de guerras desde el servidor
 */
function cargarGuerras() {
    fetch("http://localhost:8080/listar_guerras")
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
        .then(guerras => {
            let table = $('#warsTable').DataTable();
            table.clear();
            if (Array.isArray(guerras)) {
                table.rows.add(guerras).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#warsTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
            } else {
                console.error("La respuesta de listar_guerras no es un array:", guerras);
                mostrarAlerta('error', 'Error: Formato de datos de guerras inesperado.');
            }
        })
        .catch(error => {
            console.error('Error al cargar guerras:', error);
            mostrarAlerta('error', `No se pudieron cargar las guerras: ${error.message}`);
        });
}

/**
 * Abre el modal para añadir un nuevo participante a la guerra
 */
function abrirModalParticipante() {
    const form = document.getElementById('participantForm');
    const modal = document.getElementById('participantModal');

    if (!form || !modal) {
        console.error("Elementos del modal de participante no encontrados.");
        return;
    }

    form.reset();
    
    // Filtrar los imperios ya añadidos
    const participantEmpire = document.getElementById('participantEmpire');
    if (participantEmpire) {
        const imperiosYaAnadidos = participantesGuerra.map(p => parseInt(p.imperioId));
        
        participantEmpire.innerHTML = '<option value="">Seleccione un imperio</option>';
        imperiosDisponibles.forEach(imperio => {
            // Solo mostrar imperios que no estén ya añadidos
            if (!imperiosYaAnadidos.includes(imperio.id)) {
                const option = document.createElement('option');
                option.value = imperio.id;
                option.textContent = imperio.nombre;
                participantEmpire.appendChild(option);
            }
        });
    }
    
    modal.style.display = 'block';
}

/**
 * Agrega un participante a la guerra actual
 */
function agregarParticipante() {
    const imperioId = document.getElementById('participantEmpire').value;
    const rol = document.getElementById('participantRole').value;
    const ganador = document.getElementById('participantWinner').value;
    
    if (!imperioId) {
        mostrarAlerta('error', 'Debe seleccionar un imperio.');
        return;
    }
    
    // Encontrar el nombre del imperio
    const imperio = imperiosDisponibles.find(imp => imp.id == imperioId);
    if (!imperio) {
        mostrarAlerta('error', 'Imperio no encontrado.');
        return;
    }
    
    // Crear el objeto participante
    const participante = {
        imperioId: imperioId,
        imperioNombre: imperio.nombre,
        rol: rol,
        ganador: ganador === 'true' ? true : ganador === 'false' ? false : null
    };
    
    // Añadir a la lista de participantes
    participantesGuerra.push(participante);
    
    // Actualizar la interfaz
    actualizarListaParticipantes();
    
    // Cerrar el modal
    cerrarModalParticipante();
    
    mostrarAlerta('success', `Imperio "${imperio.nombre}" añadido como "${rol}".`);
}

/**
 * Actualiza la lista visual de participantes en el modal de guerra
 */
function actualizarListaParticipantes() {
    const container = document.getElementById('imperiosParticipantes');
    if (!container) return;
    
    // Limpiar el contenedor
    container.innerHTML = '';
    
    if (participantesGuerra.length === 0) {
        container.innerHTML = '<p>No hay imperios participantes añadidos.</p>';
        return;
    }
    
    // Crear la tabla de participantes
    const table = document.createElement('table');
    table.className = 'participant-table';
    
    // Cabecera de la tabla
    const thead = document.createElement('thead');
    thead.innerHTML = `
        <tr>
            <th>Imperio</th>
            <th>Rol</th>
            <th>Ganador</th>
            <th>Acciones</th>
        </tr>
    `;
    table.appendChild(thead);
    
    // Cuerpo de la tabla
    const tbody = document.createElement('tbody');
    participantesGuerra.forEach((p, index) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${p.imperioNombre}</td>
            <td>${p.rol}</td>
            <td>${p.ganador === true ? 'Sí' : p.ganador === false ? 'No' : 'Sin determinar'}</td>
            <td>
                <button type="button" class="btn-icon delete-participant" data-index="${index}">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
    table.appendChild(tbody);
    
    container.appendChild(table);
    
    // Añadir eventos a los botones de eliminar
    document.querySelectorAll('.delete-participant').forEach(btn => {
        btn.addEventListener('click', function() {
            const index = parseInt(this.getAttribute('data-index'));
            eliminarParticipante(index);
        });
    });
}

/**
 * Elimina un participante de la lista actual
 */
function eliminarParticipante(index) {
    if (index >= 0 && index < participantesGuerra.length) {
        const nombreImperio = participantesGuerra[index].imperioNombre;
        participantesGuerra.splice(index, 1);
        actualizarListaParticipantes();
        mostrarAlerta('info', `Imperio "${nombreImperio}" eliminado de los participantes.`);
    }
}

/**
 * Cierra el modal de añadir participante
 */
function cerrarModalParticipante() {
    const modal = document.getElementById('participantModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

/**
 * Muestra los detalles de una guerra específica
 */
function verGuerra(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para verGuerra");
        mostrarAlerta('error', 'No se puede mostrar la guerra: ID inválido.');
        return;
    }
    fetch(`http://localhost:8080/obtener_guerra?id=${id}`)
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
        .then(guerra => {
            if (!guerra) {
                throw new Error("No se recibió información de la guerra.");
            }
            
            const viewTitle = document.getElementById('viewWarTitle');
            const detailsContainer = document.getElementById('warDetails');
            const viewModal = document.getElementById('viewWarModal');

            if (!viewTitle || !detailsContainer || !viewModal) {
                console.error("Elementos del modal de vista no encontrados.");
                return;
            }

            viewTitle.textContent = guerra.nombre || 'Detalles del Conflicto';

            // Formatear fechas
            let fechaInicioTexto = 'No especificada';
            if (guerra.fechaInicio) {
                const fechaInicio = new Date(guerra.fechaInicio);
                fechaInicioTexto = fechaInicio.toLocaleDateString('es-ES');
            }
            
            let fechaFinTexto = 'Conflicto activo';
            let estadoGuerra = '<span class="status-active">Activo</span>';
            if (guerra.fechaFin) {
                const fechaFin = new Date(guerra.fechaFin);
                fechaFinTexto = fechaFin.toLocaleDateString('es-ES');
                estadoGuerra = '<span class="status-ended">Finalizado</span>';
            }

            // Renderizar participantes
            let participantesHTML = '<p>No hay información sobre los participantes.</p>';
            if (guerra.imperiosParticipantes && guerra.imperiosParticipantes.length > 0) {
                participantesHTML = `
                    <table class="participant-table view-table">
                        <thead>
                            <tr>
                                <th>Imperio</th>
                                <th>Rol</th>
                                <th>Ganador</th>
                            </tr>
                        </thead>
                        <tbody>
                `;
                
                guerra.imperiosParticipantes.forEach(p => {
                    let resultadoTexto = 'Sin determinar';
                    if (p.ganador === true) resultadoTexto = 'Ganador';
                    else if (p.ganador === false) resultadoTexto = 'Derrotado';
                    
                    participantesHTML += `
                        <tr>
                            <td>${p.imperioNombre}</td>
                            <td>${p.rol}</td>
                            <td>${resultadoTexto}</td>
                        </tr>
                    `;
                });
                
                participantesHTML += `
                        </tbody>
                    </table>
                `;
            }

            const detallesHTML = `
                <div class="war-details">
                    <div class="detail-row">
                        <span class="detail-label">Fecha de Inicio:</span>
                        <span class="detail-value">${fechaInicioTexto}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Fecha de Fin:</span>
                        <span class="detail-value">${fechaFinTexto}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Estado:</span>
                        <span class="detail-value">${estadoGuerra}</span>
                    </div>
                    <div class="detail-row full-width">
                        <span class="detail-label">Descripción:</span>
                        <div class="detail-description">${guerra.descripcion || 'No hay descripción disponible.'}</div>
                    </div>
                    <div class="detail-row full-width">
                        <span class="detail-label">Imperios Participantes:</span>
                        <div class="detail-participants">
                            ${participantesHTML}
                        </div>
                    </div>
                </div>
            `;

            detailsContainer.innerHTML = detallesHTML;
            viewModal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al obtener la guerra:', error);
            mostrarAlerta('error', `Error al obtener el conflicto: ${error.message}`);
        });
}

/**
 * Abre el modal de edición con los datos de la guerra seleccionada
 */
function editarGuerra(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para editarGuerra");
        mostrarAlerta('error', 'No se puede editar la guerra: ID inválido.');
        return;
    }
    
    const form = document.getElementById('warForm');
    const modalTitle = document.getElementById('warModalTitle');
    const warIdInput = document.getElementById('warId');
    const modal = document.getElementById('warModal');

    if (!form || !modalTitle || !warIdInput || !modal) {
        console.error("Elementos del modal de edición no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Editar Conflicto';
    warIdInput.value = id;
    
    // Resetear la lista de participantes
    participantesGuerra = [];

    fetch(`http://localhost:8080/obtener_guerra?id=${id}`)
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
        .then(guerra => {
            if (!guerra) {
                throw new Error("No se recibió información de la guerra para editar.");
            }

            document.getElementById('warName').value = guerra.nombre || '';
            document.getElementById('warDescription').value = guerra.descripcion || '';
            
            // Formatear fechas
            if (guerra.fechaInicio) {
                const fechaInicio = new Date(guerra.fechaInicio);
                const fechaInicioFormateada = fechaInicio.toISOString().split('T')[0];
                document.getElementById('warStartDate').value = fechaInicioFormateada;
            } else {
                document.getElementById('warStartDate').value = '';
            }
            
            if (guerra.fechaFin) {
                const fechaFin = new Date(guerra.fechaFin);
                const fechaFinFormateada = fechaFin.toISOString().split('T')[0];
                document.getElementById('warEndDate').value = fechaFinFormateada;
            } else {
                document.getElementById('warEndDate').value = '';
            }
            
            // Cargar participantes
            if (guerra.imperiosParticipantes && guerra.imperiosParticipantes.length > 0) {
                participantesGuerra = [...guerra.imperiosParticipantes];
                actualizarListaParticipantes();
            }

            modal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al cargar datos de la guerra para editar:', error);
            mostrarAlerta('error', `Error al cargar datos del conflicto: ${error.message}`);
            cerrarModal();
        });
}

/**
 * Elimina una guerra tras confirmación del usuario
 */
function eliminarGuerra(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para eliminarGuerra");
        mostrarAlerta('error', 'No se puede eliminar la guerra: ID inválido.');
        return;
    }
    
    if (confirm(`¿Está seguro que desea eliminar la guerra con ID ${id}? Esta acción no se puede deshacer.`)) {
        fetch(`http://localhost:8080/eliminar_guerra?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                    });
                }
                return res.text();
            })
            .then(resultado => {
                mostrarAlerta('success', `Conflicto ${id} eliminado correctamente. ${resultado}`);
                cargarGuerras();
            })
            .catch(error => {
                console.error('Error al eliminar la guerra:', error);
                mostrarAlerta('error', `Error al eliminar conflicto: ${error.message}`);
            });
    }
}

/**
 * Abre el modal para añadir una nueva guerra
 */
function abrirModalAnadir() {
    const form = document.getElementById('warForm');
    const modalTitle = document.getElementById('warModalTitle');
    const warIdInput = document.getElementById('warId');
    const modal = document.getElementById('warModal');

    if (!form || !modalTitle || !warIdInput || !modal) {
        console.error("Elementos del modal de añadir no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Registrar Nueva Guerra';
    warIdInput.value = '';
    
    // Limpiar la lista de participantes
    participantesGuerra = [];
    actualizarListaParticipantes();
    
    modal.style.display = 'block';
}

/**
 * Cierra el modal de añadir/editar guerra
 */
function cerrarModal() {
    const modal = document.getElementById('warModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

/**
 * Cierra el modal de detalles de la guerra
 */
function cerrarModalDetalles() {
    const modal = document.getElementById('viewWarModal');
    if (modal) {
        modal.style.display = 'none';
        const detailsContainer = document.getElementById('warDetails');
        if (detailsContainer) detailsContainer.innerHTML = '';
    }
}

/**
 * Guarda una nueva guerra o actualiza una existente
 */
/**
 * Guarda una nueva guerra o actualiza una existente
 */
function guardarGuerra() {
    const id = document.getElementById('warId').value;
    const nombre = document.getElementById('warName').value.trim();
    const fechaInicio = document.getElementById('warStartDate').value;
    const fechaFin = document.getElementById('warEndDate').value;
    const descripcion = document.getElementById('warDescription').value.trim();

    // Validación básica
    if (!nombre) {
        mostrarAlerta('error', 'El nombre del conflicto es obligatorio.');
        return;
    }
    
    if (!fechaInicio) {
        mostrarAlerta('error', 'La fecha de inicio es obligatoria.');
        return;
    }
    
    if (fechaFin && new Date(fechaFin) < new Date(fechaInicio)) {
        mostrarAlerta('error', 'La fecha de finalización no puede ser anterior a la fecha de inicio.');
        return;
    }

    // Construir el objeto guerra
    const guerra = {
        id: id || null,
        nombre: nombre,
        fechaInicio: fechaInicio,
        fechaFin: fechaFin || null,
        descripcion: descripcion,
        imperiosParticipantes: participantesGuerra
    };

    // Enviar la petición 
    const url = id ? 'http://localhost:8080/actualizar_guerra?' : 'http://localhost:8080/aniadir_guerra?';
    
    const params = new URLSearchParams();
    if (id) params.append('id', id);
    params.append('nombre', nombre);
    params.append('fechaInicio', fechaInicio);
    if (fechaFin) params.append('fechaFin', fechaFin);
    if (descripcion) params.append('descripcion', descripcion);
    
    const finalUrl = url + params.toString();
    
    fetch(finalUrl)
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                });
            }
            return res.text();
        })
        .then(resultado => {
            mostrarAlerta('warning', 'Nota: Los participantes no se han guardado aún. Esta funcionalidad se implementará próximamente.');
            mostrarAlerta('success', id ? 'Conflicto actualizado correctamente.' : 'Conflicto registrado correctamente.');
            cerrarModal();
            cargarGuerras();
        })
        .catch(error => {
            console.error('Error al guardar la guerra:', error);
            mostrarAlerta('error', `Error al guardar conflicto: ${error.message}`);
        });
}
/**
 * Aplica filtros seleccionados para filtrar guerras
 */
function aplicarFiltros() {
    const estado = document.getElementById('filterStatus').value;
    const imperioId = document.getElementById('filterEmpire').value;
    
    // Construir la URL con los parámetros
    let url = 'http://localhost:8080/filtrar_guerras?';
    const params = new URLSearchParams();
    
    if (estado) params.append('estado', estado);
    if (imperioId) params.append('imperioId', imperioId);
    
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
        .then(guerras => {
            let table = $('#warsTable').DataTable();
            table.clear();
            
            if (Array.isArray(guerras)) {
                table.rows.add(guerras).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#warsTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
                
                if (filterResults) {
                    filterResults.textContent = `Se encontraron ${guerras.length} conflictos con los filtros seleccionados.`;
                }
            } else {
                console.error("La respuesta de filtrar_guerras no es un array:", guerras);
                mostrarAlerta('error', 'Error: Formato de datos de guerras inesperado.');
                if (filterResults) filterResults.textContent = '';
            }
        })
        .catch(error => {
            console.error('Error al filtrar guerras:', error);
            mostrarAlerta('error', `Error al filtrar conflictos: ${error.message}`);
            if (filterResults) filterResults.textContent = '';
        });
}

/**
 * Resetea todos los filtros y carga todas las guerras
 */
function resetearFiltros() {
    const filterStatus = document.getElementById('filterStatus');
    const filterEmpire = document.getElementById('filterEmpire');
    
    if (filterStatus) filterStatus.value = '';
    if (filterEmpire) filterEmpire.value = '';
    
    const filterResults = document.getElementById('filterResults');
    if (filterResults) filterResults.textContent = '';
    
    cargarGuerras();
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