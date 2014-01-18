package utils

import java.io.File
import scala.io.Source

object FileUtils{
  def createPath(path: String){
    val directory = new java.io.File(path)
    try{
      if(!directory.exists){
        directory.getParentFile().mkdirs();
      }
    }
}
  
  def fileHasString(path:String, string: String): Boolean = {
      val file = new File(path)
      if (file.exists()) {
    	  return Source.fromFile(file).mkString.contains(string)
      } else {
    	  return false
      }
  }

def writeToFile(path: String, content: String) {
    this.createPath(path)
    val file = new java.io.PrintWriter(new File(path))
    try {
      file.write(content)
    } finally {
      file.close()
    }
  }
}