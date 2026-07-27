package com.fortify.ssc.parser.burp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * This class represents the dynamic analysis information associated with a
 * security issue identified by the Burp Suite scanner. It contains various
 * properties that capture details about the source and sink of the
 * vulnerability, stack traces, event handling data, and other relevant
 * information that can aid in understanding the nature of the vulnerability and
 * its potential impact.
 * 
 * @author Sangamesh Vijaykumar
 */
@Data
public class DynamicAnalysis {
    @JsonProperty
    private String source;
    @JsonProperty
    private String sink;
    @JsonProperty
    private String sourceStackTrace;
    @JsonProperty
    private String sinkStackTrace;
    @JsonProperty
    private String eventListenerStackTrace;
    @JsonProperty
    private String sourceValue;
    @JsonProperty
    private String sinkValue;
    @JsonProperty
    private String eventHandlerData;
    @JsonProperty
    private String eventHandlerDataType;
    @JsonProperty
    private String eventHandlerManipulatedData;
    @JsonProperty
    private String poc;
    @JsonProperty
    private String origin;
    @JsonProperty
    private String isOriginChecked;
    @JsonProperty
    private String sourceElementId;
    @JsonProperty
    private String sourceElementName;
    @JsonProperty
    private String eventFiredEventName;
    @JsonProperty
    private String eventFiredElementId;
    @JsonProperty
    private String eventFiredElementName;
    @JsonProperty
    private String eventFiredOuterHtml;
}