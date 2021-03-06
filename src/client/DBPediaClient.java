package client;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class DBPediaClient {
	
	public static String DBPEDIA_URL_SEARCH = "http://dbpedia.org/sparql?query=";
	
	//Queries for companies
	public static String COMPANY_QUERY_START_NAME = "select ?uri where {?uri dbpprop:name \"";
	public static String COMPANY_QUERY_START_SHORTNAME = "select ?uri where {?uri  rdfs:label \"";
	public static String COMPANY_QUERY_START_SYMBOL = "select ?uri where {?uri dbpprop:symbol \"";
	public static String COMPANY_QUERY_END = "\"@en . ?uri rdf:type dbpedia-owl:Company}";
	
	//Queries for places
	public static String CITY_QUERY_START = "select ?uri where {?uri dbpprop:name \"";
	public static String CITY_QUERY_MIDDLE = "\"@en . ?uri rdf:type dbpedia-owl:Place .?uri dbpedia-owl:country dbpedia:";
	public static String CITY_QUERY_END = " .}";
	
	
	public String searchCityResource(String cityName, String country) throws IOException{
		String modifiedCounty = country.replace(" ", "_");
		String query = DBPediaClient.CITY_QUERY_START + cityName + DBPediaClient.CITY_QUERY_MIDDLE + modifiedCounty + DBPediaClient.CITY_QUERY_END;
		String url = DBPediaClient.DBPEDIA_URL_SEARCH;
		url = url + URLEncoder.encode(query, "UTF-8");

		String uri = executeQuery(url);
		return uri;
	}
	
	public String searchCompanyResource(String companyName, String companyShortname, String companySymbol) throws IOException{
		String uri= "";
		uri = this.searchCompanyBySymbol(companySymbol);
		if (uri.isEmpty()){
			uri = this.searchCompanyByName(companyName);
			if (uri.isEmpty()){
				uri = this.searchCompanyByShortname(companyShortname);
			}
		}
		return uri;		
	}

	private String searchCompanyByName(String companyName) throws IOException{
		String url = DBPediaClient.DBPEDIA_URL_SEARCH;
		String query = DBPediaClient.COMPANY_QUERY_START_NAME + companyName + DBPediaClient.COMPANY_QUERY_END;
		url = url + URLEncoder.encode(query, "UTF-8");
		String uri = executeQuery(url);
		return uri;
	}
	
	private String searchCompanyByShortname(String companyShortname) throws IOException{
		String url = DBPediaClient.DBPEDIA_URL_SEARCH;
		String query = DBPediaClient.COMPANY_QUERY_START_SHORTNAME + companyShortname + DBPediaClient.COMPANY_QUERY_END;
		url = url + URLEncoder.encode(query, "UTF-8");
		String uri = executeQuery(url);
		return uri;
	}
	
	private String searchCompanyBySymbol(String companySymbol) throws IOException{
		String url = DBPediaClient.DBPEDIA_URL_SEARCH;
		String query = DBPediaClient.COMPANY_QUERY_START_SYMBOL + companySymbol + DBPediaClient.COMPANY_QUERY_END;
		url = url + URLEncoder.encode(query, "UTF-8");
		String uri = executeQuery(url);
		return uri;
	}

	private String executeQuery(String url) throws IOException {
		ClientResource resource = new ClientResource(url);  
		Representation repr = resource.get();
		String result = repr.getText();
		String uri = this.getUri(result);
		return uri;
	}
	
	String getUri(String result){
		//Pattern pattern = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		Pattern pattern = Pattern.compile("http://dbpedia.org/resource/[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		Matcher matcher = pattern.matcher(result);
		String uri = "";
		while (matcher.find()) {
			uri = matcher.group();
		}
		
		return uri;
	}

}
