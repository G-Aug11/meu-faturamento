Meu Faturamento — Rick Barber

Sistema de gestão desenvolvido para uma barbearia real, como parte do Projeto Integrador de Extensão do SENAC. Permite registrar atendimentos, gerenciar clientes e serviços, e acompanhar o faturamento por meio de um dashboard.

Tecnologias
Backend: Java 17, Spring Boot 3, Spring Data JPA
Banco de dados: MySQL
Frontend: HTML, CSS, JavaScript (consumindo a API REST do backend)
Funcionalidades
Login com autenticação por token
Cadastro e gestão de clientes
Cadastro e gestão de serviços
Registro de atendimentos com cálculo automático de total
Histórico de atendimentos
Dashboard com resumo do faturamento
Como rodar o projeto localmente
Clone o repositório
Crie o banco de dados executando o database.sql
Configure src/main/resources/application.properties com as credenciais do seu MySQL local
Rode o projeto (via Eclipse/Spring Tools ou mvn spring-boot:run)
Acesse http://localhost:8080

## Microsserviço de agendamentos (`agendamento-service/`)

Aplicação Spring Boot separada, que roda na porta **8081** com banco próprio (`agendamento_db`, criado sozinho na primeira execução).
Ela não acessa o banco do meu-faturamento: clientes, serviços e a validação do login são consultados **via HTTP** na API do sistema principal.

Pra rodar:
1. Ligar o MySQL do XAMPP
2. Rodar o `meu-faturamento` (porta 8080)
3. No Eclipse: File > Import > Existing Maven Projects > pasta `agendamento-service` e rodar `AgendamentoServiceApplication`
4. Acessar `http://localhost:8080/agendamentos.html`
