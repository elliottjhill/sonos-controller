package to.uk.ejh.sonos_controller.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.impl.SimpleLog;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import to.uk.ejh.sonos_controller.data.Action;

@Controller
@RequestMapping("/Control/**")
public class ActionController {
	
	// Client to make Http Requests, TODO: Move into a separate class if necessary 
	private static CloseableHttpClient client = HttpClients.createDefault();
	/** The URL of the SONOS system on the local network */
	private static final String SONOS_URL = 
			"http://192.168.1.70:1400/MediaRenderer/AVTransport/Control";
	/** Object to represent when there was an error making the HTTP request */
	private static final CloseableHttpResponse ERROR = null;
	// Logger Object 
	private static final SimpleLog LOG = new SimpleLog("Connection Log");
	
	@RequestMapping(method = RequestMethod.GET)
	protected ModelAndView handleRequestInternal(HttpServletRequest httpReq,
			HttpServletResponse servletResponse) throws Exception {
		
		ModelAndView model = new ModelAndView("ActionController");
		model.addObject("msg", "hello world, This is more information");
		String actionReq = httpReq.getServletPath().replace("/Control/", "").replace(".htm", "");
		
		Action action = Action.fromString(actionReq);
		if (action == null) {
			model.addObject("response", "Action not found: "+actionReq);
			return model;
		}
		CloseableHttpResponse resp = makeSonosRequest(action);
		
		model.addObject("code", String.valueOf(resp.getStatusLine().getStatusCode()));
		model.addObject("response", "No response");
		
		try{
			HttpEntity content = resp.getEntity();
			if (content != null) {
				model.addObject("response", EntityUtils.toString(content));
			}
		} finally {
			resp.close();
		}
		
		return model;
	}
	
	/**
	 * Make the SOAP request to the SONOS system to peform the selected {@link Action}
	 * 
	 * @param action	The action to perform
	 * @return			A {@link CloseableHttpResponse} object to get the details of the connection
	 */
	private CloseableHttpResponse makeSonosRequest(Action action) {
		try {
			HttpPost post = new HttpPost(SONOS_URL);
			post.setHeader("CONTENT-TYPE", "text/xml; charset=\"utf-8\"");
			post.setHeader("SOAPACTION", action.getSoapAction());
			post.setEntity(new StringEntity(action.getXml()));
			return client.execute(post);
		} catch (ClientProtocolException cpe) {
			LOG.error("Error requesting "+action, cpe);
		} catch (UnsupportedEncodingException uee) {
			LOG.error("Error requesting "+action, uee);
		} catch (IOException ioe) {
			LOG.error("Error requesting "+action, ioe);
		}
		return ERROR;
	}

}
