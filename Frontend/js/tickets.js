/**
 * Lógica da página de Boletos
 */

let accounts = [];
let tickets = [];

document.addEventListener('DOMContentLoaded', async function() {
    auth.requireAuth();
    await loadAccounts();
    await loadTickets();
    
    const form = document.getElementById('ticketForm');
    form.addEventListener('submit', handleSubmit);
    
    const filterSelect = document.getElementById('filterAccount');
    filterSelect.addEventListener('change', filterTickets);
});

async function loadAccounts() {
    try {
        const [current, savings] = await Promise.all([
            currentAccountAPI.listAll().catch(() => []),
            savingsAccountAPI.listAll().catch(() => [])
        ]);
        
        accounts = [...current, ...savings];
        
        const select = document.getElementById('originAccountId');
        const filterSelect = document.getElementById('filterAccount');
        
        select.innerHTML = '<option value="">Selecione uma conta...</option>';
        filterSelect.innerHTML = '<option value="">Todas as contas</option>';
        
        accounts.forEach(account => {
            const option = document.createElement('option');
            option.value = account.id;
            option.textContent = `${account.id} - Saldo: R$ ${formatCurrency(account.balance)}`;
            select.appendChild(option);
            
            const filterOption = option.cloneNode(true);
            filterSelect.appendChild(filterOption);
        });
    } catch (error) {
        console.error('Erro ao carregar contas:', error);
    }
}

async function loadTickets() {
    const loading = document.getElementById('loading');
    const table = document.getElementById('ticketsTable');
    
    try {
        loading.style.display = 'block';
        table.style.display = 'none';
        
        // Carrega boletos de todas as contas
        const allTickets = [];
        for (const account of accounts) {
            try {
                const accountTickets = await ticketAPI.listByAccount(account.id);
                allTickets.push(...accountTickets);
            } catch (error) {
                // Ignora erros de contas sem boletos
            }
        }
        
        tickets = allTickets;
        renderTickets();
        
        loading.style.display = 'none';
        if (tickets.length > 0) {
            table.style.display = 'table';
        } else {
            loading.innerHTML = '<p>Nenhum boleto encontrado</p>';
        }
    } catch (error) {
        loading.innerHTML = `<p style="color: var(--danger-color);">Erro ao carregar boletos: ${error.message}</p>`;
        console.error('Erro ao carregar boletos:', error);
    }
}

function renderTickets(filteredTickets = null) {
    const tbody = document.getElementById('ticketsTableBody');
    tbody.innerHTML = '';

    const ticketsToRender = filteredTickets || tickets;

    if (ticketsToRender.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Nenhum boleto encontrado</td></tr>';
        return;
    }

    ticketsToRender.forEach(ticket => {
        const tr = document.createElement('tr');
        const isPaid = ticket.status === 'PAID' || ticket.paid;
        tr.innerHTML = `
            <td>${ticket.id || '-'}</td>
            <td>${ticket.originAccount?.id || '-'}</td>
            <td>R$ ${formatCurrency(ticket.value)}</td>
            <td>${formatDate(ticket.dueDate)}</td>
            <td><span class="badge badge-${isPaid ? 'success' : 'warning'}">${isPaid ? 'Pago' : 'Pendente'}</span></td>
            <td>
                ${!isPaid ? `
                    <button class="btn btn-success" onclick="payTicket('${ticket.id}')" style="padding: 6px 12px; font-size: 12px; margin-right: 4px;">Pagar</button>
                ` : ''}
                <button class="btn btn-secondary" onclick="downloadTicket('${ticket.id}')" style="padding: 6px 12px; font-size: 12px;">Download</button>
                <button class="btn btn-danger" onclick="deleteTicket('${ticket.id}')" style="padding: 6px 12px; font-size: 12px;">Excluir</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function filterTickets() {
    const accountId = document.getElementById('filterAccount').value;
    
    if (!accountId) {
        renderTickets();
        return;
    }
    
    const filtered = tickets.filter(t => t.originAccount?.id === accountId);
    renderTickets(filtered);
}

async function handleSubmit(e) {
    e.preventDefault();
    
    const messageDiv = document.getElementById('formMessage');
    messageDiv.style.display = 'none';
    
    const originAccountId = document.getElementById('originAccountId').value;
    const value = parseFloat(document.getElementById('ticketValue').value);
    const dueDate = document.getElementById('dueDate').value;
    
    if (!originAccountId || !value || value <= 0 || !dueDate) {
        showMessage('Preencha todos os campos corretamente', 'error');
        return;
    }
    
    const ticketData = {
        originAccount: {
            id: originAccountId
        },
        value: value,
        dueDate: dueDate
    };

    try {
        await ticketAPI.create(ticketData);
        showMessage('Boleto criado com sucesso!', 'success');
        
        document.getElementById('ticketForm').reset();
        await loadTickets();
    } catch (error) {
        showMessage(`Erro ao criar boleto: ${error.message}`, 'error');
    }
}

async function payTicket(ticketId) {
    const accountId = prompt('Digite o ID da conta que irá pagar o boleto:');
    if (!accountId) return;
    
    try {
        await ticketAPI.pay(ticketId, accountId);
        showMessage('Boleto pago com sucesso!', 'success');
        await loadTickets();
    } catch (error) {
        showMessage(`Erro ao pagar boleto: ${error.message}`, 'error');
    }
}

async function downloadTicket(ticketId) {
    try {
        await ticketAPI.download(ticketId);
        showMessage('Download iniciado!', 'success');
    } catch (error) {
        showMessage(`Erro ao baixar boleto: ${error.message}`, 'error');
    }
}

async function deleteTicket(ticketId) {
    if (!confirm('Tem certeza que deseja excluir este boleto?')) return;
    
    try {
        await ticketAPI.delete(ticketId);
        showMessage('Boleto excluído com sucesso!', 'success');
        await loadTickets();
    } catch (error) {
        showMessage(`Erro ao excluir boleto: ${error.message}`, 'error');
    }
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

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
}

