package org.bouldercounty.parks.trails.data;


import java.util.HashMap;
import java.util.Map;


public class TrailAddressMap {

	public static final String tag = "TrailAddressMap";

	private static Map<String, String[]> addressMap = new HashMap<String, String[]>();
//	private static Map<String, Map<String, String>> _addressMap = new HashMap<String, Map<String, String>>();

	public TrailAddressMap() {
		initializeList();
	}

	private static void initializeList() {
//		if (addressMap.size() == 0) {
			addParkingLocation("anne", new String[] {"300 Pinto Dr Boulder CO 80302"} );
			addParkingLocation("bald", new String[] {"4245 Sunshine Canyon Dr Boulder CO 80302"} );
			addParkingLocation("betasso", new String[] {"623 Betasso Road Boulder CO 80302"} );

			addParkingLocation("caribou", new String[] {"1521 CR 126 Nederland CO 80466"} );
			addParkingLocation("carolyn", new String[] {"2234 South 104th Street, Broomfield, CO 80020"} );
			addParkingLocation("coalton", new String[] {"3309 McCaslin Blvd. Superior co 80027"} );

			addParkingLocation("hall", new String[] {"31635 CO Hwy 7 Lyons CO 80540"} );
			addParkingLocation("heil", new String[] {"1188 Geer Canyon Drive, Boulder, CO 80302","182 Red Gulch Road, Lyons, CO 80540"} );
			addParkingLocation("lagerman", new String[] {"7100 Pike Road, Longmont, CO 80503"} );
			addParkingLocation("legion", new String[] {"6976 Arapahoe Road, Boulder, CO 80303"} );

			addParkingLocation("mud", new String[] {"1521 CR 126 Nederland CO 80466"} );
			addParkingLocation("niwot", new String[] {"7075 N. 83rd St. Niwot CO 80503", "7710 Monarch Road, Niwot, CO 80503", "6284 North 79 Street, Niwot, CO 80503"} );
			addParkingLocation("pella", new String[] {"11600 N. 75th St. Longmont CO 80503"} );
			addParkingLocation("rabbit", new String[] {"15065 N. 55th St. Longmont CO 80503"} );

			addParkingLocation("twin", new String[] {"6500 Nautilus Dr Boulder CO 80301"} );
			addParkingLocation("walden", new String[] {"3898 Walden Ponds Boulder, CO 80301"} );
			addParkingLocation("walker", new String[] {"230 Bison Dr. Boulder, CO 80302", "7250 Flagstaff Rd. Boulder CO 80302", "7701 Flagstaff Rd. Boulder CO 80302"} );
//		}
	}

	private /*Map<String, String[]>*/ static void addParkingLocation(String id, String[] areas) {
		addressMap.put(id, areas);
	}

	public static String getParkingLocation(String id) {
		if (null == addressMap || addressMap.size() == 0) {
			initializeList();
			return addressMap.get(id)[0];
		} else {
			return addressMap.get(id)[0];
		}
	}

}