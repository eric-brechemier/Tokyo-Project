<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.0"
 
 xmlns:data="http://tokyo.sf.net/prototype1/csv"
>
  <xsl:output method="text" />
  
  <xsl:template match="data:line">
    <xsl:value-of select="data:field[1]"/>
    <xsl:call-template name="Separator"/>
    <xsl:value-of select="data:field[2]"/>
    <xsl:call-template name="Separator"/>
    <xsl:value-of select="data:field[3]"/>
    <xsl:call-template name="newLine"/>
  </xsl:template>
  
  <xsl:template match="text()" />
  
  <xsl:template name="Separator">
    <xsl:text>,</xsl:text>
  </xsl:template>
  
  <xsl:template name="newLine">
    <xsl:text>&#xD;&#xA;</xsl:text>
  </xsl:template>
  
</xsl:transform>