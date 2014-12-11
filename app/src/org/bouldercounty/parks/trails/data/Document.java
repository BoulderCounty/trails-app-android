package org.bouldercounty.parks.trails.data;

import java.util.ArrayList;
import java.util.List;

import org.bouldercounty.parks.trails.data.Folder;
import org.bouldercounty.parks.trails.data.Placemark;



public class Document {

	List<Folder> folders = new ArrayList<Folder>();
	List<Placemark> placemarks = new ArrayList<Placemark>();
	public String name;

	public Document() { }

	public Document(List<Folder> folders, List<Placemark> placemarks) { 
		this.folders = folders;
		this.placemarks = placemarks;
	}

	public List<Folder> getFolders() {
		return folders;
	}
	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}
	public void addFolder(Folder folder) {
		this.folders.add(folder);
	}
	public void addFolders(List<Folder> folders) {
		for (Folder f : folders) {
			addFolder(f);
		}
	}
	public List<Placemark> getPlacemarks() {
		return placemarks;
	}
	public void setPlacemarks(List<Placemark> placemarks) {
		this.placemarks = placemarks;
	}
	public void addPlacemark(Placemark placemark) {
		this.placemarks.add(placemark);
	}
	public void addPlacemarks(List<Placemark> placemarks) {
		for (Placemark p: placemarks) {
			addPlacemark(p);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "setName=[" + name + "] " +
		"folder list=[" + printFolderList(folders) + "]" +
		"placemark list=[" + printPlacemarkList(placemarks) + "]";
	}

	protected String printFolderList(List<Folder> folderList) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (null != folderList) {
			for (Folder f : folderList) {
				sb.append(f.toString());
			}
		}
		sb.append("]");
		return sb.toString();
	}

	protected String printPlacemarkList(List<Placemark> pmList) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (null != pmList) {
			for (Placemark pm : pmList) {
				sb.append(pm.toString());
			}
		}
		sb.append("]");
		return sb.toString();
	}

}