
package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}

object ModuleTable extends Table[ModuleRow]("module"){

	//FIELDS
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
	def name = column[String]("name",O.NotNull)
	def dependencies = column[Long]("dependencies", O.Nullable)
	def applicationId = column[Long]("application_id",O.NotNull)

	def * = id.? ~ name ~ applicationId ~ dependencies.? <> (ModuleRow.apply _, ModuleRow.unapply _)
	
	//KEYS
	def dependenciesId = foreignKey("Module_Module_dependencies", dependencies, ModuleTable)(_.id)
	def application = foreignKey("module_application_id", applicationId, ApplicationTable)(_.id)

}