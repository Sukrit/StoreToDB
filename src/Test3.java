import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.ArrayList;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBold;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class Test3 {
	private static final String SAMPLE_CSV_FILE_PATH = "C:\\\\Users\\\\Sukrit\\\\Desktop\\\\Railways\\\\Govt data\\\\Train_details_22122017.csv";

	public static void main(String[] args) throws Exception {

		try {

			Class forName = Class.forName("com.mysql.jdbc.Driver");
			Connection con = null;
			con = DriverManager.getConnection("jdbc:mysql://localhost/railways", "root", "");
			con.setAutoCommit(false);
			PreparedStatement pstm = null;
			PreparedStatement pstm2 = null;

			Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
			CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

			Time departTime = new Time(0);
			Map<String, String> stations = new HashMap<String, String>();
			Map<String, Integer> route = new HashMap<String, Integer>();
			int id = 1;
			String prev = "";
			int prevDistance = 0;

			String sql = "SELECT CODE FROM STATION;";
			pstm = (PreparedStatement) con.prepareStatement(sql);
			String sql2 = "";
			ResultSet rs = pstm.executeQuery();
			con.commit();
			while (rs.next()) {
				String code = rs.getString("Code");
				String urlName = "https://indiarailinfo.com/shtml/list.shtml?LappGetTrackList/" + code
						+ "/0/0/0?&date=1539795329125";
				URL url = new URL(urlName);
				System.out.println(urlName);
				Document doc = Jsoup.parse(url, 3000);

				ArrayList<String> downServers = new ArrayList<>();
				Element table = null;
				try {
				table = doc.select("table").get(0); // select the first table.
				}catch(IndexOutOfBoundsException e) {
					System.out.println("No table for "+code);
					continue;
				}
				Elements rows = table.select("tr");
				String stationNames = "";
				String distance = "";
				String track = "";
				String from = "";
				String to = "";

				for (int i = 0; i < rows.size() - 2; i++) {
					Element row = rows.get(i);
					Elements cols = row.select("td");

					if (i % 2 == 0) {// get station and distance
						stationNames = cols.get(4).text();
						from = getFrom(stationNames);
						to = getTo(stationNames);
						distance = cols.get(3).text();
						distance = distance.substring(0, distance.indexOf(' '));
					} else { // get track type
						track = cols.get(2).text();
						try {
							sql2 = "INSERT INTO TRACK VALUES ('" + from + "','" + to + "','" + track + "'," + distance
									+ ");";
							System.out.println(sql2);
							pstm2 = (PreparedStatement) con.prepareStatement(sql2);
							
							pstm2.executeUpdate(sql2);
							con.commit();
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}

				}

			}

			con.close();
			System.out.println("Success import excel to mysql table");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getTo(String stationNames) {
		return stationNames
				.substring(stationNames.indexOf('-') + 1, stationNames.indexOf('/', stationNames.indexOf('-'))).trim();
	}

	private static String getFrom(String stationNames) {
		return stationNames.substring(0, stationNames.indexOf('/'));
	}
}
