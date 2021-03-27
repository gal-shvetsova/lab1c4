package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

public class Main {
    static final Logger log = LogManager.getLogger("Main");

    public static void main(String[] args) {
        log.info("hello world");

        try {
            InputStream archivedStream = Main.class.getClassLoader().getResourceAsStream("RU-NVS.osm.bz2");
            OsmUnpackerDecorator unpackedStream = new OsmUnpackerDecorator(archivedStream);
            OsmXMLProcessor xmlProcessor = new OsmXMLProcessor(unpackedStream);
            xmlProcessor.printState();
            unpackedStream.close();
            archivedStream.close();
        } catch (Exception e) {
            log.error(e);
        }
    }
}
