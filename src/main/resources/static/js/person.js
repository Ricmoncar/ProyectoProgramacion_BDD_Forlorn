document.addEventListener('DOMContentLoaded', function() {
    // Inicialización de DataTables para la tabla de personas
    let personsTable = $('#personsTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
        },
        responsive: true,
        columns: [
            { data: 'id' },
            { data: 'nombre' },
            { data: 'apellido' },
            { data: 'razaNombre' },
            { data: 'imperioNombre' },
            { data: 'profesion' },
            { 
                data: 'estadisticas.lvl',
                render: function(data) {
                    return data || 1;
                }
            },
            { 
                data: 'oro',
                render: function(data) {
                    return data ? data.toLocaleString('es-ES') : '0';
                }
            },
            {
                data: null,
                orderable: false,
                searchable: false,
                render: function(data, type, row) {
                    const personaId = row.id;
                    if (personaId === undefined || personaId === null) {
                        console.error("ID de persona no encontrado en los datos de la fila:", row);
                        return 'Error ID';
                    }
                    return `<div class="person-actions">
                        <button class="view-btn" title="Ver detalles" onclick="verPersona(${personaId})"><i class="fas fa-eye"></i></button>
                        <button class="edit-btn" title="Editar" onclick="editarPersona(${personaId})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" title="Eliminar" onclick="eliminarPersona(${personaId})"><i class="fas fa-trash-alt"></i></button>
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
        order: [[1, 'asc']], // Ordenar por nombre
        createdRow: function(row, data, dataIndex) {
            // Garantiza que todas las filas tengan el fondo correcto
            $(row).css('background-color', dataIndex % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
        }
    });

    // Variables globales para el manejo de equipamiento
    window.armasDisponibles = [];
    window.armadurasDisponibles = [];
    window.herramientasDisponibles = [];
    window.arcanasDisponibles = [];
    
    // Arreglos para almacenar el equipamiento seleccionado
    window.armasEquipadas = [];
    window.armadurasEquipadas = [];
    window.herramientasEquipadas = [];
    window.arcanasAdquiridas = [];

    // Cargar datos iniciales
    cargarRazas();
    cargarImperios();
    cargarEquipamiento('arma');
    cargarEquipamiento('armadura');
    cargarEquipamiento('herramienta');
    cargarEquipamiento('arcana');
    cargarPersonas();

    // Configuración de eventos para modales
    const addBtn = document.getElementById('addPersonBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const closeViewBtn = document.getElementById('closeViewBtn');
    const cancelEquipmentBtn = document.getElementById('cancelEquipmentBtn');

    // Fixed event handler for addPersonBtn
    if (addBtn) {
        console.log('Add person button found');
        addBtn.addEventListener('click', function() {
            console.log('Add person button clicked');
            abrirModalAnadir();
        });
    } else {
        console.error("Add person button not found");
    }
    
    // Fixed event handler for cancelBtn
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            console.log('Cancel button clicked');
            cerrarModal();
        });
    } else {
        console.error("Cancel button not found");
    }
    
    if (closeViewBtn) {
        closeViewBtn.addEventListener('click', cerrarModalDetalles);
    }
    
    if (cancelEquipmentBtn) {
        cancelEquipmentBtn.addEventListener('click', cerrarModalEquipamiento);
    }

    // Configuración de eventos para filtros
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    const resetFiltersBtn = document.getElementById('resetFiltersBtn');
    const nivelSlider = document.getElementById('filterNivelMin');
    const nivelValue = document.getElementById('nivelMinValue');

    if (applyFiltersBtn) applyFiltersBtn.addEventListener('click', aplicarFiltros);
    if (resetFiltersBtn) resetFiltersBtn.addEventListener('click', resetearFiltros);
    
    // Configuración del slider de nivel
    if (nivelSlider && nivelValue) {
        nivelSlider.addEventListener('input', function() {
            nivelValue.textContent = this.value;
        });
    }

    // Configuración de eventos para pestañas de equipamiento
    const tabBtns = document.querySelectorAll('.tab-btn');
    tabBtns.forEach(button => {
        button.addEventListener('click', function() {
            // Remover clase active de todos los botones y contenidos
            document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
            
            // Añadir clase active al botón clickeado
            this.classList.add('active');
            
            // Mostrar el contenido correspondiente
            const tabId = this.getAttribute('data-tab') + '-tab';
            document.getElementById(tabId).classList.add('active');
        });
    });

    // Configuración de botones para añadir equipamiento
    const addEquipmentBtns = document.querySelectorAll('.add-equipment-btn');
    addEquipmentBtns.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault(); // Prevent form submission
            const type = this.getAttribute('data-type');
            console.log('Opening equipment modal for type:', type);
            abrirModalEquipamiento(type);
        });
    });

    // Configuración de eventos para cerrar modales
    let closeButtons = document.querySelectorAll('.close-modal');
    closeButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            console.log('Close button clicked');
            const personModal = document.getElementById('personModal');
            const viewPersonModal = document.getElementById('viewPersonModal');
            const equipmentModal = document.getElementById('equipmentModal');
            if (personModal) personModal.style.display = 'none';
            if (viewPersonModal) viewPersonModal.style.display = 'none';
            if (equipmentModal) equipmentModal.style.display = 'none';
        });
    });

    // Configuración de formularios
    const personForm = document.getElementById('personForm');
    if (personForm) {
        personForm.addEventListener('submit', function(e) {
            e.preventDefault();
            guardarPersona();
        });
    }

    const equipmentForm = document.getElementById('equipmentForm');
    if (equipmentForm) {
        equipmentForm.addEventListener('submit', function(e) {
            e.preventDefault();
            agregarEquipamiento();
        });
    }

    // Cerrar modales al hacer clic fuera del contenido
    window.addEventListener('click', function(event) {
        const personModal = document.getElementById('personModal');
        const viewPersonModal = document.getElementById('viewPersonModal');
        const equipmentModal = document.getElementById('equipmentModal');
        
        if (event.target == personModal) {
            cerrarModal();
        }
        if (event.target == viewPersonModal) {
            cerrarModalDetalles();
        }
        if (event.target == equipmentModal) {
            cerrarModalEquipamiento();
        }
    });
    
    // Corregir visualización de la tabla
    fixTableDisplay();
    
    // Configure form tabs if they exist
    const tabs = document.querySelectorAll('.tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            // Remove active class from all tabs
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            // Add active class to clicked tab
            this.classList.add('active');
            
            // Hide all tab contents
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            // Show content corresponding to clicked tab
            document.getElementById(this.getAttribute('data-tab') + '-tab').classList.add('active');
        });
    });
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
 * Carga la lista de razas para los selectores
 */
function cargarRazas() {
    fetch("http://localhost:8080/listar_razas")
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
            }
            return res.json();
        })
        .then(razas => {
            const selectRaza = document.getElementById('personRaza');
            const filterRaza = document.getElementById('filterRaza');

            if (!selectRaza || !filterRaza) {
                console.error("Elementos select de raza no encontrados en el DOM.");
                return;
            }

            // Guardar valores seleccionados actualmente
            const currentFilterValue = filterRaza.value;
            const currentFormValue = selectRaza.value;

            // Limpiar y rellenar las opciones
            selectRaza.innerHTML = '<option value="">Seleccione una raza</option>';
            filterRaza.innerHTML = '<option value="">Todas</option>';

            razas.forEach(raza => {
                if(raza && raza.id !== undefined && raza.nombre) {
                    let option = document.createElement('option');
                    option.value = raza.id;
                    option.textContent = raza.nombre;
                    selectRaza.appendChild(option.cloneNode(true));
                    filterRaza.appendChild(option);
                } else {
                    console.warn("Raza con datos inválidos recibida:", raza);
                }
            });

            // Restaurar selección previa si es posible
            if (currentFormValue) selectRaza.value = currentFormValue;
            if (currentFilterValue) filterRaza.value = currentFilterValue;
        })
        .catch(error => {
            console.error('Error al cargar razas:', error);
            mostrarAlerta('error', `No se pudieron cargar las razas: ${error.message}`);
        });
}

/**
 * Carga la lista de imperios para los selectores
 */
function cargarImperios() {
    fetch("http://localhost:8080/listar_imperios")
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
            }
            return res.json();
        })
        .then(imperios => {
            const selectImperio = document.getElementById('personImperio');
            const filterImperio = document.getElementById('filterImperio');

            if (!selectImperio || !filterImperio) {
                console.error("Elementos select de imperio no encontrados en el DOM.");
                return;
            }

            // Guardar valores seleccionados actualmente
            const currentFilterValue = filterImperio.value;
            const currentFormValue = selectImperio.value;

            // Limpiar y rellenar las opciones
            selectImperio.innerHTML = '<option value="">Sin imperio</option>';
            filterImperio.innerHTML = '<option value="">Todos</option>';

            imperios.forEach(imperio => {
                if(imperio && imperio.id !== undefined && imperio.nombre) {
                    let option = document.createElement('option');
                    option.value = imperio.id;
                    option.textContent = imperio.nombre;
                    selectImperio.appendChild(option.cloneNode(true));
                    filterImperio.appendChild(option);
                } else {
                    console.warn("Imperio con datos inválidos recibido:", imperio);
                }
            });

            // Restaurar selección previa si es posible
            if (currentFormValue) selectImperio.value = currentFormValue;
            if (currentFilterValue) filterImperio.value = currentFilterValue;
        })
        .catch(error => {
            console.error('Error al cargar imperios:', error);
            mostrarAlerta('error', `No se pudieron cargar los imperios: ${error.message}`);
        });
}

/**
 * Carga los diferentes tipos de equipamiento disponibles
 * @param {string} tipo - Tipo de equipamiento ('arma', 'armadura', 'herramienta', 'arcana')
 */
function cargarEquipamiento(tipo) {
    let endpoint = '';
    
    switch(tipo) {
        case 'arma':
            endpoint = 'listar_armas';
            break;
        case 'armadura':
            endpoint = 'listar_armaduras';
            break;
        case 'herramienta':
            endpoint = 'listar_herramientas';
            break;
        case 'arcana':
            endpoint = 'listar_arcanas';
            break;
        default:
            console.error('Tipo de equipamiento no válido:', tipo);
            return;
    }
    
    fetch(`http://localhost:8080/${endpoint}`)
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
            }
            return res.json();
        })
        .then(items => {
            // Almacenar en la variable global correspondiente
            switch(tipo) {
                case 'arma':
                    window.armasDisponibles = items;
                    break;
                case 'armadura':
                    window.armadurasDisponibles = items;
                    break;
                case 'herramienta':
                    window.herramientasDisponibles = items;
                    break;
                case 'arcana':
                    window.arcanasDisponibles = items;
                    break;
            }
        })
        .catch(error => {
            console.error(`Error al cargar ${tipo}s:`, error);
            mostrarAlerta('error', `No se pudieron cargar los ${tipo}s: ${error.message}`);
        });
}

/**
 * Carga la lista de personas desde el servidor
 */
function cargarPersonas() {
    fetch("http://localhost:8080/listar_personas")
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
        .then(personas => {
            let table = $('#personsTable').DataTable();
            table.clear();
            if (Array.isArray(personas)) {
                // Agregar el nombre de la raza y el imperio para mostrar en la tabla
                personas.forEach(persona => {
                    if (persona.razaId) {
                        persona.razaNombre = persona.raza ? persona.raza.nombre : 'Desconocida';
                    } else {
                        persona.razaNombre = 'N/A';
                    }
                    
                    if (persona.imperioId) {
                        persona.imperioNombre = persona.imperio ? persona.imperio.nombre : 'Desconocido';
                    } else {
                        persona.imperioNombre = 'N/A';
                    }
                });
                
                table.rows.add(personas).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#personsTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
            } else {
                console.error("La respuesta de listar_personas no es un array:", personas);
                mostrarAlerta('error', 'Error: Formato de datos de personas inesperado.');
            }
        })
        .catch(error => {
            console.error('Error al cargar personas:', error);
            mostrarAlerta('error', `No se pudieron cargar las personas: ${error.message}`);
        });
}

/**
 * Muestra los detalles de una persona específica
 */
function verPersona(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para verPersona");
        mostrarAlerta('error', 'No se puede mostrar la persona: ID inválido.');
        return;
    }
    fetch(`http://localhost:8080/obtener_persona?id=${id}`)
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
        .then(persona => {
            if (!persona) {
                throw new Error("No se recibió información de la persona.");
            }
            
            const viewTitle = document.getElementById('viewPersonTitle');
            const detailsContainer = document.getElementById('personDetails');
            const viewModal = document.getElementById('viewPersonModal');

            if (!viewTitle || !detailsContainer || !viewModal) {
                console.error("Elementos del modal de vista no encontrados.");
                return;
            }

            viewTitle.textContent = `${persona.nombre} ${persona.apellido || ''}`;

            // Formatear fecha
            let fechaNacimientoTexto = 'No especificada';
            if (persona.fechaNacimiento) {
                const fecha = new Date(persona.fechaNacimiento);
                fechaNacimientoTexto = fecha.toLocaleDateString('es-ES');
            }

            // Información básica
            let detallesHTML = `
                <div class="person-details">
                    <div class="person-section">
                        <h4>Información Básica</h4>
                        <div class="detail-grid">
                            <div class="detail-item">
                                <span class="detail-label">Raza:</span>
                                <span class="detail-value">${persona.raza ? persona.raza.nombre : 'N/A'}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Imperio:</span>
                                <span class="detail-value">${persona.imperio ? persona.imperio.nombre : 'N/A'}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Profesión:</span>
                                <span class="detail-value">${persona.profesion || 'N/A'}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Oro:</span>
                                <span class="detail-value">${persona.oro ? persona.oro.toLocaleString('es-ES') : '0'}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Fecha de nacimiento:</span>
                                <span class="detail-value">${fechaNacimientoTexto}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Dirección:</span>
                                <span class="detail-value">${persona.direccion || 'N/A'}</span>
                            </div>
                        </div>
                    </div>
                    
                    <div class="person-section">
                        <h4>Características Físicas</h4>
                        <div class="detail-grid">
                            <div class="detail-item">
                                <span class="detail-label">Altura:</span>
                                <span class="detail-value">${persona.alto ? persona.alto + ' m' : 'N/A'}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Ancho:</span>
                                <span class="detail-value">${persona.ancho ? persona.ancho + ' m' : 'N/A'}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">% Grasa:</span>
                                <span class="detail-value">${persona.porcentajeGrasaCorporal ? persona.porcentajeGrasaCorporal + '%' : 'N/A'}</span>
                            </div>
                        </div>
                        <div class="detail-description">
                            <span class="detail-label">Descripción Física:</span>
                            <p>${persona.descripcionFisica || 'N/A'}</p>
                        </div>
                        <div class="detail-description">
                            <span class="detail-label">Personalidad:</span>
                            <p>${persona.personalidad || 'N/A'}</p>
                        </div>
                    </div>
            `;

            // Estadísticas
            if (persona.estadisticas) {
                const stats = persona.estadisticas;
                detallesHTML += `
                    <div class="person-section">
                        <h4>Estadísticas</h4>
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
                    </div>
                `;
            }

            // Equipamiento
            detallesHTML += `<div class="person-section"><h4>Equipamiento</h4>`;

            // Armas
            detallesHTML += `<div class="equipment-section"><h5>Armas</h5>`;
            if (persona.armas && persona.armas.length > 0) {
                detallesHTML += `<ul class="equipment-list">`;
                persona.armas.forEach(arma => {
                    detallesHTML += `
                        <li>
                            <span class="equipment-name">${arma.nombre}</span>
                            <span class="equipment-status ${arma.equipada ? 'equipped' : ''}">${arma.equipada ? 'Equipada' : 'No equipada'}</span>
                        </li>
                    `;
                });
                detallesHTML += `</ul>`;
            } else {
                detallesHTML += `<p>No tiene armas.</p>`;
            }
            detallesHTML += `</div>`;

            // Armaduras
            detallesHTML += `<div class="equipment-section"><h5>Armaduras</h5>`;
            if (persona.armaduras && persona.armaduras.length > 0) {
                detallesHTML += `<ul class="equipment-list">`;
                persona.armaduras.forEach(armadura => {
                    detallesHTML += `
                        <li>
                            <span class="equipment-name">${armadura.nombre}</span>
                            <span class="equipment-status ${armadura.equipada ? 'equipped' : ''}">${armadura.equipada ? 'Equipada' : 'No equipada'}</span>
                        </li>
                    `;
                });
                detallesHTML += `</ul>`;
            } else {
                detallesHTML += `<p>No tiene armaduras.</p>`;
            }
            detallesHTML += `</div>`;

            // Herramientas
            detallesHTML += `<div class="equipment-section"><h5>Herramientas</h5>`;
            if (persona.herramientas && persona.herramientas.length > 0) {
                detallesHTML += `<ul class="equipment-list">`;
                persona.herramientas.forEach(herramienta => {
                    detallesHTML += `
                        <li>
                            <span class="equipment-name">${herramienta.nombre}</span>
                            <span class="equipment-status ${herramienta.equipada ? 'equipped' : ''}">${herramienta.equipada ? 'Equipada' : 'No equipada'}</span>
                        </li>
                    `;
                });
                detallesHTML += `</ul>`;
            } else {
                detallesHTML += `<p>No tiene herramientas.</p>`;
            }
            detallesHTML += `</div>`;

            // Arcanas
            detallesHTML += `<div class="equipment-section"><h5>Arcanas</h5>`;
            if (persona.arcanas && persona.arcanas.length > 0) {
                detallesHTML += `<ul class="equipment-list">`;
                persona.arcanas.forEach(arcana => {
                    detallesHTML += `
                        <li>
                            <span class="equipment-name">${arcana.tipo}</span>
                            <span class="equipment-status">${arcana.maestria || 'Nivel básico'}</span>
                        </li>
                    `;
                });
                detallesHTML += `</ul>`;
            } else {
                detallesHTML += `<p>No domina ninguna arcana.</p>`;
            }
            detallesHTML += `</div>`;

            detallesHTML += `</div></div>`;

            detailsContainer.innerHTML = detallesHTML;
            viewModal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al obtener la persona:', error);
            mostrarAlerta('error', `Error al obtener la persona: ${error.message}`);
        });
}

/**
 * Abre el modal de edición con los datos de la persona seleccionada
 */

function editarPersona(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para editarPersona");
        mostrarAlerta('error', 'No se puede editar la persona: ID inválido.');
        return;
    }
    
    const form = document.getElementById('personForm');
    const modalTitle = document.getElementById('personModalTitle');
    const personIdInput = document.getElementById('personId');
    const modal = document.getElementById('personModal');

    if (!form || !modalTitle || !personIdInput || !modal) {
        console.error("Elementos del modal de edición no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Editar Persona';
    personIdInput.value = id;
    
    // Resetear listas de equipamiento
    window.armasEquipadas = [];
    window.armadurasEquipadas = [];
    window.herramientasEquipadas = [];
    window.arcanasAdquiridas = [];

    fetch(`http://localhost:8080/obtener_persona?id=${id}`)
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
        .then(persona => {
            if (!persona) {
                throw new Error("No se recibió información de la persona para editar.");
            }

            // Información básica
            document.getElementById('personName').value = persona.nombre || '';
            document.getElementById('personLastName').value = persona.apellido || '';
            document.getElementById('personRaza').value = persona.razaId || '';
            document.getElementById('personImperio').value = persona.imperioId || '';
            document.getElementById('personProfesion').value = persona.profesion || '';
            document.getElementById('personOro').value = persona.oro || 0;
            
            // Características físicas
            document.getElementById('personAlto').value = persona.alto || '';
            document.getElementById('personAncho').value = persona.ancho || '';
            document.getElementById('personGrasaCorporal').value = persona.porcentajeGrasaCorporal || '';
            document.getElementById('personDescripcionFisica').value = persona.descripcionFisica || '';
            document.getElementById('personPersonalidad').value = persona.personalidad || '';
            
            // Formatear fecha
            if (persona.fechaNacimiento) {
                const fecha = new Date(persona.fechaNacimiento);
                const fechaFormateada = fecha.toISOString().split('T')[0];
                document.getElementById('personFechaNacimiento').value = fechaFormateada;
            } else {
                document.getElementById('personFechaNacimiento').value = '';
            }
            
            document.getElementById('personDireccion').value = persona.direccion || '';
            
            // Estadísticas
            if (persona.estadisticas) {
                const stats = persona.estadisticas;
                document.getElementById('statATK').value = stats.atk || 10;
                document.getElementById('statDEF').value = stats.def || 10;
                document.getElementById('statHP').value = stats.hp || 10;
                document.getElementById('statSPE').value = stats.spe || 10;
                document.getElementById('statMAT').value = stats.mat || 10;
                document.getElementById('statMDF').value = stats.mdf || 10;
                document.getElementById('statXP').value = stats.xp || 0;
                document.getElementById('statLVL').value = stats.lvl || 1;
            }
            
            // Equipamiento
            if (persona.armas && persona.armas.length > 0) {
                window.armasEquipadas = persona.armas;
                actualizarListaEquipamiento('armas');
            }
            
            if (persona.armaduras && persona.armaduras.length > 0) {
                window.armadurasEquipadas = persona.armaduras;
                actualizarListaEquipamiento('armaduras');
            }
            
            if (persona.herramientas && persona.herramientas.length > 0) {
                window.herramientasEquipadas = persona.herramientas;
                actualizarListaEquipamiento('herramientas');
            }
            
            if (persona.arcanas && persona.arcanas.length > 0) {
                window.arcanasAdquiridas = persona.arcanas;
                actualizarListaEquipamiento('arcana');
            }

            // Ensure first tab is active
            document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            
            const basicTab = document.querySelector('.tab[data-tab="basic"]');
            const basicContent = document.getElementById('basic-tab');
            if (basicTab) basicTab.classList.add('active');
            if (basicContent) basicContent.classList.add('active');

            modal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al cargar datos de la persona para editar:', error);
            mostrarAlerta('error', `Error al cargar datos de la persona: ${error.message}`);
            cerrarModal();
        });
}

/**
 * Elimina una persona tras confirmación del usuario
 */
function eliminarPersona(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para eliminarPersona");
        mostrarAlerta('error', 'No se puede eliminar la persona: ID inválido.');
        return;
    }
    
    if (confirm(`¿Está seguro que desea eliminar la persona con ID ${id}? Esta acción no se puede deshacer.`)) {
        fetch(`http://localhost:8080/eliminar_persona?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                    });
                }
                return res.text();
            })
            .then(resultado => {
                mostrarAlerta('success', `Persona ${id} eliminada correctamente. ${resultado}`);
                cargarPersonas();
            })
            .catch(error => {
                console.error('Error al eliminar persona:', error);
                mostrarAlerta('error', `Error al eliminar persona: ${error.message}`);
            });
    }
}

/**
 * Abre el modal para añadir una nueva persona
 */
function abrirModalAnadir() {
    console.log('abrirModalAnadir called');
    const form = document.getElementById('personForm');
    const modalTitle = document.getElementById('personModalTitle');
    const personIdInput = document.getElementById('personId');
    const modal = document.getElementById('personModal');

    if (!form) {
        console.error("Form element not found");
    }
    if (!modalTitle) {
        console.error("Modal title element not found");
    }
    if (!personIdInput) {
        console.error("Person ID input not found");
    }
    if (!modal) {
        console.error("Modal element not found");
    }

    if (!form || !modalTitle || !personIdInput || !modal) {
        console.error("Required elements for modal not found");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Añadir Nueva Persona';
    personIdInput.value = '';
    
    // Resetear listas de equipamiento
    window.armasEquipadas = [];
    window.armadurasEquipadas = [];
    window.herramientasEquipadas = [];
    window.arcanasAdquiridas = [];
    
    // Limpiar listas de equipamiento en la UI
    const armasLista = document.getElementById('armas-list');
    const armadurasLista = document.getElementById('armaduras-list');
    const herramientasLista = document.getElementById('herramientas-list');
    const arcanaLista = document.getElementById('arcana-list');
    
    if (armasLista) armasLista.innerHTML = '<p class="no-items">No hay armas equipadas</p>';
    if (armadurasLista) armadurasLista.innerHTML = '<p class="no-items">No hay armaduras equipadas</p>';
    if (herramientasLista) herramientasLista.innerHTML = '<p class="no-items">No hay herramientas equipadas</p>';
    if (arcanaLista) arcanaLista.innerHTML = '<p class="no-items">No hay arcana dominada</p>';
    
    // Establecer valores por defecto
    document.getElementById('personOro').value = 0;
    document.getElementById('statATK').value = 10;
    document.getElementById('statDEF').value = 10;
    document.getElementById('statHP').value = 10;
    document.getElementById('statSPE').value = 10;
    document.getElementById('statMAT').value = 10;
    document.getElementById('statMDF').value = 10;
    document.getElementById('statXP').value = 0;
    document.getElementById('statLVL').value = 1;
    
    // Ensure first tab is active
    document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
    
    const basicTab = document.querySelector('.tab[data-tab="basic"]');
    const basicContent = document.getElementById('basic-tab');
    if (basicTab) basicTab.classList.add('active');
    if (basicContent) basicContent.classList.add('active');
    
    console.log('Opening modal - current display style:', modal.style.display);
    modal.style.display = 'block';
    console.log('Modal should now be visible');
}

/**
 * Cierra el modal de añadir/editar persona
 */
function cerrarModal() {
    console.log('cerrarModal called');
    const modal = document.getElementById('personModal');
    if (modal) {
        modal.style.display = 'none';
        console.log('Modal should now be hidden');
    } else {
        console.error('Modal element not found');
    }
}

/**
 * Cierra el modal de detalles de la persona
 */
function cerrarModalDetalles() {
    console.log('cerrarModalDetalles called');
    const modal = document.getElementById('viewPersonModal');
    if (modal) {
        modal.style.display = 'none';
        const detailsContainer = document.getElementById('personDetails');
        if (detailsContainer) detailsContainer.innerHTML = '';
    } else {
        console.error('View modal element not found');
    }
}

/**
 * Abre el modal de equipamiento
 */
function abrirModalEquipamiento(tipo) {
    console.log('abrirModalEquipamiento called for type:', tipo);
    const modal = document.getElementById('equipmentModal');
    const titulo = document.getElementById('equipmentModalTitle');
    const tipoInput = document.getElementById('equipmentType');
    const equipmentSelect = document.getElementById('equipmentSelect');
    const equipadaGroup = document.getElementById('equipadaGroup');
    const maestriaGroup = document.getElementById('maestriaGroup');
    
    if (!modal || !titulo || !tipoInput || !equipmentSelect) {
        console.error("Elementos del modal de equipamiento no encontrados.");
        return;
    }
    
    tipoInput.value = tipo;
    
    // Configurar el título y contenido según el tipo
    switch(tipo) {
        case 'arma':
            titulo.textContent = 'Añadir Arma';
            equipmentSelect.innerHTML = '<option value="">Seleccione un arma</option>';
            
            // Filtrar para mostrar solo los elementos que no están ya equipados
            const armasIds = window.armasEquipadas.map(a => a.id);
            window.armasDisponibles.forEach(arma => {
                if (!armasIds.includes(arma.id)) {
                    const option = document.createElement('option');
                    option.value = arma.id;
                    option.textContent = arma.nombre;
                    equipmentSelect.appendChild(option);
                }
            });
            
            equipadaGroup.style.display = 'block';
            maestriaGroup.style.display = 'none';
            break;
            
        case 'armadura':
            titulo.textContent = 'Añadir Armadura';
            equipmentSelect.innerHTML = '<option value="">Seleccione una armadura</option>';
            
            const armadurasIds = window.armadurasEquipadas.map(a => a.id);
            window.armadurasDisponibles.forEach(armadura => {
                if (!armadurasIds.includes(armadura.id)) {
                    const option = document.createElement('option');
                    option.value = armadura.id;
                    option.textContent = armadura.nombre;
                    equipmentSelect.appendChild(option);
                }
            });
            
            equipadaGroup.style.display = 'block';
            maestriaGroup.style.display = 'none';
            break;
            
        case 'herramienta':
            titulo.textContent = 'Añadir Herramienta';
            equipmentSelect.innerHTML = '<option value="">Seleccione una herramienta</option>';
            
            const herramientasIds = window.herramientasEquipadas.map(h => h.id);
            window.herramientasDisponibles.forEach(herramienta => {
                if (!herramientasIds.includes(herramienta.id)) {
                    const option = document.createElement('option');
                    option.value = herramienta.id;
                    option.textContent = herramienta.nombre;
                    equipmentSelect.appendChild(option);
                }
            });
            
            equipadaGroup.style.display = 'block';
            maestriaGroup.style.display = 'none';
            break;
            
        case 'arcana':
            titulo.textContent = 'Añadir Arcana';
            equipmentSelect.innerHTML = '<option value="">Seleccione una arcana</option>';
            
            const arcanasIds = window.arcanasAdquiridas.map(a => a.id);
            window.arcanasDisponibles.forEach(arcana => {
                if (!arcanasIds.includes(arcana.id)) {
                    const option = document.createElement('option');
                    option.value = arcana.id;
                    option.textContent = arcana.tipo;
                    equipmentSelect.appendChild(option);
                }
            });
            
            equipadaGroup.style.display = 'none';
            maestriaGroup.style.display = 'block';
            break;
            
        default:
            console.error('Tipo de equipamiento no válido:', tipo);
            return;
    }
    
    modal.style.display = 'block';
}

/**
 * Cierra el modal de equipamiento
 */
function cerrarModalEquipamiento() {
    console.log('cerrarModalEquipamiento called');
    const modal = document.getElementById('equipmentModal');
    if (modal) {
        modal.style.display = 'none';
    } else {
        console.error('Equipment modal element not found');
    }
}

/**
 * Agrega equipamiento a la persona actual
 */
function agregarEquipamiento() {
    const tipo = document.getElementById('equipmentType').value;
    const itemId = document.getElementById('equipmentSelect').value;
    
    if (!itemId) {
        mostrarAlerta('error', 'Debe seleccionar un elemento.');
        return;
    }
    
    // Buscar el elemento en la lista correspondiente
    let elemento = null;
    switch(tipo) {
        case 'arma':
            elemento = window.armasDisponibles.find(a => a.id == itemId);
            if (elemento) {
                const equipado = document.getElementById('equipmentEquipped').value === 'true';
                window.armasEquipadas.push({
                    ...elemento,
                    equipada: equipado
                });
                actualizarListaEquipamiento('armas');
            }
            break;
            
        case 'armadura':
            elemento = window.armadurasDisponibles.find(a => a.id == itemId);
            if (elemento) {
                const equipado = document.getElementById('equipmentEquipped').value === 'true';
                window.armadurasEquipadas.push({
                    ...elemento,
                    equipada: equipado
                });
                actualizarListaEquipamiento('armaduras');
            }
            break;
            
        case 'herramienta':
            elemento = window.herramientasDisponibles.find(h => h.id == itemId);
            if (elemento) {
                const equipado = document.getElementById('equipmentEquipped').value === 'true';
                window.herramientasEquipadas.push({
                    ...elemento,
                    equipada: equipado
                });
                actualizarListaEquipamiento('herramientas');
            }
            break;
            
        case 'arcana':
            elemento = window.arcanasDisponibles.find(a => a.id == itemId);
            if (elemento) {
                const maestria = document.getElementById('arcanaMaestria').value;
                window.arcanasAdquiridas.push({
                    ...elemento,
                    maestria: maestria
                });
                actualizarListaEquipamiento('arcana');
            }
            break;
    }
    
    cerrarModalEquipamiento();
    mostrarAlerta('success', `${tipo.charAt(0).toUpperCase() + tipo.slice(1)} añadida correctamente.`);
}

/**
 * Actualiza la lista visual de equipamiento en el modal
 */
function actualizarListaEquipamiento(tipo) {
    let container = null;
    let items = [];
    
    switch(tipo) {
        case 'armas':
            container = document.getElementById('armas-list');
            items = window.armasEquipadas;
            break;
            
        case 'armaduras':
            container = document.getElementById('armaduras-list');
            items = window.armadurasEquipadas;
            break;
            
        case 'herramientas':
            container = document.getElementById('herramientas-list');
            items = window.herramientasEquipadas;
            break;
            
        case 'arcana':
            container = document.getElementById('arcana-list');
            items = window.arcanasAdquiridas;
            break;
    }
    
    if (!container) return;
    
    if (items.length === 0) {
        container.innerHTML = `<p class="no-items">No hay ${tipo} equipadas</p>`;
        return;
    }
    
    container.innerHTML = '';
    const lista = document.createElement('ul');
    lista.className = 'equipment-item-list';
    
    items.forEach((item, index) => {
        const li = document.createElement('li');
        
        if (tipo === 'arcana') {
            li.innerHTML = `
                <span class="equipment-name">${item.tipo}</span>
                <span class="equipment-property">Maestría: ${item.maestria || 'Básico'}</span>
                <button type="button" class="btn-icon delete-equipment" data-index="${index}" data-type="${tipo}">
                    <i class="fas fa-trash-alt"></i>
                </button>
            `;
        } else {
            li.innerHTML = `
                <span class="equipment-name">${item.nombre}</span>
                <span class="equipment-property">Equipado: ${item.equipada ? 'Sí' : 'No'}</span>
                <button type="button" class="btn-icon delete-equipment" data-index="${index}" data-type="${tipo}">
                    <i class="fas fa-trash-alt"></i>
                </button>
            `;
        }
        
        lista.appendChild(li);
    });
    
    container.appendChild(lista);
    
    // Añadir eventos para eliminar equipamiento
    container.querySelectorAll('.delete-equipment').forEach(btn => {
        btn.addEventListener('click', function() {
            const index = parseInt(this.getAttribute('data-index'));
            const tipo = this.getAttribute('data-type');
            eliminarEquipamiento(tipo, index);
        });
    });
}

/**
 * Elimina un equipamiento de la lista
 */
function eliminarEquipamiento(tipo, index) {
    switch(tipo) {
        case 'armas':
            if (index >= 0 && index < window.armasEquipadas.length) {
                window.armasEquipadas.splice(index, 1);
                actualizarListaEquipamiento('armas');
                mostrarAlerta('info', 'Arma eliminada.');
            }
            break;
            
        case 'armaduras':
            if (index >= 0 && index < window.armadurasEquipadas.length) {
                window.armadurasEquipadas.splice(index, 1);
                actualizarListaEquipamiento('armaduras');
                mostrarAlerta('info', 'Armadura eliminada.');
            }
            break;
            
        case 'herramientas':
            if (index >= 0 && index < window.herramientasEquipadas.length) {
                window.herramientasEquipadas.splice(index, 1);
                actualizarListaEquipamiento('herramientas');
                mostrarAlerta('info', 'Herramienta eliminada.');
            }
            break;
            
        case 'arcana':
            if (index >= 0 && index < window.arcanasAdquiridas.length) {
                window.arcanasAdquiridas.splice(index, 1);
                actualizarListaEquipamiento('arcana');
                mostrarAlerta('info', 'Arcana eliminada.');
            }
            break;
    }
}

/**
 * Guarda una nueva persona o actualiza una existente
 */


function guardarPersona() {
    const id = document.getElementById('personId').value;
    const nombre = document.getElementById('personName').value.trim();
    const apellido = document.getElementById('personLastName').value.trim();
    const razaId = document.getElementById('personRaza').value;
    const imperioId = document.getElementById('personImperio').value;
    const profesion = document.getElementById('personProfesion').value;
    const oro = document.getElementById('personOro').value;
    
    // Datos físicos
    const alto = document.getElementById('personAlto').value;
    const ancho = document.getElementById('personAncho').value;
    const grasaCorporal = document.getElementById('personGrasaCorporal').value;
    const descripcionFisica = document.getElementById('personDescripcionFisica').value.trim();
    const personalidad = document.getElementById('personPersonalidad').value.trim();
    
    // Otros datos
    const fechaNacimiento = document.getElementById('personFechaNacimiento').value;
    const direccion = document.getElementById('personDireccion').value.trim();
    
    // Estadísticas
    const estadisticas = {
        tipo: 'Personal',
        atk: document.getElementById('statATK').value,
        def: document.getElementById('statDEF').value,
        hp: document.getElementById('statHP').value,
        spe: document.getElementById('statSPE').value,
        mat: document.getElementById('statMAT').value,
        mdf: document.getElementById('statMDF').value,
        xp: document.getElementById('statXP').value,
        lvl: document.getElementById('statLVL').value
    };

    // Validación básica
    if (!nombre) {
        mostrarAlerta('error', 'El nombre de la persona es obligatorio.');
        return;
    }

    if (!razaId) {
        mostrarAlerta('error', 'Debe seleccionar una raza.');
        return;
    }

    // Construir el objeto persona
    const persona = {
        id: id || null,
        nombre: nombre,
        apellido: apellido,
        ancho: ancho || null,
        alto: alto || null,
        descripcionFisica: descripcionFisica,
        porcentajeGrasaCorporal: grasaCorporal || null,
        personalidad: personalidad,
        oro: oro || 0,
        fechaNacimiento: fechaNacimiento || null,
        profesion: profesion,
        direccion: direccion,
        razaId: razaId,
        imperioId: imperioId || null,
        estadisticas: estadisticas,
        armas: window.armasEquipadas,
        armaduras: window.armadurasEquipadas,
        herramientas: window.herramientasEquipadas,
        arcanas: window.arcanasAdquiridas
    };

    const baseUrl = id ? 'http://localhost:8080/actualizar_persona' : 'http://localhost:8080/aniadir_persona';
    
    const params = new URLSearchParams();
    if (id) params.append('id', id);
    params.append('nombre', nombre);
    if (apellido) params.append('apellido', apellido);
    if (ancho) params.append('ancho', ancho);
    if (alto) params.append('alto', alto);
    if (descripcionFisica) params.append('descripcionFisica', descripcionFisica);
    if (grasaCorporal) params.append('porcentajeGrasaCorporal', grasaCorporal);
    if (personalidad) params.append('personalidad', personalidad);
    params.append('oro', oro || 0);
    if (fechaNacimiento) params.append('fechaNacimiento', fechaNacimiento);
    if (profesion) params.append('profesion', profesion);
    if (direccion) params.append('direccion', direccion);
    params.append('razaId', razaId);
    if (imperioId) params.append('imperioId', imperioId);

    params.append('estadisticasTipo', estadisticas.tipo);
    params.append('atk', estadisticas.atk);
    params.append('def', estadisticas.def);
    params.append('hp', estadisticas.hp);
    params.append('spe', estadisticas.spe);
    params.append('mat', estadisticas.mat);
    params.append('mdf', estadisticas.mdf);
    params.append('xp', estadisticas.xp);
    params.append('lvl', estadisticas.lvl);

    const url = `${baseUrl}?${params.toString()}`;
    console.log('Requesting URL:', url);
    

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
            mostrarAlerta('success', id ? 'Persona actualizada correctamente.' : 'Persona añadida correctamente.');
            cerrarModal();
            cargarPersonas();
        })
        .catch(error => {
            console.error('Error al guardar persona:', error);
            mostrarAlerta('error', `Error al guardar persona: ${error.message}`);
            
            // Debug info
            console.log('Object being sent:', persona);
            console.log('URL being used:', url);
        });
}

/**
 * Aplica filtros seleccionados para filtrar personas
 */
function aplicarFiltros() {
    const razaId = document.getElementById('filterRaza').value;
    const imperioId = document.getElementById('filterImperio').value;
    const profesion = document.getElementById('filterProfesion').value;
    const nivelMin = document.getElementById('filterNivelMin').value;
    
    // Construir la URL con los parámetros
    let url = 'http://localhost:8080/filtrar_personas?';
    const params = new URLSearchParams();
    
    if (razaId) params.append('razaId', razaId);
    if (imperioId) params.append('imperioId', imperioId);
    if (profesion) params.append('profesion', profesion);
    if (nivelMin && parseInt(nivelMin) > 1) params.append('nivelMin', nivelMin);
    
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
        .then(personas => {
            let table = $('#personsTable').DataTable();
            table.clear();
            
            if (Array.isArray(personas)) {
                // Agregar el nombre de la raza y el imperio para mostrar en la tabla
                personas.forEach(persona => {
                    if (persona.razaId) {
                        persona.razaNombre = persona.raza ? persona.raza.nombre : 'Desconocida';
                    } else {
                        persona.razaNombre = 'N/A';
                    }
                    
                    if (persona.imperioId) {
                        persona.imperioNombre = persona.imperio ? persona.imperio.nombre : 'Desconocido';
                    } else {
                        persona.imperioNombre = 'N/A';
                    }
                });
                
                table.rows.add(personas).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#personsTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
                
                if (filterResults) {
                    filterResults.textContent = `Se encontraron ${personas.length} personas con los filtros seleccionados.`;
                }
            } else {
                console.error("La respuesta de filtrar_personas no es un array:", personas);
                mostrarAlerta('error', 'Error: Formato de datos de personas inesperado.');
                if (filterResults) filterResults.textContent = '';
            }
        })
        .catch(error => {
            console.error('Error al filtrar personas:', error);
            mostrarAlerta('error', `Error al filtrar personas: ${error.message}`);
            if (filterResults) filterResults.textContent = '';
        });
}

/**
 * Resetea todos los filtros y carga todas las personas
 */
function resetearFiltros() {
    const filterRaza = document.getElementById('filterRaza');
    const filterImperio = document.getElementById('filterImperio');
    const filterProfesion = document.getElementById('filterProfesion');
    const filterNivelMin = document.getElementById('filterNivelMin');
    const nivelMinValue = document.getElementById('nivelMinValue');
    
    if (filterRaza) filterRaza.value = '';
    if (filterImperio) filterImperio.value = '';
    if (filterProfesion) filterProfesion.value = '';
    
    if (filterNivelMin) {
        filterNivelMin.value = 1;
        if (nivelMinValue) nivelMinValue.textContent = '1';
    }
    
    const filterResults = document.getElementById('filterResults');
    if (filterResults) filterResults.textContent = '';
    
    cargarPersonas();
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

/**
 * Función de depuración para comprobar el estado de los modales
 */
function debugModals() {
    const personModal = document.getElementById('personModal');
    const viewPersonModal = document.getElementById('viewPersonModal');
    const equipmentModal = document.getElementById('equipmentModal');
    
    console.log('Modal Elements Status:');
    console.log('personModal:', personModal ? personModal.style.display : 'Not found');
    console.log('viewPersonModal:', viewPersonModal ? viewPersonModal.style.display : 'Not found');
    console.log('equipmentModal:', equipmentModal ? equipmentModal.style.display : 'Not found');
    
    // Check button event listeners
    const addBtn = document.getElementById('addPersonBtn');
    if (addBtn) {
        console.log('Add Person Button found');
        // Create a temporary click handler to test if events are working
        const oldClick = addBtn.onclick;
        addBtn.onclick = function() {
            console.log('Add Person Button clicked in debug function');
            if (oldClick) oldClick();
        };
    } else {
        console.log('Add Person Button NOT FOUND');
    }
    
    return {
        personModal: personModal ? personModal.style.display : 'Not found',
        viewPersonModal: viewPersonModal ? viewPersonModal.style.display : 'Not found',
        equipmentModal: equipmentModal ? equipmentModal.style.display : 'Not found',
        addButtonExists: Boolean(addBtn)
    };
}