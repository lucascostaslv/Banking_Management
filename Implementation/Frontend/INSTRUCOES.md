# 🔧 Instruções de Uso - Correções Aplicadas

## ✅ Correções Implementadas

1. **CORS Configurado no Backend** - Criada classe `CorsConfig.java`
2. **Tratamento de Erros Melhorado** - Mensagens mais claras no frontend
3. **Validação de Conexão** - Detecta quando o backend não está rodando

## 🚀 Como Usar Agora

### Passo 1: Reiniciar o Backend

Após adicionar a classe `CorsConfig.java`, você precisa **recompilar e reiniciar** o backend Spring Boot:

```bash
cd Banking_Management/Implementation/Banking-System
./gradlew build
./gradlew bootRun
```

Ou se estiver usando uma IDE, simplesmente reinicie a aplicação.

### Passo 2: Iniciar Servidor Local para o Frontend

**IMPORTANTE**: Não abra o HTML diretamente! Use um servidor local:

```bash
cd Banking_Management/Implementation/Frontend
python3 -m http.server 3000
```

### Passo 3: Acessar no Navegador

Abra: `http://localhost:3000`

### Passo 4: Testar Login ou Cadastro

#### Opção A: Cadastrar Novo Usuário
1. Clique em "Cadastre-se"
2. Preencha os dados:
   - Tipo: Cliente ou Gerente
   - CPF: (11 dígitos, sem pontos)
   - Nome, Sobrenome, Data de Nascimento
   - Estado: (apenas para Cliente)
3. Clique em "Cadastrar"

#### Opção B: Fazer Login com Usuário Existente

Se você já tem usuários cadastrados (via `request.http`), use:
- **Cliente**: CPF `11122233344` (Ana) ou `55566677788` (Bruno)
- **Gerente**: CPF `12312312312` ou `45645645645`

## 🐛 Solução de Problemas

### Erro: "Backend não está rodando"
- Verifique se o Spring Boot está rodando na porta 8080
- Teste: `curl http://localhost:8080/usr_client`
- Ou abra no navegador: `http://localhost:8080/usr_client`

### Erro: "Erro de conexão"
- Certifique-se de estar usando um servidor local (não abra o HTML diretamente)
- Verifique se o CORS foi configurado corretamente no backend
- Reinicie o backend após adicionar `CorsConfig.java`

### Erro: "Usuário não encontrado"
- Verifique se o CPF está correto (11 dígitos)
- Certifique-se de selecionar o tipo correto (Cliente ou Gerente)
- Cadastre um novo usuário se necessário

## 📝 Notas Importantes

- O backend deve estar rodando **antes** de abrir o frontend
- Sempre use um servidor local para o frontend (não `file://`)
- Após adicionar `CorsConfig.java`, **reinicie o backend**

## ✅ Checklist

- [ ] Backend Spring Boot rodando em `http://localhost:8080`
- [ ] Classe `CorsConfig.java` adicionada e backend reiniciado
- [ ] Servidor local iniciado para o frontend (`python3 -m http.server 3000`)
- [ ] Acessando `http://localhost:3000` (não `file://`)
- [ ] Console do navegador (F12) sem erros de CORS

