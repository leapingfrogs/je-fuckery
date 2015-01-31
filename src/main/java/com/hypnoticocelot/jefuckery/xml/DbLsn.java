package com.hypnoticocelot.jefuckery.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class DbLsn {
    @XmlAttribute(name = "val")
    private String value;

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "DbLsn{" +
                "value='" + value + '\'' +
                '}';
    }
}
