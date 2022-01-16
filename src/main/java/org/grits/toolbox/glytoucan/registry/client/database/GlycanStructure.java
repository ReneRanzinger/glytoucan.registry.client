package org.grits.toolbox.glytoucan.registry.client.database;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

@XmlType
public class GlycanStructure
{
    private String m_id = null;
    private String m_sequence = null;
    private String m_sequenceFormat = null;
    private String m_GWBSequence = null;

    public String getId()
    {
        return this.m_id;
    }

    @XmlAttribute(name = "id")
    public void setId(String a_id)
    {
        this.m_id = a_id;
    }

    public String getSequenceFormat()
    {
        return this.m_sequenceFormat;
    }

    @XmlAttribute(name = "sequenceFormat")
    public void setSequenceFormat(String a_sequenceFormat)
    {
        this.m_sequenceFormat = a_sequenceFormat;
    }

    public String getSequence()
    {
        return this.m_sequence;
    }

    @XmlAttribute(name = "sequence")
    public void setSequence(String a_sequence)
    {
        this.m_sequence = a_sequence;
    }

    public String getGWBSequence()
    {
        return this.m_GWBSequence;
    }

    @XmlAttribute(name = "GWBSequence")
    public void setGWBSequence(String GWBSequence)
    {
        this.m_GWBSequence = GWBSequence;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof GlycanStructure))
            return false;

        GlycanStructure other = (GlycanStructure) obj;
        if ((this.m_id != null && other.getId() == null)
                || (this.m_id == null && other.getId() != null) || !this.m_id.equals(other.getId()))
            return false;

        if ((this.m_sequence != null && other.getSequence() == null)
                || (this.m_sequence == null && other.getSequence() != null)
                || !this.m_sequence.equals(other.getSequence()))
            return false;

        if ((this.m_GWBSequence != null && other.getGWBSequence() == null)
                || (this.m_GWBSequence == null && other.getGWBSequence() != null)
                || !this.m_GWBSequence.equals(other.getGWBSequence()))
            return false;

        return true;
    }

}
