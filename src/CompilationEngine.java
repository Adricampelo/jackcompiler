
import java.io.IOException;
import java.nio.file.Path;

public class CompilationEngine implements AutoCloseable {

    private final VMWriter vmWriter;

    public CompilationEngine(Path outputFile) throws IOException {
        this.vmWriter = new VMWriter(outputFile);
    }

    @Override
    public void close() throws IOException {
        vmWriter.close();
    }
}
