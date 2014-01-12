package models.fields

import models.DB.FieldRow

class DoubleField(val field: FieldRow) extends Field{
	def controllerForm: String = {
		val formType = if(field.required) "of[Double]" else "optional(of[Double])"
		"\""+field.name+"\" -> "+formType
	}

	def fieldTable: String = {
		val name = field.name
		val required = if(field.required){", O.NotNull"} else {", O.Nullable"}
		s"""def $name = column[Double]("$name"$required)"""
	}

	def fieldType: String = "Double"
}