# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table `application` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`path` VARCHAR(254) NOT NULL,`create_project` BOOLEAN NOT NULL);
create table `field` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`model_id` BIGINT NOT NULL,`field_type` VARCHAR(254) NOT NULL,`required` BOOLEAN NOT NULL,`related_model_id` BIGINT);
create table `model` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`application_id` BIGINT NOT NULL);
create table `module` (`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`name` VARCHAR(254) NOT NULL,`application_id` BIGINT NOT NULL,`dependencies` BIGINT);
alter table `field` add constraint `field_related_model_id` foreign key(`related_model_id`) references `model`(`id`) on update NO ACTION on delete NO ACTION;
alter table `field` add constraint `field_model_id` foreign key(`model_id`) references `model`(`id`) on update NO ACTION on delete NO ACTION;
alter table `model` add constraint `model_application_id` foreign key(`application_id`) references `application`(`id`) on update NO ACTION on delete NO ACTION;
alter table `module` add constraint `Module_Module_dependencies` foreign key(`dependencies`) references `module`(`id`) on update NO ACTION on delete NO ACTION;
alter table `module` add constraint `module_application_id` foreign key(`application_id`) references `application`(`id`) on update NO ACTION on delete NO ACTION;

# --- !Downs

ALTER TABLE field DROP FOREIGN KEY field_related_model_id;
ALTER TABLE field DROP FOREIGN KEY field_model_id;
ALTER TABLE model DROP FOREIGN KEY model_application_id;
ALTER TABLE module DROP FOREIGN KEY Module_Module_dependencies;
ALTER TABLE module DROP FOREIGN KEY module_application_id;
drop table `application`;
drop table `field`;
drop table `model`;
drop table `module`;

