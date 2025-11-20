# Challenge Java Mottu - Aplicação Final (Sprint 4)

## 📋 Visão Geral do Projeto

Este projeto é a entrega final da disciplina de Java Advanced para o Challenge Mottu. A solução consiste em um sistema duplo:

1.  Uma **API RESTful** robusta e segura (JWT), documentada com Swagger, pronta para ser consumida por aplicações mobile e outras integrações.
2.  Uma **Aplicação Web (Backoffice)** construída com Thymeleaf e Spring Security, permitindo o gerenciamento administrativo do sistema.

O modelo de dados foi refatorado (conforme feedback da S3) para focar no gerenciamento de **Pátios** de veículos, alinhando-se de forma mais precisa às regras de negócio da Mottu.

---

## 👨‍💻 Aluno(s)

* VITOR TADEU SOARES DE SOUSA - RM559105 - 2TDSPH
* GIOVANNI DE SOUZA LIMA - RM5566536 - 2TDSPH
* Diego bassalo          - rm558710 - 2TDSPG
---

## ✨ Principais Funcionalidades

### Aplicação Web (Backoffice com Thymeleaf)

* **Autenticação Segura:** Sistema de login e logout via formulário (Spring Security).
* **Controle de Acesso por Papel:**
    * **ADMIN:** Acesso total ao CRUD de veículos e ao Dashboard.
    * **USER:** Acesso restrito (tratado com página de "Acesso Negado").
* **Gerenciamento de Veículos:** CRUD completo de veículos, agora corretamente associados a **Pátios**.
* **Dashboard de Indicadores:** Página administrativa que exibe o total de usuários e veículos cadastrados.
* **Tratamento de Erros Amigável:** Implementação de uma página de erro 403 (Acesso Negado) personalizada, tratando o feedback da S3.
* **Layout Padronizado (DRY):** Uso de fragmentos Thymeleaf para cabeçalho e rodapé, evitando repetição de código.

### API REST (Integração)

* **Segurança via JWT:** Todos os endpoints em `/api/**` são protegidos por JSON Web Tokens.
* **Documentação Interativa:** API documentada com Swagger (SpringDoc).
* **Endpoints de Gerenciamento:** CRUD completo para `Usuários` e `Veículos`.
* **Endpoints de Negócio (S4):** A API expõe lógica de negócio, como o endpoint `/api/veiculos/usuario/{usuarioId}` que lista todos os veículos que um usuário já *alugou* (via tabela `Locacao`).

### Integração com Banco de Dados (Oracle)

* **Versionamento de Schema:** O banco de dados é 100% gerenciado pelo Flyway, com 6 migrações que constroem o schema, inserem dados e aplicam as refatorações de domínio.
* **Integração com Stored Procedures (S4):** A aplicação Java chama Stored Procedures Oracle para lógicas de negócio complexas, como demonstrado no `RelatorioService` (requisito da S4 de Banco de Dados).

---

## 🛠️ Principais Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.2.5
    * Spring Web
    * Spring Data JPA
    * Spring Security
    * Spring Validation
* **Frontend (Backoffice):** Thymeleaf
* **Banco de Dados:**
    * Oracle
    * Flyway (Versionamento de Schema)
* **Mapeamento DTO:** MapStruct
* **Documentação da API:** SpringDoc OpenAPI (Swagger)
* **Autenticação:** Formulário (Web) e JSON Web Tokens (JWT para a API)

---

## 💻 Instruções para Execução Local

### Pré-requisitos

* JDK 21 ou superior.
* Apache Maven 3.6.+.
* Acesso a um schema Oracle.

### Passos para Executar

1.  **Clone o Repositório:**
    ```bash
    git clone [https://github.com/ovitortadeu/challenge-java-springboot](https://github.com/ovitortadeu/challenge-java-springboot)
    cd challenge-java-springboot/challenge java/demo
    ```

2.  **Configure o Banco de Dados:**
    * Abra o arquivo `src/main/resources/application.properties`.
    * Altere as propriedades `spring.datasource.username`  e `spring.datasource.password` para as credenciais do Oracle.

3.  **Execute a Aplicação:**
    * O Flyway criará e populará o banco de dados automaticamente na primeira inicialização.
    ```bash
    mvn spring-boot:run
    ```

---

## 🔑 Credenciais de Acesso (Web)

A aplicação é populada com usuários de exemplo pelo Flyway.

* **Acesso Web:** [http://localhost:8080/](http://localhost:8080/)
* **Acesso API (Swagger):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

#### Perfil Administrador
* **Usuário:** `admin`
* **Senha:** `admin123`
* **Permissões:** Acesso total ao CRUD de veículos e ao Dashboard.

#### Perfil Usuário Comum
* **Usuário:** `user`
* **Senha:** `user123`
* **Permissões:** Acesso restrito. Será redirecionado para a página "Acesso Negado" ao tentar acessar rotas de admin.
