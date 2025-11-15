/**
 * Módulo de comunicação com a API do Backend
 */

const API_BASE_URL = 'http://localhost:8080';

/**
 * Função genérica para fazer requisições à API
 * @param {string} endpoint - Endpoint da API
 * @param {object} options - Opções da requisição (method, body, etc)
 * @returns {Promise} - Resposta da API
 */
async function fetchAPI(endpoint, options = {}) {
    try {
        const config = {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        };

        if (options.body && typeof options.body === 'object') {
            config.body = JSON.stringify(options.body);
        }

        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        // Se a resposta não for OK, tenta parsear o erro
        if (!response.ok) {
            let errorMessage = 'Erro na requisição';
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || errorMessage;
            } catch (e) {
                errorMessage = `Erro ${response.status}: ${response.statusText}`;
            }
            throw new Error(errorMessage);
        }

        // Se a resposta for vazia (204 No Content), retorna null
        if (response.status === 204) {
            return null;
        }

        // Tenta parsear como JSON
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }

        // Se for PDF ou outro tipo, retorna a resposta
        return response;
    } catch (error) {
        console.error('Erro na API:', error);
        throw error;
    }
}

/**
 * API de Clientes
 */
const clientAPI = {
    listAll() {
        return fetchAPI('/usr_client');
    },

    findById(id) {
        return fetchAPI(`/usr_client/${id}`);
    },

    create(clientData) {
        return fetchAPI('/usr_client', {
            method: 'POST',
            body: clientData
        });
    },

    update(id, clientData) {
        return fetchAPI(`/usr_client/${id}`, {
            method: 'PUT',
            body: clientData
        });
    }
};

/**
 * API de Gerentes
 */
const managerAPI = {
    listAll() {
        return fetchAPI('/usr_manager');
    },

    findById(id) {
        return fetchAPI(`/usr_manager/${id}`);
    },

    create(managerData) {
        return fetchAPI('/usr_manager', {
            method: 'POST',
            body: managerData
        });
    },

    delete(id) {
        return fetchAPI(`/usr_manager/${id}`, {
            method: 'DELETE'
        });
    }
};

/**
 * API de Contas Correntes
 */
const currentAccountAPI = {
    listAll() {
        return fetchAPI('/acc_current');
    },

    findById(id) {
        return fetchAPI(`/acc_current/${id}`);
    },

    create(accountData) {
        return fetchAPI('/acc_current', {
            method: 'POST',
            body: accountData
        });
    },

    update(id, accountData) {
        return fetchAPI(`/acc_current/${id}`, {
            method: 'PUT',
            body: accountData
        });
    }
};

/**
 * API de Contas Poupança
 */
const savingsAccountAPI = {
    listAll() {
        return fetchAPI('/savings');
    },

    findById(id) {
        return fetchAPI(`/savings/${id}`);
    },

    create(accountData) {
        return fetchAPI('/savings', {
            method: 'POST',
            body: accountData
        });
    },

    update(id, accountData) {
        return fetchAPI(`/savings/${id}`, {
            method: 'PUT',
            body: accountData
        });
    }
};

/**
 * API de PIX
 */
const pixAPI = {
    create(pixData) {
        return fetchAPI('/pix', {
            method: 'POST',
            body: pixData
        });
    },

    findById(id) {
        return fetchAPI(`/pix/${id}`);
    }
};

/**
 * API de Chaves PIX
 */
const pixKeyAPI = {
    createEmail(accountId, email) {
        return fetchAPI('/pixkey/email', {
            method: 'POST',
            body: {
                accountId: accountId,
                keyValue: email
            }
        });
    },

    createPhone(accountId, phone) {
        return fetchAPI('/pixkey/phone', {
            method: 'POST',
            body: {
                accountId: accountId,
                keyValue: phone
            }
        });
    },

    createRandom(accountId) {
        return fetchAPI('/pixkey/random', {
            method: 'POST',
            body: {
                accountId: accountId
            }
        });
    }
};

/**
 * API de Boletos
 */
const ticketAPI = {
    create(ticketData) {
        return fetchAPI('/ticket', {
            method: 'POST',
            body: ticketData
        });
    },

    findById(id) {
        return fetchAPI(`/ticket/${id}`);
    },

    listByAccount(accountId) {
        return fetchAPI(`/ticket/list/by-account/${accountId}`);
    },

    delete(id) {
        return fetchAPI(`/ticket/${id}`, {
            method: 'DELETE'
        });
    },

    pay(id, payingAccountId) {
        return fetchAPI(`/ticket/${id}/pay`, {
            method: 'POST',
            body: {
                payingAccountId: payingAccountId
            }
        });
    },

    async download(id) {
        const response = await fetchAPI(`/ticket/${id}/download`);
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `boleto-${id}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
    }
};

