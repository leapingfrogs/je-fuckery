package com.hypnoticocelot.jefuckery.xml;

import javax.xml.bind.annotation.XmlElement;

public class Ref {
    @XmlElement(name = "DbLsn")
    private DbLsn dblsn;

    public DbLsn getDblsn() {
        return dblsn;
    }

    @Override
    public String toString() {
        return "Ref{" +
                "dblsn=" + dblsn +
                '}';
    }
}
