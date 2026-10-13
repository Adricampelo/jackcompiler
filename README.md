# Analisador Léxico da Linguagem Jack

## 1. Integrantes

- **Integrante 1:** Adrielle Campelo Cunha Cajado — Matrícula: 20250013499.
- **Integrante 2:** Gabriela Florêncio da Silva — Matrícula: 20240065491.

## 2. Linguagem de programação

O projeto foi desenvolvido em **Java**, utilizando o JDK 17. O objetivo é implementar um analisador léxico para a linguagem Jack, responsável por ler arquivos com extensão `.jack` e identificar os tokens que compõem o código-fonte.

O analisador reconhece as seguintes categorias:

- **Palavras-chave:** `class`, `function`, `method`, `var`, `let`, `if`, `else`, `while`, `return`, entre outras.
- **Identificadores:** nomes de classes, variáveis, métodos e funções.
- **Símbolos:** `{`, `}`, `(`, `)`, `[`, `]`, `+`, `-`, `=`, `;`, entre outros.
- **Constantes inteiras:** números inteiros válidos na linguagem Jack.
- **Constantes de texto:** sequências de caracteres delimitadas por aspas duplas.

O analisador também ignora espaços em branco e comentários de linha (`//`) e de bloco (`/* ... */`).

## 3. Estrutura do projeto

```text
AnalisadorLexico/
├── src/
│   ├── Main.java
│   ├── Scanner.java
│   ├── Token.java
│   ├── TokenType.java
│   └── XmlWrite.java
├── testes/
│   └── Exemplo.jack
└── README.md
```

## 4. Compilação e execução

### Pré-requisitos

- JDK 17 instalado.
- IntelliJ IDEA ou terminal com acesso ao compilador Java.
- Arquivo de entrada com extensão `.jack`.

### Execução pelo IntelliJ IDEA

1. Abra o projeto no IntelliJ IDEA.
2. Verifique se o JDK 17 está configurado.
3. Certifique-se de que as classes `Main`, `Scanner`, `Token` e `TokenType` estão no projeto.
4. Execute a classe `Main`.
5. Informe o caminho do arquivo `.jack` ou da pasta que contém os arquivos de teste como argumento do programa.

## 5. Estratégia de testes adotada

A estratégia de testes consiste em utilizar arquivos `.jack` com diferentes construções da linguagem para verificar o reconhecimento correto dos tokens e o tratamento de situações especiais.


### 5.1 Comparação por XML

Quando o gerador XML está integrado, a saída do analisador é salva em arquivos como `MainT.xml`. Esses arquivos podem ser comparados com os XMLs de referência disponibilizados pelo projeto nand2tetris.

A comparação verifica se os tokens foram identificados na ordem correta, se suas categorias estão corretas e se os lexemas correspondem ao código-fonte. Também é importante verificar a codificação dos caracteres e o formato das tags XML.


## 6. Referências

NAND2TETRIS. *Project 10: Syntax Analysis*. Disponível em: https://www.nand2tetris.org/project10.
NAND2TETRIS. *Software*. Disponível em: https://www.nand2tetris.org/software.
