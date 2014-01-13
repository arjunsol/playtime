package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.slick.driver.MySQLDriver.DeleteInvoker
import slick.lifted.{Join, MappedTypeMapper}

import utils.FileUtils

import models.fields._
import models.Model

import play.api.Logger

case class ModelRow(
	id: Option[Long] = None,
	name: String,
	applicationId: Long,
	modelId: Option[Long]
){
  lazy val fields = FieldRow.findByModel(this.id.get)
  lazy val renderFields = for (fieldRow <- this.fields) yield (FieldFactory.get(fieldRow))
  lazy val application = ApplicationRow.findById(this.applicationId)

  lazy val relatedFields: List[RelatedField] = for{
    fieldRow <- this.fields
    if(fieldRow.fieldType == "Related")
  }  yield(new RelatedField(fieldRow))

  lazy val model = new Model(this)

  def getPath(folder: String, fileTermination: String): String = {
    val basePath = this.application.get.path + "/" + this.application.get.name + "/"

    val path = basePath+folder+this.name.capitalize+fileTermination
    Logger.debug(path)
    path
  }

  def getViewPath(viewName: String): String = {
    val app = ApplicationRow.findById(this.applicationId)
    val basePath = app.get.path

    basePath + "/" + app.get.name + "/app/views/"+this.name+"/"+viewName+".scala.html"
  }

  def generateAll(): Unit = {
    this.generateController()
    this.generateTable()
    this.generateRow()
    this.generateViews()
  }

  def generateController(): Unit = {
    val path = this.getPath("app/controllers/","Controller.scala")
    FileUtils.writeToFile(path,views.html.model.template.controller(this.model,this.renderFields,this.relatedFields).toString)
  }

  def generateTable(): Unit = {
    val path = this.getPath("app/models/DB/","Table.scala")
    FileUtils.writeToFile(path,views.html.model.template.table(this.model,this.renderFields, this.relatedFields).toString)
  }

  def generateRow(): Unit = {
    val path = this.getPath("app/models/DB/","Row.scala")
    FileUtils.writeToFile(path,views.html.model.template.row(this.model,this.renderFields).toString)
  }

  def generateDetailView(): Unit = {
    val path = this.getViewPath("detail")
    FileUtils.writeToFile(path,views.html.model.template.modelviews.detail(this.model).toString)
  }

  def generateEditView(): Unit = {
    val path = this.getViewPath("edit")
    FileUtils.writeToFile(path,views.html.model.template.modelviews.edit(this.model).toString)
  }

  def generateFormView(): Unit = {
    val path = this.getViewPath("form")
    FileUtils.writeToFile(path,views.html.model.template.modelviews.form(this.model,this.renderFields).toString)
  }

  def generateIndexView(): Unit = {
    val path = this.getViewPath("index")
    FileUtils.writeToFile(path,views.html.model.template.modelviews.index(this.model).toString)
  }

  def generateInsertView(): Unit = {
    val path = this.getViewPath("insert")
    FileUtils.writeToFile(path,views.html.model.template.modelviews.insert(this.model).toString)
  }

  def generateListView(): Unit = {
    val path = this.getViewPath("list")
    FileUtils.writeToFile(path,views.html.model.template.modelviews.list(this.model,this.renderFields).toString)
  }

  def generateViews(): Unit = {
    this.generateDetailView()
    this.generateEditView()
    this.generateFormView()
    this.generateIndexView()
    this.generateInsertView()
    this.generateListView()
  }
}

object ModelRow{
  def save(model: ModelRow):Long = {
    DB.withTransaction { implicit session =>
      val id = ModelTable.returning(ModelTable.id).insert(model)

      val modelId = new FieldRow(None, "id", id, "Id", true)
      FieldTable.insert(modelId)

      val modelName = new FieldRow(None, "name", id, "Name", true)
      FieldTable.insert(modelName)
      
      id
    }
  }
  
  def update(model: ModelRow):Int = {
    DB.withTransaction { implicit session =>
      //Shows.where(_.id === show.id.get).update(show)
      val q = for {
        s <- ModelTable
        if s.id === model.id
      } yield(s)
      q.update(model)
    }
  }
  
  def delete(model: ModelRow):Int = {
    DB.withTransaction { implicit session =>
      //Shows.where(_.id === show.id.get).delete
       val q = for {
        s <- ModelTable
        if s.id === model.id.get
      } yield(s)
      //q.delete
      (new DeleteInvoker(q)).delete
    }
  }

  
  def findAll: List[ModelRow] = {
    DB.withSession { implicit session =>
      Query(ModelTable).list
    }
  }
  
  def findById(id: Long):Option[ModelRow] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- ModelTable if s.id === id
      } yield (s)
      q.firstOption
    }
  }

  def findByApplicationId(id: Long): List[ModelRow] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- ModelTable if s.applicationId === id
      } yield (s)
      q.list
    }
  }

  def findByApplication(applicationId: Long): List[ModelRow] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- ModelTable if s.applicationId === applicationId
      } yield (s)
      q.list
    }
  }

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val models = for {
        p <- ModelTable
      } yield(p)
      for(model <- models.list) yield(model.id.get.toString,model.name) 
    }
  }
}