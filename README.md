# Analisador Sintático Jack

## 1. Integrantes

- **Integrante 1:** Adrielle Campelo Cunha Cajado — Matrícula: 20250013499.
- **Integrante 2:** Gabriela Florêncio da Silva — Matrícula: 20240065491.

## 2. Descrição do projeto

Este projeto consiste no desenvolvimento de um analisador léxico e sintático para a linguagem Jack, utilizando a linguagem de programação **Java**.

O analisador léxico é responsável por identificar e classificar os elementos que compõem o código-fonte, como palavras-chave, símbolos, identificadores, constantes inteiras e constantes de texto.

Em seguida, o analisador sintático utiliza os tokens produzidos para verificar a estrutura do programa de acordo com a gramática da linguagem Jack. Essa etapa é implementada por meio da técnica de **descida recursiva**, com métodos específicos para reconhecer as diferentes regras gramaticais.

Ao final do processamento, são gerados arquivos XML contendo a representação dos tokens identificados e da estrutura sintática reconhecida.

## 3. Tecnologias e requisitos

- **Linguagem:** Java.
- **JDK:** instalado e configurado no sistema.
- **IDE:** IntelliJ IDEA ou outra IDE Java (opcional).
- **Terminal:** PowerShell, Prompt de Comando ou terminal compatível.

O projeto utiliza as classes `Scanner`, `Token`, `TokenType`, `Parser`, `XmlWriter`, `Main` e `TestRunner`, responsáveis pelas etapas de análise, geração de XML, execução e validação dos testes.

## 4. Estrutura do projeto

A organização dos arquivos está dividida da seguinte forma:

```text
jackcompiler/
│
├── src/
│   ├── Main.java
│   ├── Scanner.java
│   ├── Token.java
│   ├── TokenType.java
│   ├── Parser.java
│   ├── XmlWriter.java
│   └── TestRunner.java
│
├── tests/
│   ├── ExpressionLessSquare/
│   ├── Square/
│   ├── ArrayTest
    ├── CompararXml.ps1  
│   ├── Exemplo.jack     
│   ├── Teste.jack       
│   ├── TesteErro.jack    
│   └── TesteP.xml
│   
│
├── output/
│   └── tokens/
│
├── .gitignore
└── README.md
```

**Descrição das pastas:**

- `src/`: contém as classes Java responsáveis pela implementação do analisador léxico, do analisador sintático, da geração dos arquivos XML e da execução dos testes automatizados.
- `tests/ArrayTest/`: contém os arquivos do conjunto oficial ArrayTest, utilizados para validar o processamento de programas com arrays.
- `tests/ExpressionLessSquare/`: contém os arquivos do conjunto oficial ExpressionLessSquare, utilizados para validar estruturas sintáticas e expressões.
- `tests/Square/`: contém os arquivos do conjunto oficial Square, utilizados para verificar o processamento de programas Jack.
- `tests/CompararXml.ps1`: script PowerShell utilizado para comparar os arquivos XML sintáticos gerados com os documentos de referência.
- `tests/Exemplo.jack`, `tests/Teste.jack` e `tests/TesteErro.jack`: programas adicionais utilizados para testar diferentes construções da linguagem e o tratamento de erros.
- `output/tokens/`: diretório destinado ao armazenamento dos arquivos XML produzidos pelo analisador léxico.

Os arquivos de entrada dos conjuntos oficiais possuem documentos XML de referência, que permitem verificar se os resultados produzidos pelo analisador correspondem aos resultados esperados.

## 5. Como executar o projeto

Os comandos apresentados nesta seção devem ser executados a partir da **raiz do projeto**, isto é, da pasta `jackcompiler`.

### 5.1. Compilação

Primeiramente, abra o terminal na raiz do projeto e compile os arquivos Java:

```powershell
javac -d out (Get-ChildItem src -Filter *.java).FullName
```

Esse comando compila os arquivos presentes em `src/` e armazena os arquivos `.class` gerados no diretório `out/`.

### 5.2. Execução de um arquivo individual

Para analisar apenas um arquivo `.jack`, execute:

```powershell
java -cp out Main tests/Square/Main.jack
```

### 5.3. Execução de uma pasta

Também é possível informar diretamente uma pasta que contenha arquivos `.jack`. Nesse caso, o programa processa os arquivos encontrados diretamente no diretório informado.

Exemplo:

```powershell
java -cp out Main tests/Square
```

Para executar os outros conjuntos de testes oficiais:

```powershell
java -cp out Main tests/ExpressionLessSquare
```

```powershell
java -cp out Main tests/ArrayTest
```

A execução de uma pasta não inclui automaticamente seus subdiretórios.


## 6. Arquivos de saída XML

Para cada arquivo `.jack` processado, são produzidos dois documentos XML:

- **XML de tokens (`T.xml`):** contém a representação dos tokens identificados pelo analisador léxico.
- **XML sintático (`P.xml`):** contém a representação da estrutura sintática reconhecida pelo parser.

Os arquivos sintáticos são gerados na mesma pasta do arquivo `.jack` correspondente. Já os arquivos de tokens são armazenados em `output/tokens/`, organizados de acordo com o conjunto de testes.

### 6.1. Programas utilizados nos testes oficiais

Os três conjuntos oficiais disponibilizados para validação contêm sete programas:

| Conjunto de testes | Arquivo de entrada | XML de tokens | XML sintático |
|---|---|---|---|
| ExpressionLessSquare | Main.jack | MainT.xml | MainP.xml |
| ExpressionLessSquare | Square.jack | SquareT.xml | SquareP.xml |
| ExpressionLessSquare | SquareGame.jack | SquareGameT.xml | SquareGameP.xml |
| Square | Main.jack | MainT.xml | MainP.xml |
| Square | Square.jack | SquareT.xml | SquareP.xml |
| Square | SquareGame.jack | SquareGameT.xml | SquareGameP.xml |
| ArrayTest | Main.jack | MainT.xml | MainP.xml |

O sufixo `T` identifica os arquivos produzidos pelo analisador léxico, enquanto o sufixo `P` identifica os arquivos produzidos pelo analisador sintático.

## 7. Testes e validação

O projeto possui testes automatizados para verificar o funcionamento do analisador léxico e comparar os resultados gerados com os arquivos XML de referência.

### 7.1. Testes unitários

A classe `TestRunner` contém testes destinados a verificar diferentes funcionalidades do analisador léxico, incluindo:

- Reconhecimento de palavras-chave, identificadores e símbolos.
- Identificação de constantes inteiras e constantes de texto.
- Tratamento de comentários de linha e de bloco.
- Reconhecimento de símbolos utilizados em expressões e arrays.
- Tratamento de caracteres especiais na geração do XML.
- Verificação dos limites de constantes inteiras.
- Identificação de erros em strings, comentários e caracteres inválidos.

Para executar os testes unitários e comparar os resultados com as referências oficiais de `Square`, utilize:

```powershell
java -cp out TestRunner
```

### 7.2. Comparação com os arquivos XML oficiais

O `TestRunner` também permite comparar os arquivos XML de tokens gerados com os documentos de referência disponibilizados nos conjuntos oficiais.

Para executar a validação de cada conjunto, utilize:

```powershell
java -cp out TestRunner tests/Square
```

```powershell
java -cp out TestRunner tests/ExpressionLessSquare
```

```powershell
java -cp out TestRunner tests/ArrayTest
```

A comparação verifica se os documentos gerados correspondem aos arquivos XML esperados, permitindo identificar diferenças na identificação e classificação dos tokens.

Os arquivos XML sintáticos também são validados por meio da comparação com as referências oficiais dos sete programas utilizados nos testes.


## 8. Desafios enfrentados

Durante o desenvolvimento, um dos principais desafios foi implementar o analisador sintático utilizando a técnica de descida recursiva, de modo que cada método fosse responsável por reconhecer uma regra específica da gramática Jack.

Também foi necessário desenvolver mecanismos de leitura antecipada de tokens (*lookahead*) para distinguir construções sintáticas semelhantes, como acessos a variáveis, elementos de arrays e chamadas de sub-rotinas.Outro desafio consistiu em integrar as etapas léxica e sintática, garantindo que os tokens fossem corretamente identificados e utilizados na construção da árvore sintática representada em XML.
