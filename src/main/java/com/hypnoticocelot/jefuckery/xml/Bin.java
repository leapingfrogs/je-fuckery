package com.hypnoticocelot.jefuckery.xml;

import javax.xml.bind.annotation.XmlElement;

public class Bin {
    @XmlElement(name = "entries")
    private Entries entries;

    public Entries getEntries() {
        return entries;
    }

    @Override
    public String toString() {
        return "Bin{" +
                "entries=" + entries +
                '}';
    }
}
