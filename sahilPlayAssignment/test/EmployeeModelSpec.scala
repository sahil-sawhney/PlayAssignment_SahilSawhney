import models.{Employee, EmployeeModel}
import play.api.test.PlaySpecification
import play.api.test.WithApplication

/**
  * Created by sahil on 3/5/16.
  */
class EmployeeModelSpec extends PlaySpecification{

  "EmployeeModelSpec" should {

    val obj = new EmployeeModel

    "Unique id is generated" in new WithApplication() {
      val res=obj.findUniqueId
      res must equalTo(6)
    }

    "Valid employee name yields a list of employees containing name" in new WithApplication() {
      val res=obj.searchByEmployeeName("Sahil")
      res must equalTo(List(Employee(1, "Sahil Sawhney", "Dilshad Garden, Delhi", "16-04-1992", "16-04-1992", "Manager")))
    }

    "Multiple Employees With Same Name Pattern" in new WithApplication() {
      val res=obj.searchByEmployeeName("a")
      res must equalTo(List(
        Employee(4, "Akash Chauhan", "Punjabi Bagh, Delhi", "16-04-1992", "16-04-1992", "Designer"),
        Employee(3, "Akshay Upadhyay", "Dwarka, Delhi", "16-04-1992", "16-04-1992", "Devloper"),
        Employee(5, "Parth Gulati", "North Campus, Delhi", "16-04-1992", "16-04-1992", "Trainy"),
        Employee(1, "Sahil Sawhney", "Dilshad Garden, Delhi", "16-04-1992", "16-04-1992", "Manager"),
        Employee(2, "Varun Shukla", "Kanpur", "09-07-1995", "16-04-1992", "Team Lead")
      ))
    }

    "In-Valid employee name results in empty List" in new WithApplication() {
      val res=obj.searchByEmployeeName("Ramanujam")
      res must equalTo(Nil)
    }

    "Reterive all employee" in new WithApplication() {
      val res=obj.getEmployeeList
      res must equalTo(List(
        Employee(4, "Akash Chauhan", "Punjabi Bagh, Delhi", "16-04-1992", "16-04-1992", "Designer"),
        Employee(3, "Akshay Upadhyay", "Dwarka, Delhi", "16-04-1992", "16-04-1992", "Devloper"),
        Employee(5, "Parth Gulati", "North Campus, Delhi", "16-04-1992", "16-04-1992", "Trainy"),
        Employee(1, "Sahil Sawhney", "Dilshad Garden, Delhi", "16-04-1992", "16-04-1992", "Manager"),
        Employee(2, "Varun Shukla", "Kanpur", "09-07-1995", "16-04-1992", "Team Lead")
      ))
    }

    "Reterive correct employee from the List on bassis of id" in new WithApplication() {
      val res=obj.getEmployee(1).get
      res must equalTo(Employee(1, "Sahil Sawhney", "Dilshad Garden, Delhi", "16-04-1992", "16-04-1992", "Manager"))
    }

    "Wrong id to Retrive Employee data" in new WithApplication() {
      val res=obj.getEmployee(18)
      res must equalTo(None)
    }

    "employee is present in th list" in new WithApplication(){
      val res=obj.containsEmployee(Employee(1, "Sahil Sawhney", "Dilshad Garden, Delhi", "16-04-1992", "16-04-1992", "Manager"))
      res must equalTo(true)
    }

    "employee is not present in the List" in new WithApplication(){
      val res=obj.containsEmployee(Employee(7,"Ram Sharma","Dehradun, India","23-05-1995", "13-06-2014", "Clerk"))
      res must equalTo(false)
    }

    "Add New Employee To The List" in new WithApplication(){
      val res=obj.addEmployee(Employee(7,"Ram Sharma","Dehradun, India","23-05-1995", "13-06-2014", "Clerk"))
      res must equalTo(true)
    }

    "Adding Already Existing Employee To The List" in new WithApplication(){
      val res=obj.addEmployee(Employee(1, "Sahil Sawhney", "Dilshad Garden, Delhi", "16-04-1992", "16-04-1992", "Manager"))
      res must equalTo(false)
    }

    "Delete Employee By Existing Id" in new WithApplication(){
      val res=obj.deleteEmployeeById(2)
      res must equalTo(true)
    }

    "Delete Employee By Non-Existing Id" in new WithApplication(){
      val res=obj.deleteEmployeeById(45)
      res must equalTo(false)
    }

    "Edit Employee invalid" in new WithApplication(){
      val res=obj.editEmployeeList(Employee(1, "Sahil Sawhney", "Dilshad Garden, Delhi", "16-04-1992", "16-04-1992", "Manager"))
      res must equalTo(false)
    }

    "Edit Existing Employee" in new WithApplication(){
      val res=obj.editEmployeeList(Employee(1, "Sahil Sahni", "Dilshad Garden, Delhi", "16-04-1992", "16-04-1992", "Manager"))
      res must equalTo(true)
    }

  }
}
