public class Parser {

    private final Scanner scan;
    private Token currentToken;
    private Token peekToken;

    private final StringBuilder xmlOutput = new StringBuilder();
    private int indent = 0;

    public Parser(byte[] input) {
        this.scan = new Scanner(input);

        // Carrega o primeiro e o segundo tokens.
        advance();
        advance();
    }

    // Avança os tokens.
    private void advance() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }

    // Consulta o próximo token sem consumi-lo.
    private Token peek() {
        return peekToken;
    }

    // Verifica o tipo do token atual.
    private boolean match(TokenType type) {
        return currentToken != null
                && currentToken.getType() == type;
    }

    // Verifica, registra e consome o token atual.
    private Token consume(TokenType expected) {
        if (!match(expected)) {
            throw error(
                    currentToken,
                    "Esperado " + expected.name()
            );
        }

        Token token = currentToken;

        if (token.getType() != TokenType.EOF) {
            writeTerminal(token);
        }

        advance();

        return token;
    }

    // Abre uma tag e aumenta a indentação dos elementos internos.
    private void openTag(String name) {
        writeIndent();
        xmlOutput.append("<").append(name).append(">\n");
        indent++;
    }

    // Diminui a indentação e fecha a tag.
    private void closeTag(String name) {
        indent--;
        writeIndent();
        xmlOutput.append("</").append(name).append(">\n");
    }

    // Escreve dois espaços por nível de indentação.
    private void writeIndent() {
        for (int i = 0; i < indent; i++) {
            xmlOutput.append("  ");
        }
    }

    // Escreve um token terminal no XML, na indentação atual.
    private void writeTerminal(Token token) {
        String tag = XmlWriter.category(token.getType());

        writeIndent();
        xmlOutput.append("<")
                .append(tag)
                .append("> ")
                .append(XmlWriter.escape(token.getLexeme()))
                .append(" </")
                .append(tag)
                .append(">\n");
    }

    // Analisa a estrutura básica de uma classe Jack.
    public void compileClass() {
        openTag("class");

        consume(TokenType.CLASS);
        consume(TokenType.IDENT);
        consume(TokenType.LBRACE);

        while (match(TokenType.STATIC)
                || match(TokenType.FIELD)) {
            compileClassVarDec();
        }
        // Declarações de sub-rotinas.
        while (match(TokenType.CONSTRUCTOR)
                || match(TokenType.FUNCTION)
                || match(TokenType.METHOD)) {
            compileSubroutine();
        }

        consume(TokenType.RBRACE);

        closeTag("class");

        // Verifica se existe conteúdo após o fechamento da classe.
        if (!match(TokenType.EOF)) {
            throw error(
                    currentToken,
                    "Conteúdo inesperado após o fechamento da classe"
            );
        }
    }

    // Analisa declarações de variáveis da classe.
    private void compileClassVarDec() {
        openTag("classVarDec");

        // static ou field
        if (match(TokenType.STATIC)) {
            consume(TokenType.STATIC);
        } else if (match(TokenType.FIELD)) {
            consume(TokenType.FIELD);
        } else {
            throw error(
                    currentToken,
                    "Esperado static ou field"
            );
        }

        if (match(TokenType.INT)) {
            consume(TokenType.INT);
        } else if (match(TokenType.CHAR)) {
            consume(TokenType.CHAR);
        } else if (match(TokenType.BOOLEAN)) {
            consume(TokenType.BOOLEAN);
        } else if (match(TokenType.IDENT)) {
            consume(TokenType.IDENT);
        } else {
            throw error(
                    currentToken,
                    "Esperado um tipo: int, char, boolean ou nome de classe"
            );
        }


        consume(TokenType.IDENT);


        while (match(TokenType.COMMA)) {
            consume(TokenType.COMMA);
            consume(TokenType.IDENT);
        }


        consume(TokenType.SEMICOLON);

        closeTag("classVarDec");
    }

    // Analisa o cabeçalho de uma declaração de sub-rotina.
    private void compileSubroutine() {
        openTag("subroutineDec");

        // Reconhece constructor, function ou method.
        if (match(TokenType.CONSTRUCTOR)) {
            consume(TokenType.CONSTRUCTOR);
        } else if (match(TokenType.FUNCTION)) {
            consume(TokenType.FUNCTION);
        } else if (match(TokenType.METHOD)) {
            consume(TokenType.METHOD);
        } else {
            throw error(currentToken,
                    "Esperado constructor, function ou method");
        }

        // Reconhece o tipo de retorno.
        if (match(TokenType.VOID)) {
            consume(TokenType.VOID);
        } else {
            compileType();
        }

        // Reconhece o nome da sub-rotina.
        consume(TokenType.IDENT);

        // Analisa a lista de parâmetros.
        consume(TokenType.LPAREN);
        compileParameterList();
        consume(TokenType.RPAREN);

        // Analisa o corpo da sub-rotina.
        compileSubroutineBody();

        closeTag("subroutineDec");
    }

    // Analisa um tipo da linguagem Jack.
    private void compileType() {
        if (match(TokenType.INT)) {
            consume(TokenType.INT);
        } else if (match(TokenType.CHAR)) {
            consume(TokenType.CHAR);
        } else if (match(TokenType.BOOLEAN)) {
            consume(TokenType.BOOLEAN);
        } else if (match(TokenType.IDENT)) {
            consume(TokenType.IDENT);
        } else {
            throw error(currentToken, "Esperado um tipo válido");
        }
    }

    // Analisa a lista de parâmetros de uma sub-rotina.
    private void compileParameterList() {
        openTag("parameterList");


        if (!match(TokenType.RPAREN)) {
            compileType();
            consume(TokenType.IDENT);


            while (match(TokenType.COMMA)) {
                consume(TokenType.COMMA);
                compileType();
                consume(TokenType.IDENT);
            }
        }

        closeTag("parameterList");
    }

    // Analisa declarações de variáveis locais.
    private void compileVarDec() {
        openTag("varDec");

        consume(TokenType.VAR);
        compileType();
        consume(TokenType.IDENT);
        
        while (match(TokenType.COMMA)) {
            consume(TokenType.COMMA);
            consume(TokenType.IDENT);
        }

        consume(TokenType.SEMICOLON);

        closeTag("varDec");
    }


    private void compileSubroutineBody() {
        openTag("subroutineBody");

        consume(TokenType.LBRACE);


        while (match(TokenType.VAR)) {
            compileVarDec();
        }


        consume(TokenType.RBRACE);

        closeTag("subroutineBody");
    }

    // Cria uma mensagem de erro sintático.
    private RuntimeException error(
            Token token,
            String message
    ) {
        String encontrado;

        if (token == null) {
            encontrado = "nenhum token";
        } else {
            encontrado = token.getType()
                    + " ('" + token.getLexeme() + "')";
        }

        return new IllegalArgumentException(
                "Erro sintático: "
                        + message
                        + ". Encontrado: "
                        + encontrado
        );
    }

    // Retorna o XML produzido até o momento.
    public String getXmlOutput() {
        return xmlOutput.toString();
    }
}