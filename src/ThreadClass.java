import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

class ThreadClass implements Runnable { 

	Connection _con;
    PreparedStatement _pstm;
    String code;
    int _platforms;
    
  public ThreadClass (String s, int platforms, Connection con, PreparedStatement pstm) { 
    super(); 
    _con = con;
    _pstm = pstm ;
    code = s;
    _platforms = platforms;
  }

  @Override
  public void run() {
	  System.out.println("Run: "+ code); 
	    
	   // int platforms = getPlatformsMethod2(code);
		 

		//int platforms = getPlatforms(code);
	  	/*  
		if (platforms != 0 && platforms > _platforms) {

			String sql = "UPDATE STATION SET PLATFORMS = '" + platforms + "' where CODE = '" + code + "'";
			System.out.println(sql);
			 try {
				_pstm = (PreparedStatement) _con.prepareStatement(sql);
				_pstm.execute();
				 _con.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			// System.out.println(days);

		}*/
	  String station = getStationName(code);

		if (station!=null) {

			String sql = "UPDATE STATION SET NAME = '" + station + "' where CODE = '" + code + "'";
			System.out.println(sql);
			
			 try {
				_pstm = (PreparedStatement) _con.prepareStatement(sql);
				_pstm.execute();
				 _con.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			// System.out.println(days);

		}
  	
  }
  
  
  private static int getPlatformsMethod2(String code) {
		WebClient client = new WebClient();
		client.getOptions().setTimeout(12000000);
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		try {
		String a = code.substring(0, 1);
		String url = "http://www.alltraintimes.com/list-of-railway-stations-by-"+a+".html";
		System.out.println("First url" + url);
		HtmlPage page = client.getPage(url);
		
		HtmlElement item = (HtmlElement) page
				.getFirstByXPath(".//div[@class='list_table text-left']");
		//System.out.println(item.asXml());
		List<HtmlElement> divs = (List<HtmlElement>) item.getByXPath(".//div[@class='col-xs-12 col-sm-6 ']");
		String newLink  = null;
		for(HtmlElement div:divs) {
			//System.out.println(div.asXml());
			
			if(!div.asText().isEmpty() && div.asText().contains("("+code+")")) {
				//System.out.println(div.asXml());
				List<HtmlAnchor> links = ((List<HtmlAnchor>) div.getByXPath(".//a"));
				for(HtmlAnchor link: links) {
					newLink = link.getAttribute("href");
					System.out.println("new link is"+newLink);
					break;
				}
			}
			
		}
		
		if(newLink!=null) {
			page = client.getPage(newLink);
			
			List<HtmlElement> blocks = (List<HtmlElement>) page
					.getByXPath(".//div[@class='col-xs-12 col-sm-6']");
			
			for(HtmlElement block: blocks) {
				HtmlElement para = (HtmlElement) block.getFirstByXPath(".//p");
				if(para!=null) {
					
					String text = para.asText();
					String sub = text.substring(text.indexOf("Total Platform:"));
					String platform = (sub.substring(sub.indexOf(":")+1)).trim();
					
					return Integer.parseInt(platform);
				}
			}
			
		
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
		
	}

  private static int getPlatforms(String code) {
		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		try {
			String searchUrl = "https://enquiry.indianrail.gov.in/xyzabc/LiveStation?stnCode=" + code
					+ "&toStnCode=&withinHrs=8&trainType=ALL&scrnSize=&langFile=props.en-us";
			System.out.println(searchUrl);
			HtmlPage page = client.getPage(searchUrl);
			//System.out.println(page.getWebResponse().getStatusCode());
			int count =0;
			HtmlElement element = (HtmlElement) page.getHtmlElementById("liveStnTrnsDataTbl");
			List<HtmlElement> bodies = (List<HtmlElement>) element.getByXPath(".//tbody");
			for (HtmlElement body : bodies) {
				//System.out.println(body.asXml());

				List<HtmlElement> rows = (List<HtmlElement>) body.getByXPath(".//tr");
				// System.out.println(rows.asXml());
				if (rows.isEmpty()) {
					System.out.println("None found");
				} else {
					int max = 0;
					for (HtmlElement row : rows) {
						List<HtmlElement> tds = (List<HtmlElement>) row.getByXPath(".//td");

						for (HtmlElement td : tds) {

							HtmlElement item = (HtmlElement) td
									.getFirstByXPath(".//span[@class='w3-badge w3-text-light-grey w3-dark-grey']");
							// System.out.println(item.asXml());
							if (item != null) {
								int platform = Integer.parseInt(item.asText());
								//System.out.println(platform);
								count++;
								if (platform > max) {
									max = platform;
								}
							}
						}
					}
					//System.out.println("Count is"+count);
					return max;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			// return "Daily";
		}

		return 0;
	}
  
  private static String getStationName(String code) {
		WebClient client = new WebClient();
		client.getOptions().setTimeout(12000000);
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		try {
			String a ="";
		if(code.contains("-")) {
			a = code.substring(code.indexOf("-")+1);
			a = a.substring(0, 1);
		}else {
		 a = code.substring(0, 1);
		}
		String url = "http://www.alltraintimes.com/list-of-railway-stations-by-"+a+".html";
		System.out.println("First url" + url);
		HtmlPage page = client.getPage(url);
		
		HtmlElement item = (HtmlElement) page
				.getFirstByXPath(".//div[@class='list_table text-left']");
		//System.out.println(item.asXml());
		List<HtmlElement> divs = (List<HtmlElement>) item.getByXPath(".//div[@class='col-xs-12 col-sm-6 ']");
		String newLink  = null;
		for(HtmlElement div:divs) {
			//System.out.println(div.asXml());
			
			if(!div.asText().isEmpty() && div.asText().contains("("+code+")")) {
				//System.out.println(div.asXml());
				List<HtmlAnchor> links = ((List<HtmlAnchor>) div.getByXPath(".//a"));
				for(HtmlAnchor link: links) {
					newLink = link.getAttribute("href");
					System.out.println("new link is"+newLink);
					break;
				}
			}
			
		}
		
		if(newLink!=null) {			
			return newLink.substring(newLink.indexOf("stations/")+9, newLink.lastIndexOf("station")).replace('-', ' ');
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
