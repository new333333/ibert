<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" >
 <xsl:output method="text" encoding="UTF-8" indent="yes"/>

<xsl:template match="text()">
 <xsl:text> </xsl:text><xsl:value-of select="."/> <xsl:text> </xsl:text>
 </xsl:template>
</xsl:stylesheet>