<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
  <html>
  <body>
  <h2>Hallgatok apple-template</h2>
  <xsl:apply-templates/>
  </body>
  </html>
</xsl:template>

<xsl:template match="@id">
  <p>
  <xsl:apply-templates select="vezeteknev"/>
  <xsl:apply-templates select="keresztnev"/>
  <xsl:apply-templates select="becenev"/>
  <xsl:apply-templates select="kor"/>
  </p>
</xsl:template>

<xsl:template match="title">
  Vezetéknév: <span style="color:#ff0000">
  <xsl:value-of select="."/></span>
  <br />
</xsl:template>

<xsl:template match="artist">
  Keresztnév: <span style="color:#16ff00">
  <xsl:value-of select="."/></span>
  <br />
</xsl:template>
<xsl:template match="becenev">
  Becenév: <span style="color:#00ff00">
  <xsl:value-of select="."/></span>
  <br />
</xsl:template>
<xsl:template match="kor">
  Kor: <span style="color:#00ff57">
  <xsl:value-of select="."/></span>
  <br />
</xsl:template>

</xsl:stylesheet>