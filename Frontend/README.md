# Front-end Banking Management System

Interface web para o sistema de gerenciamento bancário, desenvolvida com HTML, CSS e JavaScript puro.

## 🚀 Características

- **Autenticação Simples**: Sistema de login usando localStorage (não afeta o backend)
- **Interface Moderna**: Design responsivo e acessível
- **Comunicação REST**: Integração completa com a API do backend
- **Gerenciamento Completo**: CRUD para clientes, contas, transações e boletos

## 📁 Estrutura de Arquivos

```
Frontend/
├── index.html          # Página de login
├── dashboard.html      # Dashboard principal
├── clients.html        # Gerenciamento de clientes
├── accounts.html       # Gerenciamento de contas
├── transactions.html   # Transações PIX
├── tickets.html        # Gerenciamento de boletos
├── css/
│   └── styles.css     # Estilos globais
└── js/
    ├── auth.js        # Sistema de autenticação
    ├── api.js         # Comunicação com API
    ├── login.js       # Lógica do login
    ├── dashboard.js   # Lógica do dashboard
    ├── clients.js     # Lógica de clientes
    ├── accounts.js    # Lógica de contas
    ├── transactions.js # Lógica de transações
    └── tickets.js     # Lógica de boletos
```

## 🔐 Autenticação

O sistema usa autenticação simples baseada em localStorage. **Não há validação no backend** - é apenas controle de acesso no front-end.

### Usuários Padrão

| Usuário | Senha | Role |
|---------|-------|------|
| `admin` | `12345` | Administrador |
| `manager` | `12345` | Gerente |
| `client` | `12345` | Cliente |

### Como Funciona

1. O usuário faz login na página `index.html`
2. As credenciais são validadas localmente (não há comunicação com backend)
3. Se válidas, uma sessão é salva no `localStorage`
4. Todas as páginas verificam a autenticação antes de carregar
5. A sessão expira após 24 horas

## 🛠️ Como Usar

### Pré-requisitos

1. Backend Spring Boot rodando na porta `8080`
2. Servidor HTTP local para servir os arquivos HTML

### Opções de Servidor

#### Opção 1: Live Server (VS Code)
1. Instale a extensão "Live Server" no VS Code
2. Clique com botão direito em `index.html`
3. Selecione "Open with Live Server"
4. O servidor iniciará na porta `5500`

#### Opção 2: Python HTTP Server
```bash
cd Frontend
python3 -m http.server 8000
```
Acesse: `http://localhost:8000`

#### Opção 3: Node.js http-server
```bash
npm install -g http-server
cd Frontend
http-server -p 8000
```

### Configuração da API

Por padrão, a API está configurada para `http://localhost:8080`. 

Se precisar alterar, edite o arquivo `js/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8080'; // Altere aqui
```

## 📋 Funcionalidades

### Dashboard
- Visão geral do sistema
- Estatísticas de clientes, contas e transações
- Ações rápidas para funcionalidades principais

### Clientes
- Listar todos os clientes
- Criar novo cliente
- Editar cliente existente
- Campos: Nome, Sobrenome, CPF, Data de Nascimento, Estado, Atividade

### Contas
- Gerenciar contas correntes e poupança
- Criar novas contas
- Editar contas existentes
- Visualizar saldo e estado das contas

### Transações PIX
- Realizar transferências via PIX
- Usar chave PIX (email, telefone ou aleatória)
- Histórico de transações

### Boletos
- Criar novos boletos
- Listar boletos por conta
- Pagar boletos
- Download de PDF do boleto
- Excluir boletos

## 🔧 Solução de Problemas

### Erro de CORS
Se encontrar erros de CORS, verifique:
1. O backend está rodando na porta 8080?
2. A configuração CORS no backend inclui a porta do seu servidor front-end?
3. O arquivo `CorsConfig.java` está atualizado?

### Erro 404 na API
1. Verifique se o backend está rodando
2. Confirme a URL da API em `js/api.js`
3. Verifique os logs do backend para erros

### Sessão Expirada
A sessão expira após 24 horas. Simplesmente faça login novamente.

## 🎨 Personalização

### Cores
Edite as variáveis CSS em `css/styles.css`:
```css
:root {
    --primary-color: #2563eb;
    --success-color: #10b981;
    /* ... */
}
```

### Adicionar Novos Usuários
Edite o arquivo `js/auth.js`:
```javascript
users: {
    'novo_usuario': { 
        password: 'senha123', 
        role: 'client', 
        name: 'Novo Usuário' 
    }
}
```

## 📝 Notas Importantes

1. **Autenticação é apenas no front-end**: Não há validação real no backend. Para produção, implemente autenticação JWT ou similar.

2. **Dados sensíveis**: Não armazene senhas reais no código JavaScript em produção.

3. **CORS**: Certifique-se de que o backend permite requisições da origem do seu front-end.

4. **Formato de Datas**: Use formato ISO (YYYY-MM-DD) para datas.

5. **Valores Monetários**: Use números decimais para valores (ex: 100.50).

## 🚀 Próximos Passos

Para melhorar o sistema:
- Implementar autenticação real no backend (JWT)
- Adicionar validação de formulários mais robusta
- Implementar paginação nas tabelas
- Adicionar filtros e busca avançada
- Melhorar tratamento de erros
- Adicionar testes automatizados

