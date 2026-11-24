Experimento SRE - Arquitetura de Monitoramento (Prime Video 2023)
Miniatura em Java 17, Spring Boot 4 e Docker Compose para comparar um fluxo distribuido (orchestrator, storage, detector-a, detector-b) com um fluxo consolidado em memoria (monolith), alem de testar agrupamento por lote (batchSize) como mitigacao de I/O e transicoes.

Estrutura do Repositorio:
sre-prime-video/
app/                  Aplicacao Spring Boot e Dockerfile
logs/                 Evidencias brutas das execucoes
docker-compose.yml    Perfis distributed e monolith
IA.md                 Registro de uso de IA e erros encontrados
README.md            

Guia de execucao:

Pre-requisitos - Docker e Docker Compose instalados e em execucao.
PowerShell (ou curl / cliente HTTP) para enviar as requisicoes de teste.

Como Executar os Cenarios: 
Execute os comandos a partir da pasta raiz (sre-prime-video/):  

Cenario Monolito (monolith)
Subir o servico:docker compose --profile monolith up --build -d
Verificar saude:curl http://localhost:8080/api/healthEnviar 
requisicao de exemplo no PowerShell:$body = @{ frameId = "frame-1"; payload = ("FRAME_DATA_" * 200); batchSize = 1 } | ConvertTo-JsonInvoke-RestMethod -Uri "http://localhost:8080/api/frames" -Method Post -ContentType "application/json" -Body $body
Encerrar o cenario:docker compose --profile monolith down

Cenario Distribuido (distributed)
Subir os servicos:docker compose --profile distributed up --build -d
Verificar saude do orquestrador:curl http://localhost:8080/api/health
Enviar requisicao de exemplo no PowerShell (com batchSize = 1 ou batchSize = 10):$body = @{ frameId = "frame-1"; payload = ("FRAME_DATA_" * 200); batchSize = 10 } | ConvertTo-JsonInvoke-RestMethod -Uri "http://localhost:8080/api/frames" -Method Post -ContentType "application/json" -Body $body
Encerrar o cenario:docker compose --profile distributed down

Evidencias e Logs - 
As saidas e resumos das execucoes ficam salvos na pasta logs/