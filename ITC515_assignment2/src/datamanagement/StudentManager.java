package datamanagement;

import java.util.List;
import org.jdom.Element; // changed to explicit import for org.jdom


public class StudentManager {
	private static StudentManager studentManager = null; // variable forming class itself
	
	private StudentMap studentMap; // class StudentMap separate class File
	private java.util.HashMap<String, StudentMap> um;

	public static StudentManager get() {
		if (studentManager == null){

			studentManager = new StudentManager();
		}
		return studentManager;
	}

	private StudentManager() {

		studentMap = new StudentMap();
		um = new java.util.HashMap<>();
	}

	public IStudent getStudent(Integer id) {
		IStudent is = studentMap.get(id);
		return is != null ? is : createStudent(id);
	}

	private Element getStudentElement(Integer id) {
		for (Element el : (List<Element>) XMLManager.getXML().getDocument().getRootElement().getChild("studentTable")
				.getChildren("student"))
			if (id.toString().equals(el.getAttributeValue("sid")))
				return el;
		return null;
	}

	private IStudent createStudent(Integer id) {
		IStudent is;
		Element el = getStudentElement(id);
		if (el != null) {
			StudentUnitRecordList rlist = StudentUnitRecordManager.instance().getRecordsByStudent(id);
			is = new Student(new Integer(el.getAttributeValue("sid")), el.getAttributeValue("fname"),
					el.getAttributeValue("lname"), rlist);

			studentMap.put(is.getID(), is);
			return is;
		}
		throw new RuntimeException("DBMD: createStudent : student not in file");
	}

	private IStudent createStudentProxy(Integer id) {
		Element el = getStudentElement(id);

		if (el != null)
			return new StudentProxy(id, el.getAttributeValue("fname"), el.getAttributeValue("lname"));
		throw new RuntimeException("DBMD: createStudent : student not in file");
	}

	public StudentMap getStudentsByUnit(String uc) {
		StudentMap s = um.get(uc);
		if (s != null) {

			return s;
		}

		s = new StudentMap();
		IStudent is;
		StudentUnitRecordList ur = StudentUnitRecordManager.instance().getRecordsByUnit(uc);
		for (IStudentUnitRecord S : ur) {

			is = createStudentProxy(new Integer(S.getStudentID()));
			s.put(is.getID(), is);
		}
		um.put(uc, s);
		return s;
	}
}
