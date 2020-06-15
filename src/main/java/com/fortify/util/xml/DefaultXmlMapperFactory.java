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
package com.fortify.util.xml;

import java.util.Date;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fortify.util.jackson.DateConverter;

public class DefaultXmlMapperFactory {
	private static final XmlMapper DEFAULT_OBJECT_MAPPER = _getDefaultObjectMapper();
	
	public static final XmlMapper getDefaultObjectMapper() {
		return DEFAULT_OBJECT_MAPPER;
	}
	
	/**
	 * <p>This method returns a default {@link XmlMapper} instance used for
	 * mapping XML data to Java objects/values. This is mostly a default
	 * {@link XmlMapper} configuration, apart from the following:</p>
	 * 
	 * <ul>
	 *  <li>Configured to not fail on missing properties</li>
	 *  <li>Adds a deserializer based on {@link DateConverter} for
	 *      deserializing date strings</li>
	 */
	private static final XmlMapper _getDefaultObjectMapper() {
		XmlMapper mapper = new XmlMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Date.class, new StdDelegatingDeserializer<Date>(DateConverter.getInstance()));
		mapper.registerModule(module);
		return mapper;
	}
}
