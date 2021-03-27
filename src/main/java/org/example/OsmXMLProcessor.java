package org.example;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OsmXMLProcessor implements AutoCloseable {
    private static final XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
    private static final Logger log = LogManager.getLogger("OsmXMLProcessor");

    private final XMLStreamReader reader;

    public OsmXMLProcessor(InputStream xmlStream) throws XMLStreamException {
        this.reader = xmlFactory.createXMLStreamReader(xmlStream);
    }

    public void printState() throws XMLStreamException {
        Map<String, Integer> userChange = new HashMap<>();
        Map<String, Integer> keyTags = new HashMap<>();

        while (this.reader.hasNext()) {
            int event = this.reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && getTagName().equals("node")) {
                String user = getAttribute("user");
                int changes = userChange.getOrDefault(user, 0);
                userChange.put(user, changes + 1);

                while (this.reader.hasNext()) {
                    event = this.reader.next();
                    if (event == XMLStreamConstants.START_ELEMENT && getTagName().equals("tag")) {
                        String key = getAttribute("k");
                        int occurred = keyTags.getOrDefault(key, 0);
                        keyTags.put(key, occurred + 1);
                    }
                    if (event == XMLStreamConstants.END_ELEMENT && getTagName().equals("node")) {
                        break;
                    }
                }
            }
        }
        userChange = sortByValue(userChange);
        keyTags = sortByValue(keyTags);
        log.info("***********************************************");
        userChange.forEach((user, changes) -> log.info("{}: {}", user, changes));
        log.info("***********************************************");
        keyTags.forEach((key, tags) -> log.info("{}: {}", key, tags));
        log.info("***********************************************");
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private String getTagName() {
        return this.reader.getLocalName();
    }

    private String getAttribute(String name) {
        return this.reader.getAttributeValue(null, name);
    }

    @Override
    public void close() throws Exception {
        this.reader.close();
    }
}
