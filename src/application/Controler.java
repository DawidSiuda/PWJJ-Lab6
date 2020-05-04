package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Controler {
	//
	// FX variables
	//

	public TextField textFieldName;
	public TextField textFieldSurname;
	public TextField textFieldClass;
	public TextField textFieldStudentID;
	public TextField textFieldNewClass;
	public TextField textFieldStudentIDToDelete;

	public ImageView background;

	public ChoiceBox<String> choiceBoxSchool;

	public Button buttonShowStudents;
	public Button buttonShowSchools;
	public Button buttonAddStudent;
	public Button buttonUpdate;
	public Button buttonShowstudententsAndSchools;
	public Button buttonDelete;
	public Button buttonExport;
	public Button buttonImport;

	public Label labelStatus;

	public ListView<String> listView;

	Connection myCon;

	ObservableList<String> schools;
	List<String> schoolsAsString = new ArrayList<>();
	List<String> listToShow;

	public Controler() {
		listToShow = new ArrayList<String>();

		//
		// Connect to database
		//

		try {
			myCon = DriverManager.getConnection("jdbc:mysql://localhost:3306/school", "root", "1234");
		} catch (Exception exc) {
			System.out.println("ERROR: Cannot connect to database.");
		}
	}

	/**
	 * Initialize FX controllers.
	 */
	public void initialize() {
		System.out.println("FUNCTION: initialize");

		//
		// Set defaults values
		//

		textFieldName.setText("John");
		textFieldSurname.setText("Doe");
		textFieldClass.setText("1A");
		textFieldStudentID.setText("1");
		textFieldNewClass.setText("2B");

		if (myCon != null) {
			labelStatus.setText("Status: CONNECTED");
		} else {
			labelStatus.setText("Status: NO CONNECTION");
		}

		//
		// Set school map
		//

		reloadSelectSchoolList();

		choiceBoxSchool.getSelectionModel().selectFirst();
	}

	public void buttonShowStudentsClicked() {

		System.out.println("FUNCTION: buttonShowStudentsClicked()");

		listToShow.clear();

		if (myCon == null) {
			System.out.println("ERROR: No connection to base.");
			MyMessage.show("No connection to database");

			return;
		}

		String sql = "SELECT id_student, id_school, name, surname, class FROM students";

		try {
			Statement stmt = myCon.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// loop through the result set
			while (rs.next()) {
				StringBuilder str = new StringBuilder();

				str.append(rs.getInt("id_student"));
				str.append(" ");
				str.append(rs.getInt("id_school"));
				str.append(" ");
				str.append(rs.getString("name"));
				str.append(" ");
				str.append(rs.getString("surname"));
				str.append(" ");
				str.append(rs.getString("class"));
				str.append(" ");

				listToShow.add(str.toString());
			}

			reloadListView();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void buttonShowSchoolsClicked() {
		System.out.println("FUNCTION: buttonShowSchoolsClicked()");

		listToShow.clear();

		if (myCon == null) {
			System.out.println("ERROR: No connection to base.");
			MyMessage.show("No connection to database");

			return;
		}

		String sql = "SELECT id_school, name, type FROM schools";

		try {
			Statement stmt = myCon.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// loop through the result set
			while (rs.next()) {
				StringBuilder str = new StringBuilder();

				str.append(rs.getInt("id_school"));
				str.append(" ");
				str.append(rs.getString("name"));
				str.append(" ");
				str.append(rs.getString("type"));
				str.append(" ");

				listToShow.add(str.toString());
			}

			reloadListView();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void buttonShowstudententsAndSchoolsClicked() {
		System.out.println("FUNCTION: buttonShowstudententsAndSchoolsClicked()");

		listToShow.clear();

		if (myCon == null) {
			System.out.println("ERROR: No connection to base.");
			MyMessage.show("No connection to database");

			return;
		}

		String sql = "SELECT st.id_student, st.name, st.surname, st.class,"
				+ " sch.name as schoolName, sch.type FROM students st"
				+ " INNER JOIN schools sch ON st.id_school = sch.id_school;";

		try {
			Statement stmt = myCon.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			// loop through the result set
			while (rs.next()) {
				StringBuilder str = new StringBuilder();

				str.append(rs.getInt("id_student"));
				str.append(" ");
				str.append(rs.getString("name"));
				str.append(" ");
				str.append(rs.getString("surname"));
				str.append(" ");
				str.append(rs.getString("class"));
				str.append(" ");
				str.append(rs.getString("schoolName"));
				str.append(" ");
				str.append(rs.getString("type"));
				str.append(" ");

				listToShow.add(str.toString());
			}

			reloadListView();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public void buttonAddStudentClicked() {
		System.out.println("FUNCTION: buttonShowSchoolsClicked()");

		if (myCon == null) {
			System.out.println("ERROR: No connection to base.");
			MyMessage.show("No connection to database");

			return;
		}

		// Get id of selected school.

		int schoolID = -1;
		int numbenOfSchoolWithtTheSameName = -1;

		String sql = "SELECT id_school FROM schools where name=?;";

		try {
			PreparedStatement pstmt = myCon.prepareStatement(sql);
			pstmt.setString(1, choiceBoxSchool.getSelectionModel().getSelectedItem());
			System.out.println("QUERY: " + pstmt);
			ResultSet rs = pstmt.executeQuery();

			rs.next();
			schoolID = rs.getInt("id_school");

			rs.last();
			numbenOfSchoolWithtTheSameName = rs.getRow();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		if (schoolID == -1 || numbenOfSchoolWithtTheSameName > 1) {
			System.out.println("INFO: schoolID=" + schoolID + " numbenOfSchoolWithtTheSameName = "
					+ numbenOfSchoolWithtTheSameName);
			MyMessage.show("No selected school");
			return;
		}

		System.out.println("INFO: Selected school id = " + schoolID);

		sql = "INSERT INTO students( id_school, name, surname, class) VALUES(?,?,?,?);";

		try {
			PreparedStatement pstmt = myCon.prepareStatement(sql);
			pstmt.setInt(1, schoolID);
			pstmt.setString(2, textFieldName.getText());
			pstmt.setString(3, textFieldSurname.getText());
			pstmt.setString(4, textFieldClass.getText());
			pstmt.executeUpdate();
			System.out.println("QUERY: " + pstmt);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		buttonShowstudententsAndSchoolsClicked();
	}

	public void buttonUpdateClicked() {
		System.out.println("FUNCTION: buttonUpdateClicked()");

		if (myCon == null) {
			System.out.println("ERROR: No connection to base.");
			MyMessage.show("No connection to database");

			return;
		}

		int value;

		try {
			value = Integer.parseInt(textFieldStudentID.getText());
		} catch (Exception e) {
			MyMessage.show("Wrong student ID.");
			return;
		}

		String sql = "UPDATE students SET class = ? WHERE id_student = ?;";

		try {
			PreparedStatement pstmt = myCon.prepareStatement(sql);
			pstmt.setString(1, textFieldNewClass.getText());
			pstmt.setInt(2, value);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			MyMessage.show("Cannot update student data.");
		}

		buttonShowstudententsAndSchoolsClicked();
	}

	public void buttonDeleteClicked() {
		System.out.println("FUNCTION: buttonUpdateClicked()");

		if (myCon == null) {
			System.out.println("ERROR: No connection to base.");
			MyMessage.show("No connection to database");

			return;
		}

		int value;

		try {
			value = Integer.parseInt(textFieldStudentIDToDelete.getText());
		} catch (Exception e) {
			MyMessage.show("Wrong student ID.");
			return;
		}

		String sql = "DELETE FROM students WHERE id_student = ?;";

		try {
			PreparedStatement pstmt = myCon.prepareStatement(sql);
			pstmt.setInt(1, value);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			MyMessage.show("Cannot delete student data.");
		}

		buttonShowstudententsAndSchoolsClicked();
	}

	public void buttonExportClicked(){
		System.out.println("FUNCTION: buttonExportClicked()");

		if (myCon == null) {
			System.out.println("ERROR: No connection to base.");
			MyMessage.show("No connection to database");

			return;
		}

		String sql = "SELECT id_school, name, type FROM schools";

		try {
			Statement stmt = myCon.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			List<School> schoolsList = new ArrayList<School>();
			while (rs.next()) {
				schoolsList.add(new School(rs.getString("name"), rs.getString("type")));
			}

			Schools schools = new Schools(schoolsList);

			JAXBContext context = JAXBContext.newInstance(Schools.class);

			javax.xml.bind.Marshaller marshaller = context.createMarshaller();
			Path path = Paths.get("./Schools.xml");
			Files.deleteIfExists(path);
			marshaller.marshal(schools, new File("./Schools.xml"));
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			MyMessage.show("Cannot export list of schools.");
		}

		buttonShowSchoolsClicked();
	}

	public void buttonImportClicked(){

		System.out.println("FUNCTION: buttonImportClicked()");

		if (myCon == null) {
			System.out.println("ERROR: No connection to base.");
			MyMessage.show("No connection to database");

			return;
		}

		try {

			//
			// Read from file.
			//

			JAXBContext context = JAXBContext.newInstance(Schools.class);

			Unmarshaller umarshaller = context.createUnmarshaller();
			Schools schools = (Schools)umarshaller.unmarshal(new File("Schools.xml"));

			if(schools == null)
			{
				MyMessage.show("Cannot import list of schools.");
				return;
			}

			//
			// Save to database
			//

			String sql = "INSERT INTO schools(name, type) VALUES(?,?);";

			for (School school : schools.getSchoolsList()) {
				PreparedStatement pstmt = myCon.prepareStatement(sql);
				pstmt.setString(1, school.getName());
				pstmt.setString(2, school.getType());
				pstmt.executeUpdate();
				System.out.println("QUERY: " + pstmt);
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			MyMessage.show("Cannot import list of schools.");
		}

		buttonShowSchoolsClicked();
	}

	private void reloadListView() {
		listView.getItems().clear();

		for (String var : listToShow) {
			listView.getItems().add(var);
		}
	}

	private void reloadSelectSchoolList() {
		if (myCon != null) {
			String sql = "SELECT id_school, name, type FROM schools";

			try {
				Statement stmt = myCon.createStatement();
				ResultSet rs = stmt.executeQuery(sql);

				// loop through the result set
				while (rs.next()) {
					schoolsAsString.add(rs.getString("name"));

				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

			schools = FXCollections.observableArrayList(schoolsAsString);

			choiceBoxSchool.setItems(schools);
		}
	}
}