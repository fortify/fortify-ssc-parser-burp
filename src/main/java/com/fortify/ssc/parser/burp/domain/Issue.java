/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.ssc.parser.burp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Issue {
	@JsonProperty private String serialNumber;
	//@JsonProperty private String type;
	@JsonProperty private String name;
	@JsonProperty private String host; // We don't use ip attribute
	@JsonProperty private String path;
	//@JsonProperty private String location;
	@JsonProperty private String severity;
	@JsonProperty private String confidence;
	@JsonProperty private String issueBackground;
	@JsonProperty private String remediationBackground;
	@JsonProperty private String references;
	@JsonProperty private String vulnerabilityClassifications;
	@JsonProperty private String issueDetail;
	//@JsonProperty private IssueDetailItem[] issueDetailsItems;
	@JsonProperty private String remediationDetail;
	@JsonProperty private RequestResponse requestresponse;
	//@JsonProperty private CollaboratorEvent[] collaboratorEvents;
	//@JsonProperty private InfiltratorEvent[] infiltratorEvents;
	//@JsonProperty private StaticAnalysis[] staticAnalysiss;
	//@JsonProperty private DynamicAnalysis[] dynamicAnalysiss;
	
}