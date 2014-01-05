package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.slick.driver.MySQLDriver.DeleteInvoker
import slick.lifted.{Join, MappedTypeMapper}

import utils.FileUtils

case class ApplicationRow(
	id: Option[Long] = None,
	name: String,
	path: String,
	createProject: Boolean
){
  lazy val models = ModelRow.findByApplication(this.id.get)
  lazy val modules = ModuleRow.findByApplicationId(this.id.get)

  def generateAll(): Unit = {
    val models = ModelRow.findAll
    for(model <- models){
      model.generateAll
    }

    this.generateRoutes(models)
    this.generateMessages(models)
    this.generateMenu(models)
  }

  def generateMessages(models: List[ModelRow]): Unit = {
    val path = this.path+"conf/messages"

    FileUtils.writeToFile(path,views.html.application.template.messages(this.name, models).toString)
    FileUtils.writeToFile(path+".es",views.html.application.template.messages_es(this.name, models).toString)
  }

  def generateRoutes(models: List[ModelRow]): Unit = {
    val path = this.path+"conf/routes"
    FileUtils.writeToFile(path,views.html.application.template.routes(models).toString)
  }

  def generateMenu(models: List[ModelRow]): Unit = {
    val path = this.path+"app/views/main.scala.html"
    FileUtils.writeToFile(path,views.html.application.template.main(models).toString)
  }
}

object ApplicationRow{
	def save(app: ApplicationRow):Long = {
		DB.withTransaction { implicit session =>
		  ApplicationTable.returning(ApplicationTable.id).insert(app)
		}
	}
  
  def update(app: ApplicationRow):Int = {
    DB.withTransaction { implicit session =>
    	//Shows.where(_.id === show.id.get).update(show)
      val q = for {
        s <- ApplicationTable
        if s.id === app.id
      } yield(s)
      q.update(app)
    }
  }
  
  def delete(app: ApplicationRow):Int = {
    DB.withTransaction { implicit session =>
    	//Shows.where(_.id === show.id.get).delete
    	 val q = for {
        s <- ApplicationTable
        if s.id === app.id.get
      } yield(s)
      //q.delete
      (new DeleteInvoker(q)).delete
    }
  }

	
	def findAll: List[ApplicationRow] = {
	  DB.withSession { implicit session =>
	  	Query(ApplicationTable).list
	  }
	}
	def findById(id: Long):Option[ApplicationRow] = {
	  DB.withSession { implicit session =>
	  	val q = for{
	  	  s <- ApplicationTable if s.id === id
	  	} yield (s)
	  	q.firstOption
	  }
	}

  /*
  def findByIdWithModels(id: Long): Option[(ApplicationRow,List[ModelRow])] = {
    DB.withSession{ implicit session =>
      val q = for{
        a <- ApplicationTable if a.id === id
        m <- ModelTable
      } yield (a,m)
      q.list
    }
  }
  */

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val applications = for {
        p <- ApplicationTable
      } yield(p)
      for(application <- applications.list) yield(application.id.get.toString,application.name) 
    }
  }
}