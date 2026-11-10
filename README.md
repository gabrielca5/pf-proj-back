# pf-proj-back

API de avaliações. Spring Boot (Java 25, Maven), Postgres, Auth0, deploy na AWS via GitHub Actions.

| Rota | Quem pode |
|---|---|
| `POST /avaliacoes` | ADMIN |
| `GET /avaliacoes` | ADMIN e USER |

## Onde fica cada coisa

| Arquivo | O que tem |
|---|---|
| `avaliacao/Avaliacao.java` | campos da entidade e validações |
| `avaliacao/AvaliacaoController.java` | rotas e quem pode chamar (`@PreAuthorize`) |
| `avaliacao/AvaliacaoService.java` | regra de negócio (preenche avaliador e data) |
| `avaliacao/AvaliacaoRepository.java` | acesso ao banco |
| `generico/` | modelo com CRUD completo em `/itens` (ver seção 7) |
| `SecurityConfig.java` | exige token em tudo e lê as roles do token |
| `application.properties` | banco e Auth0 |
| `.github/workflows/deploy.yml` | build, imagem no Docker Hub, deploy por SSH |

## 1. Auth0 do zero (uma vez só)

1. Crie a conta e o tenant em auth0.com.
2. **Applications > APIs > Create API**: Name `pf-api`, Identifier `https://pf-api`.
3. **Applications > Applications > Create Application > Single Page Web Applications**. Em Settings, preencha os três campos abaixo com `http://localhost:5173, https://SEU-FRONT.vercel.app` e salve:
   - Allowed Callback URLs
   - Allowed Logout URLs
   - Allowed Web Origins
4. Anote o **Domain** e o **Client ID** dessa aplicação.
5. **User Management > Roles**: crie `ADMIN` e `USER`.
6. **User Management > Users**: crie dois usuários e dê uma role para cada (aba Roles do usuário).
7. **Actions > Library > Create Action > Build from scratch**, trigger **Login / Post Login**, com este código:

```js
exports.onExecutePostLogin = async (event, api) => {
  const roles = event.authorization?.roles ?? [];
  api.accessToken.setCustomClaim('https://pf/roles', roles);
  api.accessToken.setCustomClaim('https://pf/email', event.user.email);
  api.idToken.setCustomClaim('https://pf/roles', roles);
};
```

8. Clique em **Deploy**. Depois, em **Actions > Triggers > post-login**, arraste a Action para o fluxo e clique em **Apply**. Sem esse passo o token vem sem roles e tudo dá 403.
9. Coloque o Domain em `application.properties` (back) e Domain e Client ID em `src/config.js` (front), no lugar de `TROCAR`.

## 2. Rodar local

```bash
docker compose up -d     # sobe o Postgres
mvn spring-boot:run      # back em http://localhost:8080
```

## 3. Máquina AWS (uma vez só)

1. No Security Group, libere as portas 22 e 8080 para qualquer origem.
2. Instale o Docker:

```bash
ssh -i chave.pem ubuntu@3.236.181.241
curl -fsSL https://get.docker.com | sudo sh
```

Se a máquina for Amazon Linux, o usuário é `ec2-user`: troque `username` no `deploy.yml`.

## 4. Secrets do GitHub

Atalho: `bash run.sh secrets caminho/chave.pem` (precisa do `gh` logado). `bash run.sh` sem argumentos lista os demais comandos.

Em **Settings > Secrets and variables > Actions** do repositório:

| Secret | Valor |
|---|---|
| `DOCKERHUB_USERNAME` | seu usuário do Docker Hub |
| `DOCKERHUB_TOKEN` | access token do Docker Hub |
| `HOST` | `3.236.181.241` |
| `SSH_KEY` | conteúdo inteiro do `.pem` |

Todo push na `main` faz o build, roda os testes e publica na máquina. O Postgres (container `pf-db`) sobe sozinho no primeiro deploy.

Se o IP da máquina mudar, atualize o secret `HOST` e o `vercel.json` do front.

## 5. Testar

Sem token tem que dar 401:

```bash
curl -i http://3.236.181.241:8080/avaliacoes
```

Com token (pegue no front, aba Network, header `Authorization`):

```bash
curl -i http://3.236.181.241:8080/avaliacoes -H "Authorization: Bearer TOKEN"
```

Logs na máquina: `sudo docker logs pf-back`.

## 6. Na prova: trocar a entidade

Exemplo: o enunciado pede `Tarefa` em `/tarefas`.

1. Renomeie a pasta `avaliacao` e as quatro classes (`Tarefa`, `TarefaController`, `TarefaService`, `TarefaRepository`). No IntelliJ: Shift+F6 em cada uma.
2. `Tarefa.java`: troque os campos e as validações.
3. `TarefaController.java`: troque o `@RequestMapping("/tarefas")` e ajuste os `@PreAuthorize`:
   - só ADMIN: `hasRole('ADMIN')`
   - ADMIN e USER: `hasAnyRole('ADMIN', 'USER')`
4. `TarefaService.java`: ajuste o que o back preenche sozinho (email de quem está logado, data).
5. `TarefaServiceTest.java`: ajuste os campos usados nos testes, senão o deploy falha no build.
6. Rota nova (por exemplo `DELETE /tarefas/{id}`): copie um método do controller e use `repository.deleteById(id)` no service.
7. Front: veja o README do `pf-proj-front`.

Se mudar o nome de um campo e o banco reclamar de coluna antiga, apague o volume na máquina:

```bash
sudo docker rm -f pf-db pf-back && sudo docker volume rm pf-pgdata
```

e rode o deploy de novo.

## 7. Modelo genérico (`generico/`)

`Item` em `/itens`, com tudo o que o simulado não pediu mas a prova pode pedir:

| Rota | Quem pode | Para copiar quando pedirem |
|---|---|---|
| `GET /itens` | ADMIN e USER | listar tudo |
| `GET /itens/meus` | ADMIN e USER | listar só os do usuário logado |
| `GET /itens/{id}` | ADMIN e USER | buscar um, com 404 se não existir |
| `POST /itens` | ADMIN | cadastrar |
| `PUT /itens/{id}` | ADMIN | editar |
| `DELETE /itens/{id}` | ADMIN | excluir |

Dois jeitos de usar:

- **Enunciado parecido com o simulado** (cadastrar e listar): adapte `avaliacao/` e copie de `generico/` só o método que faltar.
- **Enunciado com CRUD completo**: renomeie `generico/` e `Item` para a entidade pedida e apague `avaliacao/` (e o teste dela).

Antes de entregar, apague a pasta que não usou, para não subir rota que o enunciado não pede.
