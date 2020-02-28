<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:zul="http://www.zkoss.org/2005/zul">

	<xsl:template
		match="zul:tree/zul:treechildren/zul:treeitem[5]"
		priority="3">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>

		<zul:treeitem>
			<zscript><![CDATA[boolean canViewRecertification = com.soffid.iam.utils.Security.isUserInRole("recertification:query")]]></zscript>
			<zul:treerow>
				<zul:apptreecell langlabel="recertification.title" if="${displayRecertification}"
					pagina="addon/recertification/recertification.zul">
					<xsl:attribute name="if">${canViewRecertification}</xsl:attribute>
				</zul:apptreecell>
			</zul:treerow>
		</zul:treeitem>
	</xsl:template>

	<xsl:template match="node()|@*" priority="2">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>