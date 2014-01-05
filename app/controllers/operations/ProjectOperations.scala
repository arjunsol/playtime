package controllers.operations

import play.api._
import play.api.Play.current
import java.io.{FileFilter, FileWriter}
import utils.FileUtils
import models.DB.ModuleRow

object ProjectOperations {
  
	val srcPath = Play.application.path.getAbsolutePath() + "\\public\\projects\\new-scala"	
	  
	def createProject(name: String, dstPath: String) {
	
		FileOperations.copyPath(srcPath, dstPath, null)
		FileUtils.writeToFile(dstPath + "\\build.sbt",views.html.project.template.build_sbt(name).toString)
						
		writeConf(dstPath)
	}
  
	def createModule(name: String, dstPath: String) {
	  
		FileOperations.copyPath(srcPath, dstPath, new ModuleFileFilter)
		FileUtils.writeToFile(dstPath + "\\conf\\application.conf",views.html.project.template.module_application_conf(name).toString)
		FileUtils.writeToFile(dstPath + "\\app\\controllers\\application.scala",views.html.project.template.module_application_contoller(name).toString)
		FileUtils.writeToFile(dstPath + "\\conf\\" + name + ".routes",views.html.project.template.module_routes(name).toString)
		FileUtils.writeToFile(dstPath + "\\build.sbt", views.html.project.template.module_build_sbt(name).toString)
		
		FileOperations.deleteFile(dstPath + "\\" + "conf" + "\\routes")
		
		val routes = new FileWriter(dstPath + "\\..\\..\\conf\\routes", true)
		
		try {
		  routes.write("\n\n->\t/" + name + "\t" + name + ".Routes")
		} finally {
		  routes.close()
		}
		
		val build = new FileWriter(dstPath + "\\..\\..\\build.sbt", true)
		val modules = ModuleRow.findAll
		
		var moduleNames = ""
		  
		modules.foreach(module => if (moduleNames == "") moduleNames = module.name else moduleNames = moduleNames + ", " + module.name)
		
		FileUtils.writeToFile(dstPath + "\\..\\..\\modules.sbt", views.html.project.template.module_modules_sbt(ModuleRow.findAll, moduleNames).toString)
	  
	}
	
	def writeConf(dstPath: String) {
	    
	  	val random = new java.security.SecureRandom
	  	  
		val newSecret = (1 to 64).map { _ =>
			(random.nextInt(74) + 48).toChar
		}.mkString.replaceAll("\\\\+", "/")
		
		FileUtils.writeToFile(dstPath + "\\conf\\application.conf",views.html.project.template.application_conf(newSecret).toString)	  
	}

}