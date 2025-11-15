/**
 * Lógica da página de Transações PIX
 */

let accounts = [];

document.addEventListener('DOMContentLoaded', async function() {
    auth.requireAuth();
    await loadAccounts();
    
    const form = document.getElementById('pixForm');
    form.addEventListener('submit', handleSubmit);
});

async function loadAccounts() {
    try {
        const [current, savings] = await Promise.all([
            currentAccountAPI.listAll().catch(() => []),
            savingsAccountAPI.listAll().catch(() => [])
        ]);
        
        accounts = [...current, ...savings];
        
        const select = document.getElementById('originAccountId');
        select.innerHTML = '<option value="">Selecione uma conta...</option>';
        accounts.forEach(account => {
            const option = document.createElement('option');
            option.value = account.id;
            option.textContent = `${account.id} - Saldo: R$ ${formatCurrency(account.balance)}`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error('Erro ao carregar contas:', error);
    }
}

async function handleSubmit(e) {
    e.preventDefault();
    
    const messageDiv = document.getElementById('formMessage');
    messageDiv.style.display = 'none';
    
    const originAccountId = document.getElementById('originAccountId').value;
    const pixKey = document.getElementById('pixKey').value.trim();
    const value = parseFloat(document.getElementById('pixValue').value);
    
    if (!originAccountId || !pixKey || !value || value <= 0) {
        showMessage('Preencha todos os campos corretamente', 'error');
        return;
    }
    
    // Nota: Para fazer PIX, precisamos encontrar a conta destino pela chave PIX
    // Como não temos endpoint para buscar conta por chave PIX, vamos tentar diretamente
    // O backend deve lidar com isso
    
    const pixData = {
        originAccount: {
            id: originAccountId
        },
        pixKey: pixKey,
        value: value
    };

    try {
        const result = await pixAPI.create(pixData);
        showMessage('PIX realizado com sucesso!', 'success');
        
        // Limpa formulário
        document.getElementById('pixForm').reset();
        
        // Recarrega contas para atualizar saldos
        await loadAccounts();
    } catch (error) {
        showMessage(`Erro ao realizar PIX: ${error.message}`, 'error');
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

