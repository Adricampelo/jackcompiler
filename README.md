# Compilador Jack — Analisador Léxico, Sintático e Geração de Código VM

## 1. Integrantes

- **Integrante 1:** Adrielle Campelo Cunha Cajado — Matrícula: 20250013499.
- **Integrante 2:** Gabriela Florêncio da Silva — Matrícula: 20240065491.

## 1.1 Vídeo de apresentação

Link do vídeo: [https://drive.google.com/file/d/1QTpvm6lBpQsFhurec8Y50dzUDlTL86Ek/view?usp=sharing]

## 2. Descrição do projeto

Este projeto consiste no desenvolvimento de um compilador para a linguagem Jack, utilizando Java, com base nas etapas propostas pelo curso *Nand2Tetris*.

O compilador realiza as etapas de análise léxica e sintática, gerenciamento de símbolos e geração de código intermediário na linguagem VM (*Virtual Machine*).

O analisador léxico identifica e classifica os elementos do código-fonte, como palavras-chave, símbolos, identificadores, constantes inteiras e constantes de texto. Em seguida, o analisador sintático utiliza os tokens produzidos para verificar a estrutura do programa de acordo com a gramática Jack, por meio da técnica de descida recursiva.

A tabela de símbolos realiza o gerenciamento das variáveis e de seus escopos, armazenando informações como nome, tipo, categoria e índice. A partir dessas informações, o módulo de geração de código traduz as construções da linguagem Jack em instruções VM.

O projeto contempla funcionalidades como declarações de variáveis, expressões, estruturas condicionais e de repetição, chamadas de sub-rotinas, métodos, construtores, objetos, arrays e constantes de texto.


## 3. Tecnologias e requisitos

- **Linguagem de programação:** Java.
- **JDK:** instalado e configurado no sistema.
- **IDEs e editores de código utilizados**: Visual Studio Code (VS Code) e IntelliJ IDEA.
- **Terminal:** PowerShell, Prompt de Comando ou terminal compatível.
- **Ambiente de execução VM:** VM Emulator, disponibilizado pelo projeto Nand2Tetris.


## 4. Estrutura do projeto

A organização geral dos arquivos está dividida entre os diretórios de implementação e os conjuntos de testes:

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
│   ├── CompilationEngine.java
│   ├── SymbolTable.java
│   ├── Kind.java
│   └── VMWriter.java
│
├── tests/
│   ├── Seven/
│   ├── ConvertToBin/
│   ├── Square/
│   ├── Average/
│   ├── Pong/
│   ├── ComplexArrays/
│   ├── VMWriterTest.java
│   ├── SymbolTableTest.java
│   └── ...
│
├── out/
│
├── .gitignore
└── README.md
```

**Descrição dos diretórios:**

- `src/`: contém as classes responsáveis pela implementação do compilador.
- `tests/`: contém os programas Jack utilizados na validação, além das classes de testes automatizados.
- `out/`: diretório utilizado para armazenar os arquivos `.class` gerados durante a compilação Java.

Os arquivos `.vm` são gerados na mesma pasta dos respectivos arquivos `.jack` processados.

## 5. Como executar o projeto

Os comandos desta seção devem ser executados a partir da raiz do projeto, isto é, da pasta `jackcompiler`.

### 5.1. Compilação

Primeiramente, compile os arquivos Java:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem src -Filter *.java).FullName
```

Esse comando compila os arquivos presentes em `src/` e armazena os arquivos `.class` no diretório `out/`.

### 5.2. Execução de um arquivo individual

Para compilar um único arquivo `.jack`, execute:

```powershell
java -cp out Main tests/Seven/Main.jack
```

O compilador processa o arquivo informado e gera o respectivo arquivo `.vm` na mesma pasta.

### 5.3. Execução de um diretório

Também é possível informar diretamente um diretório que contenha arquivos `.jack`:

```powershell
java -cp out Main tests/Seven
```

Para processar os demais conjuntos de testes:

```powershell
java -cp out Main tests/ConvertToBin
java -cp out Main tests/Square
java -cp out Main tests/Average
java -cp out Main tests/Pong
java -cp out Main tests/ComplexArrays
```

Quando um diretório é informado, o compilador processa os arquivos `.jack` encontrados diretamente nele. A execução não inclui automaticamente seus subdiretórios.


## 6. Testes e validação

A validação do projeto é realizada por meio de testes automatizados e da compilação e execução dos programas no VM Emulator.

### 6.1. Testes unitários

O projeto possui testes automatizados para verificar o funcionamento dos componentes responsáveis pelo gerenciamento de símbolos e pela geração de instruções VM.

**VMWriterTest:** verifica a geração de diferentes instruções VM, incluindo operações de pilha, operações aritméticas, chamadas de funções, rótulos e desvios.

**SymbolTableTest:** verifica o gerenciamento de símbolos, incluindo categorias de variáveis, atribuição de índices, escopos e identificação de símbolos não declarados.

Resultados dos testes executados:

| Teste | Aprovados | Reprovados |
|---|---:|---:|
| VMWriterTest | 17 | 0 |
| SymbolTableTest | 7 | 0 |
| **Total** | **24** | **0** |

Para executar os testes, após compilar as classes correspondentes, utilize:

```powershell
java -cp out VMWriterTest
java -cp out SymbolTableTest
```

### 6.2. Validação dos programas do Project 11

Foram utilizados os seis programas oficiais do Project 11 para avaliar a geração de código VM e o comportamento dos programas no ambiente de execução.

| Programa | Geração do arquivo VM | Execução no VM Emulator |
|---|---|---|
| Seven | Concluída | Aprovada — resultado 7 |
| ConvertToBin | Concluída | Não confirmada |
| Square | Concluída | Não confirmada |
| Average | Concluída | Não confirmada |
| Pong | Concluída | Não confirmada |
| ComplexArrays | Concluída | Não confirmada |

A geração do arquivo `.vm` confirma que o compilador processou o respectivo código-fonte. Entretanto, a validação completa também depende da execução no VM Emulator.

No programa Seven, foi confirmada a execução com resultado igual a `7`, demonstrando a tradução e execução correta da expressão aritmética utilizada no exemplo.

**Observação:** os resultados da tabela devem ser atualizados após a execução e verificação dos programas restantes no VM Emulator.

## 7. Desafios enfrentados e aprendizados

Durante o desenvolvimento, um dos principais desafios foi implementar o analisador sintático por meio da técnica de descida recursiva, garantindo que cada método reconhecesse corretamente uma regra da gramática Jack.

Na etapa de geração de código, foi necessário integrar o analisador sintático à tabela de símbolos e ao gerador de instruções VM. Essa integração definiu a junção dos diferentes escopos, categorias e índices das variáveis.

Outro desafio foi implementar a tradução de estruturas de controle, chamadas de sub-rotinas, métodos e construtores, considerando as particularidades da máquina virtual, como a utilização da pilha, dos segmentos de memória e dos ponteiros.

## 8. Uso de Inteligência Artificial

Durante o desenvolvimento do projeto, foi utilizada a ferramenta **ChatGPT** e **Claude** como apoio às atividades de implementação e documentação.

As ferramentas foram utilizadas para auxiliar na compreensão de conceitos relacionados a compiladores, esclarecer dúvidas sobre a linguagem Java, apoiar na análise de erros de compilação e depuração de trechos de código e orientar a elaboração de testes e documentação.

As sugestões fornecidas foram analisadas pela equipe, e sua aplicação foi verificada por meio da compilação do projeto, da execução dos testes automatizados e da validação dos programas no VM Emulator.

A utilização da ferramenta ocorreu como apoio ao processo de desenvolvimento, sendo a análise, integração e validação das implementações realizadas pela equipe.


