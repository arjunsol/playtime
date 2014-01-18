package controllers.common

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def status = Action {
    Ok("common 200 OK")
  }
  
}