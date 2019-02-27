package io.microconfig.plugin.microconfig;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import io.microconfig.utils.reader.FileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.microconfig.plugin.utils.VirtialFileUtil.toVirtualFile;
import static java.util.stream.Collectors.toList;

public class VirtualFileReader implements FileReader {
    @Override
    public String read(File file) {
        return FileDocumentManager.
                getInstance()
                .getDocument(toVirtualFile(file))
                .getText();
    }

    @Override
    public List<String> readLines(File file) {
        return lines(file)
                .collect(toList());
    }

    @Override
    public Optional<String> firstLine(File file, Predicate<String> predicate) {
        return lines(file)
                .filter(predicate)
                .findFirst();
    }

    private Stream<String> lines(File file) {
        return new BufferedReader(new StringReader(read(file))).lines();
    }
}
