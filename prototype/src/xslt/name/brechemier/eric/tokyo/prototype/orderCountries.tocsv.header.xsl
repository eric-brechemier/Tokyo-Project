<xsl:transform
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 version="1.0"
 
 xmlns:data="http://eric.brechemier.name/tokyo/prototype/countries"
>
  <xsl:output method="data:name.brechemier.eric.tokyo.prototype.SaxonCustomSerializerAdapter" />
  
  <xsl:include href="orderCountries.impl.xsl" />

</xsl:transform>