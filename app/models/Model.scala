package models
import models.DB.ModelRow
import utils.{TextUtils => tu}

class Model(val model: ModelRow){
	val name = model.name
	val varName = tu.underscoreToCamel(name)
	val upName = varName.capitalize
	val controllerName = upName+"Controller"
	val rowName = upName+"Row"
	val tableName = upName+"Table"
	val formName = upName+"Form"
	val pluralName = varName+"s" //TODO add plural name field to model and update this method
}