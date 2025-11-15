/**
 * Sistema de Autenticação Simples
 * Usa localStorage para gerenciar sessão do usuário
 * Não afeta o backend - apenas controle de acesso no front-end
 */

const auth = {
    // Usuários pré-cadastrados (simulado - em produção viria do backend)
    users: {
        'admin': { password: '12345', role: 'admin', name: 'Administrador' },
        'manager': { password: '12345', role: 'manager', name: 'Gerente' },
        'client': { password: '12345', role: 'client', name: 'Cliente' }
    },

    /**
     * Realiza login do usuário
     * @param {string} username - Nome de usuário
     * @param {string} password - Senha
     * @returns {boolean} - true se login foi bem-sucedido
     */
    login(username, password) {
        const user = this.users[username];
        
        if (user && user.password === password) {
            const session = {
                username: username,
                role: user.role,
                name: user.name,
                loginTime: new Date().toISOString()
            };
            
            localStorage.setItem('banking_session', JSON.stringify(session));
            return true;
        }
        
        return false;
    },

    /**
     * Verifica se o usuário está autenticado
     * @returns {boolean}
     */
    isAuthenticated() {
        const session = localStorage.getItem('banking_session');
        if (!session) return false;
        
        try {
            const sessionData = JSON.parse(session);
            // Verifica se a sessão não expirou (24 horas)
            const loginTime = new Date(sessionData.loginTime);
            const now = new Date();
            const hoursDiff = (now - loginTime) / (1000 * 60 * 60);
            
            if (hoursDiff > 24) {
                this.logout();
                return false;
            }
            
            return true;
        } catch (e) {
            return false;
        }
    },

    /**
     * Obtém dados da sessão atual
     * @returns {object|null}
     */
    getSession() {
        if (!this.isAuthenticated()) return null;
        
        try {
            return JSON.parse(localStorage.getItem('banking_session'));
        } catch (e) {
            return null;
        }
    },

    /**
     * Realiza logout do usuário
     */
    logout() {
        localStorage.removeItem('banking_session');
        window.location.href = 'index.html';
    },

    /**
     * Verifica se o usuário tem uma role específica
     * @param {string} role - Role a verificar
     * @returns {boolean}
     */
    hasRole(role) {
        const session = this.getSession();
        return session && session.role === role;
    },

    /**
     * Redireciona para login se não autenticado
     */
    requireAuth() {
        if (!this.isAuthenticated()) {
            window.location.href = 'index.html';
        }
    }
};

