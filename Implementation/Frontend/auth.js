const API_BASE_URL = 'http://localhost:8080';

// Verificar se usuário está autenticado
function checkAuth() {
    const user = localStorage.getItem('currentUser');
    if (!user) {
        if (window.location.pathname.includes('dashboard')) {
            window.location.href = 'index.html';
        }
        return null;
    }
    return JSON.parse(user);
}

// Login simples por CPF
async function login(cpf, userType) {
    try {
        let user = null;
        
        if (userType === 'client') {
            const response = await fetch(`${API_BASE_URL}/usr_client`);
            if (!response.ok) {
                if (response.status === 0 || response.status === 404) {
                    throw new Error('Backend não está rodando ou URL incorreta. Verifique se o servidor está em http://localhost:8080');
                }
                throw new Error('Erro ao buscar clientes: ' + response.status);
            }
            const clients = await response.json();
            user = clients.find(c => c.cpf === cpf);
        } else if (userType === 'manager') {
            const response = await fetch(`${API_BASE_URL}/usr_manager`);
            if (!response.ok) {
                if (response.status === 0 || response.status === 404) {
                    throw new Error('Backend não está rodando ou URL incorreta. Verifique se o servidor está em http://localhost:8080');
                }
                throw new Error('Erro ao buscar gerentes: ' + response.status);
            }
            const managers = await response.json();
            user = managers.find(m => m.cpf === cpf);
        }
        
        if (user) {
            user.userType = userType;
            localStorage.setItem('currentUser', JSON.stringify(user));
            return user;
        } else {
            throw new Error('Usuário não encontrado. Verifique o CPF ou cadastre-se.');
        }
    } catch (error) {
        // Tratamento específico para erros de rede/CORS
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError') || error.name === 'TypeError') {
            throw new Error('Erro de conexão. Verifique:\n1. Se o backend está rodando em http://localhost:8080\n2. Se há problemas de CORS (configure no backend)\n3. Se está usando um servidor local para o frontend');
        }
        throw error;
    }
}

// Logout
function logout() {
    localStorage.removeItem('currentUser');
    window.location.href = 'index.html';
}

// Obter usuário atual
function getCurrentUser() {
    const user = localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
}

