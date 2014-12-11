package org.bouldercounty.parks.trails.utilities;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.bouldercounty.parks.trails.data.Document;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;



public class XMLSaxParser {

	public static Document getParsedData(String xmlSource) 
	{
		
		SAXParserFactory spf;
		SAXParser sp;
		XMLReader xr;
		/* Create a new ContentHandler and apply it to the XML-Reader*/
		XMLSaxHandler messageHandler = new XMLSaxHandler();

		try {
			
			/* Get a SAXParser from the SAXPArserFactory. */
			spf = SAXParserFactory.newInstance();
			sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			xr = sp.getXMLReader();

			xr.setContentHandler(messageHandler);

			/* Parse the xml-data */
			InputSource isrc = new InputSource();
			ByteArrayInputStream bs = new ByteArrayInputStream(xmlSource.getBytes());
			isrc.setByteStream(bs);
			xr.parse(isrc);
			/* Parsing has finished. */ 

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("XMLSaxParser", "Exception : " + e.toString());
		}

		return messageHandler.getParsedData();
	}
}
