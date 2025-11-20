# RESTful API Simple Authentication

## 🔹 Descrição
Projeto de uma **API RESTful simples**, desenvolvida em **Kotlin** com **Spring Boot**, focada em **autenticação** e **gerenciamento de usuários**.  
Permite cadastro, login, controle de permissões (admin/usuário comum), banimento temporário e atualização de privilégios.  
A API utiliza **JWT** para autenticação e controle de acesso, garantindo segurança e rastreabilidade das operações.

## 🔹 Funcionalidades Principais
- ✅ Cadastro de usuários (**sign-up**)
- ✅ Login com **username** ou **email** (**sign-in**)
- ✅ Banimento e desbanimento de contas
- ✅ Controle de permissões e cargos
- ✅ Visualização de todas as contas (somente admins)
- ✅ Exclusão de contas (somente admins)

## 🔹 Tecnologias Utilizadas
- **Kotlin**
- **Spring Boot**
- **Spring Security** (JWT)
- **Spring Data JPA** (PostgreSQL)
- **Gradle**

## 🔹 Estrutura de Endpoints (Resumo)

### Auth
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST   | `/auth/sign-up` | Cadastro de usuário |
| POST   | `/auth/sign-in` | Login com username ou email |

### Admin (somente admins)
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET    | `/admin/accounts` | Listar todas as contas |
| PATCH  | `/admin/accounts/role` | Definir ou remover cargo |
| PATCH  | `/admin/accounts/ban/{id}/{duration}/{unit}` | Banir conta |
| PATCH  | `/admin/accounts/unban/{id}` | Desbanir conta |
| DELETE | `/admin/accounts/{id}` | Deletar conta |

## 🔹 Observações
- Projeto desenvolvido em **apenas 2 dias**, do zero em Kotlin e Spring Boot até uma API funcional.
