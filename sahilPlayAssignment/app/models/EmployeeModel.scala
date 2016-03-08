package models

import java.text. SimpleDateFormat
import com.google.inject.ImplementedBy

import scala.collection.mutable.ListBuffer

/**
  *this case class include all the data of an employee that we will be persisting
  *
  * @param id             its the unique id been mentained for every employee
  * @param name           the name of the employee
  * @param address        the address of the employee
  * @param dob            the date of birth of the employee in format (dd-MM-yyy)
  * @param joiningDate    the joining date of the employee in format (dd-MM-yyy)
  * @param designation    the designation of the employee in company
  */
case class Employee(id:Int, name:String, address:String, dob:String, joiningDate:String, designation:String)

/**
  * this trait is used to inject data services
  */
@ImplementedBy(classOf[EmployeeModel])
trait EmployeeServices{

  def containsEmployee(emp: Employee): Boolean
  def findUniqueId: Int
  def getEmployeeList: List[Employee]
  def searchByEmployeeName(name: String): List[Employee]
  def getEmployee(id: Int): Option[Employee]
  def addEmployee(emp: Employee): Boolean
  def deleteEmployeeById(id: Int): Boolean
  def editEmployeeList(emp: Employee): Boolean
}


/**
  *this object provides all the necessary services to the controller and helps in maintaing
  * the records of employees
  */
class EmployeeModel extends EmployeeServices{

  /**
    * this list contains record of all the employees
    */
  private val employeeList: ListBuffer[Employee] = new ListBuffer[Employee]

  /**
    * it specifies the format in which the dates will be saved
    */
  private val dateFormat: SimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy")

  /**
    * storing initialization data in the list
    */
  employeeList += Employee(1, "Sahil Sawhney", "Dilshad Garden, Delhi", dateFormat.format(dateFormat.parse("16-04-1992"))
    , dateFormat.format(dateFormat.parse("16-04-1992")), "Manager")
  employeeList += Employee(2, "Varun Shukla", "Kanpur", dateFormat.format(dateFormat.parse("09-07-1995"))
    , dateFormat.format(dateFormat.parse("16-04-1992")), "Team Lead")
  employeeList += Employee(3, "Akshay Upadhyay", "Dwarka, Delhi", dateFormat.format(dateFormat.parse("16-04-1992"))
    , dateFormat.format(dateFormat.parse("16-04-1992")), "Devloper")
  employeeList += Employee(4, "Akash Chauhan", "Punjabi Bagh, Delhi", dateFormat.format(dateFormat.parse("16-04-1992"))
    , dateFormat.format(dateFormat.parse("16-04-1992")), "Designer")
  employeeList += Employee(5, "Parth Gulati", "North Campus, Delhi", dateFormat.format(dateFormat.parse("16-04-1992"))
    , dateFormat.format(dateFormat.parse("16-04-1992")), "Trainy")

  /**
    * it checks wether the list contains a certain employee or not
    *
    * @param emp employee whose presence we have to check
    * @return true if employee is present in the list, false otherwise
    */
  def containsEmployee(emp: Employee): Boolean = {

    val res = employeeList.filter(_.name == emp.name).filter(_.address == emp.address).filter(_.dob == emp.dob)
      .filter(_.joiningDate == emp.joiningDate).filter(_.designation == emp.designation)
    if (res.isEmpty) {
      false
    }
    else{
      true}
  }

  /**
    * it finds a new unique id for new customer
    *
    * @return unique id of Int type
    */
  def findUniqueId: Int = {
    employeeList.maxBy(_.id).id + 1
  }

  /**
    * it returns the list of all the employees
    *
    * @return List of employee
    */
  def getEmployeeList: List[Employee] = {
    employeeList.toList.sortBy(_.name)
  }

  /**
    * it search an employee for its name (full or partial)
    *
    * @param name name of the employee to be searched
    * @return List of employees having that name
    */
  def searchByEmployeeName(name: String): List[Employee] = {

    employeeList.filter(_.name.contains(name)).toList.sortBy(_.name)
  }

  /**
    * it returns the Employee on basis of its id; if the employee exists
    *
    * @param id employee is searched on basis of this id
    * @return Some(Employee) if employee is present None otherwise
    */
  def getEmployee(id: Int): Option[Employee] = {

    val tempList = employeeList.filter(_.id == id)
    tempList.headOption
  }

  /**
    * it ads an employee to the employee list
    *
    * @param emp Employee to be added
    * @return true if employee is added in the list, false otherwise
    */
  def addEmployee(emp: Employee): Boolean = {

    if (containsEmployee(emp)) {
      return false
    }
    employeeList += emp
    true
  }

  /**
    * it deletes an existing employee from Employee List
    *
    * @param id id of employee to be deleted
    * @return true if employee is deleted from the list, false otherwise
    */
  def deleteEmployeeById(id: Int): Boolean = {

      employeeList -= getEmployee(id).getOrElse(return false)
      true
  }

  /**
    * it edit the details existing of employee
    *
    * @param emp Employee whose details are to be altered
    * @return true if employee is edited successfully, false if employee is not present in the list
    */
  def editEmployeeList(emp: Employee): Boolean = {

    if (containsEmployee(emp)){
      return false}
    employeeList -= getEmployee(emp.id).get
    addEmployee(emp)
    true
  }
}


