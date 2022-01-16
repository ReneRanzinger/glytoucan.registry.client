package org.grits.toolbox.glytoucan.registry.client.om;

import java.awt.image.BufferedImage;

public class ImageInformation
{
    private BufferedImage m_image = null;
    private byte[] m_imageData = null;
    private Integer m_imageIndex = null;

    public BufferedImage getImage()
    {
        return this.m_image;
    }

    public void setImage(BufferedImage a_image)
    {
        this.m_image = a_image;
    }

    public byte[] getImageData()
    {
        return this.m_imageData;
    }

    public void setImageData(byte[] a_imageData)
    {
        this.m_imageData = a_imageData;
    }

    public Integer getImageIndex()
    {
        return this.m_imageIndex;
    }

    public void setImageIndex(Integer a_imageIndex)
    {
        this.m_imageIndex = a_imageIndex;
    }
}
