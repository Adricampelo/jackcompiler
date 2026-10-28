
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class Scanner {

    private final byte[] input;
    private int current = 0;

   
    private int line = 1;
    private int column = 1;

    
    private int tokenStartLine;
    private int tokenStartColumn;

    private static final Map<String, TokenType> keywords =
            new HashMap<>();

    
    static {
        keywords.put("class", TokenType.CLASS);
        keywords.put("constructor", TokenType.CONSTRUCTOR);
        keywords.put("function", TokenType.FUNCTION);
        keywords.put("method", TokenType.METHOD);
        keywords.put("field", TokenType.FIELD);
        keywords.put("static", TokenType.STATIC);
        keywords.put("var", TokenType.VAR);
        keywords.put("int", TokenType.INT);
        keywords.put("char", TokenType.CHAR);
        keywords.put("boolean", TokenType.BOOLEAN);
        keywords.put("void", TokenType.VOID);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("null", TokenType.NULL);
        keywords.put("this", TokenType.THIS);
        keywords.put("let", TokenType.LET);
        keywords.put("do", TokenType.DO);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("return", TokenType.RETURN);
    }

    public Scanner(byte[] input) {
        this.input = input;
    }

    
    private boolean isAtEnd() {
        return current >= input.length;
    }

    
    private char advance() {
        char ch = (char) (input[current++] & 0xFF);

        if (ch == '\r') {
            line++;
            column = 1;
        } else if (ch == '\n') {
            if (current < 2 ||
                    (input[current - 2] & 0xFF) != '\r') {
                line++;
            }
            column = 1;
        } else {
            column++;
        }

        return ch;
    }

   
    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return (char) (input[current] & 0xFF);
    }

  
    private char peekNext() {
        if (current + 1 >= input.length) {
            return '\0';
        }

        return (char) (input[current + 1] & 0xFF);
    }


    private boolean isAlpha(char ch) {
        return (ch >= 'a' && ch <= 'z')
                || (ch >= 'A' && ch <= 'Z');
    }


    private boolean isAlphaNumeric(char ch) {
        return isAlpha(ch)
                || (ch >= '0' && ch <= '9')
                || ch == '_';
    }

    
    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }


    private Token makeToken(TokenType type, String lexeme) {
        return new Token(
                type,
                lexeme,
                tokenStartLine,
                tokenStartColumn
        );
    }

 
    private IllegalArgumentException lexicalError(String message) {
        return new IllegalArgumentException(
                "Erro léxico na linha " + tokenStartLine +
                ", coluna " + tokenStartColumn +
                ": " + message
        );
    }

 
    private Token identifier() {
        int start = current;

        while (!isAtEnd() && isAlphaNumeric(peek())) {
            advance();
        }

        String text = new String(
                input,
                start,
                current - start,
                StandardCharsets.UTF_8
        );

        TokenType type = keywords.get(text);

        if (type == null) {
            type = TokenType.IDENT;
        }

        return makeToken(type, text);
    }

    private Token number() {
        int start = current;

        while (!isAtEnd() && isDigit(peek())) {
            advance();
        }

        String text = new String(
                input,
                start,
                current - start,
                StandardCharsets.UTF_8
        );


        try {
            int value = Integer.parseInt(text);

            if (value > 32767) {
                throw lexicalError(
                        "Número fora do intervalo permitido: " + text
                );
            }
        } catch (NumberFormatException e) {
            throw lexicalError(
                    "Número inteiro inválido: " + text
            );
        }

        return makeToken(TokenType.NUMBER, text);
    }

    // Reconhece constantes do tipo string
    private Token string() {
        advance(); // Consome a aspa inicial

        int start = current;

        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n' || peek() == '\r') {
                throw lexicalError(
                        "Quebra de linha dentro da string"
                );
            }

            advance();
        }

        if (isAtEnd()) {
            throw lexicalError(
                    "String sem aspa de fechamento"
            );
        }

        String text = new String(
                input,
                start,
                current - start,
                StandardCharsets.UTF_8
        );

        advance(); // Consome a aspa final

        return makeToken(TokenType.STRING, text);
    }

    // Ignora comentários de linha
    private void skipLineComment() {
        while (!isAtEnd()
                && peek() != '\n'
                && peek() != '\r') {
            advance();
        }
    }

    // Ignora comentários de bloco
    private void skipBlockComment() {
        int commentLine = line;
        int commentColumn = column;

        advance(); // Consome '/'
        advance(); // Consome '*'

        while (!isAtEnd()) {
            if (peek() == '*' && peekNext() == '/') {
                advance(); // Consome '*'
                advance(); // Consome '/'
                return;
            }

            advance();
        }

        throw new IllegalArgumentException(
                "Erro léxico na linha " + commentLine +
                ", coluna " + commentColumn +
                ": Comentário de bloco sem fechamento"
        );
    }

    // Ignora espaços e comentários
    private void skipNoise() {
        while (!isAtEnd()) {
            char ch = peek();

            if (Character.isWhitespace(ch)) {
                advance();

            } else if (ch == '/' && peekNext() == '/') {
                skipLineComment();

            } else if (ch == '/' && peekNext() == '*') {
                skipBlockComment();

            } else {
                return;
            }
        }
    }

  
    public Token nextToken() {
        skipNoise();

  
        tokenStartLine = line;
        tokenStartColumn = column;

        if (isAtEnd()) {
            return makeToken(TokenType.EOF, "");
        }

        char ch = peek();

  
        if (isAlpha(ch) || ch == '_') {
            return identifier();
        }

        if (isDigit(ch)) {
            return number();
        }


        if (ch == '"') {
            return string();
        }

   
        switch (ch) {
            case '+':
                advance();
                return makeToken(TokenType.PLUS, "+");

            case '-':
                advance();
                return makeToken(TokenType.MINUS, "-");

            case '*':
                advance();
                return makeToken(TokenType.ASTERISK, "*");

            case '.':
                advance();
                return makeToken(TokenType.DOT, ".");

            case '&':
                advance();
                return makeToken(TokenType.AND, "&");

            case '|':
                advance();
                return makeToken(TokenType.OR, "|");

            case '~':
                advance();
                return makeToken(TokenType.NOT, "~");

            case '>':
                advance();
                return makeToken(TokenType.GT, ">");

            case '<':
                advance();
                return makeToken(TokenType.LT, "<");

            case '=':
                advance();
                return makeToken(TokenType.EQ, "=");

            case '(':
                advance();
                return makeToken(TokenType.LPAREN, "(");

            case ')':
                advance();
                return makeToken(TokenType.RPAREN, ")");

            case '{':
                advance();
                return makeToken(TokenType.LBRACE, "{");

            case '}':
                advance();
                return makeToken(TokenType.RBRACE, "}");

            case '[':
                advance();
                return makeToken(TokenType.LBRACKET, "[");

            case ']':
                advance();
                return makeToken(TokenType.RBRACKET, "]");

            case ';':
                advance();
                return makeToken(TokenType.SEMICOLON, ";");

            case ',':
                advance();
                return makeToken(TokenType.COMMA, ",");

            case '/':
                advance();
                return makeToken(TokenType.SLASH, "/");

            default:
                throw lexicalError(
                        "Caractere inesperado: '" + ch + "'"
                );
        }
    }
}
