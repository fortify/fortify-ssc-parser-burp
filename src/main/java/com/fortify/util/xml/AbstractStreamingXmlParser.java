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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fortify.util.io.Region;
import com.fortify.util.io.RegionInputStream;

/**
 * This abstract class provides functionality for stream-based parsing of arbitrary XML 
 * structures. 
 * TODO Add more information/examples how to use the various
 *      parse methods.
 *      
 * TODO search 'JSON' -> replace 'XML'
 * 
 * @author Ruud Senden
 *
 */
public abstract class AbstractStreamingXmlParser<T extends AbstractStreamingXmlParser<T>> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractStreamingXmlParser.class);
	private final Map<String, XMLStreamReaderHandler> pathToHandlerMap = new LinkedHashMap<>();
	private XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
	private XmlMapper xmlMapper = DefaultXmlMapperFactory.getDefaultObjectMapper();
	@SuppressWarnings("unchecked")
	private T _this = (T)this;

	public final T handler(String path, XMLStreamReaderHandler handler) {
		pathToHandlerMap.put(path, handler);
		return _this;
	}
	
	public final <V> T handler(String path, Class<V> clazz, Consumer<V> handler) {
		return handler(path, xsr->handler.accept(xmlMapper.readValue(xsr, clazz)));
	}
	
	public final <V> T handler(String path, Class<V> clazz, BiConsumer<String, V> handler) {
		return handler(path, xsr->handler.accept(xsr.getLocalName(), xmlMapper.readValue(xsr, clazz)));
	}
	
	public final T objectMapper(XmlMapper objectMapper) {
		this.xmlMapper = objectMapper;
		return _this;
	}

	/**
	 * Parse XML contents retrieved from the given {@link InputStream} using
	 * the previously configured handlers.
	 */ 
	public final void parse(InputStream inputStream) throws XMLStreamException, IOException {
		parse(inputStream, null);
	}
	
	/**
	 * Parse XML contents retrieved from the given {@link InputStream} object
	 * for the given input region, using the previously configured handlers.
	 */
	public final void parse(InputStream inputStream, Region inputRegion) throws XMLStreamException, IOException {
		try ( final InputStream content = new RegionInputStream(inputStream, inputRegion, false) ) {
			final ExtendedXMLStreamReader xsr = new ExtendedXMLStreamReader(xmlInputFactory.createXMLStreamReader(content));
			parse(xsr, "/");
		}
	}

	/**
	 * This method checks whether a {@link XMLStreamReaderHandler} has been registered for the 
	 * current XML element. If a {@link XMLStreamReaderHandler} is found, this method will simply
	 * invoke the {@link XMLStreamReaderHandler} to parse the contents of the current XML element.
	 * If no {@link XMLStreamReaderHandler} is found, this method will continue parsing the next 
	 * element.
	 * 
	 * @param xsr
	 * @param parentPath
	 * @throws IOException
	 */
	private final void parse(final ExtendedXMLStreamReader xsr, String parentPath) throws XMLStreamException, IOException {
		while ( xsr.hasNext() ) {
			switch(xsr.next()) {
				case XMLEvent.START_ELEMENT: handleStartElement(xsr, parentPath); break;
				case XMLEvent.END_ELEMENT: parse(xsr, StringUtils.substringBeforeLast(parentPath, "/"));
			}
		}
	}

	private void handleStartElement(final ExtendedXMLStreamReader xsr, String parentPath) throws IOException, XMLStreamException {
		String currentPath = getPath(parentPath, xsr.getLocalName());
		LOG.trace("Processing "+currentPath);
		XMLStreamReaderHandler handler = pathToHandlerMap.computeIfAbsent(currentPath, k->pathToHandlerMap.get(getPath(parentPath, "*")));
		if ( handler != null ) {
			LOG.debug("Handling "+currentPath);
			handler.handle(xsr);
		} else {
			parse(xsr, currentPath);
		}
	}
	
	/**
	 * Append the given currentName to the given parentPath,
	 * correctly handling the separator.
	 * 
	 * @param parentPath
	 * @param currentName
	 * @return
	 */
	private final String getPath(String parentPath, String currentName) {
		String result = parentPath;
		if ( currentName!=null ) {
			result+=result.endsWith("/")?"":"/";
			result+=currentName;
		}
		if ( "".equals(result) ) { result="/"; }
		return result;
	}
}