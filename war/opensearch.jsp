<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="application/opensearchdescription+xml; charset=UTF-8" language="java" %>

<%
	String domain = request.getServerName();
	String port = Integer.toString(request.getServerPort());
	String base = request.getScheme() + "://" + domain + ":" + port;
%>

<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
	<ShortName>NASfVI</ShortName>
	<LongName>Natürlichsprachiges Anfragesystem für Vorlesungsverzeichnisse im Internet</LongName>
	<Description>Mit NASfVI können verschiedene Anfragen in natürlichem Deutsch an ein Vorlesungsverzeichnis gestellt werden.</Description>
	<Developer>Stefan Partusch</Developer>
	<Language>de-DE</Language>
	<OutputEncoding>UTF-8</OutputEncoding>
	<InputEncoding>UTF-8</InputEncoding>
	<Image height="16" width="16" type="image/x-icon"><%=base%>/favicon.ico</Image>
	<Url type="text/html" template="<%=base%>/\#{searchTerms}&amp;0"/>
	<Url type="application/x-suggestions+json" template="<%=base%>/suggest?q={searchTerms}"/>
	<SyndicationRight>limited</SyndicationRight>
	<AdultContent>false</AdultContent>
</OpenSearchDescription>