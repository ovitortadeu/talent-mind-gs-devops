# 💻 TalentMind - O Futuro do Trabalho (Global Solution)

🌟 ## Visão Geral do Projeto

O **TalentMind** é uma plataforma inovadora desenvolvida para a Global Solution 2025, abordando o tema "O Futuro do Trabalho". A solução foi projetada para conectar talentos a oportunidades de forma inteligente, alinhando vagas, competências e cursos de requalificação profissional.

Este projeto destaca-se pela sua **Arquitetura Cloud Native** e pela implementação de **Práticas de DevOps** de ponta. A infraestrutura utiliza Azure Container Instances (ACI) para orquestrar a aplicação Java, um banco de dados Oracle e o message broker RabbitMQ, tudo provisionado e gerenciado na nuvem.

🎯 ## Arquitetura de DevOps e Nuvem

A solução foi arquitetada para garantir alta disponibilidade, escalabilidade e automação, seguindo os pilares do DevOps:

-   **App (Java 21 + Spring Boot):** Aplicação principal containerizada e executada em *Azure Container Instances (ACI)*.
-   **Banco de Dados (Oracle XE 21c):** Hospedado em um container dedicado no Azure, garantindo persistência, isolamento e gerenciamento via *Infraestrutura como Código (IaC)*.
-   **Mensageria (RabbitMQ):** Um container exclusivo para o RabbitMQ gerencia o processamento assíncrono de compatibilidade de vagas, desacoplando os serviços.
-   **CI/CD (Azure DevOps):** Um pipeline totalmente automatizado que gerencia o ciclo de vida da aplicação, incluindo build, testes, criação de imagem Docker e deploy contínuo em ambiente de nuvem.

---

🛠️ ## Tecnologias e Ferramentas

| Categoria             | Tecnologia/Ferramenta                                      |
| --------------------- | ---------------------------------------------------------- |
| **Nuvem**             | Microsoft Azure (Resource Groups, ACR, ACI)                |
| **CI/CD**             | Azure Pipelines (YAML)                                     |
| **Containerização**   | Docker & Azure Container Registry (ACR)                    |
| **Backend**           | Java 21, Spring Boot 3, Spring AI                          |
| **Banco de Dados**    | Oracle Database 21c XE (Imagem Docker Otimizada)           |
| **Segurança**         | Azure DevOps Secret Variables (OpenAI Keys, Senhas de DB)  |

---

🚀 ## Pipeline de CI/CD

O projeto utiliza um pipeline robusto definido no arquivo `azure-pipelines.yml`, dividido em três estágios principais:

1.  **Stage: Build**
    -   Compila o projeto Java utilizando Maven.
    -   Executa testes unitários para garantir a qualidade do código.

2.  **Stage: Docker**
    -   Constrói a imagem Docker da aplicação.
    -   Envia a imagem para o *Azure Container Registry (ACR)*.

3.  **Stage: Deploy**
    -   Provisiona automaticamente a infraestrutura no *Azure Container Instances* via Azure CLI.
    -   Injeta variáveis de ambiente e segredos (como senhas e API keys) em tempo de execução para garantir a segurança.

---

🔑 ## Como Acessar a Aplicação

Após a execução bem-sucedida do pipeline de deploy, a aplicação estará disponível publicamente na nuvem.

### 1. Obter URL Pública

Execute o comando abaixo no Azure CLI para obter o endereço de acesso da aplicação:

```powershell
az container show --resource-group rg-talentmind-gs --name app-talentmind-instance --query ipAddress.fqdn --output tsv
```

### 2. Credenciais de Acesso

O sistema é populado com dados iniciais (Seed Data) através do Flyway. Utilize as seguintes credenciais para acessar como administrador:

-   **E-mail:** `admin_novo@teste.com`
-   **Senha:** `password`

---

📄 ## Documentação da API

A documentação técnica dos endpoints RESTful (Swagger/OpenAPI) está disponível publicamente e pode ser acessada no seguinte endereço:

-   **Swagger UI:** `http://<SUA-URL-AZURE>:8080/swagger-ui.html`

---

⚙️ ## Configuração de Variáveis de Ambiente (Segurança)

Por questões de segurança, chaves de API sensíveis e senhas de banco de dados não são armazenadas no código-fonte. Elas são gerenciadas como **Secret Variables** no Azure DevOps e injetadas no ambiente durante o deploy:

-   `OPENAI_API_KEY`: Chave para integração com serviços de IA Generativa.
---

📢 ## Fluxo Assíncrono com RabbitMQ

O sistema demonstra um fluxo de trabalho assíncrono para otimizar a experiência do usuário na criação de vagas:

1.  O usuário cria uma nova vaga na plataforma.
2.  O sistema grava os dados da vaga no banco de dados Oracle de forma síncrona.
3.  Um evento (`vaga-nova`) é publicado na fila `q.talentmind.vaga-nova` do RabbitMQ.
4.  Um serviço consumidor (worker) processa a mensagem em background para realizar o cálculo de compatibilidade (*match*) com os perfis de candidatos, sem impactar a performance da aplicação principal.

---

👥 ## Equipe

| RM       | Nome                        | Turma    |
| -------- | --------------------------- | -------- |
| RM559105 | Vitor Tadeu Soares de Sousa | 2TDSPH   |
| RM556536 | Giovanni de Souza Lima      | 2TDSPH   |
| RM558710 | Diego Bassalo               | 2TDSPG   |
