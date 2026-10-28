# Analisador Sintático Jack

## 1. Integrantes

- **Integrante 1:** Adrielle Campelo Cunha Cajado — Matrícula: 20250013499.
- **Integrante 2:** Gabriela Florêncio da Silva — Matrícula: 20240065491.

## 2. Linguagem utilizada

O projeto foi desenvolvido em **Java**, utilizando as classes `Scanner`, `Parser`, `Token` e `XmlWriter` para realizar a análise léxica e sintática de programas escritos na linguagem Jack.

O analisador recebe arquivos `.jack`, identifica seus tokens e constrói a árvore sintática correspondente, representando os resultados em arquivos XML.

## 3. Requisitos

- JDK instalado e configurado.
- IntelliJ IDEA ou outra IDE Java, opcionalmente.

## 4. Como executar

### Pelo IntelliJ IDEA

1. Abra o projeto na IDE.
2. Localize a classe `Main`.
3. Abra **Run → Edit Configurations**.
4. Selecione a configuração de execução da classe `Main`.
5. No campo **Program arguments**, informe o caminho do arquivo `.jack` ou da pasta que contém os arquivos `.jack`, seguido da pasta de saída.

Exemplo para processar todos os arquivos da pasta `tests/Square`:

```text
jackcompiler/tests/Square output
```

Esse exemplo considera que o diretório de trabalho está configurado na raiz do projeto que contém a pasta `jackcompiler`.

Para processar apenas um arquivo:

```text
jackcompiler/tests/Square/Main.jack output
```

Execute a classe `Main`. Os arquivos XML serão gravados na pasta de saída informada.

### Pelo terminal

A partir da raiz do projeto, compile os arquivos Java:

```bash
javac -d out src/*.java
```

Depois, execute:

```bash
java -cp out Main tests/Square output
```


## 5. Exemplo de uso

Para processar os arquivos `.jack` da pasta `tests/Square`:

```text
jackcompiler/tests/Square output
```

O programa identifica os arquivos com extensão `.jack` presentes diretamente na pasta informada e gera dois arquivos XML para cada arquivo de entrada.

Por exemplo, para `Main.jack`, são produzidos:

- `MainT.xml`: representação XML dos tokens identificados pelo analisador léxico.
- `Main.xml`: representação XML da árvore sintática construída pelo analisador sintático.

## 6. Arquivos de saída XML

O nome dos arquivos XML é definido a partir do nome do arquivo `.jack` de entrada.

Para cada arquivo processado, são gerados:

| Arquivo de entrada | XML dos tokens | XML da árvore sintática |
|---|---|---|
| `Main.jack` | `MainT.xml` | `Main.xml` |
| `Square.jack` | `SquareT.xml` | `Square.xml` |
| `SquareGame.jack` | `SquareGameT.xml` | `SquareGame.xml` |

O sufixo `T` identifica o arquivo XML dos tokens. O arquivo sem esse sufixo contém a árvore sintática produzida pelo parser.

Por padrão, os arquivos são salvos na pasta `output`, que pode ser alterada pelo segundo argumento da execução.


## 7. Desafios enfrentados

Durante o desenvolvimento, um dos desafios foi implementar o parser por meio da técnica de descida recursiva, reconhecendo as estruturas sintáticas da linguagem Jack e respeitando sua gramática, como também, organizar a geração da árvore sintática em XML.
