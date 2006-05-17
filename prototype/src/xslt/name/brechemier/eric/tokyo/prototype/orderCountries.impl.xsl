<xsl:transform
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.0"
 
 xmlns:data="http://eric.brechemier.name/tokyo/prototype/countries"
>
  <xsl:template match="data:file">
    <xsl:copy>
      <xsl:apply-templates select="data:country">
        <xsl:sort select="@continent" />
        <xsl:sort select="@country" />
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="data:country">
    <xsl:copy-of select="." />
  </xsl:template>

</xsl:transform>