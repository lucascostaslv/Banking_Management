/**
 * Lógica da página de login
 */

document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('errorMessage');

    // Se já estiver autenticado, redireciona para dashboard
    if (auth.isAuthenticated()) {
        window.location.href = 'dashboard.html';
    }

    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();

        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        // Limpa mensagem de erro anterior
        errorMessage.style.display = 'none';
        errorMessage.textContent = '';

        // Validação básica
        if (!username || !password) {
            showError('Por favor, preencha todos os campos.');
            return;
        }

        // Tenta fazer login
        if (auth.login(username, password)) {
            // Login bem-sucedido - redireciona para dashboard
            window.location.href = 'dashboard.html';
        } else {
            showError('Usuário ou senha incorretos.');
        }
    });

    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.style.display = 'block';
    }
});

