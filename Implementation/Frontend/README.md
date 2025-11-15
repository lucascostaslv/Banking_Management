# Frontend - Banking System

Front-end web desenvolvido em HTML, CSS e JavaScript vanilla para o sistema bancário.

## 📁 Estrutura de Arquivos

```
Frontend/
├── index.html      # Página de login
├── dashboard.html  # Dashboard principal
├── styles.css      # Estilos CSS
├── auth.js         # Gerenciamento de autenticação
└── script.js       # Lógica principal da aplicação
```

## 🚀 Como Usar

### 1. Pré-requisitos

- Backend Spring Boot rodando em `http://localhost:8080`
- Navegador web moderno (Chrome, Firefox, Edge, Safari)

### 2. Iniciar o Frontend

1. Abra o arquivo `index.html` no navegador
2. Ou use um servidor local (recomendado para evitar problemas de CORS):

```bash
# Usando Python 3
python3 -m http.server 3000

# Ou usando Node.js (http-server)
npx http-server -p 3000
```

3. Acesse `http://localhost:3000` no navegador

### 3. Autenticação

O sistema usa autenticação simples baseada em CPF:

1. **Login**: Digite o CPF de um usuário cadastrado e selecione o tipo (Cliente ou Gerente)
2. **Cadastro**: Clique em "Cadastre-se" para criar um novo usuário
3. Os dados de autenticação são armazenados no `localStorage` do navegador

## 🔧 Configuração

### Alterar URL da API

Se o backend estiver rodando em outra porta ou URL, edite o arquivo `auth.js` e `script.js`:

```javascript
const API_BASE_URL = 'http://localhost:8080'; // Altere aqui
```

## 📋 Funcionalidades

### ✅ Implementadas

- **Autenticação**: Login por CPF (Cliente ou Gerente)
- **Cadastro de Usuários**: Clientes e Gerentes
- **Dashboard**: Estatísticas gerais do sistema
- **Gerenciamento de Clientes**: Listar, criar, visualizar e editar
- **Gerenciamento de Gerentes**: Listar, criar, visualizar e excluir
- **Gerenciamento de Contas**: 
  - Conta Corrente
  - Conta Poupança
  - Criar, visualizar e atualizar saldo
- **Chaves PIX**: Cadastrar chaves (Email, Telefone ou Aleatória)
- **Transferências PIX**: Realizar transferências entre contas
- **Boletos**: Criar novos boletos

### ⚠️ Limitações

- Não há listagem completa de transações PIX e boletos (apenas criação)
- Para visualizar transações específicas, é necessário consultar por ID
- Download de boletos em PDF não está implementado na interface (mas o endpoint existe)

## 🎨 Interface

A interface foi desenvolvida com:
- Design moderno e responsivo
- Cores e estilos consistentes
- Modais para formulários
- Tabelas para listagens
- Feedback visual para ações do usuário

## 🔒 Segurança

⚠️ **Atenção**: Este é um sistema de demonstração com autenticação simplificada. Para produção, implemente:
- Autenticação JWT ou OAuth
- Validação de dados no frontend e backend
- Proteção CSRF
- HTTPS obrigatório
- Validação de permissões por tipo de usuário

## 🐛 Solução de Problemas

### Erro de CORS

Se encontrar erros de CORS, configure no backend Spring Boot:

```java
@CrossOrigin(origins = "http://localhost:3000")
```

Ou adicione no `application.properties`:

```properties
spring.web.cors.allowed-origins=http://localhost:3000
```

### Erro ao carregar dados

- Verifique se o backend está rodando
- Verifique a URL da API nos arquivos `auth.js` e `script.js`
- Abra o Console do navegador (F12) para ver erros detalhados

## 📝 Notas

- Os dados de autenticação são armazenados no `localStorage` do navegador
- Para limpar a sessão, use o botão "Sair" ou limpe o `localStorage` manualmente
- O sistema não diferencia permissões entre Cliente e Gerente na interface atual

