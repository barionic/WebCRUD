# 🔗 WebCRUD - Gerenciador de Links

Aplicação web desenvolvida com **JSF + PrimeFaces** para gerenciamento de URLs e suas respectivas anotações em tópicos.

---

## 🚀 Tecnologias utilizadas

- Java 11
- Jakarta EE 9
- JSF (Jakarta Faces)
- PrimeFaces
- GlassFish 6.1
- PostgreSQL

---

## ⚙️ Pré-requisitos

Antes de rodar o projeto, você precisa ter instalado:

- JDK 11
- GlassFish 6.1 configurado
- PostgreSQL
- IDE (IntelliJ, Eclipse ou VS Code)

---

## 🗄️ Configuração do Banco de Dados (PostgreSQL)

### 1. Acessar o PostgreSQL

    psql -U postgres

---

### 2. Criar o banco de dados

    CREATE DATABASE webcrud;

---

### 3. Criar usuário (opcional)

Caso queira usar um usuário específico:

    CREATE USER webcrud_user WITH PASSWORD '******';
    GRANT ALL PRIVILEGES ON DATABASE webcrud TO webcrud_user;

---

### 4. Criação das tabelas

⚠️ **Importante:**

As tabelas **não precisam ser criadas manualmente**.

O projeto está configurado com:

    <property name="jakarta.persistence.schema-generation.database.action"
              value="create-or-extend-tables"/>

👉 Isso significa que o próprio Hibernate/JPA irá:
- criar as tabelas automaticamente
- atualizar a estrutura conforme necessário

Entidades mapeadas:
- Hiperlink
- Grupo
- Tag

---

## 🔌 Configuração no GlassFish

### 1. Criar JDBC Connection Pool

- Name: `WebCrudPool`
- Resource Type: `javax.sql.DataSource`
- Database Vendor: `PostgreSQL`
- Datasource Classname: `org.postgresql.ds.PGSimpleDataSource`

#### Propriedades:

- User: `postgres` (ou `webcrud_user`)
- Password: `postgres` (ou a definida)
- DatabaseName: `webcrud`
- ServerName: `localhost`
- PortNumber: `5432`

---

### 2. Criar JDBC Resource

- JNDI Name: `jdbc/webcrudDS`
- Pool Name: `WebCrudPool`

---

## ▶️ Como rodar o projeto

1. Clone o repositório:

   git clone https://github.com/SEU_USUARIO/webcrud.git

2. Abra na sua IDE
3. Configure o GlassFish
4. Faça o deploy do projeto

5. Acesse no navegador:

   http://localhost:8080/webcrud

---

## 📌 Funcionalidades

- ✅ Cadastro de links
- ✅ Edição de links
- ✅ Remoção
- ✅ Organização com tópicos
- ✅ @Menções entre link externos e internos
- ✅ Livre ordenação de links
- ✅ Interface com PrimeFaces

---

## 🧙‍♂️ Autor

João Barion