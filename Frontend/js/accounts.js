/**
 * Lógica da página de Contas
 */

let currentAccounts = [];
let savingsAccounts = [];
let clients = [];

document.addEventListener('DOMContentLoaded', async function() {
    auth.requireAuth();
    await loadClients();
    await loadAccounts();
    
    const form = document.getElementById('accountForm');
    form.addEventListener('submit', handleSubmit);
});

async function loadClients() {
    try {
        clients = await clientAPI.listAll();
        const select = document.getElementById('clientIdSelect');
        select.innerHTML = '<option value="">Selecione um cliente...</option>';
        clients.forEach(client => {
            const option = document.createElement('option');
            option.value = client.id;
            option.textContent = `${client.first_name} ${client.last_name} (${client.id})`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Erro ao carregar clientes:', error);
    }
}

async function loadAccounts() {
    try {
        [currentAccounts, savingsAccounts] = await Promise.all([
            currentAccountAPI.listAll().catch(() => []),
            savingsAccountAPI.listAll().catch(() => [])
        ]);
        
        renderCurrentAccounts();
        renderSavingsAccounts();
    } catch (error) {
        console.error('Erro ao carregar contas:', error);
    }
}

function renderCurrentAccounts() {
    const loading = document.getElementById('loadingCurrent');
    const table = document.getElementById('currentTable');
    const tbody = document.getElementById('currentTableBody');
    
    tbody.innerHTML = '';
    
    if (currentAccounts.length === 0) {
        loading.innerHTML = '<p>Nenhuma conta corrente cadastrada</p>';
        table.style.display = 'none';
        return;
    }
    
    loading.style.display = 'none';
    table.style.display = 'table';
    
    currentAccounts.forEach(account => {
        const tr = document.createElement('tr');
        const client = clients.find(c => c.id === account.client?.id) || {};
        tr.innerHTML = `
            <td>${account.id || '-'}</td>
            <td>${client.first_name || ''} ${client.last_name || '-'}</td>
            <td>R$ ${formatCurrency(account.balance)}</td>
            <td><span class="badge badge-${getStateBadge(account.state)}">${account.state || '-'}</span></td>
            <td>
                <button class="btn btn-secondary" onclick="editAccount('current', '${account.id}')" style="padding: 6px 12px; font-size: 12px;">Editar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function renderSavingsAccounts() {
    const loading = document.getElementById('loadingSavings');
    const table = document.getElementById('savingsTable');
    const tbody = document.getElementById('savingsTableBody');
    
    tbody.innerHTML = '';
    
    if (savingsAccounts.length === 0) {
        loading.innerHTML = '<p>Nenhuma conta poupança cadastrada</p>';
        table.style.display = 'none';
        return;
    }
    
    loading.style.display = 'none';
    table.style.display = 'table';
    
    savingsAccounts.forEach(account => {
        const tr = document.createElement('tr');
        const client = clients.find(c => c.id === account.client?.id) || {};
        tr.innerHTML = `
            <td>${account.id || '-'}</td>
            <td>${client.first_name || ''} ${client.last_name || '-'}</td>
            <td>R$ ${formatCurrency(account.balance)}</td>
            <td><span class="badge badge-${getStateBadge(account.state)}">${account.state || '-'}</span></td>
            <td>
                <button class="btn btn-secondary" onclick="editAccount('savings', '${account.id}')" style="padding: 6px 12px; font-size: 12px;">Editar</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function handleAccountTypeChange() {
    const type = document.getElementById('accountTypeSelect').value;
    document.getElementById('accountType').value = type;
}

async function handleSubmit(e) {
    e.preventDefault();
    
    const messageDiv = document.getElementById('formMessage');
    messageDiv.style.display = 'none';
    
    const accountType = document.getElementById('accountTypeSelect').value;
    const accountId = document.getElementById('accountId').value;
    
    if (!accountType) {
        showMessage('Selecione o tipo de conta', 'error');
        return;
    }
    
    const accountData = {
        client: {
            id: document.getElementById('clientIdSelect').value
        },
        balance: parseFloat(document.getElementById('balance').value),
        state: document.getElementById('state').value
    };

    try {
        if (accountId) {
            if (accountType === 'current') {
                await currentAccountAPI.update(accountId, accountData);
            } else {
                await savingsAccountAPI.update(accountId, accountData);
            }
            showMessage('Conta atualizada com sucesso!', 'success');
        } else {
            if (accountType === 'current') {
                await currentAccountAPI.create(accountData);
            } else {
                await savingsAccountAPI.create(accountData);
            }
            showMessage('Conta criada com sucesso!', 'success');
        }
        
        resetForm();
        await loadAccounts();
    } catch (error) {
        showMessage(`Erro: ${error.message}`, 'error');
    }
}

async function editAccount(type, id) {
    let account;
    if (type === 'current') {
        account = await currentAccountAPI.findById(id);
        document.getElementById('accountTypeSelect').value = 'current';
    } else {
        account = await savingsAccountAPI.findById(id);
        document.getElementById('accountTypeSelect').value = 'savings';
    }
    
    if (!account) return;

    document.getElementById('accountId').value = account.id;
    document.getElementById('accountType').value = type;
    document.getElementById('clientIdSelect').value = account.client?.id || '';
    document.getElementById('balance').value = account.balance || 0;
    document.getElementById('state').value = account.state || 'ACTIVE';
    document.getElementById('formTitle').textContent = 'Editar Conta';
    
    document.querySelector('.form-container').scrollIntoView({ behavior: 'smooth' });
}

function resetForm() {
    document.getElementById('accountForm').reset();
    document.getElementById('accountId').value = '';
    document.getElementById('accountType').value = '';
    document.getElementById('formTitle').textContent = 'Nova Conta';
    document.getElementById('formMessage').style.display = 'none';
}

function showMessage(message, type) {
    const messageDiv = document.getElementById('formMessage');
    messageDiv.textContent = message;
    messageDiv.className = type === 'success' ? 'success-message' : 'error-message';
    messageDiv.style.display = 'block';
    
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 5000);
}

function formatCurrency(value) {
    if (!value) return '0,00';
    return parseFloat(value).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function getStateBadge(state) {
    switch(state) {
        case 'ACTIVE': return 'success';
        case 'INACTIVE': return 'warning';
        case 'BLOCKED': return 'danger';
        default: return 'info';
    }
}

