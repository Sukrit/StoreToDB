import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBold;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Test2 {
	private static final String SAMPLE_CSV_FILE_PATH = "C:\\\\Users\\\\Sukrit\\\\Desktop\\\\Railways\\\\Govt data\\\\Train_details_22122017.csv";

	public static void main(String[] args) throws Exception {

		try {

			Class forName = Class.forName("com.mysql.jdbc.Driver");
			Connection con = null;
			con = DriverManager.getConnection("jdbc:mysql://localhost/railways", "root", "");
			con.setAutoCommit(false);
			PreparedStatement pstm = null;

			/*
			 * System.out.println("Going to read file"); FileInputStream input = new
			 * FileInputStream("C:\\Users\\Sukrit\\Desktop\\Railways\\Govt data\\Train_details_22122017.xlsx"
			 * ); System.out.println("File read"); //POIFSFileSystem fs = new
			 * POIFSFileSystem(input); Workbook workbook;
			 * 
			 * workbook = new XSSFWorkbook(input); Sheet sheet = workbook.getSheetAt(0);
			 * workbook.close();
			 */

			try (Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
					CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
				Time departTime = new Time(0);
				Map<String, String> stations = new HashMap<String, String>();
				Map<String, Integer> route = new HashMap<String, Integer>();
				int id = 1;
				String prev = "";
				int prevDistance = 0;

				/*
				 * for (CSVRecord csvRecord : csvParser) { // Accessing Values by Column Index
				 * if(csvRecord.getRecordNumber()==1) { continue; } int number =
				 * Integer.parseInt(csvRecord.get(0)); String name = csvRecord.get(1); int seq =
				 * Integer.parseInt(csvRecord.get(2)); String stationCode = csvRecord.get(3);
				 * String stationName = csvRecord.get(4); Time arrivalTime =
				 * Time.valueOf(csvRecord.get(5)); Time departureTime =
				 * Time.valueOf(csvRecord.get(6)); int distance =
				 * Integer.parseInt(csvRecord.get(7)); String sourceStation = csvRecord.get(8);
				 * String sourceStationName = csvRecord.get(9); String destinationStation =
				 * csvRecord.get(10); String destinationStationName = csvRecord.get(11);
				 */

				String sql = "SELECT CODE FROM STATION;";
				pstm = (PreparedStatement) con.prepareStatement(sql);
				ResultSet rs = pstm.executeQuery();
				con.commit();
				 while(rs.next())
				{
					 String code = rs.getString("Code");
					// Code for updating route table
					/*
					 * if(seq==1) { prev = stationCode; prevDistance = 0; continue;
					 * 
					 * }else { if((!route.containsKey(prev+","+stationCode)) ||
					 * (route.get(prev+","+stationCode)>(distance-prevDistance))){
					 * route.put(prev+","+stationCode, (distance-prevDistance));
					 * 
					 * //System.out.println(prev+" "+stationCode+" "+(distance-prevDistance));
					 * 
					 * }
					 * 
					 * prev = stationCode; prevDistance = distance; }
					 */

					// Code to populate stop table
					/*
					 * String sql = "INSERT INTO STOP VALUES" +
					 * "("+number+","+seq+",'"+arrivalTime+"','"+departureTime+"','"+stationCode+
					 * "')"; System.out.println(sql); pstm = (PreparedStatement)
					 * con.prepareStatement(sql); pstm.execute(); con.commit();
					 */

					// Code to populate train table
					/*
					 * Time arriveTime;
					 * 
					 * if(stationCode.equals(sourceStation) && seq==1) { departTime = departureTime;
					 * } if(stationCode.equals(destinationStation) && seq!=1) { arriveTime =
					 * arrivalTime;
					 * 
					 * //System.out.println( number + "," + name + "," + sourceStation + "," +
					 * destinationStation + "," + departTime + "," + arriveTime ); String sql =
					 * "INSERT INTO train " + "VALUES(" + number + ",'" + name + "','" +
					 * sourceStation + "','" + destinationStation + "','" + departTime + "', '" +
					 * arriveTime +"' )"; System.out.println(sql); pstm = (PreparedStatement)
					 * con.prepareStatement(sql); pstm.execute(); con.commit(); }
					 */

					// Code to populate station table
					/*
					 * if(!stations.containsKey(stationCode)) { stations.put(stationCode,
					 * stationName); String sql = "INSERT INTO STATION VALUES" +
					 * "('"+stationCode+"','"+stationName+"')"; System.out.println(sql); pstm =
					 * (PreparedStatement) con.prepareStatement(sql); pstm.execute(); con.commit();
					 * 
					 * }
					 */

					int platforms = getPlatforms(code);
					if (platforms != 0) {

						sql = "UPDATE STATION SET PLATFORMS = '" + platforms + "' where CODE = '" + code + "'";
						System.out.println(sql);
						 pstm = (PreparedStatement) con.prepareStatement(sql);
						 pstm.execute();
						 con.commit();
						// System.out.println(days);

					}

				}

				// Code for updating route table
				/*
				 * for(String key: route.keySet()) {
				 * 
				 * int distance = route.get(key); String[] values= key.split(","); String
				 * previous = values[0]; String cur = values[1]; String sql =
				 * "INSERT INTO ROUTE VALUES" +
				 * "("+id+",'"+previous+"','"+cur+"',"+distance+")"; System.out.println(sql);
				 * pstm = (PreparedStatement) con.prepareStatement(sql); pstm.execute();
				 * con.commit(); id++; }
				 */

			}

			con.close();
			System.out.println("Success import excel to mysql table");
		} catch (IOException e) {
		}

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
					int max = 2;
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
								if (platform > 2) {
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

	private static String getRunningDays(int number) {
		WebClient client = new WebClient();
		client.getOptions().setCssEnabled(false);
		client.getOptions().setJavaScriptEnabled(false);
		try {
			String searchUrl = "https://etrain.info/in?TRAIN=" + number;
			HtmlPage page = client.getPage(searchUrl);
			// System.out.println(page.getWebResponse().getStatusCode());

			HtmlElement element = (HtmlElement) page.getHtmlElementById("lowerdata");

			// List<HtmlElement> items = (List<HtmlElement>)
			// page.getByXPath("//p[@class='result-info']" ;
			List<HtmlElement> items = (List<HtmlElement>) element.getByXPath(".//tr[@class='even dborder']");
			if (items.isEmpty()) {
				System.out.println("No items found !");
			} else {
				for (HtmlElement item : items) {
					// HtmlAnchor itemAnchor = ((HtmlAnchor) item.getFirstByXPath(".//a"));

					// String itemName = itemAnchor.asText();
					// String itemUrl = itemAnchor.getHrefAttribute() ;
					HtmlElement spanPrice = ((HtmlElement) item.getFirstByXPath(".//td[@class='nobl']"));

					HtmlBold itemAnchor = ((HtmlBold) spanPrice.getFirstByXPath(".//b"));
					String days = spanPrice.asText().substring(spanPrice.asText().indexOf(":") + 1).trim();
					System.out.println(days);
					return days;

					// System.out.println( String.format("Name : %s Url : %s Price : %s", itemName,
					// itemPrice, itemUrl));
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			// return "Daily";
		}

		return null;
	}
}