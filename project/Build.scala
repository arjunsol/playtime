import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "Playtime"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "com.typesafe.play" %% "play-slick" % "0.5.0.8",
    "mysql" % "mysql-connector-java" % "5.1.27"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
   )
}