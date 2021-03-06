package models.fields

import models.DB.FieldRow

class IdField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		"\"id\" -> optional(longNumber)"
	}

	override def htmlForm: String = {
 """@row match {
    	case Some(row) => {
    		<input type="hidden" name="id" value="@row.id.get">
    	}
    	case None => {}
	  }"""
	}

	def fieldTable: String = {
		"def id = column[Long](\"id\", O.PrimaryKey, O.AutoInc)"
	}

	override def nameInTable: String = {
		"id.?"	
	}

	override def list: String = {
		val modelName = field.model.name
		val controllerName = modelName.capitalize+"Controller"
		s"""<a href="@controllers.routes.$controllerName.edit(row.id.get)">@row.id</a>"""
	}

	def fieldType: String = "Long"

	override def classDefinition: String = {
		"id: Option[Long] = None"
	}
}