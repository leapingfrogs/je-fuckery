package com.hypnoticocelot.jefuckery;

import com.hypnoticocelot.jefuckery.xml.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class DbPrintLogProcessor {
    public static void main(String[] args) throws JAXBException, IOException {
        final JAXBContext jaxbContext = JAXBContext.newInstance(Entry.class);
        final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        final File xmlFile = new File("dbprintlog.xml");

        Files.lines(xmlFile.toPath()).forEach((line) -> {
            if (line.startsWith("<entry") && line.contains("type=\"BIN/8\"")) {
                try {
                    final Entry entry = (Entry) jaxbUnmarshaller.unmarshal(new StringReader(line));

                    final String binFile = entry.getLsn().split("/")[0];
                    final List<String> inOtherFiles = entry.getBin().getEntries().getRefs().stream()
                            .map((ref) -> ref.getDblsn().getValue().split("/")[0])
                            .filter((lnFile) -> !lnFile.equals(binFile))
                            .collect(Collectors.toList());

                    if (!inOtherFiles.isEmpty()) {
                        System.out.println("I FOUND A PERFECT BIN!!!");
                        System.out.println("entry = " + entry);
                        System.out.println("entry.getLsn() = " + entry.getLsn());
                        System.out.println("inOtherFiles = " + inOtherFiles);
                    }
                } catch (JAXBException e) {
                    System.err.println("Problem parsing XML: " + line);
                    System.err.println(e.getMessage());
                    e.printStackTrace(System.err);
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
