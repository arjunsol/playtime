
package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.slick.driver.MySQLDriver.DeleteInvoker
import slick.lifted.{Join, MappedTypeMapper}

case class ModuleRow(
  
    id: Option[Long] = None,
  
    name: String,
    
    applicationId: Long,
  
    dependencies: Option[Long]    
  
){
  
}

object ModuleRow{
  def save(Module: ModuleRow):Long = {
    DB.withTransaction { implicit session =>
      ModuleTable.returning(ModuleTable.id).insert(Module)
    }
  }
  
  def update(Module: ModuleRow):Int = {
    DB.withTransaction { implicit session =>
      val q = for {
        s <- ModuleTable
        if s.id === Module.id
      } yield(s)
      q.update(Module)
    }
  }
  
  def delete(Module: ModuleRow):Int = {
    DB.withTransaction { implicit session =>
       val q = for {
        s <- ModuleTable
        if s.id === Module.id.get
      } yield(s)
      (new DeleteInvoker(q)).delete
    }
  }

  
  def findAll: List[ModuleRow] = {
    DB.withSession { implicit session =>
      Query(ModuleTable).list
    }
  }
  
  def findById(id: Long):Option[ModuleRow] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- ModuleTable if s.id === id
      } yield (s)
      q.firstOption
    }
  }
  
  def findByApplicationId(id: Long): List[ModuleRow] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- ModuleTable if s.applicationId === id
      } yield (s)
      q.list
    }
  }

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val Modules = for {
        p <- ModuleTable
      } yield(p)
      for(Module <- Modules.list) yield(Module.id.get.toString,Module.name) 
    }
  }
}