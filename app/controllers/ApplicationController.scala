package controllers

import play.api._
import play.api.mvc._
import models.DB._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}
import controllers.operations.FileOperations
import utils.FileUtils

object ApplicationController extends Controller {
  def index = Action {
    //Ok(views.html.index("Your new application is ready."))
    val applications = ApplicationRow.findAll

    Ok(views.html.application.index(applications))
  }

  val applicationForm = Form(
	    mapping(
	      "id" -> optional(longNumber),
	      "name" -> nonEmptyText,
        "path" -> nonEmptyText,
        "createProject" -> boolean
	    )(ApplicationRow.apply)(ApplicationRow.unapply)
	)

  def insert = Action {
	  Ok(views.html.application.insert(applicationForm))
  }
	

  def detail(id: Long) = Action {
    ApplicationRow.findById(id).map{
      application => {
        //val models = ModelRow.findByApplicationId(application.id.get)
        Ok(views.html.application.detail(application,application.models))
      }
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    applicationForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.application.insert(formWithErrors)),
      application => {
        
        if (application.createProject) {
	        val srcPath = Play.application.path.getAbsolutePath() + "\\public\\projects\\new-scala"
	        val dstPath = application.path + "\\" + application.name
	        val random = new java.security.SecureRandom
	        val newSecret = (1 to 64).map { _ =>
	        	(random.nextInt(74) + 48).toChar
	        }.mkString.replaceAll("\\\\+", "/")
	        
	        FileOperations.copyPath(srcPath, dstPath)
	        FileUtils.writeToFile(dstPath + "\\build.sbt",views.html.model.template.build_sbt(application.name).toString)
	        FileUtils.writeToFile(dstPath + "\\conf\\application.conf",views.html.model.template.application_conf(newSecret).toString)
        }
        val id = ApplicationRow.save(application)        
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