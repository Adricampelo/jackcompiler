# Jack Compiler — Analisador Léxico e Sintático

Projeto desenvolvido em Java para realizar a análise léxica e sintática da linguagem Jack, utilizada no curso Nand2Tetris.

## Funcionalidades

- Identificação de tokens da linguagem Jack.
- Reconhecimento de palavras-chave, símbolos, identificadores, números e strings.
- Análise sintática de classes, variáveis, sub-rotinas, comandos e expressões.
- Geração de arquivos XML.
- Identificação de erros sintáticos com linha e coluna.
- Execução de testes com programas oficiais do Nand2Tetris.

## Tecnologias

- Java
- XML
- Git e GitHub
- PowerShell para automação dos testes

## Estrutura do projeto

```text
jackcompiler/
├── src/
│   ├── Main.java
│   ├── Parser.java
│   ├── Scanner.java
│   ├── Token.java
│   ├── TokenType.java
│   ├── XmlWriter.java
│   └── TestRunner.java
├── tests/
│   ├── ArrayTest/
│   ├── ExpressionlessSquare/
│   ├── Square/
│   ├── CompararXml.ps1
│   ├── Teste.jack
│   └── TesteErro.jack
└── README.md
```

## Como compilar

É necessário ter o JDK 16 ou superior instalado.

No terminal, execute:

```powershell
javac -d out src/*.java
```

## Como executar

Para analisar um arquivo Jack:

```powershell
java -cp .\out Main .\tests\Teste.jack .\output
```

Para analisar uma pasta:

```powershell
java -cp .\out Main .\tests\Square .\output\Square
```

## Arquivos gerados

Para cada arquivo Jack, são gerados dois arquivos XML:

- `NomeT.xml`: contém os tokens identificados pelo analisador léxico.
- `Nome.xml`: contém a estrutura sintática produzida pelo parser.

## Testes

O projeto utiliza programas de teste do Nand2Tetris.

Para executar a comparação automatizada dos XMLs, utilize:

```powershell
powershell -ExecutionPolicy Bypass -File .\tests\CompararXml.ps1
```

## Tratamento de erros

O analisador identifica erros léxicos e sintáticos, apresentando mensagens com a localização do problema.

Exemplo:

```text
Erro sintático na linha 5, coluna 9
Esperado: SEMICOLON
Encontrado: RETURN ('return')
```

## Limitações

O projeto realiza análise léxica e sintática. Não realiza geração de código VM nem executa programas Jack.

## Referência

[Nand2Tetris — Projeto 10](https://www.nand2tetris.org/project10)
