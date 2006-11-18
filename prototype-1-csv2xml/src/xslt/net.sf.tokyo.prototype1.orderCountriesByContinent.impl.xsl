<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.0"
 
 xmlns:data="http://tokyo.sf.net/prototype1/csv"
>
  <!--
  Implementation part of XSL Transformation:
  Reorder {"countries","http://tokyo.sf.net/prototype1/countries"}
  -->
  
  <xsl:template match="data:csv">
    <xsl:copy>
      <xsl:copy-of select="@*" />
      <xsl:apply-templates select="data:line">
        <xsl:sort select="data:field[3]" />
        <xsl:sort select="data:field[1]" />
      </xsl:apply-templates>
    </xsl:copy>
  </xsl:template>
  
  <xsl:template match="data:line">
    <xsl:copy-of select="." />
  </xsl:template>

</xsl:transform>