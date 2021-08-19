CREATE SCHEMA `p8db` ;

CREATE TABLE `p8db`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `login` VARCHAR(10) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `login_UNIQUE` (`login`) VISIBLE);

CREATE TABLE `p8db`.`teams` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(10) NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `p8db`.`users_teams` (
  `user_id` INT NULL,
  `team_id` INT NULL,
  UNIQUE INDEX `user_id, team_id` (`user_id` ASC, `team_id` ASC) INVISIBLE,
  CONSTRAINT `user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `p8db`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `team_id`
    FOREIGN KEY (`team_id`)
    REFERENCES `p8db`.`teams` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION);

INSERT INTO `p8db`.`users` (`login`) VALUES('ivanov');
INSERT INTO `p8db`.`teams` (`name`) VALUES('teamA');
