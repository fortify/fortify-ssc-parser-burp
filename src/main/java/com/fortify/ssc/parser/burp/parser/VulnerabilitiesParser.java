package com.fortify.ssc.parser.burp.parser;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.fortify.plugin.api.BasicVulnerabilityBuilder.Priority;
import com.fortify.plugin.api.ScanData;
import com.fortify.plugin.api.ScanParsingException;
import com.fortify.plugin.api.StaticVulnerabilityBuilder;
import com.fortify.plugin.api.VulnerabilityHandler;
import com.fortify.ssc.parser.burp.CustomVulnAttribute;
import com.fortify.ssc.parser.burp.domain.Issue;
import com.fortify.ssc.parser.burp.domain.RequestResponse;
import com.fortify.util.ssc.parser.EngineTypeHelper;
import com.fortify.util.ssc.parser.xml.ScanDataStreamingXmlParser;

public class VulnerabilitiesParser {
	private static final String ENGINE_TYPE = EngineTypeHelper.getEngineType();
	private static final Map<String, Priority> MAP_SEVERITY_TO_PRIORITY = Stream.of(
			  new AbstractMap.SimpleImmutableEntry<>("Information", Priority.Low),    
			  new AbstractMap.SimpleImmutableEntry<>("Low", Priority.Medium),
			  new AbstractMap.SimpleImmutableEntry<>("Medium",Priority.High),
              new AbstractMap.SimpleImmutableEntry<>("High",Priority.Critical))
		.collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
	private final ScanData scanData;
	private final VulnerabilityHandler vulnerabilityHandler;
	

    public VulnerabilitiesParser(final ScanData scanData, final VulnerabilityHandler vulnerabilityHandler) {
    	this.scanData = scanData;
		this.vulnerabilityHandler = vulnerabilityHandler;
	}
    
    /**
	 * Main method to commence parsing the input provided by the configured {@link ScanData}.
	 * @throws ScanParsingException
	 * @throws IOException
	 */
	public final void parse() throws ScanParsingException, IOException {
		new ScanDataStreamingXmlParser()
			.handler("/issues/issue", Issue.class, this::buildVulnerabilities)
			.parse(scanData);
	}
	
	
	private final void buildVulnerabilities(Issue issue) {
		StaticVulnerabilityBuilder vb = vulnerabilityHandler.startStaticVulnerability(getInstanceId(issue));
		vb.setEngineType(ENGINE_TYPE);
		//vb.setKingdom(FortifyKingdom.ENVIRONMENT.getKingdomName());
		vb.setAnalyzer("BURP");
		vb.setCategory(issue.getName());
		
		// Set mandatory values to JavaDoc-recommended values
		vb.setAccuracy(5.0f);
		vb.setConfidence(getConfidence(issue));
		vb.setLikelihood(2.5f);
		
		vb.setFileName(getFileName(issue));
		vb.setPriority(getPriority(issue));
		
		vb.setStringCustomAttributeValue(CustomVulnAttribute.severity, issue.getSeverity());
		vb.setStringCustomAttributeValue(CustomVulnAttribute.confidence, issue.getConfidence());
		vb.setStringCustomAttributeValue(CustomVulnAttribute.host, issue.getHost());
		vb.setStringCustomAttributeValue(CustomVulnAttribute.path, issue.getPath());
		vb.setStringCustomAttributeValue(CustomVulnAttribute.issue, getIssueText(issue));
		vb.setStringCustomAttributeValue(CustomVulnAttribute.remediation, getRemediationText(issue));
		vb.setStringCustomAttributeValue(CustomVulnAttribute.references, getReferencesText(issue));
		vb.setStringCustomAttributeValue(CustomVulnAttribute.request, getRequestText(issue));
		vb.setStringCustomAttributeValue(CustomVulnAttribute.response, getResponseText(issue));
		vb.completeVulnerability();
    }

	private String getInstanceId(Issue issue) {
		return issue.getSerialNumber();
	}

	private float getConfidence(Issue issue) {
		return 2.5f; // TODO Map from issue.getConfidence() 
	}

	private String getFileName(Issue issue) {
		String host = issue.getHost();
		String path = issue.getPath();
		
		return Stream.of(host, path)
        	.filter(StringUtils::isNotBlank)
        	.collect(Collectors.joining());
	}

	private String getIssueText(Issue issue) {
		StringBuilder sb = new StringBuilder();
		appendSection(sb, "Details", issue.getIssueDetail());
		appendSection(sb, "Background", issue.getIssueBackground());
		return sb.toString();
	}
	
	private String getRemediationText(Issue issue) {
		StringBuilder sb = new StringBuilder();
		appendSection(sb, "Details", issue.getRemediationDetail());
		appendSection(sb, "Background", issue.getRemediationBackground());
		return sb.toString();
	}
	
	private String getReferencesText(Issue issue) {
		StringBuilder sb = new StringBuilder();
		appendSection(sb, "Classifications", issue.getVulnerabilityClassifications());
		appendSection(sb, "References", issue.getReferences());
		return sb.toString();
	}
	
	private String getRequestText(Issue issue) {
		RequestResponse requestResponse = issue.getRequestresponse();
		return requestResponse==null ? "" : getCodeAsHtml(requestResponse.getRequestDecoded(), 20000);
	}
	
	private String getResponseText(Issue issue) {
		RequestResponse requestResponse = issue.getRequestresponse();
		return requestResponse==null ? "" : getCodeAsHtml(requestResponse.getResponseDecoded(), 20000);
	}
	
	private final String getCodeAsHtml(String code, int maxTotalLength) {
		StringBuilder sb = new StringBuilder();
		if ( StringUtils.isNotBlank(code) ) {
			final String codePrefix = "<pre><code>";
			final String codeSuffix = "</code></pre>";
			final int maxCodeLength = maxTotalLength-codePrefix.length()-codeSuffix.length();
			
			sb.append(codePrefix)
				.append(StringUtils.abbreviate(code, maxCodeLength))
				.append(codeSuffix);
		} 
		return sb.toString();
	}
	
	private final void appendSection(StringBuilder sb, String header, String text) {
		if ( StringUtils.isNotBlank(text) ) {
			sb.append("<b>").append(header).append("</b><br/>\n").append(text).append("<br/>\n");
		}
	}

	private Priority getPriority(Issue issue) {
		return MAP_SEVERITY_TO_PRIORITY.getOrDefault(issue.getSeverity(), Priority.Medium);
	}
	
	
}
