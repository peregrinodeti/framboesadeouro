# Prêmio Framboesa de Ouro
## _Manual de operação e configuração do sistema_

## Sobre o Projeto

Este projeto visa listar os filmes que foram indicados ao prêmio da idústria de cinema chamado Framboesa de Ouro, durante os anos de 1980 a 2019. Também é possível verificar quais produtores ganharam esse prêmio. Além de permitir realizar operações sobre a base de filmes.

## Configurando o sistema

O sistema em si não necessita de maiores configurações. Entretanto para realizar a carga inicial de informações no banco de dados, é necessário importar os dados de um arquivo CSV. Para isso é necessário configurar uma variável de ambiente conforme o seu sistema operacional. Assim quando a aplicação terminar de subir, os dados para consulta estarão disponíveis.

Crie a seguinte variável de ambiente conforme descrito a seguir:

- **ARQUIVO_CSV**: Essa variável de ambiente "ARQUIVO_CSV" indica o diretório e o nome da fonte de dados em formato ".CSV", arquivo esse que será importada pelo sistema. O diretório deve ser informada junto com o nome do arquivo, como, por exemplo, no caso do Windows seria: **"C:/csv/movielist.csv"**. Caso essa variável de ambiente não seja informada, o sistema irá incializar sem realizar a importação de dados.

## Formato do arquivo CSV para Importação

O arquivo para importação têm a seguinte configuração. Na sua primeira linha consta as seguintes colunas (dados separados com ";"), compondo as informações de um filme:


- **year**: Formato numérico. Valor obrigatório;
- **title**: Formato string. Valor obrigatório;
- **studios**: Formato string. Valor obrigatório;
- **producers**: Formato string, com nomes separados por vírgula e a palavra " and ". Valor obrigatório;
- **winner**: Formato string, com valores "yes", "no" ou "". Campo opcional.

Na sua segunda linha em diante consta as informações dos filmes, conforme o exemplo a seguir:

```
year;title;studios;producers;winner
1980;Can't Stop the Music;Associated Film Distribution;Allan Carr;yes
1980;Cruising;Lorimar Productions, United Artists;Jerry Weintraub;
1980;The Formula;MGM, United Artists;Steve Shagan;
1980;Friday the 13th;Paramount Pictures;Sean S. Cunningham;
1980;The Nude Bomb;Universal Studios;Jennings Lang;
1980;The Jazz Singer;Associated Film Distribution;Jerry Leider;
1980;Raise the Titanic;Associated Film Distribution;William Frye;
1980;Saturn 3;Associated Film Distribution;Stanley Donen;
1980;Windows;United Artists;Mike Lobell;
1980;Xanadu;Universal Studios;Lawrence Gordon;
1981;Mommie Dearest;Paramount Pictures;Frank Yablans;yes
1981;Endless Love;Universal Studios, PolyGram;Dyson Lovell;
1981;Heaven's Gate;United Artists;Joann Carelli;
1981;The Legend of the Lone Ranger;Universal Studios, Associated Film Distribution;Walter Coblenz;
1981;Tarzan, the Ape Man;MGM, United Artists;John Derek;
2019;Cats;Universal Pictures;Debra Hayward, Tim Bevan, Eric Fellner, and Tom Hooper;yes
2019;The Fanatic;Quiver Distribution;Daniel Grodnik, Oscar Generale, and Bill Kenwright;
2019;The Haunting of Sharon Tate;Saban Films;Lucas Jarach, Daniel Farrands, and Eric Brenner;
2019;A Madea Family Funeral;Lionsgate;Ozzie Areu, Will Areu, and Mark E. Swinton;
2019;Rambo: Last Blood;Lionsgate;Avi Lerner, Kevin King Templeton, Yariv Lerner, and Les Weldon;
```

## Iniciando a aplicação

Caso você execute a aplicação via uma IDE como, por exemplo, o Eclipse. Procure a classe **FramboesaDeOuroApplication** que está no pacote **com.premio.cinema**. E via o menu você têm as opções de execução **Run -> Run As -> Java Application** ou **Spring Boot App**.

Você também pode tentar compilar a aplicação via Maven. Para tanto acesse via terminal (shell) do seu sistema operacional a pasta do projeto que contêm o arquivo **mvnw.cmd** e digite os seguintes comandos:
```sh
mvn clean package spring-boot:repackage
java -jar target/framboesadeouro-0.0.1-SNAPSHOT.jar
```

O primeiro comando monta a aplicação, e o segundo inicia a mesma. Ocorrendo tudo bem, sem nenhum erro, a aplicação já estará disponível via navegador para isso basta digitar o endereço: **`http://localhost:8080/`**, para ter acesso a aplicação.

Observação caso ocorra algum problema na compilação do tipo **Fatal error compiling: error: invalid target release: 17**, verifique se a versão instalada do Java é a mesma da versão utilizada pelo Maven para compilar o projeto. Para tanto digite os comandos na shell **java -version** e depois **mvn -version**. Nas saídas que se apresentarem deve constar a versão 17 do Java. Caso haja diferença, verifique se a variável de ambiente do seu sistema operacional como, por exemplo, **JAVA_HOME**, ou outra similar está apontado para o diretório de instalação da versão 17 do Java. 

## Interface banco de dados H2

Uma vez que a aplicação está iniciada é possível também via navegador ter acesso a interface do banco de dados H2. Para tanto, por exemplo, acesse pelo navegador o endereço: **`http://localhost:8080/h2`**. Na tela que se abre existem algumas informações para preencher, elas a princípio já vêm pré-preenchidas com base nas informações da aplicação constantes no arquivo **application.properties**. Segue um exemplo das informações que podem aparecer nesse tela:

- **Saved Settings**: Opção selecionada padrão é Generic H2 (Embedded);
- **Setting Name**: Opção selecionada padrão é Generic H2 (Embedded);
- **Driver Class**: O valor ali é: org.h2.Driver;
- **JDBC URL**: O valor ali pode ser, por exemplo: jdbc:h2:mem:cinema. Sendo que a palavra cinema é o nome do banco de dados em memória, podendo ser qualquer outro nome para a base de dados;
- **User Name**: Nome do usuário de acesso, geralmente é a palavra sa;
- **Password**: O valor deste campo geralmente é vazio.

Com as informações preenchidas é possível testar a comunicação com o banco via a opção **Test Connection**. Para verificar se está tudo configurado corretamente ou ajustar em caso de erro. Estando tudo correto, basta acessar a interface do banco de dados via opção **Connect**. 

## Listagem e operações de filmes

A primitiva **filmes** disponibiliza a edição CRUD dos filmes, através dos verbos HTTP GET, POST, DELETE, PUT e PATCH. O verbo GET irá retornar apenas um registro se informado o **id** do filme (ex.: "/filmes/1f2d7e78-1efc-4176-bb63-d90a73419607"). Já para acessar todos os filmes com o sistema no ar digite o endereço, por exemplo: **`http://localhost:8080/filmes`**. O ano, título, descrição dos estúdios e produtores do filme são dados obrigatórios. Já o dado vencedor é opcional. Exemplo de corpo da primitiva no verbo POST: 
```json
{
	"year": 1980,
	"title": "Can't Stop the Music",
	"studios": "Associated Film Distribution",
	"producers": "Allan Carr",
	"winner": true
}
```

## Listagem de vencedores

Com a aplicação iniciada é possível realizar uma consulta REST via GET, utilizando a diretiva **vencedores**. Assim a partir, por exemplo, do acesso: **`http://localhost:8080/vencedores`**, o sistema terá como saída a listagem dos vencedores do prêmio com o menor e maior intervalo entre as premiações. 

## Organização interna do projeto

A estrutura do projeto está organizado conforme os seguintes pacotes e pastas:

- **com.premio.cinema**: Pacote que contêm a classe principal do projeto;
- **com.premio.cinema.carga.dados**: Pacote que contêm a classe que realiza a importação do arquivo CSV;
- **com.premio.cinema.carga.controller**: Pacote que contêm as classes responsáveis por responder as requisições seja diretamente via navegador ou ferramenta de teste para consultas REST;
- **com.premio.cinema.carga.error**: Pacote que contêm a classe responsável por tratar os erros de requisição via navegador ou das consultas REST;
- **com.premio.cinema.carga.exception**: Pacote que contêm as classes responsáveis por tratar os erros da requisição das consultas REST;
- **com.premio.cinema.modelo**: Pacote que contêm a classe responsável que representa a tabela filme no banco H2, com base na estrutura do arquivo CSV importado;
- **com.premio.cinema.modelo.interno**: Pacote que contêm as classes responsáveis por representar o modelo de resultado da consulta dos vencedores do prêmio Framboesa de Ouro, com base no menor e maior intervalo entre uma e outra premiação;
- **com.premio.cinema.repositorio**: Pacote que contêm a classe responsável for realizar as consultas padrão CRUD no banco de dados, além de consultas específicas;
- **com.premio.cinema.util**: Pacote que contêm a classe responsável por montar toda a estrutura para resposta a consulta de vencedores;

Além disso na pasta **resources**, se encontram os arquivos de configuração da aplicação, de log e arquivos HTML (css, js etc) em geral. O projeto também têm um pacote com um conjunto de classes para realizar testes automatizados também denomiado **com.premio.cinema**, que contêm a classe **FramboesaDeOuroApplicationTests**. 

## Tecnologias Utilizadas

Este exemplo foi desenvolvido utilizando as seguintes tecnologias e dependências listadas no arquivo pom.xml do projeto:

- Banco de dados H2 - Com armazenamento em memória não ficando disponíveis os dados após o fechamento da aplicação;
- Bootstrap - Biblioteca CSS;
- Java versão 17;
- Open CSV - Biblioteca para manipulação de CSV;
- Plug-in RESTer para o Firefox, para teste das requisições via REST;
- Spring Boot;
- Spring Tool Suite 4.13.0.

O sistema foi desenvolvido utilizando a IDE Eclipse versão 2021-12.

## Testes

O sistema possui uma classe de testes chamada **FramboesaDeOuroApplicationTests**, que contêm a simulação das operações básicas de CRUD via REST. Para rodas esses testes, pode-se executar a classe menciona via uma IDE como, por exemplo, o Eclipse na opção de menu **Run -> Run As -> JUnit Test**. Ou via terminal (shell) do seu sistema operacional, na pasta do projeto que contêm o arquivo **mvnw.cmd** e digite os seguintes comandos:

```sh
mvn test -Dtest=FramboesaDeOuroApplicationTests
```
