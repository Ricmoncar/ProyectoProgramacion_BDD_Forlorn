document.addEventListener('DOMContentLoaded', function() {
    // Inicializar DataTables
    let continentsTable = $('#continentsTable').DataTable({
        language: {
            url: 'https://cdn.datatables.net/plug-ins/1.10.25/i18n/Spanish.json'
        },
        responsive: true,
        columns: [
            { data: 'id' },
            { data: 'nombre' },
            { data: 'planetaNombre' },
            { data: 'hemisferio' },
            { data: 'clima' },
            { data: 'tamanio' },
            { data: 'habitable', 
              render: function(data) {
                  return data == 1 ? 'Sí' : 'No';
              }
            },
            { 
                data: null,
                orderable: false,
                render: function(data) {
                    return `<div class="continent-actions">
                        <button class="view-btn" onclick="verContinente(${data.id})"><i class="fas fa-eye"></i></button>
                        <button class="edit-btn" onclick="editarContinente(${data.id})"><i class="fas fa-edit"></i></button>
                        <button class="delete-btn" onclick="eliminarContinente(${data.id})"><i class="fas fa-trash-alt"></i></button>
                    </div>`;
                }
            }
        ]
    });
    
    // Cargar continentes y planetas al inicio
    cargarPlanetas();
    cargarContinentes();
    
    // Event listeners para modales
    document.getElementById('addContinentBtn').addEventListener('click', abrirModalAnadir);
    document.getElementById('cancelBtn').addEventListener('click', cerrarModal);
    document.getElementById('closeViewBtn').addEventListener('click', cerrarModalDetalles);
    
    // Event listeners para los filtros
    document.getElementById('applyFiltersBtn').addEventListener('click', aplicarFiltros);
    document.getElementById('resetFiltersBtn').addEventListener('click', resetearFiltros);
    
    // Event listeners para cerrar modales con el botón X
    let closeButtons = document.querySelectorAll('.close-modal');
    closeButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            document.getElementById('continentModal').style.display = 'none';
            document.getElementById('viewContinentModal').style.display = 'none';
        });
    });
    
    // Event listener para formulario
    document.getElementById('continentForm').addEventListener('submit', function(e) {
        e.preventDefault();
        guardarContinente();
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
            // Llenar los selects de planetas
            let selectPlaneta = document.getElementById('continentPlanet');
            let filterPlaneta = document.getElementById('filterPlaneta');
            
            // Limpiar opciones existentes
            selectPlaneta.innerHTML = '<option value="">Seleccione un planeta</option>';
            filterPlaneta.innerHTML = '<option value="">Seleccione un planeta</option>';

            planetas.forEach(planeta => {
                let option = document.createElement('option');
                option.value = planeta.id;
                option.textContent = planeta.nombre;
                selectPlaneta.appendChild(option);

                let filterOption = document.createElement('option');
                filterOption.value = planeta.id;
                filterOption.textContent = planeta.nombre;
                filterPlaneta.appendChild(filterOption);
            });
        })
        .catch(error => {
            console.error('Error al cargar planetas:', error);
        });
}
