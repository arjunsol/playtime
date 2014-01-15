package controllers

import play.api._
import play.api.mvc._
import models.DB._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}
import controllers.operations.FileOperations
import controllers.operations.ProjectOperations
import utils.FileUtils
import play.templates.Format
import play.api.data.format.Formats._

object ApplicationController extends Controller {
  
  implicit val application = new ApplicationRow(None, "", "", true, None)
  
  def index = Action {
    
    val applications = ApplicationRow.findAll

    Ok(views.html.application.index(applications))
  }

  val applicationForm = Form(
	    mapping(
	    "id" -> optional(of[Long]),
	    "name" -> nonEmptyText,
        "path" -> nonEmptyText,
        "createProject" -> boolean,
        "parentId" -> optional(of[Long])
	    )(ApplicationRow.apply)(ApplicationRow.unapply)
	)

  def insert() = Action {
	  Ok(views.html.application.insert(applicationForm))
  }
  
  def insertModule(id: Long) = Action {
	  Ok(views.html.application.insert_module(applicationForm, id))
  }
	

  def detail(id: Long) = Action {
    ApplicationRow.findById(id).map{
      application => {
        Ok(views.html.application.detail(application, application.modules, application.models))
      }
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    applicationForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.application.insert(formWithErrors)),
      application => {
        
        val id = ApplicationRow.save(application)
        
        if (0 == 0) {
	        if (application.createProject) {
	          ProjectOperations.createProject(application.name, application.path + "\\" + application.name)
	        }       
        } else {
        	val parentApp = ApplicationRow.findById(id).get
        	val modulePath = parentApp.path + "\\" + parentApp.name + "\\modules" + "\\" + application.name
        	ProjectOperations.createModule(application.name, modulePath, id)          
        }
                
        Redirect(routes.ApplicationController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    ApplicationRow.findById(id).map{
      application:ApplicationRow => Ok(views.html.application.edit(applicationForm.fill(application),application))
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    ApplicationRow.findById(id).map{
      applicationRow => {
          ApplicationRow.delete(applicationRow)
          Redirect(routes.ApplicationController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    ApplicationRow.findById(id).map{
      application => {
      applicationForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.application.edit(formWithErrors,application)),
        application => {
          ApplicationRow.update(application)
          Redirect(routes.ApplicationController.detail(application.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }

  def generateAll(id: Long) = Action {
    ApplicationRow.findById(id).map{
      application:ApplicationRow => {
        application.generateAll
        Redirect(routes.ApplicationController.detail(application.id.get)) 
      }
    }.getOrElse(NotFound)
  }
}