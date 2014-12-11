package org.bouldercounty.parks.trails.utilities;


import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import org.bouldercounty.parks.trails.data.Document;
import org.bouldercounty.parks.trails.data.Folder;
import org.bouldercounty.parks.trails.data.Placemark;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class XMLSaxHandler extends DefaultHandler{

	public static String tag = "XMLSaxHandler";

	private boolean in_placemarktag = false;

	private boolean in_documentnametag = false;
	private boolean in_foldernametag = false;
	private boolean in_placemarknametag = false;

	private boolean in_coordinatestag = false;

	private boolean in_addresstag = false;
	private boolean in_descriptiontag = false;
	private boolean in_documenttag = false;
	private boolean in_foldertag = false;

	private List<Folder> folderStack = new ArrayList<Folder>();

	private List<String> tmpStringList = new ArrayList<String>();

	public Document kmlDocument = new Document();

	private List<Folder> folders = new ArrayList<Folder>();
	private Folder folder = new Folder();
	
	private List<Placemark> folderDataPlacemarks = new ArrayList<Placemark>(); // for folders
	private List<Placemark> docDataPlacemarks = new ArrayList<Placemark>(); // for document
	
	Placemark dpm = new Placemark();


	public Document getParsedData() {
		return this.kmlDocument;
	}

	@Override
	public void startDocument() throws SAXException {
		this.kmlDocument = new Document();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/**
	 * Gets called on opening tags like: <tag> Can provide attribute(s), when
	 * xml was like: <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {

		if (localName.equals("Document")) {
			this.in_documenttag = true;
		} else if (localName.equals("Placemark")) {
			this.in_placemarktag = true;
			if ( atts.getLength() > 0 && null != atts.getValue("id") ) {
				dpm.setId( atts.getValue("id") );
			}
		} else if (localName.equals("Folder")) {
			if (this.in_foldertag) {
				// put the previous folder on the stack - hopefully it has a frickin name already!
				folderStack.add(folderStack.size(), folder);
				folder = new Folder(); // takes place of outer/trail folder
			}
			this.in_foldertag = true;
		} else if (localName.equals("description")) {
			this.in_descriptiontag = true;
		} else if (localName.equals("name")) {
			if (in_placemarktag) {
				this.in_placemarknametag = true;
			} else if (in_foldertag) {
				this.in_foldernametag = true;
			} else if (in_documenttag) {
				this.in_documentnametag = true;
			}
		} else if (localName.equals("coordinates")) {
			this.in_coordinatestag = true;
		} else if (localName.equals("Address")) {
			this.in_addresstag = true;
		} else {

		}
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
	{
		try { 
			if (localName.equals("Document")) {
				this.in_documenttag = false;
				// set folder in document
				kmlDocument.setPlacemarks(docDataPlacemarks);
				kmlDocument.setFolders(folders);
			} else if (localName.equals("Placemark")) {
				this.in_placemarktag = false;
				if (!in_foldertag) {
						this.docDataPlacemarks.add(dpm);
				} else {
					this.folderDataPlacemarks.add(dpm);
				}
				dpm = new Placemark();
			} else if (localName.equals("Folder")) {
				// take care of the current folder...
				if (folderDataPlacemarks.size() > 0) {
					folder.addPlacemarks(folderDataPlacemarks);
				}
				 if (folderStack.size() == 0) {
						folders.add(folder);
					 folder.trailSystemName = folder.name;
					this.in_foldertag = false;
					 folder = new Folder(); 
					 folders = new ArrayList<Folder>();
				 } else {
					 Folder tmpFolder = folder;

					 // now set the last folder from the stack as the current folder
					 folder = folderStack.remove(folderStack.size()-1);

					 // set parentFolder, trailSystem name
					 tmpFolder.trailSystemName = folder.getName();
					 tmpFolder.parentFolder = folder;

					 folders.add(tmpFolder);
					 folder.addFolders(folders);

					 // set the folders list on the popped/current folder
					 folders = new ArrayList<Folder>();
				 }
				 // reset variables
				 folders = new ArrayList<Folder>();
				 folderDataPlacemarks = new ArrayList<Placemark>();
			} else if (localName.equals("name")) {
				if (in_placemarktag) {
					this.in_placemarknametag = false;
				} else if (in_foldertag) {
					this.in_foldernametag = false;
				} else if (in_documenttag) {
					this.in_documentnametag = false;
				}
			} else if (localName.equals("description")) {
				this.in_descriptiontag = false;
			} else if (localName.equals("coordinates")) {
				this.in_coordinatestag = false;
				this.dpm.addCoordinates(tmpStringList); // moved from in_placemarktag to here...
				tmpStringList = new ArrayList<String>();
			} else if (localName.equals("Address")) {
				this.in_addresstag = false;
//				Log.e(tag, "pm.parkingAddress=[" + this.dpm.parkingAddress + "]");
			} else {
//				Log.v(tag+".endElement = ", localName);
			}
		} catch (Exception e) {
			Log.e(tag, "Exception: " + e.getMessage());
			Log.e(tag, "Exception: " + e.toString());
		}
//		Log.e(tag, "");
	}

	/**
	 * Gets called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.in_documentnametag) {
			kmlDocument.setName(new String(ch, start, length));
//			Log.e(tag, "in_documentnametag=[" + new String(ch, start, length));
		} else if (this.in_foldernametag) {
			folder.setName(new String(ch, start, length));
//			Log.e(tag, "in_foldernametag=[" + new String(ch, start, length));
		} else if (this.in_placemarknametag) {
			dpm.setName(new String(ch, start, length));
//			Log.e(tag, "in_placemarknametag=[" + new String(ch, start, length));
		} else if (this.in_addresstag) {
			dpm.parkingAddress = new String(ch, start, length);
			dpm.isParking = true;
//			Log.e(tag, "in_addresstag=[" + new String(ch, start, length));
		} else if (this.in_descriptiontag) {
			String s = new String(ch, start, length);
//			Log.e(tag, "************* original description [" + s + "]*************");
			if (null != s && s.length() > 2) {
				if (s.contains(",")) {
					parseDescription(s, dpm);
					dpm.setDescription(s);
				} else {
//					Log.e(tag, "no ',' found!!!!!");
				}
			}
		} else if (this.in_coordinatestag) {
			tmpStringList.add(new String(ch, start, length));
		} else {
		}
	}

	private boolean parseDescription(String description, Placemark p) {
		try {
				if (!description.contains("Parking")) {
					  String[] descArr = description.split(",");
					  p.trailDistance = descArr[0];
					  if (descArr[descArr.length-1].contains("Easy") || 
						descArr[descArr.length-1].contains("Mod") || 
						descArr[descArr.length-1].contains("Diff")) 
					  {
						  p.trailDifficulty = descArr[descArr.length-1];
					  }
					  setTrailUses(descArr, p);
				}
		} catch (Exception e) {
			Log.e(tag, "Exception : " + e.getMessage());
		}
	  return true;
	}

	private void setTrailUses(String[] descArr, Placemark p) {

		  StringBuilder extras = new StringBuilder();

		  for (int d=1; d<descArr.length; d++) {
				if (descArr[d].contains("horse") || descArr[d].contains("Horse")) {
//					Log.e(tag, "horse = [" + descArr[d] + "]");
					p.horse = true;
				} else if ((descArr[d].contains("dog") || descArr[d].contains("Dog")) 
//						&& !(descArr[d].contains("Off") || descArr[d].contains("off"))
						) 
				{
//					Log.e(tag, "dog = [" + descArr[d] + "]");
					p.dog = true;
				} else if (descArr[d].contains("hiking") || descArr[d].contains("Hiking")) {
//					Log.e(tag, "hike = [" + descArr[d] + "]");
					p.hike = true;
				} else if ( (descArr[d].contains("bike") || descArr[d].contains("Bike")) 
						&& !(descArr[d].contains("no") || descArr[d].contains("No")) 
						) 
				{
//					Log.e(tag, "bike = [" + descArr[d] + "]");
					p.bike = true;
				} else {
					// set extradescription here...
					if (d<descArr.length-1) {
							extras.append(descArr[d]);
							p.extraDescription = extras.toString();
					}
				}
		  }
	}

}