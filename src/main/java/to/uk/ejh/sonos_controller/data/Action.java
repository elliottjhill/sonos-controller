package to.uk.ejh.sonos_controller.data;

/**
 * Enum of all actions that we currently support for the SONOS system
 * 
 * @author Elliott Hill
 *
 */
public enum Action {
	PLAY("<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
				" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
				"<s:Body>" +
					"<u:Play xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">" +
						"<InstanceID>0</InstanceID>" +
						"<Speed>1</Speed>" +
					"</u:Play>" +
				"</s:Body>" +
		"</s:Envelope>", "\"urn:schemas-upnp-org:service:AVTransport:1#Play\"" );
	// The XML required to perform the action
	private String xml;
	// The Header "SOAPACTION" value to sent to the SONOS system
	private String soapAction;
	
	private Action(String xml, String soapAction) {
		this.xml = xml;
		this.soapAction = soapAction;
	}
	
	/**
	 * Get the XML to send as a SOAP body to request the Sonos system to complete the action
	 * @return
	 */
	public String getXml() {
		return xml;
	}
	
	/**
	 * Get the "SOAPACTION" value to send as a HTTP header to the SONOS system
	 * @return
	 */
	public String getSoapAction() {
		return soapAction;
	}
	
	/**
	 * Get the Action object whose name matches the given String
	 * @param s	The String of the action to return
	 * @return	The Action object matching the String, or null if none match
	 */
	public static Action fromString(String s) {
		for (Action a : Action.values()) {
			if (a.toString().equalsIgnoreCase(s)) {
				return a;
			}
		}
		return null;
	}
}
