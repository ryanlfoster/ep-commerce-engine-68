<?xml version='1.0' encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:template match="/">
  	<!-- Lets remove the response header from SOLR output -->
    <xsl:apply-templates select="response/result"/>
  </xsl:template>
  
  <xsl:template match="*">
  	<xsl:copy-of select="."/>
  </xsl:template>

</xsl:stylesheet>
