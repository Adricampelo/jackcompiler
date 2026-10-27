public class Parser {

    private final Scanner scan;
    private Token currentToken;
    private Token peekToken;

    private final StringBuilder xmlOutput = new StringBuilder();
    private int indent = 0;

    public Parser(byte[] input) {
        this.scan = new Scanner(input);
        advance();
        advance();
    }

  
    private void advance() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }


    private Token peek() {
        return peekToken;
    }


    private boolean match(TokenType type) {
        return currentToken != null
                && currentToken.getType() == type;
    }


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


    private void openTag(String name) {
        writeIndent();
        xmlOutput.append("<").append(name).append(">\n");
        indent++;
    }


    private void closeTag(String name) {
        indent--;
        writeIndent();
        xmlOutput.append("</").append(name).append(">\n");
    }


    private void writeIndent() {
        for (int i = 0; i < indent; i++) {
            xmlOutput.append("  ");
        }
    }


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


    public void compileClass() {
        openTag("class");

        consume(TokenType.CLASS);
        consume(TokenType.IDENT);
        consume(TokenType.LBRACE);

        while (match(TokenType.STATIC)
                || match(TokenType.FIELD)) {
            compileClassVarDec();
        }

        while (match(TokenType.CONSTRUCTOR)
                || match(TokenType.FUNCTION)
                || match(TokenType.METHOD)) {
            compileSubroutine();
        }

        consume(TokenType.RBRACE);

        closeTag("class");

        if (!match(TokenType.EOF)) {
            throw error(
                    currentToken,
                    "Conteúdo inesperado após o fechamento da classe"
            );
        }
    }


    private void compileClassVarDec() {
        openTag("classVarDec");

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


    private void compileSubroutine() {
        openTag("subroutineDec");


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


        if (match(TokenType.VOID)) {
            consume(TokenType.VOID);
        } else {
            compileType();
        }

        consume(TokenType.IDENT);

        consume(TokenType.LPAREN);
        compileParameterList();
        consume(TokenType.RPAREN);
        compileSubroutineBody();

        closeTag("subroutineDec");
    }


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

        compileStatements();

        consume(TokenType.RBRACE);

        closeTag("subroutineBody");
    }

    private void compileStatements() {
        openTag("statements");

        while (true) {
            if (match(TokenType.LET)) {
                compileLet();
            } else if (match(TokenType.IF)) {
                compileIf();
            } else if (match(TokenType.WHILE)) {
                compileWhile();
            } else if (match(TokenType.DO)) {
                compileDo();
            } else if (match(TokenType.RETURN)) {
                compileReturn();
            } else {
                break;
            }
        }

        closeTag("statements");
    }


    private void compileExpression() {
        openTag("expression");

        compileTerm();

        while (isOperator()) {
            consume(currentToken.getType());
            compileTerm();
        }

        closeTag("expression");
    }

    private boolean isOperator() {
        return match(TokenType.PLUS)
                || match(TokenType.MINUS)
                || match(TokenType.ASTERISK)
                || match(TokenType.SLASH)
                || match(TokenType.AND)
                || match(TokenType.OR)
                || match(TokenType.LT)
                || match(TokenType.GT)
                || match(TokenType.EQ);
    }

    private boolean isKeywordConstant() {
        return match(TokenType.TRUE)
                || match(TokenType.FALSE)
                || match(TokenType.NULL)
                || match(TokenType.THIS);
    }

    private void compileTerm() {
        openTag("term");

        if (match(TokenType.NUMBER)) {
            consume(TokenType.NUMBER);

        } else if (match(TokenType.STRING)) {
            consume(TokenType.STRING);

        } else if (isKeywordConstant()) {
            consume(currentToken.getType());

        } else if (match(TokenType.LPAREN)) {
            consume(TokenType.LPAREN);
            compileExpression();
            consume(TokenType.RPAREN);

        } else if (match(TokenType.MINUS) || match(TokenType.NOT)) {
            consume(currentToken.getType());
            compileTerm();

        } else if (match(TokenType.IDENT)) {
            TokenType next = peek().getType();

            if (next == TokenType.LBRACKET) {
                consume(TokenType.IDENT);
                consume(TokenType.LBRACKET);
                compileExpression();
                consume(TokenType.RBRACKET);
            } else if (next == TokenType.LPAREN || next == TokenType.DOT) {
                compileSubroutineCall();
            } else {
                consume(TokenType.IDENT);
            }

        } else {
            throw error(
                    currentToken,
                    "Esperado um termo: número, string, identificador, "
                            + "constante, \"(\", \"-\" ou \"~\""
            );
        }

        closeTag("term");
    }


    private void compileSubroutineCall() {
        consume(TokenType.IDENT);
        if (match(TokenType.DOT)) {
            consume(TokenType.DOT);
            consume(TokenType.IDENT);
        }

        consume(TokenType.LPAREN);
        compileExpressionList();
        consume(TokenType.RPAREN);
    }


    private void compileExpressionList() {
        openTag("expressionList");

        if (!match(TokenType.RPAREN)) {
            compileExpression();

            while (match(TokenType.COMMA)) {
                consume(TokenType.COMMA);
                compileExpression();
            }
        }

        closeTag("expressionList");
    }


    private void compileLet() {
        openTag("letStatement");

        consume(TokenType.LET);
        consume(TokenType.IDENT);

        if (match(TokenType.LBRACKET)) {
            consume(TokenType.LBRACKET);
            compileExpression();
            consume(TokenType.RBRACKET);
        }

        consume(TokenType.EQ);
        compileExpression();
        consume(TokenType.SEMICOLON);

        closeTag("letStatement");
    }

    private void compileIf() { }

    private void compileWhile() { }

    private void compileDo() {
        openTag("doStatement");
        consume(TokenType.DO);
        compileSubroutineCall();
        consume(TokenType.SEMICOLON);
        closeTag("doStatement");
    }

    private void compileReturn() { }


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

    
    public String getXmlOutput() {
        return xmlOutput.toString();
    }
}
