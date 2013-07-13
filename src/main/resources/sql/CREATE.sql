SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`player`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`player` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `playername` VARCHAR(16) NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`inventory`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `mydb`.`inventory` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `playerID` INT NOT NULL ,
  `content` BLOB NULL ,
  `armor` BLOB NULL ,
  `server` CHAR(32) NULL ,
  PRIMARY KEY (`id`, `playerID`) ,
  INDEX `fk_inventories_player_idx` (`playerID` ASC) ,
  UNIQUE INDEX `adf` USING BTREE (`playerID` ASC, `server` ASC) ,
  CONSTRAINT `fk_inventories_player`
    FOREIGN KEY (`playerID` )
    REFERENCES `mydb`.`player` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `mydb` ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
