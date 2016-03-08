package controllers

import java.text.SimpleDateFormat
import java.util.Date
import com.google.inject.Inject
import models.{EmployeeServices, Employee}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AnyContent, Result, Action, Controller}
import scala.concurrent.Future
/**
  * The imports below are used in the code. Deleting them will result in error
  */
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
  *this case class is used to provide mapping for searching an employee by its name
  *
  * @param name     the name of the employee that will be searched
  */
case class SearchEmployee(name:String)

/**
  *this controller class handles all the mapping of the forms and redirections to several views of the application
  */
class EmployeeController @Inject()(emp: EmployeeServices) extends Controller {

  /**
    * it provides mapping for adding new Employee.
    * to add a new Employee, Employee credentials entered in the form must full fill all the constraints.
    */
  private val addFormMapping = Form(
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "address" -> nonEmptyText,
      "dob" -> nonEmptyText.verifying("Date Format Must Be: (\"dd-MM-yyyy\")", value => ckeckDateFormat(value)),
      "joiningDate" -> nonEmptyText.verifying("Date Format Must Be: (\"dd-MM-yyyy\")", value => ckeckDateFormat(value)),
      "designation" -> nonEmptyText
    )(Employee.apply)(Employee.unapply)
      verifying("This Employee Already Exist In Records",
      value => emp.addEmployee(value))
  )

  /**
    * it provides mapping for editing an existing Employee.
    * to edit an existing Employee, Employee credentials entered in the form must full fill all the constraints.
    */
  private val editFormMapping = Form(
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "address" -> nonEmptyText,
      "dob" -> nonEmptyText.verifying("Date Format Must Be: (\"dd-MM-yyyy\")", value => ckeckDateFormat(value)),
      "joiningDate" -> nonEmptyText.verifying("Date Format Must Be: (\"dd-MM-yyyy\")", value => ckeckDateFormat(value)),
      "designation" -> nonEmptyText
    )(Employee.apply)(Employee.unapply)
      verifying("Record Already Exist !!!", value => emp.editEmployeeList(value))
  )

  /**
    * it provides mapping for searching an existing Employee.
    */
  private val SearchFormMapping = Form(
    mapping(
      "name" -> nonEmptyText
    )(SearchEmployee.apply)(SearchEmployee.unapply)
  )

  /**
    * this method returns the date format which will be used to check the form bindings in controller
    *
    * @return a SimpleDateFormat
    */
  private def dateFormating: SimpleDateFormat = {

    val dateFormat: SimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy")
    dateFormat
  }

  /**
    * it checks wether the entered date is in correct format or not ie (dd-MM-yyyy)
    *
    * @param dateStr its the date whose format is to be checked
    * @return it returns true if date is valid, false otherwise
    */
  private def ckeckDateFormat(dateStr: String): Boolean = {

    try {
      val dt = new Date()
      (dateStr == dateFormating.format(dateFormating.parse(dateStr))) &&
        (dateFormating.parse(dateStr).compareTo(dt) < 0)
    }
    catch {
      case ex: Exception => false
    }
  }

  /**
    * it renders the view to add new Employee
    *
    * @return response with "Ok" and render view to add new Employee
    */
  def addView:Action[AnyContent] = Action {

    implicit request =>
      Ok(views.html.addEmployee(addFormMapping))
  }

  /**
    * it checks wether all mapping constraints to add a new employee are met or not
    *
    * @return response with BadRequest and render the page to add new employee if mapping constraints are not met
    *         otherwise redirect to dashboard on success
    */
  def addEmployeeSubmitForm:Action[AnyContent] = Action.async {

    implicit request =>
      Future {
        addFormMapping.bindFromRequest.fold(
          failed => {
            BadRequest(views.html.addEmployee(failed))
          },
          sucsses => {
            Redirect(routes.EmployeeController.dashboardView).flashing("success" -> "New Employee Added Successfully...")
          }
        )
      }
  }

  /**
    * it renders the view of dashbord
    *
    * @return response with "Ok" and render view of dashboard
    */
  def dashboardView:Action[AnyContent] = Action {

    implicit request =>
      Ok(views.html.dashboard(SearchFormMapping, emp.getEmployeeList))
  }

  /**
    * it checks wether all mapping constraints to search a new employee by name are met or not
    *
    * @return response with BadRequest and render the page of dashboard if mapping constraints are not met
    *         otherwise redirect to dashboard on success
    */
  def filterEmployee:Action[AnyContent] = Action.async { implicit request =>
    Future {
      SearchFormMapping.bindFromRequest.fold(
        failure =>
          BadRequest(views.html.dashboard(SearchFormMapping,  emp.getEmployeeList)),
        success =>
          Ok(views.html.dashboard(SearchFormMapping,  emp.searchByEmployeeName(success.name)))
      )
    }
  }

  /**
    * it renders the view to edit details of the selected employee if the employee id exist otherwise redirects to the dashboard
    *
    * @param id it is the id of the employee whose details are to be edited
    * @return response with redirection o dashboard if employee with given id is not present
    *         otherwise renders the view to edit employee credentials on success
    */
  def editView(id: Int):Action[AnyContent] = Action {

    implicit request =>
      //the if statement is placed to keep check on url manipulation
      if (emp.getEmployee(id).isEmpty) {
        Redirect(routes.EmployeeController.dashboardView).flashing("error" -> "The Employee You Smartly Tried To Edit Is Not A Valid Employee")
      }
      else {
        Ok(views.html.editEmployee(editFormMapping, emp.getEmployee(id).get))
      }
  }

  /**
    * it checks wether all mapping constraints to edit an existing employee by name are met or not
    *
    * @return response with BadRequest and render the page to edit employee detail if mapping constraints are not met
    *         otherwise redirect to dashboard on success
    */
  def editRecord:Action[AnyContent] = Action.async {

    implicit request =>
      Future {
        editFormMapping.bindFromRequest.fold(
          failed => {
            BadRequest(views.html.editEmployee(failed, Employee(0, "dummy", "dummy", "16-04-1992", "16-04-1992", "dummy")))
          },
          sucsses => {
            Redirect(routes.EmployeeController.dashboardView).flashing("success" -> "Employee Record Updated Successfully...")
          }
        )
      }
  }

  /**
    * it deletes the record on basis of the id passed as parameter
    *
    * @param id the id whose coressponding records are to be deleted
    * @return response by redirecting to dashboard after deleting the corrosponding record
    */
  def deleteEmployee(id: Int):Action[AnyContent] = Action {

    implicit request =>
      emp.deleteEmployeeById(id)
      Redirect(routes.EmployeeController.dashboardView).flashing("success" -> "Employee Record Deleted Successfully...")
  }

}


