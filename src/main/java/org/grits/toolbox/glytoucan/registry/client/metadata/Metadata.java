package org.grits.toolbox.glytoucan.registry.client.metadata;

public class Metadata
{
    private Integer m_taxon = null;
    private String m_species = null;
    private String m_ueberon = null;
    private String m_tissue = null;
    private String m_cellLine = null;
    private String m_strain = null;

    public Integer getTaxon()
    {
        return this.m_taxon;
    }

    public String getSpecies()
    {
        return this.m_species;
    }

    public String getUeberon()
    {
        return this.m_ueberon;
    }

    public String getTissue()
    {
        return this.m_tissue;
    }

    public String getCellLine()
    {
        return this.m_cellLine;
    }

    public String getStrain()
    {
        return this.m_strain;
    }

    public void setTaxon(Integer a_taxon)
    {
        this.m_taxon = a_taxon;
    }

    public void setSpecies(String a_species)
    {
        this.m_species = a_species;
    }

    public void setUeberon(String a_ueberon)
    {
        this.m_ueberon = a_ueberon;
    }

    public void setTissue(String a_tissue)
    {
        this.m_tissue = a_tissue;
    }

    public void setCellLine(String a_cellLine)
    {
        this.m_cellLine = a_cellLine;
    }

    public void setStrain(String a_strain)
    {
        this.m_strain = a_strain;
    }
}
