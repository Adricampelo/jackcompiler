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

  
    private String newLabel(String prefix) {
        return prefix + (labelCounter++);
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

    // Implementados no passo 7 (declarações de classes, variáveis e funções).
    private void compileClassVarDec() throws IOException {
        throw new UnsupportedOperationException(
                "compileClassVarDec ainda não implementado"
        );
    }

    private void compileSubroutine() throws IOException {
        throw new UnsupportedOperationException(
                "compileSubroutine ainda não implementado"
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