
package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}

object FieldTable extends Table[FieldRow]("field"){
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name",O.NotNull)
  def modelId = column[Long]("model_id",O.NotNull)

  def fieldType = column[String]("field_type",O.NotNull)
  def required = column[Boolean]("required")

  def relatedModelId = column[Long]("related_model_id",O.Nullable)

  def * = id.? ~ name ~ modelId ~ fieldType ~ required ~ relatedModelId.? <> (FieldRow.apply _, FieldRow.unapply _)

  def model = foreignKey("field_model_id", modelId, ModelTable)(_.id)
  def relatedModel = foreignKey("field_related_model_id", relatedModelId, ModelTable)(_.id)
}