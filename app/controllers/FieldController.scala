
package controllers

import play.api._
import play.api.mvc._

import models.DB._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}

object FieldController extends Controller {
  def index = Action {
    val fields = FieldRow.findAll

    Ok(views.html.field.index(fields))
  }

  val fieldForm = Form(
      mapping(
        "id" -> optional(longNumber),
        "name" -> nonEmptyText,
        "model" -> longNumber,
        "fieldType" -> nonEmptyText,
        "required" -> boolean,
        "relatedModelId" -> optional(longNumber)
      )(FieldRow.apply)(FieldRow.unapply)
  )

  def insert = Action {
    val modelOptions = ModelRow.getOptions
    val typeOptions = FieldRow.getTypeOptions
    Ok(views.html.field.insert(fieldForm,modelOptions,typeOptions))
  }
  

  def detail(id: Long) = Action {
    FieldRow.findById(id).map{
      field => Ok(views.html.field.detail(field))
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    fieldForm.bindFromRequest.fold(
      formWithErrors => {
        val modelOptions = ModelRow.getOptions
        val typeOptions = FieldRow.getTypeOptions
        BadRequest(views.html.field.insert(formWithErrors,modelOptions,typeOptions))
        },
      field => {
        val id = FieldRow.save(field)
        Redirect(routes.FieldController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    FieldRow.findById(id).map{
      field:FieldRow => {
        val modelOptions = ModelRow.getOptions
        val typeOptions = FieldRow.getTypeOptions
        Ok(views.html.field.edit(fieldForm.fill(field),modelOptions, typeOptions, field))
      }
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    FieldRow.findById(id).map{
      fieldRow => {
          FieldRow.delete(fieldRow)
          Redirect(routes.FieldController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    FieldRow.findById(id).map{
      field => {
      fieldForm.bindFromRequest.fold(
        formWithErrors => {
          val modelOptions = ModelRow.getOptions
          val typeOptions = FieldRow.getTypeOptions
          BadRequest(views.html.field.edit(formWithErrors, modelOptions, typeOptions, field))
        },
        field => {
          FieldRow.update(field)
          Redirect(routes.FieldController.detail(field.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }
}