public class XmlWriter {

    // Converte o tipo interno na categoria oficial do nand2tetris
    public static String category(TokenType type) {
        switch (type) {
            case NUMBER:
                return "integerConstant";
            case STRING:
                return "stringConstant";
            case IDENT:
                return "identifier";

            case LPAREN: case RPAREN:
            case LBRACE: case RBRACE:
            case LBRACKET: case RBRACKET:
            case COMMA: case SEMICOLON: case DOT:
            case PLUS: case MINUS: case ASTERISK: case SLASH:
            case AND: case OR: case NOT:
            case LT: case GT: case EQ:
                return "symbol";

            case EOF:
                throw new IllegalArgumentException("EOF não é escrito no XML");

            default:
                return "keyword";
        }
    }

    // O & precisa ser o primeiro, senão ele escapa os próprios escapes
    public static String escape(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
