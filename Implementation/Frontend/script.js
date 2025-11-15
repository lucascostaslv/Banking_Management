const API_BASE_URL = 'http://localhost:8080';

// Verificar autenticação ao carregar dashboard
if (window.location.pathname.includes('dashboard')) {
    window.addEventListener('DOMContentLoaded', () => {
        const user = checkAuth();
        if (user) {
            document.getElementById('userInfo').textContent = 
                `${user.first_name} ${user.last_name} (${user.userType === 'client' ? 'Cliente' : 'Gerente'})`;
            loadDashboard();
        }
    });
}

// Login Form
if (document.getElementById('loginForm')) {
    document.getElementById('loginForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const cpf = document.getElementById('cpf').value;
        const userType = document.getElementById('userType').value;
        const errorDiv = document.getElementById('loginError');
        
        errorDiv.style.display = 'none';
        
        try {
            await login(cpf, userType);
            window.location.href = 'dashboard.html';
        } catch (error) {
            errorDiv.textContent = error.message;
            errorDiv.style.display = 'block';
        }
    });
}

// Register Form
if (document.getElementById('registerForm')) {
    document.getElementById('regUserType').addEventListener('change', (e) => {
        document.getElementById('stateGroup').style.display = 
            e.target.value === 'client' ? 'block' : 'none';
    });
    
    document.getElementById('registerForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const userType = document.getElementById('regUserType').value;
        const data = {
            cpf: document.getElementById('regCpf').value,
            first_name: document.getElementById('regFirstName').value,
            last_name: document.getElementById('regLastName').value,
            birth_day: document.getElementById('regBirthDay').value
        };
        
        if (userType === 'client') {
            data.state = document.getElementById('regState').value;
        }
        
        try {
            const endpoint = userType === 'client' ? '/usr_client' : '/usr_manager';
            const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            
            if (response.ok) {
                alert('Usuário cadastrado com sucesso!');
                closeRegister();
                document.getElementById('registerForm').reset();
            } else {
                const error = await response.json().catch(() => ({ message: 'Erro desconhecido' }));
                alert('Erro: ' + (error.message || 'Erro ao cadastrar usuário. Verifique se o backend está rodando.'));
            }
        } catch (error) {
            if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError') || error.name === 'TypeError') {
                alert('Erro de conexão. Verifique:\n1. Se o backend está rodando em http://localhost:8080\n2. Se há problemas de CORS\n3. Se está usando um servidor local para o frontend');
            } else {
                alert('Erro ao cadastrar: ' + error.message);
            }
        }
    });
}

// Navegação entre seções
function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.getElementById(sectionId).classList.add('active');
    
    // Carregar dados da seção
    switch(sectionId) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'clients':
            loadClients();
            break;
        case 'managers':
            loadManagers();
            break;
        case 'accounts':
            loadAccounts('current');
            break;
        case 'pixKeys':
            loadPixKeys();
            break;
        case 'pix':
            loadPixTransactions();
            break;
        case 'tickets':
            loadTickets();
            break;
    }
}

// Dashboard
async function loadDashboard() {
    try {
        const [clients, currentAccounts, savingsAccounts] = await Promise.all([
            fetch(`${API_BASE_URL}/usr_client`).then(r => r.json()),
            fetch(`${API_BASE_URL}/acc_current`).then(r => r.json()),
            fetch(`${API_BASE_URL}/savings`).then(r => r.json())
        ]);
        
        document.getElementById('totalClients').textContent = clients.length;
        document.getElementById('totalAccounts').textContent = 
            currentAccounts.length + savingsAccounts.length;
        document.getElementById('totalTransactions').textContent = '-';
    } catch (error) {
        console.error('Erro ao carregar dashboard:', error);
        document.getElementById('totalClients').textContent = 'Erro';
        document.getElementById('totalAccounts').textContent = 'Erro';
    }
}

// Clientes
async function loadClients() {
    try {
        const response = await fetch(`${API_BASE_URL}/usr_client`);
        if (!response.ok) throw new Error('Erro ao carregar clientes');
        
        const clients = await response.json();
        
        if (clients.length === 0) {
            document.getElementById('clientsList').innerHTML = 
                '<div class="card"><p>Nenhum cliente cadastrado.</p></div>';
            return;
        }
        
        const html = `
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nome</th>
                        <th>CPF</th>
                        <th>Estado</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    ${clients.map(client => `
                        <tr>
                            <td>${client.id}</td>
                            <td>${client.first_name} ${client.last_name}</td>
                            <td>${client.cpf}</td>
                            <td>${client.state || '-'}</td>
                            <td>
                                <button onclick="viewClient('${client.id}')" class="btn btn-small">Ver</button>
                                <button onclick="editClient('${client.id}')" class="btn btn-small">Editar</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
        
        document.getElementById('clientsList').innerHTML = html;
    } catch (error) {
        document.getElementById('clientsList').innerHTML = 
            `<div class="error-message">Erro ao carregar clientes: ${error.message}</div>`;
    }
}

// Gerentes
async function loadManagers() {
    try {
        const response = await fetch(`${API_BASE_URL}/usr_manager`);
        if (!response.ok) throw new Error('Erro ao carregar gerentes');
        
        const managers = await response.json();
        
        if (managers.length === 0) {
            document.getElementById('managersList').innerHTML = 
                '<div class="card"><p>Nenhum gerente cadastrado.</p></div>';
            return;
        }
        
        const html = `
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nome</th>
                        <th>CPF</th>
                        <th>Role</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    ${managers.map(manager => `
                        <tr>
                            <td>${manager.id}</td>
                            <td>${manager.first_name} ${manager.last_name}</td>
                            <td>${manager.cpf}</td>
                            <td>${manager.role || '-'}</td>
                            <td>
                                <button onclick="viewManager('${manager.id}')" class="btn btn-small">Ver</button>
                                <button onclick="deleteManager('${manager.id}')" class="btn btn-small btn-danger">Excluir</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
        
        document.getElementById('managersList').innerHTML = html;
    } catch (error) {
        document.getElementById('managersList').innerHTML = 
            `<div class="error-message">Erro ao carregar gerentes: ${error.message}</div>`;
    }
}

// Contas
function showAccountType(type) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    
    if (type === 'current') {
        document.getElementById('currentAccounts').style.display = 'block';
        document.getElementById('savingsAccounts').style.display = 'none';
        loadAccounts('current');
    } else {
        document.getElementById('currentAccounts').style.display = 'none';
        document.getElementById('savingsAccounts').style.display = 'block';
        loadAccounts('savings');
    }
}

async function loadAccounts(type) {
    try {
        const endpoint = type === 'current' ? '/acc_current' : '/savings';
        const response = await fetch(`${API_BASE_URL}${endpoint}`);
        if (!response.ok) throw new Error('Erro ao carregar contas');
        
        const accounts = await response.json();
        const listId = type === 'current' ? 'currentAccountsList' : 'savingsAccountsList';
        
        if (accounts.length === 0) {
            document.getElementById(listId).innerHTML = 
                '<div class="card"><p>Nenhuma conta cadastrada.</p></div>';
            return;
        }
        
        const html = `
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Número</th>
                        <th>Cliente</th>
                        <th>Saldo</th>
                        <th>Tipo</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    ${accounts.map(account => `
                        <tr>
                            <td>${account.id}</td>
                            <td>${account.accountNumber}</td>
                            <td>${account.client?.first_name || ''} ${account.client?.last_name || ''}</td>
                            <td>R$ ${parseFloat(account.balance || 0).toFixed(2)}</td>
                            <td>${account.type}</td>
                            <td>
                                <button onclick="viewAccount('${account.id}', '${type}')" class="btn btn-small">Ver</button>
                                <button onclick="updateAccountBalance('${account.id}', '${type}')" class="btn btn-small">Atualizar Saldo</button>
                            </td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
        
        document.getElementById(listId).innerHTML = html;
    } catch (error) {
        const listId = type === 'current' ? 'currentAccountsList' : 'savingsAccountsList';
        document.getElementById(listId).innerHTML = 
            `<div class="error-message">Erro ao carregar contas: ${error.message}</div>`;
    }
}

// Chaves PIX
async function loadPixKeys() {
    document.getElementById('pixKeysList').innerHTML = `
        <div class="card">
            <p>Para visualizar chaves PIX, consulte uma conta específica.</p>
            <p>Use o formulário abaixo para cadastrar uma nova chave PIX.</p>
        </div>
    `;
}

// Transferências PIX
async function loadPixTransactions() {
    document.getElementById('pixList').innerHTML = `
        <div class="card">
            <p>Para visualizar transferências PIX, consulte uma conta específica.</p>
            <p>Use o formulário abaixo para criar uma nova transferência.</p>
        </div>
    `;
}

// Boletos
async function loadTickets() {
    document.getElementById('ticketsList').innerHTML = `
        <div class="card">
            <p>Para visualizar boletos, consulte uma conta específica.</p>
            <p>Use o formulário abaixo para criar um novo boleto.</p>
        </div>
    `;
}

// Modais de criação
function showCreateClientForm() {
    const modal = `
        <div class="modal" id="createClientModal">
            <div class="modal-content">
                <span class="close" onclick="closeModal('createClientModal')">&times;</span>
                <h2>Novo Cliente</h2>
                <form id="createClientForm">
                    <div class="form-group">
                        <label>CPF:</label>
                        <input type="text" name="cpf" required maxlength="11">
                    </div>
                    <div class="form-group">
                        <label>Nome:</label>
                        <input type="text" name="first_name" required>
                    </div>
                    <div class="form-group">
                        <label>Sobrenome:</label>
                        <input type="text" name="last_name" required>
                    </div>
                    <div class="form-group">
                        <label>Data de Nascimento:</label>
                        <input type="date" name="birth_day" required>
                    </div>
                    <div class="form-group">
                        <label>Estado:</label>
                        <select name="state" required>
                            <option value="AC">Acre</option>
                            <option value="AL">Alagoas</option>
                            <option value="AP">Amapá</option>
                            <option value="AM">Amazonas</option>
                            <option value="BA">Bahia</option>
                            <option value="CE">Ceará</option>
                            <option value="DF">Distrito Federal</option>
                            <option value="ES">Espírito Santo</option>
                            <option value="GO">Goiás</option>
                            <option value="MA">Maranhão</option>
                            <option value="MT">Mato Grosso</option>
                            <option value="MS">Mato Grosso do Sul</option>
                            <option value="MG">Minas Gerais</option>
                            <option value="PA">Pará</option>
                            <option value="PB">Paraíba</option>
                            <option value="PR">Paraná</option>
                            <option value="PE">Pernambuco</option>
                            <option value="PI">Piauí</option>
                            <option value="RJ">Rio de Janeiro</option>
                            <option value="RN">Rio Grande do Norte</option>
                            <option value="RS">Rio Grande do Sul</option>
                            <option value="RO">Rondônia</option>
                            <option value="RR">Roraima</option>
                            <option value="SC">Santa Catarina</option>
                            <option value="SP" selected>São Paulo</option>
                            <option value="SE">Sergipe</option>
                            <option value="TO">Tocantins</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary">Criar</button>
                </form>
            </div>
        </div>
    `;
    document.getElementById('modals').innerHTML = modal;
    document.getElementById('createClientForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData);
        
        try {
            const response = await fetch(`${API_BASE_URL}/usr_client`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            
            if (response.ok) {
                alert('Cliente criado com sucesso!');
                closeModal('createClientModal');
                loadClients();
            } else {
                const error = await response.json();
                alert('Erro: ' + (error.message || 'Erro ao criar cliente'));
            }
        } catch (error) {
            alert('Erro: ' + error.message);
        }
    });
}

function showCreateManagerForm() {
    const modal = `
        <div class="modal" id="createManagerModal">
            <div class="modal-content">
                <span class="close" onclick="closeModal('createManagerModal')">&times;</span>
                <h2>Novo Gerente</h2>
                <form id="createManagerForm">
                    <div class="form-group">
                        <label>CPF:</label>
                        <input type="text" name="cpf" required maxlength="11">
                    </div>
                    <div class="form-group">
                        <label>Nome:</label>
                        <input type="text" name="first_name" required>
                    </div>
                    <div class="form-group">
                        <label>Sobrenome:</label>
                        <input type="text" name="last_name" required>
                    </div>
                    <div class="form-group">
                        <label>Data de Nascimento:</label>
                        <input type="date" name="birth_day" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Criar</button>
                </form>
            </div>
        </div>
    `;
    document.getElementById('modals').innerHTML = modal;
    document.getElementById('createManagerForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData);
        
        try {
            const response = await fetch(`${API_BASE_URL}/usr_manager`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            
            if (response.ok) {
                alert('Gerente criado com sucesso!');
                closeModal('createManagerModal');
                loadManagers();
            } else {
                const error = await response.json();
                alert('Erro: ' + (error.message || 'Erro ao criar gerente'));
            }
        } catch (error) {
            alert('Erro: ' + error.message);
        }
    });
}

function showCreateAccountForm(type) {
    fetch(`${API_BASE_URL}/usr_client`)
        .then(r => r.json())
        .then(clients => {
            const modal = `
                <div class="modal" id="createAccountModal">
                    <div class="modal-content">
                        <span class="close" onclick="closeModal('createAccountModal')">&times;</span>
                        <h2>Nova ${type === 'current' ? 'Conta Corrente' : 'Conta Poupança'}</h2>
                        <form id="createAccountForm">
                            <div class="form-group">
                                <label>Cliente:</label>
                                <select name="clientId" required>
                                    ${clients.length > 0 ? clients.map(c => 
                                        `<option value="${c.id}">${c.first_name} ${c.last_name} (${c.cpf})</option>`
                                    ).join('') : '<option value="">Nenhum cliente disponível</option>'}
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary">Criar</button>
                        </form>
                    </div>
                </div>
            `;
            document.getElementById('modals').innerHTML = modal;
            document.getElementById('createAccountForm').addEventListener('submit', async (e) => {
                e.preventDefault();
                const clientId = e.target.clientId.value;
                const endpoint = type === 'current' ? '/acc_current' : '/savings';
                
                try {
                    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ client: { id: clientId } })
                    });
                    
                    if (response.ok) {
                        alert('Conta criada com sucesso!');
                        closeModal('createAccountModal');
                        loadAccounts(type);
                    } else {
                        const error = await response.json();
                        alert('Erro: ' + (error.message || 'Erro ao criar conta'));
                    }
                } catch (error) {
                    alert('Erro: ' + error.message);
                }
            });
        })
        .catch(error => {
            alert('Erro ao carregar clientes: ' + error.message);
        });
}

function showCreatePixKeyForm() {
    Promise.all([
        fetch(`${API_BASE_URL}/acc_current`).then(r => r.json()),
        fetch(`${API_BASE_URL}/savings`).then(r => r.json())
    ]).then(([current, savings]) => {
        const allAccounts = [...current, ...savings];
        const modal = `
            <div class="modal" id="createPixKeyModal">
                <div class="modal-content">
                    <span class="close" onclick="closeModal('createPixKeyModal')">&times;</span>
                    <h2>Nova Chave PIX</h2>
                    <form id="createPixKeyForm">
                        <div class="form-group">
                            <label>Conta:</label>
                            <select name="accountId" required>
                                ${allAccounts.length > 0 ? allAccounts.map(a => 
                                    `<option value="${a.id}">${a.accountNumber} - ${a.client?.first_name || ''} ${a.client?.last_name || ''}</option>`
                                ).join('') : '<option value="">Nenhuma conta disponível</option>'}
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Tipo de Chave:</label>
                            <select name="keyType" id="pixKeyType" required>
                                <option value="email">Email</option>
                                <option value="phone">Telefone</option>
                                <option value="random">Aleatória</option>
                            </select>
                        </div>
                        <div class="form-group" id="keyValueGroup">
                            <label>Valor da Chave:</label>
                            <input type="text" name="keyValue" placeholder="email@exemplo.com ou +5521999998888">
                        </div>
                        <button type="submit" class="btn btn-primary">Criar</button>
                    </form>
                </div>
            </div>
        `;
        document.getElementById('modals').innerHTML = modal;
        
        document.getElementById('pixKeyType').addEventListener('change', (e) => {
            document.getElementById('keyValueGroup').style.display = 
                e.target.value === 'random' ? 'none' : 'block';
        });
        
        document.getElementById('createPixKeyForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const accountId = e.target.accountId.value;
            const keyType = e.target.keyType.value;
            const keyValue = e.target.keyValue?.value;
            
            let endpoint = '';
            let body = {};
            
            if (keyType === 'email') {
                endpoint = '/pixkey/email';
                body = { accountId, keyValue };
            } else if (keyType === 'phone') {
                endpoint = '/pixkey/phone';
                body = { accountId, keyValue };
            } else {
                endpoint = '/pixkey/random';
                body = { accountId };
            }
            
            try {
                const response = await fetch(`${API_BASE_URL}${endpoint}`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(body)
                });
                
                if (response.ok) {
                    alert('Chave PIX criada com sucesso!');
                    closeModal('createPixKeyModal');
                } else {
                    const error = await response.json();
                    alert('Erro: ' + (error.message || 'Erro ao criar chave PIX'));
                }
            } catch (error) {
                alert('Erro: ' + error.message);
            }
        });
    }).catch(error => {
        alert('Erro ao carregar contas: ' + error.message);
    });
}

function showCreatePixForm() {
    Promise.all([
        fetch(`${API_BASE_URL}/acc_current`).then(r => r.json()),
        fetch(`${API_BASE_URL}/savings`).then(r => r.json())
    ]).then(([current, savings]) => {
        const allAccounts = [...current, ...savings];
        const modal = `
            <div class="modal" id="createPixModal">
                <div class="modal-content">
                    <span class="close" onclick="closeModal('createPixModal')">&times;</span>
                    <h2>Nova Transferência PIX</h2>
                    <form id="createPixForm">
                        <div class="form-group">
                            <label>Conta de Origem:</label>
                            <select name="originAccountId" required>
                                ${allAccounts.length > 0 ? allAccounts.map(a => 
                                    `<option value="${a.id}">${a.accountNumber} - ${a.client?.first_name || ''} ${a.client?.last_name || ''}</option>`
                                ).join('') : '<option value="">Nenhuma conta disponível</option>'}
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Chave PIX do Destino:</label>
                            <input type="text" name="pixKey" required placeholder="email@exemplo.com ou +5521999998888">
                        </div>
                        <div class="form-group">
                            <label>Valor:</label>
                            <input type="number" name="value" step="0.01" required min="0.01">
                        </div>
                        <button type="submit" class="btn btn-primary">Transferir</button>
                    </form>
                </div>
            </div>
        `;
        document.getElementById('modals').innerHTML = modal;
        
        document.getElementById('createPixForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const originAccountId = e.target.originAccountId.value;
            const pixKey = e.target.pixKey.value;
            const value = parseFloat(e.target.value.value);
            
            try {
                const response = await fetch(`${API_BASE_URL}/pix`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        originAccount: { id: originAccountId },
                        pixKey: pixKey,
                        value: value
                    })
                });
                
                if (response.ok) {
                    alert('Transferência PIX realizada com sucesso!');
                    closeModal('createPixModal');
                } else {
                    const error = await response.json();
                    alert('Erro: ' + (error.message || 'Erro ao realizar transferência'));
                }
            } catch (error) {
                alert('Erro: ' + error.message);
            }
        });
    }).catch(error => {
        alert('Erro ao carregar contas: ' + error.message);
    });
}

function showCreateTicketForm() {
    Promise.all([
        fetch(`${API_BASE_URL}/acc_current`).then(r => r.json()),
        fetch(`${API_BASE_URL}/savings`).then(r => r.json())
    ]).then(([current, savings]) => {
        const allAccounts = [...current, ...savings];
        const modal = `
            <div class="modal" id="createTicketModal">
                <div class="modal-content">
                    <span class="close" onclick="closeModal('createTicketModal')">&times;</span>
                    <h2>Novo Boleto</h2>
                    <form id="createTicketForm">
                        <div class="form-group">
                            <label>Conta:</label>
                            <select name="originAccountId" required>
                                ${allAccounts.length > 0 ? allAccounts.map(a => 
                                    `<option value="${a.id}">${a.accountNumber} - ${a.client?.first_name || ''} ${a.client?.last_name || ''}</option>`
                                ).join('') : '<option value="">Nenhuma conta disponível</option>'}
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Valor:</label>
                            <input type="number" name="value" step="0.01" required min="0.01">
                        </div>
                        <div class="form-group">
                            <label>Data de Vencimento:</label>
                            <input type="date" name="dueDate" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Criar Boleto</button>
                    </form>
                </div>
            </div>
        `;
        document.getElementById('modals').innerHTML = modal;
        
        document.getElementById('createTicketForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const originAccountId = e.target.originAccountId.value;
            const value = parseFloat(e.target.value.value);
            const dueDate = e.target.dueDate.value;
            
            try {
                const response = await fetch(`${API_BASE_URL}/ticket`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        originAccount: { id: originAccountId },
                        value: value,
                        dueDate: dueDate
                    })
                });
                
                if (response.ok) {
                    alert('Boleto criado com sucesso!');
                    closeModal('createTicketModal');
                } else {
                    const error = await response.json();
                    alert('Erro: ' + (error.message || 'Erro ao criar boleto'));
                }
            } catch (error) {
                alert('Erro: ' + error.message);
            }
        });
    }).catch(error => {
        alert('Erro ao carregar contas: ' + error.message);
    });
}

// Funções auxiliares
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.remove();
    }
}

function showRegister() {
    document.getElementById('registerModal').style.display = 'flex';
}

function closeRegister() {
    document.getElementById('registerModal').style.display = 'none';
}

function deleteManager(id) {
    if (confirm('Tem certeza que deseja excluir este gerente?')) {
        fetch(`${API_BASE_URL}/usr_manager/${id}`, { method: 'DELETE' })
            .then(response => {
                if (response.ok) {
                    alert('Gerente excluído com sucesso!');
                    loadManagers();
                } else {
                    return response.json().then(error => {
                        throw new Error(error.message || 'Erro ao excluir gerente');
                    });
                }
            })
            .catch(error => alert('Erro: ' + error.message));
    }
}

function viewClient(id) {
    fetch(`${API_BASE_URL}/usr_client/${id}`)
        .then(r => {
            if (!r.ok) throw new Error('Cliente não encontrado');
            return r.json();
        })
        .then(client => {
            alert(`Cliente: ${client.first_name} ${client.last_name}\nCPF: ${client.cpf}\nEstado: ${client.state || '-'}\nData de Nascimento: ${client.birth_day}`);
        })
        .catch(error => alert('Erro: ' + error.message));
}

function editClient(id) {
    fetch(`${API_BASE_URL}/usr_client/${id}`)
        .then(r => {
            if (!r.ok) throw new Error('Cliente não encontrado');
            return r.json();
        })
        .then(client => {
            const newFirstName = prompt('Novo nome:', client.first_name);
            const newLastName = prompt('Novo sobrenome:', client.last_name);
            const newState = prompt('Novo estado:', client.state || 'SP');
            
            if (newFirstName && newLastName) {
                const updated = {
                    ...client,
                    first_name: newFirstName,
                    last_name: newLastName,
                    state: newState
                };
                
                fetch(`${API_BASE_URL}/usr_client/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(updated)
                })
                .then(r => {
                    if (r.ok) {
                        alert('Cliente atualizado!');
                        loadClients();
                    } else {
                        throw new Error('Erro ao atualizar');
                    }
                })
                .catch(error => alert('Erro: ' + error.message));
            }
        })
        .catch(error => alert('Erro: ' + error.message));
}

function viewManager(id) {
    fetch(`${API_BASE_URL}/usr_manager/${id}`)
        .then(r => {
            if (!r.ok) throw new Error('Gerente não encontrado');
            return r.json();
        })
        .then(manager => {
            alert(`Gerente: ${manager.first_name} ${manager.last_name}\nCPF: ${manager.cpf}\nRole: ${manager.role || '-'}\nData de Nascimento: ${manager.birth_day}`);
        })
        .catch(error => alert('Erro: ' + error.message));
}

function viewAccount(id, type) {
    const endpoint = type === 'current' ? '/acc_current' : '/savings';
    fetch(`${API_BASE_URL}${endpoint}/${id}`)
        .then(r => {
            if (!r.ok) throw new Error('Conta não encontrada');
            return r.json();
        })
        .then(account => {
            alert(`Conta: ${account.accountNumber}\nCliente: ${account.client?.first_name || ''} ${account.client?.last_name || ''}\nSaldo: R$ ${parseFloat(account.balance || 0).toFixed(2)}\nTipo: ${account.type}\nData de Abertura: ${new Date(account.openDate).toLocaleDateString('pt-BR')}`);
        })
        .catch(error => alert('Erro: ' + error.message));
}

function updateAccountBalance(id, type) {
    const newBalance = prompt('Digite o novo saldo:');
    if (newBalance && !isNaN(newBalance)) {
        const endpoint = type === 'current' ? '/acc_current' : '/savings';
        fetch(`${API_BASE_URL}${endpoint}/${id}`)
            .then(r => {
                if (!r.ok) throw new Error('Conta não encontrada');
                return r.json();
            })
            .then(account => {
                account.balance = parseFloat(newBalance);
                return fetch(`${API_BASE_URL}${endpoint}/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(account)
                });
            })
            .then(r => {
                if (r.ok) {
                    alert('Saldo atualizado!');
                    loadAccounts(type);
                } else {
                    throw new Error('Erro ao atualizar saldo');
                }
            })
            .catch(error => alert('Erro: ' + error.message));
    }
}

