HOST ?= 3.236.181.241
SSH_USER ?= ubuntu
DOCKERHUB_USERNAME ?= gabrielca5
MSG ?= atualiza
SSH = ssh -i "$(KEY)" -o StrictHostKeyChecking=accept-new $(SSH_USER)@$(HOST)

.PHONY: help db setup-aws secrets deploy check logs ssh reset-db

help:
	@echo "Uso (KEY = caminho do .pem):"
	@echo "  make db                  sobe o Postgres local"
	@echo "  make setup-aws KEY=...   instala o Docker na maquina AWS (1 vez)"
	@echo "  make secrets KEY=...     cadastra os 4 secrets no GitHub (precisa do gh)"
	@echo "  make deploy MSG='texto'  commit + push na main (dispara o deploy)"
	@echo "  make check               testa o back local e o da AWS (esperado: 401)"
	@echo "  make logs KEY=...        logs do back na AWS"
	@echo "  make ssh KEY=...         abre um terminal na AWS"
	@echo "  make reset-db KEY=...    apaga o banco da AWS (use se mudar campos)"

db:
	docker compose up -d

setup-aws: need-key
	$(SSH) "curl -fsSL https://get.docker.com | sudo sh && sudo docker run --rm hello-world"

secrets: need-key
	gh secret set HOST --body "$(HOST)"
	gh secret set DOCKERHUB_USERNAME --body "$(DOCKERHUB_USERNAME)"
	gh secret set SSH_KEY < "$(KEY)"
	@read -s -p "Docker Hub token: " t; echo; gh secret set DOCKERHUB_TOKEN --body "$$t"
	gh secret list

deploy:
	git add -A
	git commit -m "$(MSG)" || true
	git push -u origin main
	@echo "Acompanhe em: $$(git remote get-url origin | sed 's/\.git$$//')/actions"

check:
	@echo "local:"; curl -s -o /dev/null -w "  %{http_code}\n" http://localhost:8080/avaliacoes || true
	@echo "aws:";   curl -s -m 10 -o /dev/null -w "  %{http_code}\n" http://$(HOST):8080/avaliacoes || echo "  sem resposta (Security Group sem a porta 8080 ou deploy nao terminou)"

logs: need-key
	$(SSH) "sudo docker logs --tail 100 pf-back"

ssh: need-key
	$(SSH)

reset-db: need-key
	$(SSH) "sudo docker rm -f postgres pf-back; sudo docker volume rm pgdata"
	@echo "Rode 'make deploy' para recriar tudo."

need-key:
	@test -n "$(KEY)" || (echo "Passe KEY=caminho/da/chave.pem"; exit 1)
