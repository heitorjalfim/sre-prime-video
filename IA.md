Principais Prompts Utilizados e Decisões Tomadas

Prompt 1: Estruturação inicial do projeto e DockerfileResumo do pedido: Solicitação da estrutura de pastas na raiz (app, logs, docker-compose.yml, README.md, IA.md) e do Dockerfile multi-stage para Spring Boot.
O que foi aceito: A separação em estágios de build (Maven) e execução (JRE Alpine) e a organização de pastas.
O que foi mudado: A versão do Java no Dockerfile foi ajustada de Java 21 para Java 17 para alinhar com a tag java.version definida no pom.xml do projeto.


Prompt 2: Implementação das classes Java para os modos distribuído e monolitoResumo do pedido: Geração das classes MonitorConfig, FrameRequest, ProcessResponse, MonitorService e MonitorController em Java/Spring Boot.
O que foi aceito: A arquitetura baseada em uma única imagem Docker assumindo diferentes papéis via variável de ambiente APP_ROLE (monolith, orchestrator, storage, detector-a, detector-b) e o suporte ao parâmetro batchSize para testar mitigação.
O que foi mudado/descartado: Ajustes na organização dos arquivos no pacote com.fccpd.app e adição manual do MonitorController após validar a compilação das classes de serviço e modelo.

Prompt 3: Configuração do docker-compose.yml com perfis isoladosResumo do pedido: Criação do arquivo docker-compose.yml separando o ambiente distribuído e o monolítico.
O que foi aceito: O uso da diretiva profiles ("distributed" e "monolith") para permitir subir e derrubar cada topologia de forma independente na mesma porta externa (8080).

Prompt 4: Comandos de teste de carga e geração de logs no PowerShellResumo do pedido: Criação de scripts em PowerShell para disparar requisições HTTP POST em lote, medir tempo total, vazão e custo estimado, e salvar os resultados brutos na pasta logs/.O que foi aceito: O uso de Invoke-RestMethod com Stopwatch e exportação em JSON acrescida da linha de resumo (SUMMARY) para garantir rastreabilidade literal dos números no laudo.

Erros Encontrados na Prática e Como Foram Corrigidos:

Erro 1: Aviso de propriedade desconhecida no application.properties
Sintoma: O VS Code destacou app.role, storage.url, detector.a.url e detector.b.url como propriedades desconhecidas.   
Causa: As propriedades haviam sido declaradas antes da criação da classe anotada com @ConfigurationProperties(prefix = "app") e antes da indexação dos metadados pelo Spring Boot Language Server.
Correção: Criação da classe MonitorConfig com @ConfigurationProperties(prefix = "app") e validação via compilação Maven.

Erro 2: Falha de resolução do tipo ParameterizedTypeReference no VS Code
Sintoma: A IDE acusou erro na chamada restClient.get()...body(String.class), informando ausência do tipo ParameterizedTypeReference no classpath.   
Causa: Dessincronização do servidor de linguagem Java no VS Code após a inclusão das novas classes e dependências do Spring Boot.   
Correção: Execução do comando mvnw.cmd test-compile no terminal para validar que o Maven compilava normalmente e limpeza/atualização do workspace Java no VS Code.