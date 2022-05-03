package com.fortify.ssc.parser.burp.parser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fortify.plugin.api.ScanBuilder;
import com.fortify.plugin.api.ScanData;
import com.fortify.plugin.api.ScanParsingException;
import com.fortify.util.ssc.parser.xml.ScanDataStreamingXmlParser;
import com.fortify.util.xml.ExtendedXMLStreamReader;

public class ScanParser {
	private static final Logger LOG = LoggerFactory.getLogger(ScanParser.class);
	private final ScanData scanData;
    private final ScanBuilder scanBuilder;
    
	public ScanParser(final ScanData scanData, final ScanBuilder scanBuilder) {
		this.scanData = scanData;
		this.scanBuilder = scanBuilder;
	}
	
	public final void parse() throws ScanParsingException, IOException {
		new ScanDataStreamingXmlParser()
			.handler("/issues", this::processIssuesAttributes)
			.parse(scanData);
		scanBuilder.completeScan();
	}
	
	private final void processIssuesAttributes(ExtendedXMLStreamReader xsr) {
		scanBuilder.setScanDate(parseScanDate(xsr.getAttributeValue(null, "exportTime")));
		scanBuilder.setEngineVersion(xsr.getAttributeValue(null, "burpVersion"));
		xsr.setSkipRemaining(true);
	}
	
	private final Date parseScanDate(String dateString) {
		Date date;
		try {
			date = new SimpleDateFormat("EEE MMM dd H:m:s z yyyy").parse(dateString);
		} catch (ParseException e) {
			LOG.warn(String.format("Error parsing {} as date", dateString), e);
			date = new Date();
		}
		return date;
	}
}
