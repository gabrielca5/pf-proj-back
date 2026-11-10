#!/usr/bin/env bash
# Uso: bash run.sh <comando> [argumento]
# O argumento e o caminho do .pem (comandos de AWS) ou a mensagem do commit (deploy).

HOST="${HOST:-3.236.181.241}"
SSH_USER="${SSH_USER:-ubuntu}"
DOCKERHUB_USERNAME="${DOCKERHUB_USERNAME:-gabrielca5}"
ARG="$2"

ssh_aws() {
  [ -n "$ARG" ] || { echo "Passe o caminho do .pem: bash run.sh $1 caminho/chave.pem"; exit 1; }
  ssh -i "$ARG" -o StrictHostKeyChecking=accept-new "$SSH_USER@$HOST" "${@:2}"
}

case "$1" in
  db)
    docker compose up -d
    ;;
  setup-aws)
    ssh_aws setup-aws "curl -fsSL https://get.docker.com | sudo sh && sudo docker run --rm hello-world"
    ;;
  secrets)
    [ -f "$ARG" ] || { echo "Passe o caminho do .pem: bash run.sh secrets caminho/chave.pem"; exit 1; }
    gh secret set HOST --body "$HOST"
    gh secret set DOCKERHUB_USERNAME --body "$DOCKERHUB_USERNAME"
    gh secret set SSH_KEY < "$ARG"
    read -s -p "Docker Hub token: " token; echo
    gh secret set DOCKERHUB_TOKEN --body "$token"
    gh secret list
    ;;
  deploy)
    git add -A
    git commit -m "${ARG:-atualiza}" || true
    git push -u origin main
    echo "Acompanhe em: $(git remote get-url origin | sed 's/\.git$//')/actions"
    ;;
  check)
    echo "local:"; curl -s -o /dev/null -w "  %{http_code}\n" http://localhost:8080/avaliacoes || true
    echo "aws:";   curl -s -m 10 -o /dev/null -w "  %{http_code}\n" "http://$HOST:8080/avaliacoes" \
      || echo "  sem resposta (Security Group sem a porta 8080 ou deploy nao terminou)"
    ;;
  logs)
    ssh_aws logs "sudo docker logs --tail 100 pf-back"
    ;;
  ssh)
    ssh_aws ssh
    ;;
  reset-db)
    ssh_aws reset-db "sudo docker rm -f postgres pf-back; sudo docker volume rm pgdata"
    echo "Rode 'bash run.sh deploy' para recriar tudo."
    ;;
  *)
    echo "Uso: bash run.sh <comando> [argumento]"
    echo "  db                       sobe o Postgres local"
    echo "  setup-aws <chave.pem>    instala o Docker na maquina AWS (1 vez)"
    echo "  secrets <chave.pem>      cadastra os 4 secrets no GitHub (precisa do gh)"
    echo "  deploy ['mensagem']      commit + push na main (dispara o deploy)"
    echo "  check                    testa o back local e o da AWS (esperado: 401)"
    echo "  logs <chave.pem>         logs do back na AWS"
    echo "  ssh <chave.pem>          abre um terminal na AWS"
    echo "  reset-db <chave.pem>     apaga o banco da AWS (use se mudar campos)"
    ;;
esac
