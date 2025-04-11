document.addEventListener('DOMContentLoaded', function() {
    // Inicializar DataTables
    let planetsTable = $('#planetsTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
        },
        responsive: true,
        columns: [
            { data: 'id' },
            { data: 'nombre' },
            { data: 'ubicacion' },
            { data: 'habitable', 
              render: function(data) {
                  return data == 1 ? 'Sí' : 'No';
              }
            },
            { data: 'nivelAgua' },
            { data: 'tamanio' },
            { 
                data: null,
                orderable: false,
                render: function(data) {
                    return `<div class="planet-actions">
                        <button class="view-btn" onclick="verPlaneta(${data.id})"><i class="fas fa-eye"></i></button>
                        <button class="edit-btn" onclick="editarPlaneta(${data.id})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" onclick="eliminarPlaneta(${data.id})"><i class="fas fa-trash-alt"></i></button>
                    </div>`;
                }
            }
        ]
    });
    
    // Cargar planetas al inicio
    cargarPlanetas();
    
    // Event listeners para modales
    document.getElementById('addPlanetBtn').addEventListener('click', abrirModalAnadir);
    document.getElementById('cancelBtn').addEventListener('click', cerrarModal);
    document.getElementById('closeViewBtn').addEventListener('click', cerrarModalDetalles);
    
    // Event listeners para cerrar modales con el botón X
    let closeButtons = document.querySelectorAll('.close-modal');
    closeButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            document.getElementById('planetModal').style.display = 'none';
            document.getElementById('viewPlanetModal').style.display = 'none';
        });
    });
    
    // Event listener para formulario
    document.getElementById('planetForm').addEventListener('submit', function(e) {
        e.preventDefault();
        guardarPlaneta();
    });
});

function cargarPlanetas() {
    fetch("http://localhost:8080/listar_planetas")
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP: ${res.status}`);
            }
            return res.json();
        })
        .then(planetas => {
            // Limpiar y recargar la tabla
            let table = $('#planetsTable').DataTable();
            table.clear();
            table.rows.add(planetas).draw();
        })
        .catch(error => {
            console.error('Error al cargar los planetas:', error);
            alert('Error al cargar los planetas. Por favor, intente de nuevo más tarde.');
        });
}

function abrirModalAnadir() {
    // Resetear el formulario
    document.getElementById('modalTitle').textContent = 'Añadir Nuevo Planeta';
    document.getElementById('planetForm').reset();
    document.getElementById('planetId').value = '';
    
    // Mostrar el modal
    document.getElementById('planetModal').style.display = 'block';
}

function cerrarModal() {
    document.getElementById('planetModal').style.display = 'none';
}

function cerrarModalDetalles() {
    document.getElementById('viewPlanetModal').style.display = 'none';
}

function editarPlaneta(id) {
    // Buscar el planeta en la tabla
    let table = $('#planetsTable').DataTable();
    let planetaData = table.rows().data().toArray().find(planeta => planeta.id == id);
    
    if (planetaData) {
        // Actualizar título modal
        document.getElementById('modalTitle').textContent = 'Editar Planeta';
        
        // Llenar el formulario con los datos del planeta
        document.getElementById('planetId').value = planetaData.id;
        document.getElementById('planetName').value = planetaData.nombre;
        document.getElementById('planetLocation').value = planetaData.ubicacion || '';
        document.getElementById('planetHabitable').value = planetaData.habitable;
        document.getElementById('planetWaterLevel').value = planetaData.nivelAgua || '';
        
        // Si hay fecha de creación, formatearla correctamente para el input date
        if (planetaData.fechaCreacion) {
            let fecha = new Date(planetaData.fechaCreacion);
            let fechaFormateada = fecha.toISOString().split('T')[0];
            document.getElementById('planetCreationDate').value = fechaFormateada;
        } else {
            document.getElementById('planetCreationDate').value = '';
        }
        
        document.getElementById('planetSize').value = planetaData.tamanio || '';
        document.getElementById('planetDensity').value = planetaData.densidad || '';
        document.getElementById('planetDescription').value = planetaData.descripcion || '';
        
        // Mostrar el modal
        document.getElementById('planetModal').style.display = 'block';
    } else {
        console.error('No se encontró el planeta con ID:', id);
        alert('Error al cargar los datos del planeta.');
    }
}

function guardarPlaneta() {
    const formData = new FormData();
    formData.append('nombre', document.getElementById('planetName').value);
    formData.append('ubicacion', document.getElementById('planetLocation').value);
    formData.append('habitable', document.getElementById('planetHabitable').value);
    formData.append('nivelAgua', document.getElementById('planetWaterLevel').value);
    formData.append('fechaCreacion', document.getElementById('planetCreationDate').value);
    formData.append('tamanio', document.getElementById('planetSize').value);
    formData.append('densidad', document.getElementById('planetDensity').value);
    formData.append('descripcion', document.getElementById('planetDescription').value);
    
    const planetId = document.getElementById('planetId').value;
    if (planetId) {
        formData.append('id', planetId);
    }
    
    // Construir la URL y opciones
    const url = planetId ? 'http://localhost:8080/actualizar_planeta' : 'http://localhost:8080/aniadir_planeta';
    
    // Convertir FormData a URLSearchParams para enviar como query parameters
    const params = new URLSearchParams();
    for (const pair of formData) {
        params.append(pair[0], pair[1]);
    }
    
    // Hacer la petición
    fetch(`${url}?${params.toString()}`)
        .then(res => {
            if (!res.ok) {
                throw new Error(`Error HTTP: ${res.status}`);
            }
            return res.text();
        })
        .then(() => {
            cerrarModal();
            cargarPlanetas();
            alert(planetId ? 'Planeta actualizado con éxito.' : 'Planeta añadido con éxito.');
        })
        .catch(error => {
            console.error('Error al guardar el planeta:', error);
            alert('Error al guardar el planeta: ' + error.message);
        });
}

function eliminarPlaneta(id) {
    if (confirm('¿Está seguro de que desea eliminar este planeta? Esta acción no se puede deshacer.')) {
        fetch(`http://localhost:8080/eliminar_planeta?id=${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`Error HTTP: ${res.status}`);
                }
                return res.text();
            })
            .then(() => {
                cargarPlanetas();
                alert('Planeta eliminado con éxito.');
            })
            .catch(error => {
                console.error('Error al eliminar el planeta:', error);
                alert('Error al eliminar el planeta. Por favor, intente de nuevo.');
            });
    }
}

function verPlaneta(id) {
    // Buscar el planeta en la tabla
    let table = $('#planetsTable').DataTable();
    let planetaData = table.rows().data().toArray().find(planeta => planeta.id == id);
    
    if (planetaData) {
        // Actualizar título del modal
        document.getElementById('viewPlanetName').textContent = planetaData.nombre;
        
        // Crear contenido HTML para los detalles
        let fechaFormateada = '';
        if (planetaData.fechaCreacion) {
            let fecha = new Date(planetaData.fechaCreacion);
            fechaFormateada = fecha.toLocaleDateString('es-ES');
        }
        
        let detallesHTML = `
            <div class="planet-detail-grid">
                <div class="detail-item">
                    <strong>ID:</strong> ${planetaData.id}
                </div>
                <div class="detail-item">
                    <strong>Ubicación:</strong> ${planetaData.ubicacion || 'No especificada'}
                </div>
                <div class="detail-item">
                    <strong>Habitable:</strong> ${planetaData.habitable == 1 ? 'Sí' : 'No'}
                </div>
                <div class="detail-item">
                    <strong>Nivel de Agua:</strong> ${planetaData.nivelAgua || 'No especificado'} %
                </div>
                <div class="detail-item">
                    <strong>Fecha de Creación:</strong> ${fechaFormateada || 'No especificada'}
                </div>
                <div class="detail-item">
                    <strong>Tamaño:</strong> ${planetaData.tamanio || 'No especificado'} km
                </div>
                <div class="detail-item">
                    <strong>Densidad:</strong> ${planetaData.densidad || 'No especificada'} g/cm³
                </div>
            </div>
            
            <div class="detail-description">
                <h4>Descripción:</h4>
                <p>${planetaData.descripcion || 'No hay descripción disponible.'}</p>
            </div>
        `;
        
        document.getElementById('planetDetails').innerHTML = detallesHTML;
        
        document.getElementById('viewPlanetModal').style.display = 'block';
    } else {
        console.error('No se encontró el planeta con ID:', id);
        alert('Error al cargar los detalles del planeta.');
    }
}

