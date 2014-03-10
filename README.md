Playtime!
======

CRUD Generator for Play 2.2.2 and play-slick

Forked from Grator: https://github.com/geckopeb/grator/wiki which was inspired by CATO(PHP/Smarty).

Features
-------------

<h3>Create top level application</h3>  
Playtime will create the play application for you, currently this only supports a play 2.2.1 skeleton app as generated by using: ```"play new [My app]"```
	
<h3>Allow creation of sub-modules</h3>
Working on larger applications can leave you with a cluttered application with hundreds or thousands of classes. Playtime will help you with layering your app by creating sub-modules. Your parent parent applications dependencies will be updated and children will be compiled along with your main application.
	
<h3>Design models for each of your applications/modules</h3>
Playtime allows you design modules, adding simple and relational fields to models. Playtime will then generate these models for use with play-slick.

An example Table definition:

```scala
package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}

object TestTable extends Table[TestRow]("test"){

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name",O.NotNull)
	
  def * = id.? ~ name ~ <> (TestRow.apply _, TestRow.unapply _)
  
}
```

An example model:  
(model definitions include helper methods for finding and eventually pagination)  

```scala
package models.DB

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import scala.slick.driver.MySQLDriver.DeleteInvoker
import slick.lifted.{Join, MappedTypeMapper}

case class TestRow(
  
    id: Option[Long] = None,
  
    name: String  
){}

object TestRow{
  def save(test: TestRow):Long = {
    DB.withTransaction { implicit session =>
      TestTable.returning(TestTable.id).insert(test)
    }
  }
  
  def update(test: TestRow):Int = {
    DB.withTransaction { implicit session =>
      val q = for {
        s <- TestTable
        if s.id === test.id
      } yield(s)
      q.update(test)
    }
  }
  
  def delete(test: TestRow):Int = {
    DB.withTransaction { implicit session =>
       val q = for {
        s <- TestTable
        if s.id === test.id.get
      } yield(s)
      (new DeleteInvoker(q)).delete
    }
  }
  
  def findAll: List[TestRow] = {
    DB.withSession { implicit session =>
      Query(TestTable).list
    }
  }
  
  def findById(id: Long):Option[TestRow] = {
    DB.withSession { implicit session =>
      val q = for{
        s <- TestTable if s.id === id
      } yield (s)
      q.firstOption
    }
  }

  def getOptions(): Seq[(String,String)] = {
    DB.withSession { implicit session =>
      val tests = for {
        p <- TestTable
      } yield(p)
      for(test <- tests.list) yield(test.id.get.toString,test.name) 
    }
  }
}
```

<h3>Generate controllers/views</h3>
Playtime will output controllers and views for each model you have designed.

An example controller:

```scala

package controllers

import play.api._
import play.api.mvc._

import models.DB._

import play.api.data._
import play.api.data.Forms._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}
import play.api.data.format.Formats._

object TestController extends Controller {
  def index = Action {
    val tests = TestRow.findAll

    Ok(views.html.test.index(tests))
  }

  val TestForm = Form(
      mapping(
        "id" -> optional(longNumber),
        "name" -> nonEmptyText
      )(TestRow.apply)(TestRow.unapply)
  )

  def insert = Action {
    Ok(views.html.test.insert(TestForm))
  }
  

  def detail(id: Long) = Action {
    TestRow.findById(id).map{
      test => Ok(views.html.test.detail(test))
    }.getOrElse(NotFound)
  }
  
  def save = Action { implicit request =>
    TestForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.test.insert(formWithErrors)),
      test => {
        val id = TestRow.save(test)
        Redirect(routes.TestController.detail(id))
      }
    )
  }
  
  def edit(id: Long) = Action{
    TestRow.findById(id).map{
      test:TestRow => Ok(views.html.test.edit(TestForm.fill(test),test))
    }.getOrElse(NotFound)
  }
  
  def delete(id: Long) = Action{
    implicit request =>
    TestRow.findById(id).map{
      test => {
          TestRow.delete(test)
          Redirect(routes.TestController.index())
    }
    }.getOrElse(NotFound)
  }
  
  def update(id: Long) = Action{ implicit request =>
    TestRow.findById(id).map{
      test => {
      TestForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.test.edit(formWithErrors,test)),
        test => {
          TestRow.update(test)
          Redirect(routes.TestController.detail(test.id.get))
        }
      )
    }
    }.getOrElse(NotFound)
  }
}
```

An example view:


```html
@(rows: List[models.DB.TestRow])(implicit lang: Lang)

@main(Messages("test.name")){
	<div class="page-header">
    	<h1>@Messages("test.model_name")</h1>
  </div>
	
  @views.html.test.list(rows)

 	<p>
		<a href="@controllers.routes.TestController.insert()"
		class="btn">
		<i class="icon-plus"></i> @Messages("test.new")</a>
	</p>
}
```
