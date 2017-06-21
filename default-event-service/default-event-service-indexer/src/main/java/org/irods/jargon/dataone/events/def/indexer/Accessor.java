package org.irods.jargon.dataone.events.def.indexer;

public class Accessor {

	private String name = "";
	private String zone = "";

	public Accessor() {
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}

	/**
	 * @param zone
	 *            the zone to set
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Accessor [");
		if (name != null) {
			builder.append("name=").append(name).append(", ");
		}
		if (zone != null) {
			builder.append("zone=").append(zone);
		}
		builder.append("]");
		return builder.toString();
	}

}
