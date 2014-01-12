package controllers

import play.api._
import play.api.mvc._

import models.DB._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}

object ModelController extends Controller {
  def index = Action {
    //Ok(views.html.index("Your new model is ready."))
    val models = ModelRow.findAll

    Ok(views.html.model.index(models))
  }

  val modelForm = Form(
      mapping(
        "id" -> optional(longNumber),
        "name" -> nonEmptyText,
        "application" -> longNumber,
        "models" -> optional(longNumber)
      )(ModelRow.apply)(ModelRow.unapply)
  )

  def insert = Action {
    Ok(views.html.model.insert(modelForm, ApplicationRow.getOptions))
  }
  

  def detail(id: Long) = Action {
    ModelRow.findById(id).map{
      model => Ok(views.html.model.detail(model))
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    modelForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.model.insert(formWithErrors,ApplicationRow.getOptions)),
      model => {
        val id = ModelRow.save(model)
        Redirect(routes.ModelController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    ModelRow.findById(id).map{
      model:ModelRow => Ok(views.html.model.edit(modelForm.fill(model), ApplicationRow.getOptions, model))
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    ModelRow.findById(id).map{
      ModelRow => {
          //ModelRow.delete(ModelRow)
          Redirect(routes.ModelController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    ModelRow.findById(id).map{
      model => {
      modelForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.model.edit(formWithErrors, ApplicationRow.getOptions, model)),
        model => {
          ModelRow.update(model)
          Redirect(routes.ModelController.detail(model.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }

  def generateAll(id: Long) = Action {
    ModelRow.findById(id).map{
      model:ModelRow => {
        model.generateAll
        Redirect(routes.ModelController.detail(model.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateController(id: Long) = Action {
    ModelRow.findById(id).map{
      model:ModelRow => {
        model.generateController
        Redirect(routes.ModelController.detail(model.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateTable(id: Long) = Action {
    ModelRow.findById(id).map{
      model:ModelRow => {
        model.generateTable
        Redirect(routes.ModelController.detail(model.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateRow(id: Long) = Action {
    ModelRow.findById(id).map{
      model:ModelRow => {
        model.generateRow()
        Redirect(routes.ModelController.detail(model.id.get)) 
      }
    }.getOrElse(NotFound)
  }

  def generateViews(id: Long) = Action {
    ModelRow.findById(id).map{
      model:ModelRow => {
        model.generateViews()
        Redirect(routes.ModelController.detail(model.id.get)) 
      }
    }.getOrElse(NotFound)
  }
}