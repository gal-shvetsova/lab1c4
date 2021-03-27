package org.example;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class OsmUnpackerDecorator extends InputStream implements AutoCloseable {
    private final BZip2CompressorInputStream inputStream;

    public OsmUnpackerDecorator(InputStream archiveStream) throws IOException {
        this.inputStream = new BZip2CompressorInputStream(archiveStream);
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
}
