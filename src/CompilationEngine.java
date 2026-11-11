import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;


public class CompilationEngine implements AutoCloseable {

    private final Scanner scan;
    private final VMWriter vmWriter;
    private final SymbolTable symbolTable = new SymbolTable();

    private Token currentToken;
    private Token peekToken;

    private String className;
    private int labelCounter = 0;

    // Sub-rotina em compilação (usados também nos passos seguintes)
    private TokenType subroutineKind;      // CONSTRUCTOR, FUNCTION ou METHOD
    private String subroutineName;
    private String subroutineReturnType;

    public CompilationEngine(byte[] input, Path outputFile)
            throws IOException {
        this(input, new VMWriter(outputFile));
    }

    public CompilationEngine(byte[] input, Writer output)
            throws IOException {
        this(input, new VMWriter(output));
    }

    private CompilationEngine(byte[] input, VMWriter vmWriter) {
        this.scan = new Scanner(input);
        this.vmWriter = vmWriter;
        advance();
        advance();
    }

    // Leitura de tokens


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
            throw error(currentToken, "Esperado: " + expected.name());
        }

        Token token = currentToken;
        advance();
        return token;
    }

    // Apoio à geração de código

    private int nextLabelId() {
        return labelCounter++;
    }


    private String segmentOf(Kind kind) {
        switch (kind) {
            case STATIC:   return "static";
            case FIELD:    return "this";
            case ARGUMENT: return "argument";
            case VAR:      return "local";
            default:
                throw new IllegalArgumentException(
                        "Categoria sem segmento VM: " + kind
                );
        }
    }


    private void pushVariable(Token name) throws IOException {
        Kind kind = symbolTable.kindOf(name.getLexeme());

        if (kind == Kind.NONE) {
            throw error(name, "Variável não declarada: "
                    + name.getLexeme());
        }

        vmWriter.writePush(
                segmentOf(kind), symbolTable.indexOf(name.getLexeme())
        );
    }


    private void popVariable(Token name) throws IOException {
        Kind kind = symbolTable.kindOf(name.getLexeme());

        if (kind == Kind.NONE) {
            throw error(name, "Variável não declarada: "
                    + name.getLexeme());
        }

        vmWriter.writePop(
                segmentOf(kind), symbolTable.indexOf(name.getLexeme())
        );
    }

 
    private void define(Token name, String type, Kind kind) {
        try {
            symbolTable.define(name.getLexeme(), type, kind);
        } catch (IllegalArgumentException e) {
            throw error(name, e.getMessage());
        }
    }


    // Estrutura da classe
    

    public void compileClass() throws IOException {
        consume(TokenType.CLASS);
        className = consume(TokenType.IDENT).getLexeme();
        consume(TokenType.LBRACE);

        while (match(TokenType.STATIC) || match(TokenType.FIELD)) {
            compileClassVarDec();
        }

        while (match(TokenType.CONSTRUCTOR)
                || match(TokenType.FUNCTION)
                || match(TokenType.METHOD)) {
            compileSubroutine();
        }

        consume(TokenType.RBRACE);

        if (!match(TokenType.EOF)) {
            throw error(currentToken,
                    "Esperado: fim do arquivo (EOF)");
        }
    }

    // Declarações 

    private String compileType() {
        if (match(TokenType.INT)
                || match(TokenType.CHAR)
                || match(TokenType.BOOLEAN)
                || match(TokenType.IDENT)) {
            Token type = currentToken;
            advance();
            return type.getLexeme();
        }

        throw error(currentToken, "Esperado: um tipo válido");
    }


    private void compileClassVarDec() {
        Kind kind;

        if (match(TokenType.STATIC)) {
            kind = Kind.STATIC;
            consume(TokenType.STATIC);
        } else if (match(TokenType.FIELD)) {
            kind = Kind.FIELD;
            consume(TokenType.FIELD);
        } else {
            throw error(currentToken, "Esperado: static ou field");
        }

        String type = compileType();
        define(consume(TokenType.IDENT), type, kind);

        while (match(TokenType.COMMA)) {
            consume(TokenType.COMMA);
            define(consume(TokenType.IDENT), type, kind);
        }

        consume(TokenType.SEMICOLON);
    }


    private void compileSubroutine() throws IOException {
        if (match(TokenType.CONSTRUCTOR)
                || match(TokenType.FUNCTION)
                || match(TokenType.METHOD)) {
            subroutineKind = currentToken.getType();
            advance();
        } else {
            throw error(currentToken,
                    "Esperado: constructor, function ou method");
        }

        if (match(TokenType.VOID)) {
            subroutineReturnType = "void";
            consume(TokenType.VOID);
        } else {
            subroutineReturnType = compileType();
        }

        subroutineName = consume(TokenType.IDENT).getLexeme();

        symbolTable.startSubroutine();

        // Em um método, o argumento 0 é o próprio objeto (this)
        if (subroutineKind == TokenType.METHOD) {
            symbolTable.define("this", className, Kind.ARGUMENT);
        }

        consume(TokenType.LPAREN);
        compileParameterList();
        consume(TokenType.RPAREN);

        compileSubroutineBody();
    }


    private void compileParameterList() {
        if (match(TokenType.RPAREN)) {
            return;
        }

        String type = compileType();
        define(consume(TokenType.IDENT), type, Kind.ARGUMENT);

        while (match(TokenType.COMMA)) {
            consume(TokenType.COMMA);
            type = compileType();
            define(consume(TokenType.IDENT), type, Kind.ARGUMENT);
        }
    }


    private void compileVarDec() {
        consume(TokenType.VAR);

        String type = compileType();
        define(consume(TokenType.IDENT), type, Kind.VAR);

        while (match(TokenType.COMMA)) {
            consume(TokenType.COMMA);
            define(consume(TokenType.IDENT), type, Kind.VAR);
        }

        consume(TokenType.SEMICOLON);
    }


    private void compileSubroutineBody() throws IOException {
        consume(TokenType.LBRACE);

        while (match(TokenType.VAR)) {
            compileVarDec();
        }

        vmWriter.writeFunction(
                className + "." + subroutineName,
                symbolTable.varCount(Kind.VAR)
        );

        compileStatements();

        consume(TokenType.RBRACE);
    }

 
    private void compileStatements() throws IOException {
        while (true) {
            if (match(TokenType.LET)) {
                compileLet();
            } else if (match(TokenType.DO)) {
                compileDo();
            } else if (match(TokenType.IF)) {
                compileIf();
            } else if (match(TokenType.WHILE)) {
                compileWhile();
            } else if (match(TokenType.RETURN)) {
                throw notYet("comando return", "11");
            } else {
                return;
            }
        }
    }


    // Comandos let e do (passo 9)
   
    private void compileLet() throws IOException {
        consume(TokenType.LET);
        Token name = consume(TokenType.IDENT);

        if (match(TokenType.LBRACKET)) {
            throw notYet("atribuição a array", "14");
        }

        consume(TokenType.EQ);
        compileExpression();
        consume(TokenType.SEMICOLON);

        popVariable(name);
    }

    
    private void compileDo() throws IOException {
        consume(TokenType.DO);
        compileSubroutineCall();
        consume(TokenType.SEMICOLON);

        
        vmWriter.writePop("temp", 0);
    }


    private void compileSubroutineCall() throws IOException {
        Token first = consume(TokenType.IDENT);
        String target;
        int nArgs = 0;

        if (match(TokenType.DOT)) {
            consume(TokenType.DOT);
            Token sub = consume(TokenType.IDENT);

            if (symbolTable.kindOf(first.getLexeme()) != Kind.NONE) {
                pushVariable(first);
                target = symbolTable.typeOf(first.getLexeme())
                        + "." + sub.getLexeme();
                nArgs = 1;
            } else {
                target = first.getLexeme() + "." + sub.getLexeme();
            }
        } else {
            if (subroutineKind == TokenType.FUNCTION) {
                throw error(first, "Chamada de método sem objeto "
                        + "dentro de uma function: " + first.getLexeme());
            }

            vmWriter.writePush("pointer", 0);
            target = className + "." + first.getLexeme();
            nArgs = 1;
        }

        consume(TokenType.LPAREN);
        nArgs += compileExpressionList();
        consume(TokenType.RPAREN);

        vmWriter.writeCall(target, nArgs);
    }

  
    private int compileExpressionList() throws IOException {
        if (match(TokenType.RPAREN)) {
            return 0;
        }

        int count = 1;
        compileExpression();

        while (match(TokenType.COMMA)) {
            consume(TokenType.COMMA);
            compileExpression();
            count++;
        }

        return count;
    }

    // Comandos if e while
  
    private void compileIf() throws IOException {
        int id = nextLabelId();
        String ifTrue = "IF_TRUE" + id;
        String ifFalse = "IF_FALSE" + id;
        String ifEnd = "IF_END" + id;

        consume(TokenType.IF);
        consume(TokenType.LPAREN);
        compileExpression();
        consume(TokenType.RPAREN);

        vmWriter.writeIf(ifTrue);
        vmWriter.writeGoto(ifFalse);
        vmWriter.writeLabel(ifTrue);

        consume(TokenType.LBRACE);
        compileStatements();
        consume(TokenType.RBRACE);

        if (match(TokenType.ELSE)) {
            vmWriter.writeGoto(ifEnd);
            vmWriter.writeLabel(ifFalse);

            consume(TokenType.ELSE);
            consume(TokenType.LBRACE);
            compileStatements();
            consume(TokenType.RBRACE);

            vmWriter.writeLabel(ifEnd);
        } else {
            vmWriter.writeLabel(ifFalse);
        }
    }

    private void compileWhile() throws IOException {
        int id = nextLabelId();
        String whileExp = "WHILE_EXP" + id;
        String whileEnd = "WHILE_END" + id;

        consume(TokenType.WHILE);
        vmWriter.writeLabel(whileExp);

        consume(TokenType.LPAREN);
        compileExpression();
        consume(TokenType.RPAREN);

        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(whileEnd);

        consume(TokenType.LBRACE);
        compileStatements();
        consume(TokenType.RBRACE);

        vmWriter.writeGoto(whileExp);
        vmWriter.writeLabel(whileEnd);
    }

    // Expressões 

    private boolean isOperator() {
        return match(TokenType.PLUS) || match(TokenType.MINUS)
                || match(TokenType.ASTERISK) || match(TokenType.SLASH)
                || match(TokenType.AND) || match(TokenType.OR)
                || match(TokenType.LT) || match(TokenType.GT)
                || match(TokenType.EQ);
    }

    private void compileExpression() throws IOException {
        compileTerm();

        while (isOperator()) {
            Token op = currentToken;
            advance();
            compileTerm();
            writeBinaryOperator(op);
        }
    }

    private void writeBinaryOperator(Token op) throws IOException {
        switch (op.getType()) {
            case PLUS:     vmWriter.writeArithmetic("add"); break;
            case MINUS:    vmWriter.writeArithmetic("sub"); break;
            case AND:      vmWriter.writeArithmetic("and"); break;
            case OR:       vmWriter.writeArithmetic("or");  break;
            case LT:       vmWriter.writeArithmetic("lt");  break;
            case GT:       vmWriter.writeArithmetic("gt");  break;
            case EQ:       vmWriter.writeArithmetic("eq");  break;
            case ASTERISK: vmWriter.writeCall("Math.multiply", 2); break;
            case SLASH:    vmWriter.writeCall("Math.divide", 2);   break;
            default:
                throw error(op, "Operador inválido");
        }
    }

    private void compileTerm() throws IOException {
        if (match(TokenType.NUMBER)) {
            Token number = consume(TokenType.NUMBER);
            writeIntegerConstant(number);

        } else if (match(TokenType.TRUE)) {
            consume(TokenType.TRUE);
            vmWriter.writePush("constant", 0);
            vmWriter.writeArithmetic("not");      // true = -1

        } else if (match(TokenType.FALSE) || match(TokenType.NULL)) {
            advance();
            vmWriter.writePush("constant", 0);

        } else if (match(TokenType.THIS)) {
            consume(TokenType.THIS);
            vmWriter.writePush("pointer", 0);

        } else if (match(TokenType.LPAREN)) {
            consume(TokenType.LPAREN);
            compileExpression();
            consume(TokenType.RPAREN);

        } else if (match(TokenType.MINUS)) {
            consume(TokenType.MINUS);
            compileTerm();
            vmWriter.writeArithmetic("neg");

        } else if (match(TokenType.NOT)) {
            consume(TokenType.NOT);
            compileTerm();
            vmWriter.writeArithmetic("not");

        } else if (match(TokenType.IDENT)) {
            TokenType next = peek().getType();

            if (next == TokenType.LBRACKET) {
                throw notYet("acesso a arrays", "14");
            } else if (next == TokenType.LPAREN
                    || next == TokenType.DOT) {
                throw notYet("chamada de sub-rotina em expressão", "11");
            }

            pushVariable(consume(TokenType.IDENT));

        } else if (match(TokenType.STRING)) {
            throw notYet("constante string", "14");

        } else {
            throw error(currentToken, "Esperado: um termo válido");
        }
    }

    private void writeIntegerConstant(Token number) throws IOException {
        int value;

        try {
            value = Integer.parseInt(number.getLexeme());
        } catch (NumberFormatException e) {
            throw error(number, "Constante inteira inválida");
        }

        if (value < 0 || value > 32767) {
            throw error(number,
                    "Constante inteira fora do intervalo 0..32767");
        }

        vmWriter.writePush("constant", value);
    }

    private UnsupportedOperationException notYet(String what, String step) {
        return new UnsupportedOperationException(
                what + " ainda não implementado(a) (linha "
                + currentToken.getLine() + ", passo " + step + ")"
        );
    }

    // Erros

    private RuntimeException error(Token token, String message) {
        if (token == null) {
            return new IllegalArgumentException(
                    "Erro de compilação\n" + message
                    + "\nEncontrado: nenhum token"
            );
        }

        String encontrado = token.getType() == TokenType.EOF
                ? "fim do arquivo (EOF)"
                : token.getType() + " ('" + token.getLexeme() + "')";

        return new IllegalArgumentException(
                "Erro de compilação na linha " + token.getLine()
                + ", coluna " + token.getColumn()
                + "\n" + message
                + "\nEncontrado: " + encontrado
        );
    }

    @Override
    public void close() throws IOException {
        vmWriter.close();
    }
}