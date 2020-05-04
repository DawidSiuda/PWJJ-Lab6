package application;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "schools")
@XmlAccessorType(XmlAccessType.FIELD)
public class Schools {

//	SchoolsContainer(){
//	}

	Schools(List<School> schoolsList) {
		this.schoolsList = schoolsList;
	}

	Schools() {
		schoolsList = new ArrayList<School>();
	}

	@XmlElement(name = "school")
	private List<School> schoolsList = null;



	public List<School> getSchoolsList() {
		return schoolsList;
	}



	public void setSchoolsList(List<School> schoolsList) {
		this.schoolsList = schoolsList;
	}
}
