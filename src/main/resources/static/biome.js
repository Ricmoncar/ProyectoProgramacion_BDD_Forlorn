document.addEventListener('DOMContentLoaded', function() {
    /* Inicialización de DataTables */
    let biomesTable = $('#biomesTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
        },
        responsive: true,
        columns: [
            { data: 'id' },
            { data: 'nombre' },
            { data: 'continenteNombre' },
            { data: 'clima' },
            { 
                data: 'porcentajeHumedad', 
                render: function(data) {
                    return data !== null ? `${data}%` : 'N/A';
                }
            },
            { data: 'precipitaciones' },
            { 
                data: 'temperaturaMedia', 
                render: function(data) {
                    return data !== null ? `${data}°C` : 'N/A';
                }
            },
            {
                data: null,
                orderable: false,
                searchable: false,
                render: function(data, type, row) {
                    const biomaId = row.id;
                    if (biomaId === undefined || biomaId === null) {
                        console.error("ID de bioma no encontrado en los datos de la fila:", row);
                        return 'Error ID';
                    }
                    return `<div class="biome-actions">
                        <button class="view-btn" title="Ver detalles" onclick="verBioma(${biomaId})"><i class="fas fa-eye"></i></button>
                        <button class="edit-btn" title="Editar" onclick="editarBioma(${biomaId})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" title="Eliminar" onclick="eliminarBioma(${biomaId})"><i class="fas fa-trash-alt"></i></button>
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
        // Solución al problema de visualización con colores de fondo
        createdRow: function(row, data, dataIndex) {
            // Garantiza que todas las filas tengan el fondo correcto
            $(row).css('background-color', dataIndex % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
        }
    });

    /* Carga inicial de datos */
    cargarContinentes();
    cargarBiomas();

    /* Configuración de eventos para modales */
    const addBtn = document.getElementById('addBiomeBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const closeViewBtn = document.getElementById('closeViewBtn');

    if (addBtn) addBtn.addEventListener('click', abrirModalAnadir);
    if (cancelBtn) cancelBtn.addEventListener('click', cerrarModal);
    if (closeViewBtn) closeViewBtn.addEventListener('click', cerrarModalDetalles);

    /* Configuración de eventos para filtros */
    const applyFiltersBtn = document.getElementById('applyFiltersBtn');
    const resetFiltersBtn = document.getElementById('resetFiltersBtn');
    const humedadSlider = document.getElementById('filterHumedad');
    const humedadValue = document.getElementById('humedadValue');

    if (applyFiltersBtn) applyFiltersBtn.addEventListener('click', aplicarFiltros);
    if (resetFiltersBtn) resetFiltersBtn.addEventListener('click', resetearFiltros);
    
    /* Configuración del slider de humedad */
    if (humedadSlider && humedadValue) {
        humedadSlider.addEventListener('input', function() {
            humedadValue.textContent = `${this.value}%`;
        });
    }

    /* Configuración de eventos para cerrar modales */
    let closeButtons = document.querySelectorAll('.close-modal');
    closeButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            const biomeModal = document.getElementById('biomeModal');
            const viewBiomeModal = document.getElementById('viewBiomeModal');
            if (biomeModal) biomeModal.style.display = 'none';
            if (viewBiomeModal) viewBiomeModal.style.display = 'none';
        });
    });

    /* Configuración del formulario */
    const biomeForm = document.getElementById('biomeForm');
    if (biomeForm) {
        biomeForm.addEventListener('submit', function(e) {
            e.preventDefault();
            guardarBioma();
        });
    }

    /* Cerrar modal al hacer clic fuera del contenido */
    window.addEventListener('click', function(event) {
        const biomeModal = document.getElementById('biomeModal');
        const viewBiomeModal = document.getElementById('viewBiomeModal');
        if (event.target == biomeModal) {
            cerrarModal();
        }
        if (event.target == viewBiomeModal) {
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
 * Carga la lista de continentes desde el servidor para los select
 */
function cargarContinentes() {
    fetch("http://localhost:8080/listar_continentes")
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP ${res.status}: ${res.statusText}`);
            }
            return res.json();
        })
        .then(continentes => {
            const selectContinente = document.getElementById('biomeContinent');
            const filterContinente = document.getElementById('filterContinente');

            if (!selectContinente || !filterContinente) {
                console.error("Elementos select de continente no encontrados en el DOM.");
                return;
            }

            // Guardar valores seleccionados actualmente
            const currentFilterValue = filterContinente.value;
            const currentFormValue = selectContinente.value;

            // Limpiar y rellenar las opciones
            selectContinente.innerHTML = '<option value="">Seleccione un continente</option>';
            filterContinente.innerHTML = '<option value="">Todos</option>';

            continentes.forEach(continente => {
                if(continente && continente.id !== undefined && continente.nombre) {
                    let option = document.createElement('option');
                    option.value = continente.id;
                    option.textContent = continente.nombre;
                    selectContinente.appendChild(option.cloneNode(true));
                    filterContinente.appendChild(option);
                } else {
                    console.warn("Continente con datos inválidos recibido:", continente);
                }
            });

            // Restaurar selección previa si es posible
            if (currentFormValue) selectContinente.value = currentFormValue;
            if (currentFilterValue) filterContinente.value = currentFilterValue;
        })
        .catch(error => {
            console.error('Error al cargar continentes:', error);
            mostrarAlerta('error', `No se pudieron cargar los continentes: ${error.message}`);
        });
}

/**
 * Carga la lista de biomas desde el servidor
 */
function cargarBiomas() {
    fetch("http://localhost:8080/listar_biomas")
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
        .then(biomas => {
            let table = $('#biomesTable').DataTable();
            table.clear();
            if (Array.isArray(biomas)) {
                table.rows.add(biomas).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#biomesTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
            } else {
                console.error("La respuesta de listar_biomas no es un array:", biomas);
                mostrarAlerta('error', 'Error: Formato de datos de biomas inesperado.');
            }
        })
        .catch(error => {
            console.error('Error al cargar biomas:', error);
            mostrarAlerta('error', `No se pudieron cargar los biomas: ${error.message}`);
        });
}

/**
 * Muestra los detalles de un bioma específico
 */
function verBioma(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para verBioma");
        mostrarAlerta('error', 'No se puede mostrar el bioma: ID inválido.');
        return;
    }
    fetch(`http://localhost:8080/obtener_bioma?id=${id}`)
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
        .then(bioma => {
            if (!bioma) {
                throw new Error("No se recibió información del bioma.");
            }
            
            const viewTitle = document.getElementById('viewBiomeTitle');
            const detailsContainer = document.getElementById('biomeDetails');
            const viewModal = document.getElementById('viewBiomeModal');

            if (!viewTitle || !detailsContainer || !viewModal) {
                console.error("Elementos del modal de vista no encontrados.");
                return;
            }

            viewTitle.textContent = bioma.nombre || 'Detalles del Bioma';

            const porcentajeHumedadTexto = bioma.porcentajeHumedad !== null ? `${bioma.porcentajeHumedad}%` : 'No especificado';
            const temperaturaMediaTexto = bioma.temperaturaMedia !== null ? `${bioma.temperaturaMedia}°C` : 'No especificada';

            const detallesHTML = `
                <div class="biome-details">
                    <div class="detail-row">
                        <span class="detail-label">Continente:</span>
                        <span class="detail-value">${bioma.continenteNombre || 'N/A'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Clima:</span>
                        <span class="detail-value">${bioma.clima || 'N/A'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Porcentaje de Humedad:</span>
                        <span class="detail-value">${porcentajeHumedadTexto}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Precipitaciones:</span>
                        <span class="detail-value">${bioma.precipitaciones || 'N/A'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Temperatura Media:</span>
                        <span class="detail-value">${temperaturaMediaTexto}</span>
                    </div>
                </div>
            `;

            detailsContainer.innerHTML = detallesHTML;
            viewModal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al obtener el bioma:', error);
            mostrarAlerta('error', `Error al obtener el bioma: ${error.message}`);
        });
}

/**
 * Abre el modal de edición con los datos del bioma seleccionado
 */
function editarBioma(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para editarBioma");
        mostrarAlerta('error', 'No se puede editar el bioma: ID inválido.');
        return;
    }
    
    const form = document.getElementById('biomeForm');
    const modalTitle = document.getElementById('biomeModalTitle');
    const biomeIdInput = document.getElementById('biomeId');
    const modal = document.getElementById('biomeModal');

    if (!form || !modalTitle || !biomeIdInput || !modal) {
        console.error("Elementos del modal de edición no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Editar Bioma';
    biomeIdInput.value = id;

    fetch(`http://localhost:8080/obtener_bioma?id=${id}`)
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
        .then(bioma => {
            if (!bioma) {
                throw new Error("No se recibió información del bioma para editar.");
            }

            document.getElementById('biomeName').value = bioma.nombre || '';
            document.getElementById('biomeContinent').value = bioma.continenteId || '';
            document.getElementById('biomeClimate').value = bioma.clima || 'Templado';
            
            const humedadInput = document.getElementById('biomeHumidity');
            if (humedadInput) {
                humedadInput.value = bioma.porcentajeHumedad !== null ? bioma.porcentajeHumedad : '';
            }
            
            const precipitacionesSelect = document.getElementById('biomePrecipitation');
            if (precipitacionesSelect) {
                precipitacionesSelect.value = bioma.precipitaciones || 'Moderadas';
            }
            
            const tempInput = document.getElementById('biomeTemperature');
            if (tempInput) {
                tempInput.value = bioma.temperaturaMedia !== null ? bioma.temperaturaMedia : '';
            }

            modal.style.display = 'block';
        })
        .catch(error => {
            console.error('Error al cargar datos del bioma para editar:', error);
            mostrarAlerta('error', `Error al cargar datos del bioma: ${error.message}`);
            cerrarModal();
        });
}

/**
 * Elimina un bioma tras confirmación del usuario
 */
function eliminarBioma(id) {
    if (id === undefined || id === null) {
        console.error("ID inválido para eliminarBioma");
        mostrarAlerta('error', 'No se puede eliminar el bioma: ID inválido.');
        return;
    }
    
    if (confirm(`¿Está seguro que desea eliminar el bioma con ID ${id}? Esta acción no se puede deshacer.`)) {
        fetch(`http://localhost:8080/eliminar_bioma?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => {
                        throw new Error(`Error HTTP ${res.status}: ${res.statusText}. Detalle: ${text}`);
                    });
                }
                return res.text();
            })
            .then(resultado => {
                mostrarAlerta('success', `Bioma ${id} eliminado correctamente. ${resultado}`);
                cargarBiomas();
            })
            .catch(error => {
                console.error('Error al eliminar bioma:', error);
                mostrarAlerta('error', `Error al eliminar bioma: ${error.message}`);
            });
    }
}

/**
 * Abre el modal para añadir un nuevo bioma
 */
function abrirModalAnadir() {
    const form = document.getElementById('biomeForm');
    const modalTitle = document.getElementById('biomeModalTitle');
    const biomeIdInput = document.getElementById('biomeId');
    const modal = document.getElementById('biomeModal');

    if (!form || !modalTitle || !biomeIdInput || !modal) {
        console.error("Elementos del modal de añadir no encontrados.");
        return;
    }

    form.reset();
    modalTitle.textContent = 'Añadir Nuevo Bioma';
    biomeIdInput.value = '';
    modal.style.display = 'block';
}

/**
 * Cierra el modal de añadir/editar bioma
 */
function cerrarModal() {
    const modal = document.getElementById('biomeModal');
    if (modal) {
        modal.style.display = 'none';
    }
}

/**
 * Cierra el modal de detalles del bioma
 */
function cerrarModalDetalles() {
    const modal = document.getElementById('viewBiomeModal');
    if (modal) {
        modal.style.display = 'none';
        const detailsContainer = document.getElementById('biomeDetails');
        if (detailsContainer) detailsContainer.innerHTML = '';
    }
}

/**
 * Guarda un nuevo bioma o actualiza uno existente
 */
function guardarBioma() {
    const id = document.getElementById('biomeId').value;
    const nombre = document.getElementById('biomeName').value.trim();
    const continenteId = document.getElementById('biomeContinent').value;
    const clima = document.getElementById('biomeClimate').value;
    const humedadInput = document.getElementById('biomeHumidity');
    const humedad = humedadInput.value ? parseFloat(humedadInput.value) : null;
    const precipitaciones = document.getElementById('biomePrecipitation').value;
    const temperaturaInput = document.getElementById('biomeTemperature');
    const temperatura = temperaturaInput.value ? parseFloat(temperaturaInput.value) : null;

    // Validación básica
    if (!nombre) {
        mostrarAlerta('error', 'El nombre del bioma es obligatorio.');
        return;
    }

    if (!continenteId) {
        mostrarAlerta('error', 'Debe seleccionar un continente.');
        return;
    }

    // Construir la URL con los parámetros
    let url = id ? 'http://localhost:8080/actualizar_bioma?' : 'http://localhost:8080/aniadir_bioma?';
    
    const params = new URLSearchParams();
    if (id) params.append('id', id);
    params.append('nombre', nombre);
    params.append('continenteId', continenteId);
    params.append('clima', clima);
    if (humedad !== null) params.append('porcentajeHumedad', humedad);
    params.append('precipitaciones', precipitaciones);
    if (temperatura !== null) params.append('temperaturaMedia', temperatura);
    
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
            mostrarAlerta('success', id ? 'Bioma actualizado correctamente.' : 'Bioma añadido correctamente.');
            cerrarModal();
            cargarBiomas();
        })
        .catch(error => {
            console.error('Error al guardar bioma:', error);
            mostrarAlerta('error', `Error al guardar bioma: ${error.message}`);
        });
}

/**
 * Aplica filtros seleccionados para filtrar biomas
 */
function aplicarFiltros() {
    const continenteId = document.getElementById('filterContinente').value;
    const clima = document.getElementById('filterClima').value;
    const humedadMinima = document.getElementById('filterHumedad').value;
    const temperaturaSelect = document.getElementById('filterTemperatura').value;
    
    // Construir la URL con los parámetros
    let url = 'http://localhost:8080/filtrar_biomas?';
    const params = new URLSearchParams();
    
    if (continenteId) params.append('continenteId', continenteId);
    if (clima) params.append('clima', clima);
    if (humedadMinima && parseInt(humedadMinima) > 0) params.append('humedadMinima', humedadMinima);
    if (temperaturaSelect) {
        // Convertir la selección de temperatura a un rango numérico
        switch(temperaturaSelect) {
            case 'baja':
                params.append('tempMin', '-100'); // Valor muy bajo para incluir cualquier temperatura baja
                params.append('tempMax', '10');
                break;
            case 'media':
                params.append('tempMin', '10');
                params.append('tempMax', '25');
                break;
            case 'alta':
                params.append('tempMin', '25');
                params.append('tempMax', '100'); // Valor muy alto para incluir cualquier temperatura alta
                break;
        }
    }
    
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
        .then(biomas => {
            let table = $('#biomesTable').DataTable();
            table.clear();
            
            if (Array.isArray(biomas)) {
                table.rows.add(biomas).draw();
                
                // Corrige el problema de visualización repintando las filas
                $('#biomesTable tbody tr').each(function(index) {
                    $(this).css('background-color', index % 2 === 0 ? 'rgba(30, 30, 30, 0.8)' : 'var(--dark-secondary)');
                });
                
                if (filterResults) {
                    filterResults.textContent = `Se encontraron ${biomas.length} biomas con los filtros seleccionados.`;
                }
            } else {
                console.error("La respuesta de filtrar_biomas no es un array:", biomas);
                mostrarAlerta('error', 'Error: Formato de datos de biomas inesperado.');
                if (filterResults) filterResults.textContent = '';
            }
        })
        .catch(error => {
            console.error('Error al filtrar biomas:', error);
            mostrarAlerta('error', `Error al filtrar biomas: ${error.message}`);
            if (filterResults) filterResults.textContent = '';
        });
}

/**
 * Resetea todos los filtros y carga todos los biomas
 */
function resetearFiltros() {
    const filterContinente = document.getElementById('filterContinente');
    const filterClima = document.getElementById('filterClima');
    const filterHumedad = document.getElementById('filterHumedad');
    const filterTemperatura = document.getElementById('filterTemperatura');
    const humedadValue = document.getElementById('humedadValue');
    
    if (filterContinente) filterContinente.value = '';
    if (filterClima) filterClima.value = '';
    if (filterHumedad) {
        filterHumedad.value = 0;
        if (humedadValue) humedadValue.textContent = '0%';
    }
    if (filterTemperatura) filterTemperatura.value = '';
    
    const filterResults = document.getElementById('filterResults');
    if (filterResults) filterResults.textContent = '';
    
    cargarBiomas();
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