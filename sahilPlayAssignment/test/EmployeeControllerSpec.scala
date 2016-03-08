import controllers.EmployeeController
import models.{EmployeeServices, Employee}
import org.specs2.mock.Mockito
import org.mockito.Mockito._
import play.api.test.{WithApplication, FakeRequest, PlaySpecification}


/**
  * Created by sahil on 3/5/16.
  */
class EmployeeControllerSpec extends PlaySpecification with Mockito {

  "EmployeeControllerSpec" should {

    val service = mock[EmployeeServices]
    val controller = new EmployeeController(service)

    "render the view to of Dashboard" in new WithApplication(){
      when(service.getEmployeeList).thenReturn(List(
        Employee(4, "Akash Chauhan", "Punjabi Bagh, Delhi", "16-04-1992", "16-04-1992", "Designer"),
        Employee(3, "Akshay Upadhyay", "Dwarka, Delhi", "16-04-1992", "16-04-1992", "Devloper"),
        Employee(5, "Parth Gulati", "North Campus, Delhi", "16-04-1992", "16-04-1992", "Trainy"),
        Employee(1, "Sahil Sawhney", "Dilshad Garden, Delhi", "16-04-1992", "16-04-1992", "Manager"),
        Employee(2, "Varun Shukla", "Kanpur", "09-07-1995", "16-04-1992", "Team Lead")))
      val res=controller.dashboardView(FakeRequest(GET,"/dashboard"))
      status(res) must equalTo(200)
    }

    "get detail of employee by name" in new WithApplication() {
      when(service.searchByEmployeeName("Akash")).thenReturn(List(Employee(4, "Akash Chauhan", "Punjabi Bagh, Delhi", "16-04-1992", "16-04-1992", "Designer")))
      val res = controller.filterEmployee(FakeRequest(POST, "/filteredRecords").withFormUrlEncodedBody("name" -> "Akash"))
      status(res) must equalTo(OK)
    }

    "search for invalid employee name" in new WithApplication() {
      when(service.searchByEmployeeName("Somil")).thenReturn(Nil)
      val res =  controller.filterEmployee(FakeRequest(POST, "/filteredRecords").withFormUrlEncodedBody("name" -> "Somil"))
      status(res) must equalTo(OK)
    }

    "add a new employee which already exist" in new WithApplication(){
      when(service.addEmployee(Employee(6, "Akash Chauhan", "Punjabi Bagh, Delhi", "16-04-1992", "16-04-1992", "Designer"))).thenReturn(false)
      val res=controller.addEmployeeSubmitForm(FakeRequest(POST, "/employeeAded").withFormUrlEncodedBody(
        "id" ->"6", "name" -> "Akash Chauhan", "address" -> "Punjabi Bagh, Delhi", "dob" -> "16-04-1992", "joiningDate" -> "16-04-1992", "designation" -> "Designer"))
      status(res) must equalTo(400)
    }

    "add a new employee with correct credentials" in new WithApplication(){
      when(service.addEmployee(Employee(6, "Rahul Mahajan", "Punjabi Bagh, Delhi", "16-04-1992", "16-04-1992", "Painter"))).thenReturn(true)
      val res=controller.addEmployeeSubmitForm(FakeRequest(POST, "/employeeAded").withFormUrlEncodedBody(
        "id" -> "6", "name" -> "Rahul Mahajan", "address" -> "Punjabi Bagh, Delhi", "dob" -> "16-04-1992", "joiningDate" -> "16-04-1992", "designation" -> "Painter"))
      status(res) must equalTo(303)
    }

    "add a new employee with unvalid credentials" in new WithApplication(){
      when(service.addEmployee(Employee(6, "Akash Chauhan", "Punjabi Bagh, Delhi", "16-04-92", "1-04-1992", "Designer"))).thenReturn(false)
      val res=controller.addEmployeeSubmitForm(FakeRequest(POST, "/employeeAded").withFormUrlEncodedBody(
        "id" -> "6", "name" -> "Akash Chauhan", "address" -> "Punjabi Bagh, Delhi", "dob" -> "16-04-92", "joiningDate" -> "1-04-1992", "designation" -> "Designer"))
      status(res) must equalTo(400)
    }

    "render the view to add an employee" in new WithApplication(){
      val res=controller.addView(FakeRequest(GET,"/newEmployee"))
      status(res) must equalTo(200)
    }

    "delete record of an employee" in new WithApplication(){
      when(service.deleteEmployeeById(1)).thenReturn(true)
      val res=controller.deleteEmployee(1)(FakeRequest(GET,"/dashboard"))
      status(res) must equalTo(303)
    }

    "Render the employee view with valid id" in new WithApplication(){
      when(service.getEmployee(1)).thenReturn(Option(Employee(1, "Sahil Sawhney", "Dilshad Garden, Delhi", "16-04-1992", "16-04-1992", "Manager")))
      val res=controller.editView(1)(FakeRequest(GET,"/editEmployee/:param"))
      status(res) must equalTo(OK)
    }

    "render dashboard if id to edit ios not valid" in new WithApplication(){
      when(service.getEmployee(10)).thenReturn(None)
      val res=controller.editView(10)(FakeRequest(GET,"/editEmployee/:param"))
      status(res) must equalTo(303)
    }

    "Record edited is correct" in new WithApplication(){
      when(service.editEmployeeList(Employee(4, "Rahul Mahajan", "Punjabi Bagh, Delhi", "16-04-1992", "16-04-1992", "Painter"))).thenReturn(true)
      val res=controller.editRecord(FakeRequest(POST, "/editedRecords").withFormUrlEncodedBody(
        "id" -> "4", "name" -> "Rahul Mahajan", "address" -> "Punjabi Bagh, Delhi", "dob" -> "16-04-1992", "joiningDate" -> "16-04-1992", "designation" -> "Painter"))
      status(res) must equalTo(303)
    }

    "Record edited is already present" in new WithApplication(){
      when(service.editEmployeeList(Employee(4, "Akash Chauhan", "Punjabi Bagh, Delhi", "16-04-1992", "16-04-1992", "Designer"))).thenReturn(false)
      val res=controller.editRecord(FakeRequest(POST, "/editedRecords").withFormUrlEncodedBody(
        "id" -> "4", "name" -> "Akash Chauhan", "address" -> "Punjabi Bagh, Delhi", "dob" -> "16-04-1992", "joiningDate" -> "16-04-1992", "designation" -> "Designer"))
      status(res) must equalTo(400)
    }

    "Record edited has incorrect fpormat" in new WithApplication(){
      when(service.editEmployeeList(Employee(4, "Akash Chauhan", "Punjabi Bagh, Delhi", "16-04-199", "16-04-192", "Designer"))).thenReturn(false)
      val res=controller.editRecord(FakeRequest(POST, "/editedRecords").withFormUrlEncodedBody(
        "id" -> "4", "name" -> "Akash Chauhan", "address" -> "Punjabi Bagh, Delhi", "dob" -> "16-04-199", "joiningDate" -> "16-04-192", "designation" -> "Designer"))
      status(res) must equalTo(400)
    }

  }
}
