<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan" xmlns:m="http://maven.apache.org/POM/4.0.0" xmlns="http://maven.apache.org/POM/4.0.0" exclude-result-prefixes="xalan xsl m">

	<xsl:output method="xml" encoding="UTF-8" indent="yes" xalan:indent-amount="4" />

	<xsl:preserve-space elements="*" />

	<xsl:template match="/m:project/m:properties[not(m:licensing.skip)]">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
			<xsl:comment>
				Disable checking that your dependencies all have license blocks and none of them are GPL or LGPL.
				You likely don't want this without some configuration. See the code-checking-rules project for
				how we use that internally in EP.
			</xsl:comment>
			<licensing.skip>true</licensing.skip>
		</xsl:copy>
	</xsl:template>

	<!-- Add the artifactId, groupId, and version as property replacements -->
	<xsl:template match="m:parent">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>

		<groupId>${groupId}</groupId>
		<artifactId>${artifactId}</artifactId>
		<version>${version}</version>
	</xsl:template>

	<!-- remove 'artifactId' of this project, it's added by the m:parent match up above -->
	<xsl:template match="/m:project/m:artifactId">
	</xsl:template>

	<!-- remove 'groupId' of this project, it's added by the m:parent match up above -->
	<xsl:template match="/m:project/m:groupId">
	</xsl:template>

	<!-- remove 'version' of this project, it's added by the m:parent match up above -->
	<xsl:template match="/m:project/m:version">
	</xsl:template>

	<!-- remove 'with-archetypes' profile -->
	<xsl:template match="m:profile[m:id='with-archetypes']">
	</xsl:template>

	<!-- remove 'build-server-properties' profile -->
	<xsl:template match="m:profile[m:id='build-server-properties']">
	</xsl:template>

	<!-- remove 'build-server-repositories' profile -->
	<xsl:template match="m:profile[m:id='build-server-repositories']">
	</xsl:template>

	<!-- remove 'archetype-goal' properties -->
	<xsl:template match="m:properties/m:archetype-goal">
	</xsl:template>

	<!-- remove 'archetype-invoker-build-server-profile-1' properties -->
	<xsl:template match="m:properties/m:archetype-invoker-build-server-profile-1">
	</xsl:template>

	<!-- remove 'archetype-invoker-build-server-profile-2' properties -->
	<xsl:template match="m:properties/m:archetype-invoker-build-server-profile-2">
	</xsl:template>

	<!-- remove 'archetype-invoker-snapshot-repository-id' properties -->
	<xsl:template match="m:properties/m:archetype-invoker-snapshot-repository-id">
	</xsl:template>


	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()" />
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>

