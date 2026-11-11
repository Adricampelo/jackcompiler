
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private static class Symbol {
        private final String type;
        private final Kind kind;
        private final int index;

        public Symbol(String type, Kind kind, int index) {
            this.type = type;
            this.kind = kind;
            this.index = index;
        }
    }

    private final Map<String, Symbol> classScope = new HashMap<>();
    private final Map<String, Symbol> subroutineScope = new HashMap<>();

    private final Map<Kind, Integer> counters = new HashMap<>();

    public SymbolTable() {
        resetCounters();
    }

    public void startSubroutine() {
        subroutineScope.clear();

        counters.put(Kind.ARGUMENT, 0);
        counters.put(Kind.VAR, 0);
    }

    public void define(String name, String type, Kind kind) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome inválido.");
        }

        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Tipo inválido.");
        }

        if (kind == null || kind == Kind.NONE) {
            throw new IllegalArgumentException("Categoria inválida.");
        }

        Map<String, Symbol> scope = getScope(kind);

        if (scope.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Variável já declarada neste escopo: " + name
            );
        }

        int index = counters.get(kind);

        scope.put(name, new Symbol(type, kind, index));

        counters.put(kind, index + 1);
    }

    public int varCount(Kind kind) {
        if (kind == null || kind == Kind.NONE) {
            return 0;
        }

        return counters.get(kind);
    }

    public Kind kindOf(String name) {
        Symbol symbol = findSymbol(name);
        return symbol == null ? Kind.NONE : symbol.kind;
    }

    public String typeOf(String name) {
        Symbol symbol = findSymbol(name);
        return symbol == null ? null : symbol.type;
    }

    public int indexOf(String name) {
        Symbol symbol = findSymbol(name);
        return symbol == null ? -1 : symbol.index;
    }

    private Symbol findSymbol(String name) {
        Symbol symbol = subroutineScope.get(name);

        if (symbol == null) {
            symbol = classScope.get(name);
        }

        return symbol;
    }

    private Map<String, Symbol> getScope(Kind kind) {
        if (kind == Kind.STATIC || kind == Kind.FIELD) {
            return classScope;
        }

        return subroutineScope;
    }

    private void resetCounters() {
        for (Kind kind : Kind.values()) {
            counters.put(kind, 0);
        }
    }
}
