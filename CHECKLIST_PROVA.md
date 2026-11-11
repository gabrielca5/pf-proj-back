# Checklist: resolver um enunciado

Receitas de cada item: `COMO_MEXER.md` (back) e o `COMO_MEXER.md` do front.
Logins e IPs: `../logins.txt`.

Dica de tempo: cada deploy leva uns 3 minutos. Faça o back, suba, e mexa no front enquanto o job roda.

## 0. Ler o enunciado (2 minutos)

Preencha antes de tocar no codigo:

| Pergunta | Resposta |
|---|---|
| Nome da entidade e da rota (ex: Tarefa, /tarefas) | |
| Campos, com tipo e se sao obrigatorios | |
| Quais rotas existem (metodo e caminho) | |
| Role de cada rota (ADMIN, USER, os dois, qualquer logado) | |
| Regras de negocio (o que o back preenche, valida, recusa) | |
| Filtros ou rotas extras (ex: so as minhas) | |
| Fila: quando envia, o que o consumidor faz | |
| O que o front precisa mostrar ou fazer | |

## 1. Back

- [ ] Renomear a pasta `avaliacao` e as 4 classes para a entidade pedida
- [ ] Entidade: campos novos, validacoes (`@NotBlank`, `@Min`, `@Email`), campos que o back preenche
- [ ] Controller: `@RequestMapping("/rota")`, um metodo por rota pedida
- [ ] Controller: `@PreAuthorize` em cada rota (`hasRole('ADMIN')`, `hasAnyRole('ADMIN', 'USER')`)
- [ ] Service: regras de negocio, erros com `ResponseStatusException` (400, 403, 404, 409)
- [ ] Repository: `findBy...` se pedir filtro
- [ ] Rota nova (filtro, editar, excluir): copiar da receita ou da pasta `generico/`
- [ ] Fila, se pedir: mensagem no service, o que o `Consumidor` faz, fila nova em `FilaConfig`
- [ ] Teste: ajustar `...ServiceTest` (campos, `@Mock Produtor`). Se nao compilar, o deploy inteiro falha
- [ ] Apagar a pasta que nao vai usar (`generico/` ou a antiga), senao sobem rotas que o enunciado nao pediu
- [ ] Se renomeou campo ou tabela: depois do deploy, `bash run.sh reset-db ../projsoft26b.pem` e deploy de novo

## 2. Subir o back

- [ ] Testar local se der tempo (Docker Desktop aberto, `bash run.sh db`, Run no `PfApplication.java`)
- [ ] `bash run.sh deploy "mensagem"`
- [ ] `gh run watch` ate ficar verde
- [ ] `bash run.sh check`: a linha `aws:` tem que mostrar 401
- [ ] Se vermelho: `gh run view --log-failed | tail -40`
  - "Build e testes": erro de compilacao ou teste, o log mostra a linha
  - "Deploy na AWS" com `no space left`: `bash run.sh prune ../projsoft26b.pem` e `gh run rerun --failed`

## 3. Front

- [ ] `src/config.js`: `API = '/api/rota'`
- [ ] `src/App.jsx`: `VAZIO` com os campos novos
- [ ] `<form>`: um `<input>` por campo
- [ ] `<table>`: um `<th>` e um `<td>` por campo
- [ ] Botoes extras (excluir, editar) e quem ve cada um (`admin`, `roles.includes(...)`)
- [ ] `npm run dev` e testar com ADMIN e USER (o proxy ja aponta para a AWS)
- [ ] `git add -A && git commit -m "front" && git push` (a Vercel publica sozinha)

## 4. Auth0 (so se pedir role ou usuario novo)

- [ ] User Management, Roles: criar a role (maiusculas, igual ao `hasRole`)
- [ ] User Management, Users: atribuir a role ao usuario (aba Roles)
- [ ] Sair e entrar de novo no front: o token antigo nao tem a role nova

## 5. Conferencia final

Teste nos dois usuarios, no site da Vercel (nao so no localhost):

- [ ] ADMIN faz tudo o que o enunciado pede
- [ ] USER faz so o que pode, e recebe 403 no resto
- [ ] Sem login: 401
- [ ] Dado invalido: 400 com a mensagem do campo
- [ ] Fila, se pedida: `bash run.sh logs ../projsoft26b.pem` mostra `Mensagem recebida`
- [ ] Os dois repositorios com push feito: `git status` sem "ahead"
- [ ] Entrega pede link ou repositorio: front `https://pf-proj-front.vercel.app`, repos `github.com/gabrielca5/pf-proj-back` e `pf-proj-front`

## Se algo der errado

| Sintoma | Primeiro passo |
|---|---|
| 401 depois do login | issuer ou audience em `application.properties`; sair e entrar de novo |
| 403 | role do usuario no Auth0, ou `@PreAuthorize` da rota |
| 400 | validacao da entidade (a resposta diz o campo) |
| 502 na tela | back fora do ar: `bash run.sh check`, depois `logs` |
| Tela em branco | F12, aba Console |
| Login da Vercel com `callback mismatch` | URL do site nos 3 campos da aplicacao PF no Auth0 |
| Coluna antiga no banco | `bash run.sh reset-db ../projsoft26b.pem` e deploy |
| Deploy vermelho | `gh run view --log-failed | tail -40` |
