
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

class Scanner {

    private final byte[] input;
    private int current = 0;

    private static final Map<String, TokenType> keywords =
            new HashMap<>();

    // Palavras-chave do Jack
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
        return (char) (input[current++] & 0xFF);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return (char) (input[current] & 0xFF);
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

        return new Token(type, text);
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private Token number() {
        int start = current;

        while (!isAtEnd() && isDigit(peek())) {
            advance();
        }

        String text = new String(
                input, start, current - start,
                StandardCharsets.UTF_8
        );

        int value = Integer.parseInt(text);

        if (value > 32767) {
            throw new Error("Número fora do intervalo: " + text);
        }

        return new Token(TokenType.NUMBER, text);
    }

    private Token string() {
        advance(); // Consome a aspa inicial
        int start = current;

        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n' || peek() == '\r') {
                throw new Error("Quebra de linha dentro da string");
            }
            advance();
        }

        if (isAtEnd()) {
            throw new Error("String sem aspa de fechamento");
        }

        String text = new String(
                input, start, current - start,
                StandardCharsets.UTF_8
        );

        advance(); // Consome a aspa final

        return new Token(TokenType.STRING, text);
    }

    public Token nextToken() {
        while (!isAtEnd() && Character.isWhitespace(peek())) {
            advance();
        }

        if (isAtEnd()) {
            return new Token(TokenType.EOF, "");
        }

        char ch = peek();

        if (isAlpha(ch)) {
            return identifier();
        }

        if (isDigit(ch)) {
            return number();
        }

        if (ch == '"') {
            return string();
        }

        switch (ch) {
            case '+': advance(); return new Token(TokenType.PLUS, "+");
            case '-': advance(); return new Token(TokenType.MINUS, "-");
            case '*': advance(); return new Token(TokenType.ASTERISK, "*");
            case '.': advance(); return new Token(TokenType.DOT, ".");
            case '&': advance(); return new Token(TokenType.AND, "&");
            case '|': advance(); return new Token(TokenType.OR, "|");
            case '~': advance(); return new Token(TokenType.NOT, "~");
            case '>': advance(); return new Token(TokenType.GT, ">");
            case '<': advance(); return new Token(TokenType.LT, "<");
            case '=': advance(); return new Token(TokenType.EQ, "=");
            case '(': advance(); return new Token(TokenType.LPAREN, "(");
            case ')': advance(); return new Token(TokenType.RPAREN, ")");
            case '{': advance(); return new Token(TokenType.LBRACE, "{");
            case '}': advance(); return new Token(TokenType.RBRACE, "}");
            case '[': advance(); return new Token(TokenType.LBRACKET, "[");
            case ']': advance(); return new Token(TokenType.RBRACKET, "]");
            case ';': advance(); return new Token(TokenType.SEMICOLON, ";");
            case ',': advance(); return new Token(TokenType.COMMA, ",");
            case '/':
                advance();
                return new Token(TokenType.SLASH, "/");

            default:
                throw new Error("Caractere inesperado: " + ch);
        }
    }
}
