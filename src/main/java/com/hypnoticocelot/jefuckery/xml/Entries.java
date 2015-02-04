package com.hypnoticocelot.jefuckery.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import java.util.ArrayList;
import java.util.List;

public class Entries {
    @XmlElement(name = "ref")
    private List<Ref> refs = new ArrayList<>();

    public List<Ref> getRefs() {
        return refs;
    }

    @Override
    public String toString() {
        return "Entries{" +
                "refs=" + refs +
                '}';
    }
}
