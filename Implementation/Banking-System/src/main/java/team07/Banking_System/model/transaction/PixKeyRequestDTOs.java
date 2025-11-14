package team07.Banking_System.model.transaction;

// DTO para registrar chaves Email ou Telefone
public class PixKeyRequestDTOs {

    /** DTO para chaves que usam um valor, como Email ou Telefone */
    public static class KeyValueDTO {
        private String accountId;
        private String keyValue; // Usaremos este campo para email ou telefone

        // Getters e Setters
        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }
        
        public String getKeyValue() { return keyValue; }
        public void setKeyValue(String keyValue) { this.keyValue = keyValue; }
    }

    /** DTO para chaves que só precisam do ID da conta, como Aleatória */
    public static class KeyAccountDTO {
        private String accountId;

        // Getters e Setters
        public String getAccountId() { return accountId; }
        public void setAccountId(String accountId) { this.accountId = accountId; }
    }
}