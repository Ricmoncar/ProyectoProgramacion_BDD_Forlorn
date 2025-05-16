document.addEventListener('DOMContentLoaded', function() {
    /* Variables globales */
    let weaponsTable, armorsTable, toolsTable, arcanasTable;

    /* Inicialización de DataTables */
    function initializeTables() {
        weaponsTable = $('#weaponsTable').DataTable({
            language: {
                url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
            },
            responsive: true,
            columns: [
                { data: 'id' },
                { data: 'nombre' },
                { data: 'material', render: function(data) { return data || 'N/A'; } },
                { 
                    data: 'peso',
                    render: function(data) {
                        return data !== null ? `${data} kg` : 'N/A';
                    }
                },
                { 
                    data: 'pvp',
                    render: function(data) {
                        return data !== null ? `${data} oro` : 'N/A';
                    }
                },
                { data: 'origen', render: function(data) { return data || 'N/A'; } },
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    render: function(data, type, row) {
                        const weaponId = row.id;
                        return `<div class="equipment-actions">
                            <button class="view-btn" title="Ver detalles" onclick="verArma(${weaponId})"><i class="fas fa-eye"></i></button>
                            <button class="edit-btn" title="Editar" onclick="editarArma(${weaponId})"><i class="fas fa-edit"></i></button>
                            <button class="delete-btn" title="Eliminar" onclick="eliminarArma(${weaponId})"><i class="fas fa-trash-alt"></i></button>
                        </div>`;
                    },
                    className: 'dt-center'
                }
            ],
            order: [[1, 'asc']],
            createdRow: function(row, data, dataIndex) {
                $(row).css('background-color', dataIndex % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
            }
        });

        armorsTable = $('#armorsTable').DataTable({
            language: {
                url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
            },
            responsive: true,
            columns: [
                { data: 'id' },
                { data: 'nombre' },
                { data: 'material', render: function(data) { return data || 'N/A'; } },
                { 
                    data: 'peso',
                    render: function(data) {
                        return data !== null ? `${data} kg` : 'N/A';
                    }
                },
                { 
                    data: 'pvp',
                    render: function(data) {
                        return data !== null ? `${data} oro` : 'N/A';
                    }
                },
                { data: 'origen', render: function(data) { return data || 'N/A'; } },
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    render: function(data, type, row) {
                        const armorId = row.id;
                        return `<div class="equipment-actions">
                            <button class="view-btn" title="Ver detalles" onclick="verArmadura(${armorId})"><i class="fas fa-eye"></i></button>
                            <button class="edit-btn" title="Editar" onclick="editarArmadura(${armorId})"><i class="fas fa-edit"></i></button>
                            <button class="delete-btn" title="Eliminar" onclick="eliminarArmadura(${armorId})"><i class="fas fa-trash-alt"></i></button>
                        </div>`;
                    },
                    className: 'dt-center'
                }
            ],
            order: [[1, 'asc']],
            createdRow: function(row, data, dataIndex) {
                $(row).css('background-color', dataIndex % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
            }
        });

        toolsTable = $('#toolsTable').DataTable({
            language: {
                url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
            },
            responsive: true,
            columns: [
                { data: 'id' },
                { data: 'nombre' },
                { data: 'material', render: function(data) { return data || 'N/A'; } },
                { 
                    data: 'peso',
                    render: function(data) {
                        return data !== null ? `${data} kg` : 'N/A';
                    }
                },
                { 
                    data: 'pvp',
                    render: function(data) {
                        return data !== null ? `${data} oro` : 'N/A';
                    }
                },
                { data: 'uso', render: function(data) { return data || 'N/A'; } },
                { data: 'origen', render: function(data) { return data || 'N/A'; } },
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    render: function(data, type, row) {
                        const toolId = row.id;
                        return `<div class="equipment-actions">
                            <button class="view-btn" title="Ver detalles" onclick="verHerramienta(${toolId})"><i class="fas fa-eye"></i></button>
                            <button class="edit-btn" title="Editar" onclick="editarHerramienta(${toolId})"><i class="fas fa-edit"></i></button>
                            <button class="delete-btn" title="Eliminar" onclick="eliminarHerramienta(${toolId})"><i class="fas fa-trash-alt"></i></button>
                        </div>`;
                    },
                    className: 'dt-center'
                }
            ],
            order: [[1, 'asc']],
            createdRow: function(row, data, dataIndex) {
                $(row).css('background-color', dataIndex % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
            }
        });

        arcanasTable = $('#arcanasTable').DataTable({
            language: {
                url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
            },
            responsive: true,
            columns: [
                { data: 'id' },
                { data: 'tipo' },
                { data: 'maestria' },
                { data: 'dificultad' },
                { 
                    data: 'fecha',
                    render: function(data) {
                        if (!data) return 'N/A';
                        const fecha = new Date(data);
                        return fecha.toLocaleDateString('es-ES');
                    }
                },
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    render: function(data, type, row) {
                        const arcanaId = row.id;
                        return `<div class="equipment-actions">
                            <button class="view-btn" title="Ver detalles" onclick="verArcana(${arcanaId})"><i class="fas fa-eye"></i></button>
                            <button class="edit-btn" title="Editar" onclick="editarArcana(${arcanaId})"><i class="fas fa-edit"></i></button>
                            <button class="delete-btn" title="Eliminar" onclick="eliminarArcana(${arcanaId})"><i class="fas fa-trash-alt"></i></button>
                        </div>`;
                    },
                    className: 'dt-center'
                }
            ],
            order: [[1, 'asc']],
            createdRow: function(row, data, dataIndex) {
                $(row).css('background-color', dataIndex % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
            }
        });
    }

    /* Manejo de pestañas */
    function setupTabs() {
        const tabBtns = document.querySelectorAll('.tab-btn');
        const tabContents = document.querySelectorAll('.tab-content');

        tabBtns.forEach(btn => {
            btn.addEventListener('click', () => {
                const targetTab = btn.getAttribute('data-tab');

                // Remover clase activa de todas las pestañas y contenidos
                tabBtns.forEach(b => b.classList.remove('active'));
                tabContents.forEach(c => c.classList.remove('active'));

                // Añadir clase activa a la pestaña y contenido seleccionados
                btn.classList.add('active');
                document.getElementById(targetTab).classList.add('active');
            });
        });
    }

    /* Carga de datos */
    function cargarArmas() {
        fetch("http://localhost:8080/listar_armas")
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
            .then(armas => {
                weaponsTable.clear();
                if (Array.isArray(armas)) {
                    weaponsTable.rows.add(armas).draw();
                    // Corrige el problema de visualización repintando las filas
                    $('#weaponsTable tbody tr').each(function(index) {
                        $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                    });
                } else {
                    console.error("La respuesta de listar_armas no es un array:", armas);
                    mostrarAlerta('error', 'Error: Formato de datos de armas inesperado.');
                }
            })
            .catch(error => {
                console.error('Error al cargar armas:', error);
                mostrarAlerta('error', `No se pudieron cargar las armas: ${error.message}`);
            });
    }

    function cargarArmaduras() {
        fetch("http://localhost:8080/listar_armaduras")
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
            .then(armaduras => {
                armorsTable.clear();
                if (Array.isArray(armaduras)) {
                    armorsTable.rows.add(armaduras).draw();
                    // Corrige el problema de visualización repintando las filas
                    $('#armorsTable tbody tr').each(function(index) {
                        $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                    });
                } else {
                    console.error("La respuesta de listar_armaduras no es un array:", armaduras);
                    mostrarAlerta('error', 'Error: Formato de datos de armaduras inesperado.');
                }
            })
            .catch(error => {
                console.error('Error al cargar armaduras:', error);
                mostrarAlerta('error', `No se pudieron cargar las armaduras: ${error.message}`);
            });
    }

    function cargarHerramientas() {
        fetch("http://localhost:8080/listar_herramientas")
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
            .then(herramientas => {
                toolsTable.clear();
                if (Array.isArray(herramientas)) {
                    toolsTable.rows.add(herramientas).draw();
                    // Corrige el problema de visualización repintando las filas
                    $('#toolsTable tbody tr').each(function(index) {
                        $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                    });
                } else {
                    console.error("La respuesta de listar_herramientas no es un array:", herramientas);
                    mostrarAlerta('error', 'Error: Formato de datos de herramientas inesperado.');
                }
            })
            .catch(error => {
                console.error('Error al cargar herramientas:', error);
                mostrarAlerta('error', `No se pudieron cargar las herramientas: ${error.message}`);
            });
    }

    function cargarArcanas() {
        fetch("http://localhost:8080/listar_arcanas")
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
            .then(arcanas => {
                arcanasTable.clear();
                if (Array.isArray(arcanas)) {
                    arcanasTable.rows.add(arcanas).draw();
                    // Corrige el problema de visualización repintando las filas
                    $('#arcanasTable tbody tr').each(function(index) {
                        $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                    });
                } else {
                    console.error("La respuesta de listar_arcanas no es un array:", arcanas);
                    mostrarAlerta('error', 'Error: Formato de datos de arcanas inesperado.');
                }
            })
            .catch(error => {
                console.error('Error al cargar arcanas:', error);
                mostrarAlerta('error', `No se pudieron cargar las arcanas: ${error.message}`);
            });
    }

    /* Funciones de Armas */
    window.verArma = function(id) {
        // Placeholder - necesita implementación en el backend
        mostrarAlerta('info', 'Función de ver arma pendiente de implementación.');
    };

    window.editarArma = function(id) {
        // Placeholder - necesita implementación
        mostrarAlerta('info', 'Función de editar arma pendiente de implementación.');
    };

    window.eliminarArma = function(id) {
        // Placeholder - necesita implementación
        mostrarAlerta('info', 'Función de eliminar arma pendiente de implementación.');
    };

    /* Funciones de Armaduras */
    window.verArmadura = function(id) {
        // Placeholder - necesita implementación en el backend
        mostrarAlerta('info', 'Función de ver armadura pendiente de implementación.');
    };

    window.editarArmadura = function(id) {
        // Placeholder - necesita implementación
        mostrarAlerta('info', 'Función de editar armadura pendiente de implementación.');
    };

    window.eliminarArmadura = function(id) {
        // Placeholder - necesita implementación
        mostrarAlerta('info', 'Función de eliminar armadura pendiente de implementación.');
    };

    /* Funciones de Herramientas */
    window.verHerramienta = function(id) {
        // Placeholder - necesita implementación en el backend
        mostrarAlerta('info', 'Función de ver herramienta pendiente de implementación.');
    };

    window.editarHerramienta = function(id) {
        // Placeholder - necesita implementación
        mostrarAlerta('info', 'Función de editar herramienta pendiente de implementación.');
    };

    window.eliminarHerramienta = function(id) {
        // Placeholder - necesita implementación
        mostrarAlerta('info', 'Función de eliminar herramienta pendiente de implementación.');
    };

    /* Funciones de Arcanas */
    window.verArcana = function(id) {
        // Placeholder - necesita implementación en el backend
        mostrarAlerta('info', 'Función de ver arcana pendiente de implementación.');
    };

    window.editarArcana = function(id) {
        // Placeholder - necesita implementación
        mostrarAlerta('info', 'Función de editar arcana pendiente de implementación.');
    };

    window.eliminarArcana = function(id) {
        // Placeholder - necesita implementación
        mostrarAlerta('info', 'Función de eliminar arcana pendiente de implementación.');
    };

    /* Funciones de modales */
    function abrirModalArma() {
        const form = document.getElementById('weaponForm');
        const modalTitle = document.getElementById('weaponModalTitle');
        const weaponIdInput = document.getElementById('weaponId');
        const modal = document.getElementById('weaponModal');

        if (!form || !modalTitle || !weaponIdInput || !modal) {
            console.error("Elementos del modal de arma no encontrados.");
            return;
        }

        form.reset();
        modalTitle.textContent = 'Añadir Nueva Arma';
        weaponIdInput.value = '';
        modal.style.display = 'block';
    }

    function abrirModalArmadura() {
        const form = document.getElementById('armorForm');
        const modalTitle = document.getElementById('armorModalTitle');
        const armorIdInput = document.getElementById('armorId');
        const modal = document.getElementById('armorModal');

        if (!form || !modalTitle || !armorIdInput || !modal) {
            console.error("Elementos del modal de armadura no encontrados.");
            return;
        }

        form.reset();
        modalTitle.textContent = 'Añadir Nueva Armadura';
        armorIdInput.value = '';
        modal.style.display = 'block';
    }

    function abrirModalHerramienta() {
        const form = document.getElementById('toolForm');
        const modalTitle = document.getElementById('toolModalTitle');
        const toolIdInput = document.getElementById('toolId');
        const modal = document.getElementById('toolModal');

        if (!form || !modalTitle || !toolIdInput || !modal) {
            console.error("Elementos del modal de herramienta no encontrados.");
            return;
        }

        form.reset();
        modalTitle.textContent = 'Añadir Nueva Herramienta';
        toolIdInput.value = '';
        modal.style.display = 'block';
    }

    function abrirModalArcana() {
        const form = document.getElementById('arcanaForm');
        const modalTitle = document.getElementById('arcanaModalTitle');
        const arcanaIdInput = document.getElementById('arcanaId');
        const modal = document.getElementById('arcanaModal');

        if (!form || !modalTitle || !arcanaIdInput || !modal) {
            console.error("Elementos del modal de arcana no encontrados.");
            return;
        }

        form.reset();
        modalTitle.textContent = 'Añadir Nueva Arcana';
        arcanaIdInput.value = '';
        modal.style.display = 'block';
    }

    function cerrarModales() {
        const modales = ['weaponModal', 'armorModal', 'toolModal', 'arcanaModal'];
        modales.forEach(modalId => {
            const modal = document.getElementById(modalId);
            if (modal) modal.style.display = 'none';
        });
    }

    /* Función de alertas */
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

    /* Configuración de eventos */
    function setupEventListeners() {
        // Botones de añadir
        const addWeaponBtn = document.getElementById('addWeaponBtn');
        const addArmorBtn = document.getElementById('addArmorBtn');
        const addToolBtn = document.getElementById('addToolBtn');
        const addArcanaBtn = document.getElementById('addArcanaBtn');

        if (addWeaponBtn) addWeaponBtn.addEventListener('click', abrirModalArma);
        if (addArmorBtn) addArmorBtn.addEventListener('click', abrirModalArmadura);
        if (addToolBtn) addToolBtn.addEventListener('click', abrirModalHerramienta);
        if (addArcanaBtn) addArcanaBtn.addEventListener('click', abrirModalArcana);

        // Botones de cancelar en los modales
        const cancelBtns = ['cancelWeaponBtn', 'cancelArmorBtn', 'cancelToolBtn', 'cancelArcanaBtn'];
        cancelBtns.forEach(btnId => {
            const btn = document.getElementById(btnId);
            if (btn) btn.addEventListener('click', cerrarModales);
        });

        // Eventos para cerrar modales con X
        let closeButtons = document.querySelectorAll('.close-modal');
        closeButtons.forEach(function(btn) {
            btn.addEventListener('click', cerrarModales);
        });

        // Cerrar modal al hacer clic fuera del contenido
        window.addEventListener('click', function(event) {
            const modals = ['weaponModal', 'armorModal', 'toolModal', 'arcanaModal'];
            modals.forEach(modalId => {
                const modal = document.getElementById(modalId);
                if (event.target == modal) {
                    cerrarModales();
                }
            });
        });

        // Configuración de los formularios
        const forms = [
            { id: 'weaponForm', handler: guardarArma },
            { id: 'armorForm', handler: guardarArmadura },
            { id: 'toolForm', handler: guardarHerramienta },
            { id: 'arcanaForm', handler: guardarArcana }
        ];

        forms.forEach(({id, handler}) => {
            const form = document.getElementById(id);
            if (form) {
                form.addEventListener('submit', function(e) {
                    e.preventDefault();
                    handler();
                });
            }
        });
    }

    /* Funciones de guardado */
    function guardarArma() {
        mostrarAlerta('info', 'Función de guardar arma pendiente de implementación.');
    }

    function guardarArmadura() {
        mostrarAlerta('info', 'Función de guardar armadura pendiente de implementación.');
    }

    function guardarHerramienta() {
        mostrarAlerta('info', 'Función de guardar herramienta pendiente de implementación.');
    }

    function guardarArcana() {
        mostrarAlerta('info', 'Función de guardar arcana pendiente de implementación.');
    }

    /* Inicialización */
    setupTabs();
    initializeTables();
    setupEventListeners();
    
    // Cargar datos iniciales
    cargarArmas();
    cargarArmaduras();
    cargarHerramientas();
    cargarArcanas();

    /* Corregir visualización de las tablas */
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
});