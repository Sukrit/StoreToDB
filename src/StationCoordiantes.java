import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StationCoordiantes {
public static void main(String[] args)
{
    JSONParser parser=new JSONParser();

    try{
    	Class forName = Class.forName("com.mysql.jdbc.Driver");
        Connection con = null;
        con = DriverManager.getConnection("jdbc:mysql://localhost/railways", "root", "");
        con.setAutoCommit(false);
        PreparedStatement pstm = null;

        Object obj = parser.parse(new FileReader("C:\\Users\\Sukrit\\Desktop\\Railways\\railways-master\\railways-master\\stations.json"));
        JSONObject jsonObject = (JSONObject ) obj;
        ArrayList<JSONObject> features = (ArrayList<JSONObject>) jsonObject.get("features");
        
        for(JSONObject object : features) {
        	JSONObject geometry = (JSONObject) object.get("geometry");
        	 
        	 if(geometry!=null) {
        		 
        		 ArrayList<Double> coordinates = (ArrayList<Double>) geometry.get("coordinates");
        		 JSONObject properties = (JSONObject) object.get("properties");
        		 String code = (String) properties.get("code");
        		 String state = (String) properties.get("state");
        		 String zone = (String) properties.get("zone");
        		 String address = (String) properties.get("address");
        	
        		 
        		 //System.out.println("Details are "+code+" "+state+" "+zone+" "+address+" "+coordinates.get(0)+" "+coordinates.get(1));
        		 
        		 String sql = "UPDATE STATION SET lat = "+coordinates.get(1)+", `long` ="+coordinates.get(0)+" WHERE CODE = '"+code+"';";
        		 System.out.println(sql);
        		 pstm = (PreparedStatement) con.prepareStatement(sql);
     			 pstm.executeUpdate();
     			 con.commit();
        	 }
             
        }

        con.close();
    }catch(Exception e) {
    	e.printStackTrace();
    }
    
    
}
}