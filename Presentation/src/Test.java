import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: Alex
 * Date: 29/5/2013
 * Time: 1:28 μμ
 * To change this template use File | Settings | File Templates.
 */
public class Test {

	public Test() {
		StringTokenizer stringTokenizer = new StringTokenizer(
				"(38.03632926647352, 23.707879185676575),(38.03588140194542, 23.710421919822693)|", "|");

		String linePath = null;
		String lat, lng;
		Geometry geom;
		if (stringTokenizer.hasMoreTokens()) {
			linePath = stringTokenizer.nextToken();
			StringTokenizer st = new StringTokenizer(linePath, "),(");
			while (st.hasMoreTokens()) {
				lat = st.nextToken().trim();
				lng = st.nextToken().trim();

				System.out.println(lat);
				System.out.println(lng);

				try {
					WKTReader fromText = new WKTReader();
					geom = fromText.read("POINT(" + lat + " " + lng + ")");
					geom.setSRID(4326);
				} catch (ParseException e) {
					throw new RuntimeException("Not a WKT string");
				}
			}
		}
	}

	public static void main(String args[]) {
		new Test();
	}
}
