package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}

object ModelTable extends Table[ModelRow]("model"){
	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name",O.NotNull)
	def applicationId = column[Long]("application_id",O.NotNull)

	def * = id.? ~ name ~ applicationId <> (ModelRow.apply _, ModelRow.unapply _)

	def application = foreignKey("model_application_id", applicationId, ApplicationTable)(_.id)
}

