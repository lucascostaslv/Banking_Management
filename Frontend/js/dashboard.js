/**
 * Lógica do Dashboard
 */

document.addEventListener('DOMContentLoaded', async function() {
    // Verifica autenticação
    auth.requireAuth();

    // Exibe nome do usuário
    const session = auth.getSession();
    if (session) {
        document.getElementById('currentUser').textContent = session.name;
    }

    // Carrega estatísticas
    await loadStats();
});

async function loadStats() {
    try {
        // Carrega dados em paralelo
        const [clients, currentAccounts, savingsAccounts, tickets] = await Promise.all([
            clientAPI.listAll().catch(() => []),
            currentAccountAPI.listAll().catch(() => []),
            savingsAccountAPI.listAll().catch(() => []),
            ticketAPI.listByAccount ? [] : [] // Placeholder - precisa de accountId
        ]);

        // Atualiza estatísticas
        document.getElementById('totalClients').textContent = clients.length || 0;
        document.getElementById('totalAccounts').textContent = 
            (currentAccounts.length || 0) + (savingsAccounts.length || 0);
        document.getElementById('totalTransactions').textContent = '-'; // Precisa endpoint específico
        document.getElementById('totalTickets').textContent = '-'; // Precisa endpoint específico

    } catch (error) {
        console.error('Erro ao carregar estatísticas:', error);
    }
}

