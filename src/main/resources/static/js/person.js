document.addEventListener('DOMContentLoaded', function() {
    // Inicialización de la tabla de personas con DataTables
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
                        console.error("ID de persona no encontrado:", row);
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
        order: [[1, 'asc']]
    });

    // Cargar datos iniciales
    cargarRazas();
    cargarImperios();
    cargarPersonas();

    // Configurar eventos de botones principales
    const addBtn = document.getElementById('addPersonBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const closeViewBtn = document.getElementById('closeViewBtn');

    if (addBtn) addBtn.addEventListener('click', abrirModalAnadir);
    if (cancelBtn) cancelBtn.addEventListener('click', cerrarModal);
    if (closeViewBtn) closeViewBtn.addEventListener('click', cerrarModalDetalles);

    // Configurar eventos de filtros
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    const resetFiltersBtn = document.getElementById('resetFiltersBtn');

    if (applyFiltersBtn) applyFiltersBtn.addEventListener('click', aplicarFiltros);
    if (resetFiltersBtn) resetFiltersBtn.addEventListener('click', resetearFiltros);

    // Configurar eventos para cerrar modales
    document.querySelectorAll('.close-modal').forEach(btn => {
        btn.addEventListener('click', function() {
            document.getElementById('personModal').style.display = 'none';
            document.getElementById('viewPersonModal').style.display = 'none';
            document.getElementById('equipmentSelectionModal').style.display = 'none';
        });
    });
    
    // Configurar botón de cancelar del modal de equipamiento
    const cancelEquipmentBtn = document.getElementById('cancelEquipmentBtn');
    if (cancelEquipmentBtn) {
        cancelEquipmentBtn.addEventListener('click', function() {
            document.getElementById('equipmentSelectionModal').style.display = 'none';
        });
    }

    // Configurar formulario
    const personForm = document.getElementById('personForm');
    if (personForm) {
        personForm.addEventListener('submit', function(e) {
            e.preventDefault();
            guardarPersona();
        });
    }

    // Configurar pestañas de equipamiento
    document.querySelectorAll('.tab-btn').forEach(button => {
        button.addEventListener('click', function() {
            document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
            
            this.classList.add('active');
            const tabId = this.getAttribute('data-tab');
            document.getElementById(tabId).classList.add('active');
        });
    });

    // Cerrar modales al hacer clic fuera
    window.addEventListener('click', function(event) {
        const personModal = document.getElementById('personModal');
        const viewPersonModal = document.getElementById('viewPersonModal');
        const equipmentModal = document.getElementById('equipmentSelectionModal');
        
        if (event.target == personModal) cerrarModal();
        if (event.target == viewPersonModal) cerrarModalDetalles();
        if (event.target == equipmentModal) equipmentModal.style.display = 'none';
    });
    
    // Arreglar visualización de tablas
    arreglarVisualizacionTabla();
});

// Función para arreglar la visualización de las tablas con tema oscuro
function arreglarVisualizacionTabla() {
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

// Cargar lista de razas para los selectores
function cargarRazas() {
    fetch("/listar_razas")
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
            }
            return res.json();
        })
        .then(razas => {
            const selectRaza = document.getElementById('personRace');
            const filterRaza = document.getElementById('filterRace');

            if (!selectRaza || !filterRaza) {
                console.error("No se encontraron los elementos select de raza en el DOM");
                return;
            }

            // Limpiar y rellenar opciones
            selectRaza.innerHTML = '<option value="">Seleccione una raza</option>';
            filterRaza.innerHTML = '<option value="">Todas</option>';

            razas.forEach(raza => {
                if(raza && raza.id !== undefined && raza.nombre) {
                    let option = document.createElement('option');
                    option.value = raza.id;
                    option.textContent = raza.nombre;
                    selectRaza.appendChild(option.cloneNode(true));
                    filterRaza.appendChild(option);
                }
            });
        })
        .catch(error => {
            console.error('Error al cargar razas:', error);
            mostrarAlerta('error', `No se pudieron cargar las razas: ${error.message}`);
        });
}

// Cargar lista de imperios para los selectores  
function cargarImperios() {
    fetch("/listar_imperios")
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
            }
            return res.json();
        })
        .then(imperios => {
            const selectImperio = document.getElementById('personEmpire');
            const filterImperio = document.getElementById('filterEmpire');

            if (!selectImperio || !filterImperio) {
                console.error("No se encontraron los elementos select de imperio en el DOM");
                return;
            }

            // Limpiar y rellenar opciones
            selectImperio.innerHTML = '<option value="">Sin imperio</option>';
            filterImperio.innerHTML = '<option value="">Todos</option>';

            imperios.forEach(imperio => {
                if(imperio && imperio.id !== undefined && imperio.nombre) {
                    let option = document.createElement('option');
                    option.value = imperio.id;
                    option.textContent = imperio.nombre;
                    selectImperio.appendChild(option.cloneNode(true));
                    filterImperio.appendChild(option);
                }
            });
        })
        .catch(error => {
            console.error('Error al cargar imperios:', error);
            mostrarAlerta('error', `No se pudieron cargar los imperios: ${error.message}`);
        });
}

// Cargar lista de personas desde el servidor
function cargarPersonas() {
    fetch("/listar_personas")
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
                // Agregar nombres de raza e imperio para mostrar en tabla
                personas.forEach(persona => {
                    persona.razaNombre = persona.raza ? persona.raza.nombre : 'N/A';
                    persona.imperioNombre = persona.imperio ? persona.imperio.nombre : 'N/A';
                });
                
                table.rows.add(personas).draw();
                
                // Arreglar colores de filas
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

// Ver detalles de una persona específica
function verPersona(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para verPersona");
        mostrarAlerta('error', 'No se puede mostrar la persona: ID inválido.');
        return;
    }
    
    fetch(`/obtener_persona?id=${id}`)
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => { 
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`)
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

            // Formatear fecha de nacimiento
            let fechaNacimientoTexto = 'No especificada';
            if (persona.fechaNacimiento) {
                const fecha = new Date(persona.fechaNacimiento);
                fechaNacimientoTexto = fecha.toLocaleDateString('es-ES');
            }

            // Crear HTML con los detalles
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

            detallesHTML += `</div>`;

            // Mostrar el contenido inicial
            detailsContainer.innerHTML = detallesHTML;

            // Cargar y mostrar equipamiento
            cargarEquipamientoPersona(persona.id, function(equipamiento) {
                if (equipamiento && (equipamiento.armas.length > 0 || equipamiento.armaduras.length > 0 || equipamiento.herramientas.length > 0 || equipamiento.arcanas.length > 0)) {
                    let equipamientoHTML = `
                        <div class="person-section">
                            <h4>Equipamiento</h4>
                    `;

                    if (equipamiento.armas.length > 0) {
                        equipamientoHTML += `
                            <div class="equipment-section">
                                <h5>Armas</h5>
                                <ul class="equipment-list">`;
                        equipamiento.armas.forEach(arma => {
                            equipamientoHTML += `<li><span class="equipment-name">${arma.nombre}</span> <span class="equipment-property">${arma.material || 'Material desconocido'}</span></li>`;
                        });
                        equipamientoHTML += `</ul></div>`;
                    }

                    if (equipamiento.armaduras.length > 0) {
                        equipamientoHTML += `
                            <div class="equipment-section">
                                <h5>Armaduras</h5>
                                <ul class="equipment-list">`;
                        equipamiento.armaduras.forEach(armadura => {
                            equipamientoHTML += `<li><span class="equipment-name">${armadura.nombre}</span> <span class="equipment-property">${armadura.material || 'Material desconocido'}</span></li>`;
                        });
                        equipamientoHTML += `</ul></div>`;
                    }

                    if (equipamiento.herramientas.length > 0) {
                        equipamientoHTML += `
                            <div class="equipment-section">
                                <h5>Herramientas</h5>
                                <ul class="equipment-list">`;
                        equipamiento.herramientas.forEach(herramienta => {
                            equipamientoHTML += `<li><span class="equipment-name">${herramienta.nombre}</span> <span class="equipment-property">${herramienta.uso || 'Uso general'}</span></li>`;
                        });
                        equipamientoHTML += `</ul></div>`;
                    }

                    if (equipamiento.arcanas.length > 0) {
                        equipamientoHTML += `
                            <div class="equipment-section">
                                <h5>Arcanas</h5>
                                <ul class="equipment-list">`;
                        equipamiento.arcanas.forEach(arcana => {
                            equipamientoHTML += `<li><span class="equipment-name">${arcana.tipo}</span> <span class="equipment-property">Maestría: ${arcana.maestria || 'Básica'}</span></li>`;
                        });
                        equipamientoHTML += `</ul></div>`;
                    }

                    equipamientoHTML += `</div>`;
                    
                    // Agregar el equipamiento al contenido existente
                    detailsContainer.innerHTML += equipamientoHTML;
                } else {
                    // Agregar mensaje de "sin equipamiento"
                    detailsContainer.innerHTML += `
                        <div class="person-section">
                            <h4>Equipamiento</h4>
                            <p class="no-items">Esta persona no tiene equipamiento asignado.</p>
                        </div>
                    `;
                }

                viewModal.style.display = 'block';
            });
        })
        .catch(error => {
            console.error('Error al obtener la persona:', error);
            mostrarAlerta('error', `Error al obtener la persona: ${error.message}`);
        });
}

// Abrir modal de edición con los datos de la persona seleccionada
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

    fetch(`/obtener_persona?id=${id}`)
        .then(res => {
            if (!res.ok) {
                return res.text().then(text => { 
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`)
                });
            }
            return res.json();
        })
        .then(persona => {
            if (!persona) {
                throw new Error("No se recibió información de la persona para editar.");
            }

            // Rellenar información básica
            document.getElementById('personName').value = persona.nombre || '';
            document.getElementById('personSurname').value = persona.apellido || '';
            document.getElementById('personRace').value = persona.razaId || '';
            document.getElementById('personEmpire').value = persona.imperioId || '';
            document.getElementById('personProfession').value = persona.profesion || '';
            document.getElementById('personGold').value = persona.oro || 0;
            
            // Características físicas
            document.getElementById('personHeight').value = persona.alto || '';
            document.getElementById('personWidth').value = persona.ancho || '';
            document.getElementById('personBodyFat').value = persona.porcentajeGrasaCorporal || '';
            document.getElementById('personPhysicalDesc').value = persona.descripcionFisica || '';
            document.getElementById('personPersonality').value = persona.personalidad || '';
            
            // Formatear fecha
            if (persona.fechaNacimiento) {
                const fecha = new Date(persona.fechaNacimiento);
                const fechaFormateada = fecha.toISOString().split('T')[0];
                document.getElementById('personBirthDate').value = fechaFormateada;
            }
            
            document.getElementById('personAddress').value = persona.direccion || '';
            
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

            modal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al cargar datos de la persona para editar:', error);
            mostrarAlerta('error', `Error al cargar datos de la persona: ${error.message}`);
            cerrarModal();
        });
}

// Eliminar una persona tras confirmación del usuario
function eliminarPersona(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para eliminarPersona");
        mostrarAlerta('error', 'No se puede eliminar la persona: ID inválido.');
        return;
    }
    
    if (confirm(`¿Está seguro que desea eliminar la persona con ID ${id}? Esta acción no se puede deshacer.`)) {
        fetch(`/eliminar_persona?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                    });
                }
                return res.text();
            })
            .then(resultado => {
                mostrarAlerta('success', `Persona eliminada correctamente. ${resultado}`);
                cargarPersonas();
            })
            .catch(error => {
                console.error('Error al eliminar persona:', error);
                mostrarAlerta('error', `Error al eliminar persona: ${error.message}`);
            });
    }
}

// Abrir modal para añadir una nueva persona
function abrirModalAnadir() {
    const form = document.getElementById('personForm');
    const modalTitle = document.getElementById('personModalTitle');
    const personIdInput = document.getElementById('personId');
    const modal = document.getElementById('personModal');

    if (!form || !modalTitle || !personIdInput || !modal) {
        console.error("Elementos del modal de añadir no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Añadir Nueva Persona';
    personIdInput.value = '';
    
    // Establecer valores por defecto
    document.getElementById('personGold').value = 0;
    document.getElementById('statATK').value = 10;
    document.getElementById('statDEF').value = 10;
    document.getElementById('statHP').value = 10;
    document.getElementById('statSPE').value = 10;
    document.getElementById('statMAT').value = 10;
    document.getElementById('statMDF').value = 10;
    document.getElementById('statXP').value = 0;
    document.getElementById('statLVL').value = 1;
    
    modal.style.display = 'block';
}

// Cerrar el modal de añadir/editar persona
function cerrarModal() {
    const modal = document.getElementById('personModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

// Cerrar el modal de detalles de la persona
function cerrarModalDetalles() {
    const modal = document.getElementById('viewPersonModal');
    if (modal) {
        modal.style.display = 'none';
        const detailsContainer = document.getElementById('personDetails');
        if (detailsContainer) detailsContainer.innerHTML = '';
    }
}

// Funcion para guardar una persona

function guardarPersona() {
    const idValue = document.getElementById('personId').value;
    const id = idValue ? parseInt(idValue) : null;
    
    const personaData = {
        id: id,
        nombre: document.getElementById('personName').value.trim(),
        apellido: document.getElementById('personSurname').value.trim() || null,
        razaId: document.getElementById('personRace').value ? parseInt(document.getElementById('personRace').value) : null,
        imperioId: document.getElementById('personEmpire').value ? parseInt(document.getElementById('personEmpire').value) : null,
        profesion: document.getElementById('personProfession').value.trim() || null,
        oro: parseInt(document.getElementById('personGold').value) || 0,
        alto: parseFloat(document.getElementById('personHeight').value) || null,
        ancho: parseFloat(document.getElementById('personWidth').value) || null,
        porcentajeGrasaCorporal: parseFloat(document.getElementById('personBodyFat').value) || null,
        descripcionFisica: document.getElementById('personPhysicalDesc').value.trim() || null,
        personalidad: document.getElementById('personPersonality').value.trim() || null,
        fechaNacimiento: document.getElementById('personBirthDate').value || null,
        direccion: document.getElementById('personAddress').value.trim() || null,
        
        estadisticas: {
            tipo: 'Personal',
            atk: parseInt(document.getElementById('statATK').value) || 10,
            def: parseInt(document.getElementById('statDEF').value) || 10,
            hp: parseInt(document.getElementById('statHP').value) || 10,
            spe: parseInt(document.getElementById('statSPE').value) || 10,
            mat: parseInt(document.getElementById('statMAT').value) || 10,
            mdf: parseInt(document.getElementById('statMDF').value) || 10,
            xp: parseInt(document.getElementById('statXP').value) || 0,
            lvl: parseInt(document.getElementById('statLVL').value) || 1
        },

        armas: [],
        armaduras: [],
        herramientas: [],
        arcanas: []
    };

    if (!personaData.nombre) {
        mostrarAlerta('error', 'El nombre de la persona es obligatorio.');
        return;
    }
    if (!personaData.razaId) {
        mostrarAlerta('error', 'Debe seleccionar una raza.');
        return;
    }

    document.querySelectorAll('#personWeapons .equipment-item-selected').forEach(item => {
        const equipmentId = item.getAttribute('data-equipment-id');
        if (equipmentId) {
            personaData.armas.push({ id: parseInt(equipmentId) });
        }
    });

    document.querySelectorAll('#personArmor .equipment-item-selected').forEach(item => {
        const equipmentId = item.getAttribute('data-equipment-id');
        if (equipmentId) {
            personaData.armaduras.push({ id: parseInt(equipmentId) });
        }
    });

    document.querySelectorAll('#personTools .equipment-item-selected').forEach(item => {
        const equipmentId = item.getAttribute('data-equipment-id');
        if (equipmentId) {
            personaData.herramientas.push({ id: parseInt(equipmentId) });
        }
    });

    document.querySelectorAll('#personArcana .equipment-item-selected').forEach(item => {
        const equipmentId = item.getAttribute('data-equipment-id');
        if (equipmentId) {
            personaData.arcanas.push({ id: parseInt(equipmentId) });
        }
    });

    const url = id ? '/actualizar_persona' : '/aniadir_persona';

    console.log("Enviando JSON para guardar persona a URL:", url);
    console.log("Datos Persona:", JSON.stringify(personaData, null, 2));

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(personaData)
    })
    .then(res => {
        if (!res.ok) {
            return res.text().then(text => {
                let errorDetail = text;
                try {
                    const jsonError = JSON.parse(text);
                    errorDetail = jsonError.message || jsonError.error || text;
                } catch (e) { /* No era JSON */ }
                throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${errorDetail}`);
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
    });
}

// Aplicar filtros seleccionados para filtrar personas
function aplicarFiltros() {
    const razaId = document.getElementById('filterRace').value;
    const imperioId = document.getElementById('filterEmpire').value;
    const profesion = document.getElementById('filterProfession').value;
    const nivelMin = document.getElementById('filterMinLevel').value;
    
    // Construir URL con parámetros
    let url = '/filtrar_personas?';
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
            return res.json();
        })
        .then(personas => {
            let table = $('#personsTable').DataTable();
            table.clear();
            
            if (Array.isArray(personas)) {
                // Agregar nombres de raza e imperio para mostrar en tabla
                personas.forEach(persona => {
                    persona.razaNombre = persona.raza ? persona.raza.nombre : 'N/A';
                    persona.imperioNombre = persona.imperio ? persona.imperio.nombre : 'N/A';
                });
                
                table.rows.add(personas).draw();
                
                // Arreglar colores de filas
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

// Resetear todos los filtros y cargar todas las personas
function resetearFiltros() {
    const filterRace = document.getElementById('filterRace');
    const filterEmpire = document.getElementById('filterEmpire');
    const filterProfession = document.getElementById('filterProfession');
    const filterMinLevel = document.getElementById('filterMinLevel');
    
    if (filterRace) filterRace.value = '';
    if (filterEmpire) filterEmpire.value = '';
    if (filterProfession) filterProfession.value = '';
    if (filterMinLevel) filterMinLevel.value = 1;
    
    const filterResults = document.getElementById('filterResults');
    if (filterResults) filterResults.textContent = '';
    
    cargarPersonas();
}

// Función para cargar el equipamiento de una persona
function cargarEquipamientoPersona(personaId, callback) {
    const equipamiento = {
        armas: [],
        armaduras: [],
        herramientas: [],
        arcanas: []
    };

    let completedRequests = 0;
    const totalRequests = 4;

    function checkCompletion() {
        completedRequests++;
        if (completedRequests === totalRequests) {
            callback(equipamiento);
        }
    }

    // Cargar armas de la persona
    fetch(`/listar_armas_persona?personaId=${personaId}`)
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}`);
            }
            return res.json();
        })
        .then(armas => {
            if (Array.isArray(armas)) {
                equipamiento.armas = armas;
            }
        })
        .catch(error => {
            console.warn('Error al cargar armas de la persona:', error);
            // No mostrar error al usuario, solo log
        })
        .finally(() => {
            checkCompletion();
        });

    // Cargar armaduras de la persona
    fetch(`/listar_armaduras_persona?personaId=${personaId}`)
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}`);
            }
            return res.json();
        })
        .then(armaduras => {
            if (Array.isArray(armaduras)) {
                equipamiento.armaduras = armaduras;
            }
        })
        .catch(error => {
            console.warn('Error al cargar armaduras de la persona:', error);
        })
        .finally(() => {
            checkCompletion();
        });

    // Cargar herramientas de la persona
    fetch(`/listar_herramientas_persona?personaId=${personaId}`)
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}`);
            }
            return res.json();
        })
        .then(herramientas => {
            if (Array.isArray(herramientas)) {
                equipamiento.herramientas = herramientas;
            }
        })
        .catch(error => {
            console.warn('Error al cargar herramientas de la persona:', error);
        })
        .finally(() => {
            checkCompletion();
        });

    // Cargar arcanas de la persona
    fetch(`/listar_arcanas_persona?personaId=${personaId}`)
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}`);
            }
            return res.json();
        })
        .then(arcanas => {
            if (Array.isArray(arcanas)) {
                equipamiento.arcanas = arcanas;
            }
        })
        .catch(error => {
            console.warn('Error al cargar arcanas de la persona:', error);
        })
        .finally(() => {
            checkCompletion();
        });
}

// Función para abrir el modal de selección de equipamiento
function openEquipmentModal(equipmentType) {
    const modal = document.getElementById('equipmentSelectionModal');
    const modalTitle = document.getElementById('equipmentModalTitle');
    const availableEquipment = document.getElementById('availableEquipment');
    
    if (!modal || !modalTitle || !availableEquipment) {
        console.error('Elementos del modal de equipamiento no encontrados');
        return;
    }
    
    // Configurar título según el tipo
    const titles = {
        'weapon': 'Seleccionar Arma',
        'armor': 'Seleccionar Armadura', 
        'tool': 'Seleccionar Herramienta',
        'arcana': 'Seleccionar Arcana'
    };
    
    modalTitle.textContent = titles[equipmentType] || 'Seleccionar Equipamiento';
    
    // Mostrar indicador de carga
    availableEquipment.innerHTML = '<p>Cargando equipamiento...</p>';
    modal.style.display = 'block';
    
    // Cargar equipamiento según el tipo
    loadEquipmentByType(equipmentType, availableEquipment);
}

// Función para cargar equipamiento por tipo
function loadEquipmentByType(type, container) {
    const endpoints = {
        'weapon': '/listar_armas',
        'armor': '/listar_armaduras',
        'tool': '/listar_herramientas', 
        'arcana': '/listar_arcanas'
    };
    
    const endpoint = endpoints[type];
    if (!endpoint) {
        container.innerHTML = '<p>Tipo de equipamiento no válido</p>';
        return;
    }
    
    fetch(endpoint)
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
            }
            return res.json();
        })
        .then(items => {
            if (!Array.isArray(items) || items.length === 0) {
                container.innerHTML = '<p>No hay equipamiento disponible</p>';
                return;
            }
            
            let html = '<div class="equipment-grid">';
            items.forEach(item => {
                const name = item.nombre || item.tipo || 'Sin nombre';
                const price = item.pvp ? `${item.pvp} oro` : 'Gratis';
                const description = item.descripcion || item.material || 'Sin descripción';
                
                html += `
                    <div class="equipment-item" data-id="${item.id}" data-type="${type}">
                        <h4>${name}</h4>
                        <p class="equipment-price">${price}</p>
                        <p class="equipment-desc">${description}</p>
                        <button class="btn btn-small select-equipment-btn" onclick="selectEquipment(${item.id}, '${type}', '${name}')">
                            Seleccionar
                        </button>
                    </div>
                `;
            });
            html += '</div>';
            
            container.innerHTML = html;
        })
        .catch(error => {
            console.error(`Error al cargar ${type}:`, error);
            container.innerHTML = `<p>Error al cargar equipamiento: ${error.message}</p>`;
        });
}

// Función para seleccionar equipamiento
function selectEquipment(equipmentId, equipmentType, equipmentName) {
    const personId = document.getElementById('personId').value;
    
    if (!personId) {
        mostrarAlerta('warning', 'Debe guardar la persona primero antes de añadir equipamiento');
        return;
    }
    
    // Cerrar modal de selección
    document.getElementById('equipmentSelectionModal').style.display = 'none';
    
    // Añadir equipamiento a la lista visual
    addEquipmentToPersonList(equipmentType, equipmentId, equipmentName);
    
    mostrarAlerta('success', `${equipmentName} añadido correctamente`);
}

// Función para añadir equipamiento a la lista visual
function addEquipmentToPersonList(type, equipmentId, equipmentName) {
    const containers = {
        'weapon': 'personWeapons',
        'armor': 'personArmor',
        'tool': 'personTools',
        'arcana': 'personArcana'
    };
    
    const containerId = containers[type];
    const container = document.getElementById(containerId);
    
    if (!container) {
        console.error(`Contenedor ${containerId} no encontrado`);
        return;
    }
    
    // Remover mensaje de "no hay items" si existe
    const noItemsMsg = container.querySelector('.no-items');
    if (noItemsMsg) {
        noItemsMsg.remove();
    }
    
    // Crear elemento de equipamiento
    const equipmentItem = document.createElement('div');
    equipmentItem.className = 'equipment-item-selected';

    equipmentItem.setAttribute('data-equipment-id', equipmentId); 

    
    equipmentItem.innerHTML = `
        <span class="equipment-name">${equipmentName}</span>
        <button class="remove-equipment-btn" onclick="removeEquipmentFromList(this)" title="Quitar">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    container.appendChild(equipmentItem);
}

// Función para quitar equipamiento de la lista visual
function removeEquipmentFromList(button) {
    const equipmentItem = button.parentElement;
    const container = equipmentItem.parentElement;
    
    equipmentItem.remove();
    
    // Si no quedan items, mostrar mensaje
    if (container.children.length === 0) {
        const noItemsMsg = document.createElement('p');
        noItemsMsg.className = 'no-items';
        
        const messages = {
            'personWeapons': 'No hay armas equipadas',
            'personArmor': 'No hay armaduras equipadas',
            'personTools': 'No hay herramientas equipadas',
            'personArcana': 'No hay arcanas dominadas'
        };
        
        noItemsMsg.textContent = messages[container.id] || 'No hay equipamiento';
        container.appendChild(noItemsMsg);
    }
}

// Mostrar alertas en la parte superior derecha de la pantalla
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
