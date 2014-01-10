name := "Playtime!"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "commons-io" % "commons-io" % "2.4"
)     

play.Project.playScalaSettings
