document.addEventListener('DOMContentLoaded', function() {
    /* Variables globales */
    let weaponsTable, armorsTable, toolsTable, arcanasTable;
    let imperiosDisponibles = [];

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

    /* Carga de imperiums */
    function cargarImperios() {
        fetch("http://localhost:8080/listar_imperios")
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                }
                return res.json();
            })
            .then(imperios => {
                imperiosDisponibles = imperios;
                
                // Llenar los selectores de imperio en todos los modales
                ['weaponOrigin', 'armorOrigin', 'toolOrigin'].forEach(selectId => {
                    const select = document.getElementById(selectId);
                    if (select) {
                        select.innerHTML = '<option value="">Seleccione un imperio</option>';
                        imperios.forEach(imperio => {
                            const option = document.createElement('option');
                            option.value = imperio.id;
                            option.textContent = imperio.nombre;
                            select.appendChild(option);
                        });
                    }
                });
            })
            .catch(error => {
                console.error('Error al cargar imperios:', error);
                mostrarAlerta('error', `No se pudieron cargar los imperios: ${error.message}`);
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
        fetch(`http://localhost:8080/obtener_arma?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                }
                return res.json();
            })
            .then(arma => {
                let detallesHTML = `
                    <div class="equipment-details">
                        <h4>Detalles del Arma</h4>
                        <div class="detail-row">
                            <span class="detail-label">Nombre:</span>
                            <span class="detail-value">${arma.nombre}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Material:</span>
                            <span class="detail-value">${arma.material || 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Peso:</span>
                            <span class="detail-value">${arma.peso ? arma.peso + ' kg' : 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Precio:</span>
                            <span class="detail-value">${arma.pvp ? arma.pvp + ' oro' : 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Imperio de Origen:</span>
                            <span class="detail-value">${arma.origen || 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Fecha de Creación:</span>
                            <span class="detail-value">${arma.fechaCreacion ? new Date(arma.fechaCreacion).toLocaleDateString('es-ES') : 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Buff de Estadísticas:</span>
                            <span class="detail-value">${arma.bufoEstadisticas || 'Sin modificadores'}</span>
                        </div>
                        <div class="detail-row full-width">
                            <span class="detail-label">Descripción:</span>
                            <div class="detail-description">${arma.descripcion || 'N/A'}</div>
                        </div>
                    </div>
                `;
                mostrarModal('Arma', detallesHTML);
            })
            .catch(error => {
                console.error('Error al obtener arma:', error);
                mostrarAlerta('error', `Error al obtener arma: ${error.message}`);
            });
    };

    window.editarArma = function(id) {
        fetch(`http://localhost:8080/obtener_arma?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                }
                return res.json();
            })
            .then(arma => {
                // Llenar el formulario con los datos del arma
                document.getElementById('weaponId').value = arma.id;
                document.getElementById('weaponName').value = arma.nombre;
                document.getElementById('weaponMaterial').value = arma.material || '';
                document.getElementById('weaponWeight').value = arma.peso || '';
                document.getElementById('weaponPrice').value = arma.pvp || '';
                
                // Buscar el ID del imperio por su nombre
                const imperio = imperiosDisponibles.find(imp => imp.nombre === arma.origen);
                document.getElementById('weaponOrigin').value = imperio ? imperio.id : '';
                
                if (arma.fechaCreacion) {
                    const fecha = new Date(arma.fechaCreacion);
                    document.getElementById('weaponCreationDate').value = fecha.toISOString().split('T')[0];
                }
                
                // Parsear los buffs desde el string
                const buffs = parsearEstadisticas(arma.bufoEstadisticas);
                document.getElementById('weaponBufAtk').value = buffs.atk || 0;
                document.getElementById('weaponBufDef').value = buffs.def || 0;
                document.getElementById('weaponBufHp').value = buffs.hp || 0;
                document.getElementById('weaponBufSpe').value = buffs.spe || 0;
                document.getElementById('weaponBufMat').value = buffs.mat || 0;
                document.getElementById('weaponBufMdf').value = buffs.mdf || 0;
                
                document.getElementById('weaponDescription').value = arma.descripcion || '';
                
                // Cambiar el título del modal
                document.getElementById('weaponModalTitle').textContent = 'Editar Arma';
                
                // Mostrar el modal
                document.getElementById('weaponModal').style.display = 'block';
            })
            .catch(error => {
                console.error('Error al obtener arma para editar:', error);
                mostrarAlerta('error', `Error al obtener arma: ${error.message}`);
            });
    };

    window.eliminarArma = function(id) {
        if (confirm('¿Está seguro de que desea eliminar esta arma?')) {
            fetch(`http://localhost:8080/eliminar_arma?id=${id}`)
                .then(res => {
                    if (!res.ok) {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                    }
                    return res.text();
                })
                .then(resultado => {
                    mostrarAlerta('success', 'Arma eliminada correctamente.');
                    cargarArmas();
                })
                .catch(error => {
                    console.error('Error al eliminar arma:', error);
                    mostrarAlerta('error', `Error al eliminar arma: ${error.message}`);
                });
        }
    };

    /* Funciones de Armaduras */
    window.verArmadura = function(id) {
        fetch(`http://localhost:8080/obtener_armadura?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                }
                return res.json();
            })
            .then(armadura => {
                let detallesHTML = `
                    <div class="equipment-details">
                        <h4>Detalles de la Armadura</h4>
                        <div class="detail-row">
                            <span class="detail-label">Nombre:</span>
                            <span class="detail-value">${armadura.nombre}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Material:</span>
                            <span class="detail-value">${armadura.material || 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Peso:</span>
                            <span class="detail-value">${armadura.peso ? armadura.peso + ' kg' : 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Precio:</span>
                            <span class="detail-value">${armadura.pvp ? armadura.pvp + ' oro' : 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Imperio de Origen:</span>
                            <span class="detail-value">${armadura.origen || 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Fecha de Creación:</span>
                            <span class="detail-value">${armadura.fechaCreacion ? new Date(armadura.fechaCreacion).toLocaleDateString('es-ES') : 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Buff de Estadísticas:</span>
                            <span class="detail-value">${armadura.bufoEstadisticas || 'Sin modificadores'}</span>
                        </div>
                        <div class="detail-row full-width">
                            <span class="detail-label">Descripción:</span>
                            <div class="detail-description">${armadura.descripcion || 'N/A'}</div>
                        </div>
                    </div>
                `;
                mostrarModal('Armadura', detallesHTML);
            })
            .catch(error => {
                console.error('Error al obtener armadura:', error);
                mostrarAlerta('error', `Error al obtener armadura: ${error.message}`);
            });
    };

    window.editarArmadura = function(id) {
        fetch(`http://localhost:8080/obtener_armadura?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                }
                return res.json();
            })
            .then(armadura => {
                // Llenar el formulario con los datos de la armadura
                document.getElementById('armorId').value = armadura.id;
                document.getElementById('armorName').value = armadura.nombre;
                document.getElementById('armorMaterial').value = armadura.material || '';
                document.getElementById('armorWeight').value = armadura.peso || '';
                document.getElementById('armorPrice').value = armadura.pvp || '';
                
                // Buscar el ID del imperio por su nombre
                const imperio = imperiosDisponibles.find(imp => imp.nombre === armadura.origen);
                document.getElementById('armorOrigin').value = imperio ? imperio.id : '';
                
                if (armadura.fechaCreacion) {
                    const fecha = new Date(armadura.fechaCreacion);
                    document.getElementById('armorCreationDate').value = fecha.toISOString().split('T')[0];
                }
                
                // Parsear los buffs desde el string
                const buffs = parsearEstadisticas(armadura.bufoEstadisticas);
                document.getElementById('armorBufAtk').value = buffs.atk || 0;
                document.getElementById('armorBufDef').value = buffs.def || 0;
                document.getElementById('armorBufHp').value = buffs.hp || 0;
                document.getElementById('armorBufSpe').value = buffs.spe || 0;
                document.getElementById('armorBufMat').value = buffs.mat || 0;
                document.getElementById('armorBufMdf').value = buffs.mdf || 0;
                
                document.getElementById('armorDescription').value = armadura.descripcion || '';
                
                // Cambiar el título del modal
                document.getElementById('armorModalTitle').textContent = 'Editar Armadura';
                
                // Mostrar el modal
                document.getElementById('armorModal').style.display = 'block';
            })
            .catch(error => {
                console.error('Error al obtener armadura para editar:', error);
                mostrarAlerta('error', `Error al obtener armadura: ${error.message}`);
            });
    };

    window.eliminarArmadura = function(id) {
        if (confirm('¿Está seguro de que desea eliminar esta armadura?')) {
            fetch(`http://localhost:8080/eliminar_armadura?id=${id}`)
                .then(res => {
                    if (!res.ok) {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                    }
                    return res.text();
                })
                .then(resultado => {
                    mostrarAlerta('success', 'Armadura eliminada correctamente.');
                    cargarArmaduras();
                })
                .catch(error => {
                    console.error('Error al eliminar armadura:', error);
                    mostrarAlerta('error', `Error al eliminar armadura: ${error.message}`);
                });
        }
    };

    /* Funciones de Herramientas */
    window.verHerramienta = function(id) {
        fetch(`http://localhost:8080/obtener_herramienta?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                }
                return res.json();
            })
            .then(herramienta => {
                let detallesHTML = `
                    <div class="equipment-details">
                        <h4>Detalles de la Herramienta</h4>
                        <div class="detail-row">
                            <span class="detail-label">Nombre:</span>
                            <span class="detail-value">${herramienta.nombre}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Material:</span>
                            <span class="detail-value">${herramienta.material || 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Peso:</span>
                            <span class="detail-value">${herramienta.peso ? herramienta.peso + ' kg' : 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Precio:</span>
                            <span class="detail-value">${herramienta.pvp ? herramienta.pvp + ' oro' : 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Uso:</span>
                            <span class="detail-value">${herramienta.uso || 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Imperio de Origen:</span>
                            <span class="detail-value">${herramienta.origen || 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Fecha de Creación:</span>
                            <span class="detail-value">${herramienta.fechaCreacion ? new Date(herramienta.fechaCreacion).toLocaleDateString('es-ES') : 'N/A'}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Buff de Estadísticas:</span>
                            <span class="detail-value">${herramienta.bufoEstadisticas || 'Sin modificadores'}</span>
                        </div>
                        <div class="detail-row full-width">
                            <span class="detail-label">Descripción:</span>
                            <div class="detail-description">${herramienta.descripcion || 'N/A'}</div>
                        </div>
                    </div>
                `;
                mostrarModal('Herramienta', detallesHTML);
            })
            .catch(error => {
                console.error('Error al obtener herramienta:', error);
                mostrarAlerta('error', `Error al obtener herramienta: ${error.message}`);
            });
    };

    window.editarHerramienta = function(id) {
        fetch(`http://localhost:8080/obtener_herramienta?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                }
                return res.json();
            })
            .then(herramienta => {
                // Llenar el formulario con los datos de la herramienta
                document.getElementById('toolId').value = herramienta.id;
                document.getElementById('toolName').value = herramienta.nombre;
                document.getElementById('toolMaterial').value = herramienta.material || '';
                document.getElementById('toolWeight').value = herramienta.peso || '';
                document.getElementById('toolPrice').value = herramienta.pvp || '';
                document.getElementById('toolUse').value = herramienta.uso || '';
                
                // Buscar el ID del imperio por su nombre
                const imperio = imperiosDisponibles.find(imp => imp.nombre === herramienta.origen);
                document.getElementById('toolOrigin').value = imperio ? imperio.id : '';
                
                if (herramienta.fechaCreacion) {
                    const fecha = new Date(herramienta.fechaCreacion);
                    document.getElementById('toolCreationDate').value = fecha.toISOString().split('T')[0];
                }
                
                // Parsear los buffs desde el string
                const buffs = parsearEstadisticas(herramienta.bufoEstadisticas);
                document.getElementById('toolBufAtk').value = buffs.atk || 0;
                document.getElementById('toolBufDef').value = buffs.def || 0;
                document.getElementById('toolBufHp').value = buffs.hp || 0;
                document.getElementById('toolBufSpe').value = buffs.spe || 0;
                document.getElementById('toolBufMat').value = buffs.mat || 0;
                document.getElementById('toolBufMdf').value = buffs.mdf || 0;
                
                document.getElementById('toolDescription').value = herramienta.descripcion || '';
                
                // Cambiar el título del modal
                document.getElementById('toolModalTitle').textContent = 'Editar Herramienta';
                
                // Mostrar el modal
                document.getElementById('toolModal').style.display = 'block';
            })
            .catch(error => {
                console.error('Error al obtener herramienta para editar:', error);
                mostrarAlerta('error', `Error al obtener herramienta: ${error.message}`);
            });
    };

    window.eliminarHerramienta = function(id) {
        if (confirm('¿Está seguro de que desea eliminar esta herramienta?')) {
            fetch(`http://localhost:8080/eliminar_herramienta?id=${id}`)
                .then(res => {
                    if (!res.ok) {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                    }
                    return res.text();
                })
                .then(resultado => {
                    mostrarAlerta('success', 'Herramienta eliminada correctamente.');
                    cargarHerramientas();
                })
                .catch(error => {
                    console.error('Error al eliminar herramienta:', error);
                    mostrarAlerta('error', `Error al eliminar herramienta: ${error.message}`);
                });
        }
    };

    /* Funciones de Arcanas */
    window.verArcana = function(id) {
        fetch(`http://localhost:8080/obtener_arcana?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                }
                return res.json();
            })
            .then(arcana => {
                let detallesHTML = `
                    <div class="equipment-details">
                        <h4>Detalles de la Arcana</h4>
                        <div class="detail-row">
                            <span class="detail-label">Tipo:</span>
                            <span class="detail-value">${arcana.tipo}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Maestría:</span>
                            <span class="detail-value">${arcana.maestria}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Dificultad:</span>
                            <span class="detail-value">${arcana.dificultad}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Fecha:</span>
                            <span class="detail-value">${arcana.fecha ? new Date(arcana.fecha).toLocaleDateString('es-ES') : 'N/A'}</span>
                        </div>
                    </div>
                `;
                mostrarModal('Arcana', detallesHTML);
            })
            .catch(error => {
                console.error('Error al obtener arcana:', error);
                mostrarAlerta('error', `Error al obtener arcana: ${error.message}`);
            });
    };

    window.editarArcana = function(id) {
        fetch(`http://localhost:8080/obtener_arcana?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                }
                return res.json();
            })
            .then(arcana => {
                // Llenar el formulario con los datos de la arcana
                document.getElementById('arcanaId').value = arcana.id;
                document.getElementById('arcanaType').value = arcana.tipo;
                document.getElementById('arcanaMastery').value = arcana.maestria;
                document.getElementById('arcanaDifficulty').value = arcana.dificultad;
                
                if (arcana.fecha) {
                    const fecha = new Date(arcana.fecha);
                    document.getElementById('arcanaDate').value = fecha.toISOString().split('T')[0];
                }
                
                // Cambiar el título del modal
                document.getElementById('arcanaModalTitle').textContent = 'Editar Arcana';
                
                // Mostrar el modal
                document.getElementById('arcanaModal').style.display = 'block';
            })
            .catch(error => {
                console.error('Error al obtener arcana para editar:', error);
                mostrarAlerta('error', `Error al obtener arcana: ${error.message}`);
            });
    };

    window.eliminarArcana = function(id) {
        if (confirm('¿Está seguro de que desea eliminar esta arcana?')) {
            fetch(`http://localhost:8080/eliminar_arcana?id=${id}`)
                .then(res => {
                    if (!res.ok) {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                    }
                    return res.text();
                })
                .then(resultado => {
                    mostrarAlerta('success', 'Arcana eliminada correctamente.');
                    cargarArcanas();
                })
                .catch(error => {
                    console.error('Error al eliminar arcana:', error);
                    mostrarAlerta('error', `Error al eliminar arcana: ${error.message}`);
                });
        }
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
        
        // Resetear valores de buff a 0
        ['weaponBufAtk', 'weaponBufDef', 'weaponBufHp', 'weaponBufSpe', 'weaponBufMat', 'weaponBufMdf'].forEach(id => {
            document.getElementById(id).value = 0;
        });
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
        
        // Resetear valores de buff a 0
        ['armorBufAtk', 'armorBufDef', 'armorBufHp', 'armorBufSpe', 'armorBufMat', 'armorBufMdf'].forEach(id => {
            document.getElementById(id).value = 0;
        });
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
        
        // Resetear valores de buff a 0
        ['toolBufAtk', 'toolBufDef', 'toolBufHp', 'toolBufSpe', 'toolBufMat', 'toolBufMdf'].forEach(id => {
            document.getElementById(id).value = 0;
        });
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

    /* Función para mostrar modal de detalles */
    function mostrarModal(titulo, contenido) {
        // Crear modal dinámico para mostrar detalles
        let modal = document.getElementById('detailsModal');
        if (!modal) {
            modal = document.createElement('div');
            modal.id = 'detailsModal';
            modal.className = 'modal';
            modal.innerHTML = `
                <div class="modal-content">
                    <span class="close-modal">&times;</span>
                    <h3 id="detailsModalTitle">${titulo}</h3>
                    <div id="detailsModalContent">${contenido}</div>
                </div>
            `;
            document.body.appendChild(modal);
            
            // Agregar evento al botón de cerrar
            modal.querySelector('.close-modal').addEventListener('click', () => {
                modal.style.display = 'none';
            });
        } else {
            document.getElementById('detailsModalTitle').textContent = titulo;
            document.getElementById('detailsModalContent').innerHTML = contenido;
        }
        
        modal.style.display = 'block';
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

    /* Función para parsear estadísticas desde string */
    function parsearEstadisticas(estadisticasString) {
        const stats = {
            atk: 0,
            def: 0,
            hp: 0,
            spe: 0,
            mat: 0,
            mdf: 0
        };
        
        if (!estadisticasString || estadisticasString === 'Sin modificadores') {
            return stats;
        }
        
        // Parsear string como "+5 ATK, +3 DEF, -2 HP"
        const regex = /([+-]\d+)\s+(ATK|DEF|HP|SPE|MAT|MDF)/gi;
        let match;
        
        while ((match = regex.exec(estadisticasString)) !== null) {
            const valor = parseInt(match[1]);
            const stat = match[2].toLowerCase();
            stats[stat] = valor;
        }
        
        return stats;
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
        const id = document.getElementById('weaponId').value;
        const nombre = document.getElementById('weaponName').value.trim();
        const material = document.getElementById('weaponMaterial').value.trim();
        const peso = document.getElementById('weaponWeight').value;
        const precio = document.getElementById('weaponPrice').value;
        const imperioOrigen = document.getElementById('weaponOrigin').value;
        const fechaCreacion = document.getElementById('weaponCreationDate').value;
        const descripcion = document.getElementById('weaponDescription').value.trim();
        
        // Obtener valores de buff
        const bufAtk = document.getElementById('weaponBufAtk').value;
        const bufDef = document.getElementById('weaponBufDef').value;
        const bufHp = document.getElementById('weaponBufHp').value;
        const bufSpe = document.getElementById('weaponBufSpe').value;
        const bufMat = document.getElementById('weaponBufMat').value;
        const bufMdf = document.getElementById('weaponBufMdf').value;
        
        if (!nombre) {
            mostrarAlerta('error', 'El nombre del arma es obligatorio');
            return;
        }
        
        if (!imperioOrigen) {
            mostrarAlerta('error', 'Debe seleccionar un imperio de origen');
            return;
        }
        
        // Construir la URL
        const baseUrl = id ? 'http://localhost:8080/actualizar_arma' : 'http://localhost:8080/aniadir_arma';
        const params = new URLSearchParams();
        
        if (id) params.append('id', id);
        params.append('nombre', nombre);
        if (material) params.append('material', material);
        if (descripcion) params.append('descripcion', descripcion);
        if (peso) params.append('peso', peso);
        if (precio) params.append('pvp', precio);
        params.append('imperioOrigen', imperioOrigen);
        if (fechaCreacion) params.append('fechaCreacion', fechaCreacion);
        params.append('bufAtk', bufAtk);
        params.append('bufDef', bufDef);
        params.append('bufHp', bufHp);
        params.append('bufSpe', bufSpe);
        params.append('bufMat', bufMat);
        params.append('bufMdf', bufMdf);
        
        const url = `${baseUrl}?${params.toString()}`;
        
        fetch(url)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
                }
                return res.text();
            })
            .then(resultado => {
                mostrarAlerta('success', id ? 'Arma actualizada correctamente' : 'Arma añadida correctamente');
                cerrarModales();
                cargarArmas();
            })
            .catch(error => {
                console.error('Error al guardar arma:', error);
                mostrarAlerta('error', `Error al guardar arma: ${error.message}`);
            });
    }

    function guardarArmadura() {
        const id = document.getElementById('armorId').value;
        const nombre = document.getElementById('armorName').value.trim();
        const material = document.getElementById('armorMaterial').value.trim();
        const peso = document.getElementById('armorWeight').value;
        const precio = document.getElementById('armorPrice').value;
        const imperioOrigen = document.getElementById('armorOrigin').value;
        const fechaCreacion = document.getElementById('armorCreationDate').value;
        const descripcion = document.getElementById('armorDescription').value.trim();
        
        // Obtener valores de buff
        const bufAtk = document.getElementById('armorBufAtk').value;
        const bufDef = document.getElementById('armorBufDef').value;
        const bufHp = document.getElementById('armorBufHp').value;
        const bufSpe = document.getElementById('armorBufSpe').value;
        const bufMat = document.getElementById('armorBufMat').value;
        const bufMdf = document.getElementById('armorBufMdf').value;
        
        if (!nombre) {
            mostrarAlerta('error', 'El nombre de la armadura es obligatorio');
            return;
        }
        
        if (!imperioOrigen) {
            mostrarAlerta('error', 'Debe seleccionar un imperio de origen');
            return;
        }
        
        // Construir la URL
        const baseUrl = id ? 'http://localhost:8080/actualizar_armadura' : 'http://localhost:8080/aniadir_armadura';
        const params = new URLSearchParams();
        
        if (id) params.append('id', id);
        params.append('nombre', nombre);
        if (material) params.append('material', material);
        if (descripcion) params.append('descripcion', descripcion);
        if (peso) params.append('peso', peso);
        if (precio) params.append('pvp', precio);
        params.append('imperioOrigen', imperioOrigen);
        if (fechaCreacion) params.append('fechaCreacion', fechaCreacion);
        params