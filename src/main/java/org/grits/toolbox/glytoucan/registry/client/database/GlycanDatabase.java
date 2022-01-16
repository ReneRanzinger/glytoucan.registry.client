package org.grits.toolbox.glytoucan.registry.client.database;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * Storage object for a glycan database. This class corresponds to the XML file
 * that contains the database and can be (de)serialized from/to the XML file
 * using JAXB. The class represents the &lt;database&gt; main tag.
 *
 * @author rene
 *
 */
@XmlRootElement(name = "database")
public class GlycanDatabase
{
    /** Name of the database */
    private String m_name = null;
    /**
     * List of glycans that are stored in the database, Each glycan is
     * represented by an individual &lt;glycan&gt; tag.
     */
    @XmlElement(name = "glycan")
    private List<GlycanStructure> m_structures = new ArrayList<GlycanStructure>();
    /** Description text of the database */
    private String m_description = null;
    /**
     * Number of structures in the database, this should be equal to
     * m_structures.size().
     */
    private Integer m_structureCount = null;
    /** Version of the database */
    private String m_version = "1.0";

    public String getName()
    {
        return this.m_name;
    }

    @XmlAttribute
    public void setName(String a_name)
    {
        this.m_name = a_name;
    }

    @XmlTransient
    public List<GlycanStructure> getStructures()
    {
        return this.m_structures;
    }

    public void setStructures(List<GlycanStructure> a_structures)
    {
        this.m_structures = a_structures;
    }

    public boolean addStructure(GlycanStructure a_structure)
    {
        return this.m_structures.add(a_structure);
    }

    public String getDescription()
    {
        return this.m_description;
    }

    @XmlAttribute
    public void setDescription(String a_description)
    {
        this.m_description = a_description;
    }

    public Integer getStructureCount()
    {
        return this.m_structureCount;
    }

    @XmlAttribute
    public void setStructureCount(Integer a_structureCount)
    {
        this.m_structureCount = a_structureCount;
    }

    public String getVersion()
    {
        return this.m_version;
    }

    @XmlAttribute
    public void setVersion(String a_version)
    {
        this.m_version = a_version;
    }
}
