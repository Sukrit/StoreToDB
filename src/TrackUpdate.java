import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBold;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TrackUpdate {
	private static final String SAMPLE_CSV_FILE_PATH = "C:\\\\Users\\\\Sukrit\\\\Desktop\\\\Railways\\\\Govt data\\\\Train_details_22122017.csv";

	public static void main(String[] args) throws Exception {

		try {

			Class forName = Class.forName("com.mysql.jdbc.Driver");
			Connection con = null;
			con = DriverManager.getConnection("jdbc:mysql://localhost/railways", "root", "");
			con.setAutoCommit(false);
			PreparedStatement pstm = null;

			System.out.println("Loading all tracks");

			String query = "SELECT * FROM TRACK";
			pstm = (PreparedStatement) con.prepareStatement(query);
			ResultSet rs = pstm.executeQuery();
			con.commit();

			while (rs.next()) {

				String from = rs.getString("from");
				String to = rs.getString("to");
				String track = rs.getString("Type");
				float distance = rs.getFloat("Distance");

				List<String> stations = new ArrayList<String>();
				List<String> visited  = new ArrayList<String>();
				//stations.add(from);
				//populateStops(from, to, con, stations);
				
				if(populateStops2(from, to, con, stations, distance, 0.0, visited)) {
					stations.add(from);
				}

				for (String fromStation : stations) {
					for (String ToStation : stations) {
						String query2 = "UPDATE NODE SET TYPE = '" + track + "' WHERE `FROM`='" + fromStation + "' AND `TO` = '"
								+ ToStation + "';";
						try {
							PreparedStatement pstm2 = (PreparedStatement) con.prepareStatement(query2);
							int rs2 = pstm2.executeUpdate();
							con.commit();
							System.out.println(query2);
							
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}

			con.close();
			System.out.println("Success import excel to mysql table");
		} catch (Exception e) {
		}

	}

	private static boolean populateStops2(String item, String to, Connection con, List<String> stations, float distance, Double caldistance, List<String> visited) {
		
		visited.add(item);
		
		if(item.equals(to)) {
			return true;
		}
		
		if(caldistance > distance) {
			return false;
		}
		
		PreparedStatement pstm = null;

		String query = "select * from node where `from`='"+item+"';";

		try {
			pstm = (PreparedStatement) con.prepareStatement(query);

			ResultSet rs = pstm.executeQuery();
			con.commit();
			while(rs.next()) {
				if (!visited.contains(rs.getString("to")) && populateStops2(rs.getString("to"), to, con, stations, distance, (caldistance+rs.getDouble("Distance")), visited)) {
					stations.add(rs.getString("to"));
					return true;
				}
			}
			
			
		}catch(Exception e) {
			
		}
		
		return false;
		
	}

	private static void populateStops(String from, String to, Connection con, List<String> stations) {

		
		System.out.println("Populating nodes");

		PreparedStatement pstm = null;

		String query = "select max(k.`serial number`-s.`serial number`) as boo"
				+ " from stop s, stop k where s.station='" + from + "' and k.station= '" + to + "' "
				+ "and s.`train number`=k.`train number` order by boo desc limit 1";

		System.out.println(query);

		try {
			pstm = (PreparedStatement) con.prepareStatement(query);

			ResultSet rs = pstm.executeQuery();
			con.commit();
			rs.next();
			int count = rs.getInt("boo");


			String sql = "select s.`train number`,s.`serial number` from stop s, stop k where s.station='" + from
					+ "' and k.station='" + to
					+ "' and s.`train number`= k.`train number` and (k.`serial number`- s.`serial number`) =" + count;

			System.out.println(sql);

			PreparedStatement pstm2 = (PreparedStatement) con.prepareStatement(sql);
			ResultSet rs1 = pstm2.executeQuery();
			con.commit();
			rs1.next();
			int trainNumber = rs1.getInt("Train number");
			int serialNumber = rs1.getInt("Serial Number");

			String sql2 = "select station from stop where `train number` = " + trainNumber
					+ "  and `serial number` between " + (serialNumber + 1) + " and " + (serialNumber + count-1) + ";";

			System.out.println(sql2);

			PreparedStatement pstm3 = (PreparedStatement) con.prepareStatement(sql2);
			ResultSet rs2 = pstm3.executeQuery();
			con.commit();

			String first = from;
			while (rs2.next()) {
				populateStops(first, rs2.getString("Station"), con, stations);
				first = rs2.getString("Station");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
}