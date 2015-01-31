package com.hypnoticocelot.jefuckery.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entry")
public class Entry {
    @XmlAttribute
    private String lsn;
    @XmlAttribute
    private String type;
    @XmlAttribute
    private String prev;
    @XmlAttribute
    private long size;
    @XmlAttribute(name = "cksum")
    private long checkSum;
    @XmlElement(name = "bin")
    private Bin bin;

    public String getLsn() {
        return lsn;
    }

    public String getType() {
        return type;
    }

    public String getPrev() {
        return prev;
    }

    public long getSize() {
        return size;
    }

    public long getCheckSum() {
        return checkSum;
    }

    public Bin getBin() {
        return bin;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "lsn='" + lsn + '\'' +
                ", type='" + type + '\'' +
                ", prev='" + prev + '\'' +
                ", size=" + size +
                ", checkSum=" + checkSum +
                ", bin=" + bin +
                '}';
    }
}
