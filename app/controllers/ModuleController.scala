package controllers

import play.api._
import play.api.mvc._
import models.DB._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}
import controllers.operations.ProjectOperations
import controllers.operations.FileOperations
import scala.collection.immutable.HashMap

object ModuleController extends Controller {
  
  def index = Action {
    val modules = ModuleRow.findAll
    Ok(views.html.module.index(modules))
  }
  
  def getViewMap():Map[ModuleRow, String] = {
    val Modules = ModuleRow.findAll
    
    var map:Map[ModuleRow,String] = Map()
    var name = ""
    
    Modules.foreach{module =>
    	if (module.dependencies != None) {
    	  name = ModuleRow.findById(module.dependencies.get).get.name.toString()
    	}	 
      map += (module -> name) 
    }
    
    return map
  }

  val ModuleForm = Form(
      mapping(
					"id" -> optional(longNumber),
					"name" -> nonEmptyText,
					"applicationId" -> longNumber,
					"dependencies" -> optional(longNumber)

      )(ModuleRow.apply)(ModuleRow.unapply)
  )

  def insert = Action {
    Ok(views.html.module.insert(ModuleForm, ApplicationRow.getOptions))
  }  

  def detail(id: Long) = Action {
    ModuleRow.findById(id).map{ 
      module => Ok(views.html.module.detail(module))
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    ModuleForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.module.insert(formWithErrors, ApplicationRow.getOptions)),
      module => {
        
        val id = ModuleRow.save(module)
        
        val application = ApplicationRow.findById(module.applicationId).get
        
        val modulePath = application.path + "\\" + application.name + "\\modules" + "\\" + module.name
        
        ProjectOperations.createModule(module.name, modulePath)
        
        Redirect(routes.ModuleController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    ModuleRow.findById(id).map{
      Module:ModuleRow => Ok(views.html.module.edit(ModuleForm.fill(Module), ApplicationRow.getOptions, Module))
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    ModuleRow.findById(id).map{
      Module => {
          ModuleRow.delete(Module)
          Redirect(routes.ModuleController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    ModuleRow.findById(id).map{
      Module => {
      ModuleForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.module.edit(formWithErrors, ApplicationRow.getOptions, Module)),
        Module => {
          ModuleRow.update(Module)
          Redirect(routes.ModuleController.detail(Module.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }
}