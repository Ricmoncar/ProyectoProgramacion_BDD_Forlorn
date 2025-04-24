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