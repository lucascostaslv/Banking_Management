/**
 * Lógica da página de Clientes
 */

let clients = [];

document.addEventListener('DOMContentLoaded', async function() {
    auth.requireAuth();
    await loadClients();
    
    const form = document.getElementById('clientForm');
    form.addEventListener('submit', handleSubmit);
});

async function loadClients() {
    const loading = document.getElementById('loading');
    const table = document.getElementById('clientsTable');
    
    try {
        loading.style.display = 'block';
        table.style.display = 'none';
        
        clients = await clientAPI.listAll();
        renderClients();
        
        loading.style.display = 'none';
        table.style.display = 'table';
    } catch (error) {
        loading.innerHTML = `<p style="color: var(--danger-color);">Erro ao carregar clientes: ${error.message}</p>`;
        console.error('Erro ao carregar clientes:', error);
    }
}

function renderClients() {
    const tbody = document.getElementById('clientsTableBody');
    tbody.innerHTML = '';

    if (clients.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Nenhum cliente cadastrado</td></tr>';
        return;
    }

    clients.forEach(client => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${client.id || '-'}</td>
            <td>${client.first_name || ''} ${client.last_name || ''}</td>
            <td>${client.cpf || '-'}</td>
            <td>${formatDate(client.birth_day) || '-'}</td>
            <td>${client.state || '-'}</td>
            <td>
                <button class="btn btn-secondary" onclick="editClient('${client.id}')" style="padding: 6px 12px; font-size: 12px;">Editar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function handleSubmit(e) {
    e.preventDefault();
    
    const messageDiv = document.getElementById('formMessage');
    messageDiv.style.display = 'none';
    
    const clientId = document.getElementById('clientId').value;
    const clientData = {
        first_name: document.getElementById('firstName').value.trim(),
        last_name: document.getElementById('lastName').value.trim(),
        cpf: document.getElementById('cpf').value.trim(),
        birth_day: document.getElementById('birthDay').value,
        state: document.getElementById('state').value,
        act: parseInt(document.getElementById('act').value),
        type: 2 // Tipo cliente
    };

    try {
        if (clientId) {
            await clientAPI.update(clientId, clientData);
            showMessage('Cliente atualizado com sucesso!', 'success');
        } else {
            await clientAPI.create(clientData);
            showMessage('Cliente criado com sucesso!', 'success');
        }
        
        resetForm();
        await loadClients();
    } catch (error) {
        showMessage(`Erro: ${error.message}`, 'error');
    }
}

function editClient(id) {
    const client = clients.find(c => c.id === id);
    if (!client) return;

    document.getElementById('clientId').value = client.id;
    document.getElementById('firstName').value = client.first_name || '';
    document.getElementById('lastName').value = client.last_name || '';
    document.getElementById('cpf').value = client.cpf || '';
    document.getElementById('birthDay').value = client.birth_day || '';
    document.getElementById('state').value = client.state || '';
    document.getElementById('act').value = client.act || 0;
    document.getElementById('formTitle').textContent = 'Editar Cliente';
    
    // Scroll para o formulário
    document.querySelector('.form-container').scrollIntoView({ behavior: 'smooth' });
}

function resetForm() {
    document.getElementById('clientForm').reset();
    document.getElementById('clientId').value = '';
    document.getElementById('formTitle').textContent = 'Novo Cliente';
    document.getElementById('formMessage').style.display = 'none';
}

function showMessage(message, type) {
    const messageDiv = document.getElementById('formMessage');
    messageDiv.textContent = message;
    messageDiv.className = type === 'success' ? 'success-message' : 'error-message';
    messageDiv.style.display = 'block';
    
    // Auto-hide após 5 segundos
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
}

